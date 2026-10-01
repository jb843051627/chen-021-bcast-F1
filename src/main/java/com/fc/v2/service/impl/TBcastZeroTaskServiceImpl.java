package com.fc.v2.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fc.v2.mapper.auto.TBcastSiteMapper;
import com.fc.v2.mapper.auto.TBcastZeroChannelMapper;
import com.fc.v2.mapper.auto.TBcastZeroRoundMapper;
import com.fc.v2.mapper.auto.TBcastZeroSkipMapper;
import com.fc.v2.mapper.auto.TBcastZeroTaskMapper;
import com.fc.v2.model.auto.TBcastSite;
import com.fc.v2.model.auto.TBcastZeroChannel;
import com.fc.v2.model.auto.TBcastZeroRound;
import com.fc.v2.model.auto.TBcastZeroSkip;
import com.fc.v2.model.auto.TBcastZeroTask;
import com.fc.v2.service.ITBcastZeroTaskService;
import com.fc.v2.service.bcast.WorkdayCalendar;
import com.fc.v2.service.bcast.ZeroOpening;
import com.fc.v2.service.bcast.ZeroRoundResult;
import com.fc.v2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

/**
 * 零报告催办到期单 Service业务层处理（scheduling-job 形状：周期执行）。
 *
 * 规矩：
 * - 捞单只认开口档踩过去没有，用 {@link WorkdayCalendar}+{@link ZeroOpening} 这一把尺，与回写同源；
 * - 保障期封盘日手里有已到档的也不动，等下一轮；
 * - 上一轮已催到的（status=1）不当候催捞回；
 * - 台站摘走/关停、值机联系人空缺，当场转催不动，卡环与原委并写同条，清单照挂；
 * - 催到须同时落"情形转催到"+"催到那一刻"，并各落站内、手机两路痕迹；
 * - 同轮一桩走岔不连坐其余；收口三数回库逐条点，空轮记"本轮无到期"。
 *
 * @author fuce
 * @date 2026-09-14
 */
@Service
public class TBcastZeroTaskServiceImpl implements ITBcastZeroTaskService {

    private static final Logger log = LoggerFactory.getLogger(TBcastZeroTaskServiceImpl.class);

    /** 条目情形 */
    private static final int STATUS_WAIT = 0;
    private static final int STATUS_DONE = 1;
    private static final int STATUS_FAIL = 2;

    /** 底册情形 */
    private static final int SITE_CLOSED = 1;

    /** 路别 */
    private static final int CHANNEL_STATION = 0;
    private static final int CHANNEL_PHONE = 1;
    private static final int CHANNEL_SENT = 0;

    @Resource
    private TBcastZeroTaskMapper taskMapper;
    @Resource
    private TBcastZeroSkipMapper skipMapper;
    @Resource
    private TBcastZeroRoundMapper roundMapper;
    @Resource
    private TBcastZeroChannelMapper channelMapper;
    @Resource
    private TBcastSiteMapper siteMapper;
    @Resource
    private PlatformTransactionManager transactionManager;

    @Override
    public TBcastZeroTask selectTBcastZeroTaskById(Long id) {
        return taskMapper.selectById(id);
    }

    @Override
    public List<TBcastZeroTask> selectActiveList() {
        Map<LocalDate, Integer> cal = loadCalendar();
        Date now = new Date();
        LocalDate today = toLocalDate(now);
        List<TBcastZeroTask> list = taskMapper.selectList(new QueryWrapper<TBcastZeroTask>()
                .eq("del_flag", 0)
                .orderByAsc("due_at", "id"));
        for (TBcastZeroTask t : list) {
            // 展示也用同一把尺，避免年末挑单与回写两张表接不平
            if (t.getDueAt() != null) {
                t.setRemainingWorkdays(WorkdayCalendar.workdaysRemaining(today,
                        ZeroOpening.toLdt(t.getDueAt()).toLocalDate(), cal));
            }
            t.setReachedNow(ZeroOpening.reached(t.getDueAt(),
                    t.getAmount() == null ? BigDecimal.ZERO : t.getAmount(), now, cal));
        }
        return list;
    }

    /** 载入现行日历：date -> skip_type（已撤/已删的不进） */
    private Map<LocalDate, Integer> loadCalendar() {
        List<TBcastZeroSkip> rows = skipMapper.selectList(new QueryWrapper<TBcastZeroSkip>()
                .eq("status", 0)
                .eq("del_flag", 0));
        Map<LocalDate, Integer> map = new HashMap<LocalDate, Integer>();
        for (TBcastZeroSkip r : rows) {
            if (r.getSkipDate() != null) {
                map.put(toLocalDate(r.getSkipDate()), r.getSkipType());
            }
        }
        return map;
    }

    private static LocalDate toLocalDate(Date d) {
        if (d instanceof java.sql.Date) {
            return ((java.sql.Date) d).toLocalDate();
        }
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    public List<TBcastZeroTask> listDue(Date at) {
        Map<LocalDate, Integer> cal = loadCalendar();
        List<TBcastZeroTask> all = taskMapper.selectList(new QueryWrapper<TBcastZeroTask>()
                .eq("del_flag", 0)
                .eq("status", STATUS_WAIT)
                .orderByAsc("due_at", "id"));
        List<TBcastZeroTask> due = new ArrayList<TBcastZeroTask>();
        for (TBcastZeroTask t : all) {
            // 只认开口档踩过去没有；封盘/底册/联系人留给 runOnce，不在捞单这一层剔
            if (ZeroOpening.reached(t.getDueAt(), t.getAmount() == null ? BigDecimal.ZERO : t.getAmount(), at, cal)) {
                due.add(t);
            }
        }
        return due;
    }

    @Override
    public ZeroRoundResult runOnce(Date at) {
        Date now = at == null ? new Date() : at;
        LocalDate today = toLocalDate(now);
        Map<LocalDate, Integer> cal = loadCalendar();
        boolean frozen = WorkdayCalendar.isFrozen(today, cal);

        // 先立本轮轮次（条目 last_round_id 要挂它）
        TBcastZeroRound round = new TBcastZeroRound();
        round.setRoundNo(new SimpleDateFormat("'ROUND-'yyyyMMdd-HHmmss").format(now));
        round.setRunAt(now);
        round.setWinDate(java.sql.Date.valueOf(today));
        round.setFrozen(frozen ? 1 : 0);
        round.setPickedCount(0);
        round.setPendingCount(0);
        round.setDoneCount(0);
        round.setFailCount(0);
        round.setDelFlag(0);
        roundMapper.insert(round);
        final Long roundId = round.getId();

        List<TBcastZeroTask> due = listDue(now);

        // 保障期占住的那几日：手里就是有已到档的也不动，等下一轮
        if (!frozen) {
            for (TBcastZeroTask t : due) {
                processOneInNewTx(t, roundId, now);
            }
        }

        // 收口三个数：回库逐条点到位，不照内存/屏幕誊
        Integer pending = taskMapper.selectCount(new QueryWrapper<TBcastZeroTask>()
                .eq("del_flag", 0)
                .eq("status", STATUS_WAIT));
        Integer done = taskMapper.selectCount(new QueryWrapper<TBcastZeroTask>()
                .eq("del_flag", 0)
                .eq("status", STATUS_DONE)
                .eq("last_round_id", roundId));
        Integer fail = taskMapper.selectCount(new QueryWrapper<TBcastZeroTask>()
                .eq("del_flag", 0)
                .eq("status", STATUS_FAIL)
                .eq("last_round_id", roundId));
        int picked = (done == null ? 0 : done) + (fail == null ? 0 : fail);

        String message;
        if (picked == 0) {
            // 整轮一条没捞着：正常收口，不当事故
            message = frozen ? "本轮无到期（保障期封盘压单）" : "本轮无到期";
        } else {
            message = (frozen ? "保障期封盘；" : "")
                    + "本轮催到" + done + "条，催不动" + fail + "条，还欠开口" + pending + "条";
        }

        round.setPickedCount(picked);
        round.setPendingCount(pending == null ? 0 : pending);
        round.setDoneCount(done == null ? 0 : done);
        round.setFailCount(fail == null ? 0 : fail);
        round.setMessage(message);
        roundMapper.updateById(round);

        ZeroRoundResult result = new ZeroRoundResult();
        result.setRoundId(roundId);
        result.setRoundNo(round.getRoundNo());
        result.setFrozen(frozen);
        result.setPickedCount(picked);
        result.setPendingCount(round.getPendingCount());
        result.setDoneCount(round.getDoneCount());
        result.setFailCount(round.getFailCount());
        result.setMessage(message);
        result.getPicked().addAll(listPickedOfRound(roundId));
        return result;
    }

    /** 本轮经办（催到/催不动）的条目，回库点 */
    private List<TBcastZeroTask> listPickedOfRound(Long roundId) {
        return taskMapper.selectList(new QueryWrapper<TBcastZeroTask>()
                .eq("del_flag", 0)
                .eq("last_round_id", roundId)
                .in("status", STATUS_DONE, STATUS_FAIL)
                .orderByAsc("id"));
    }

    /**
     * 逐条在**独立新事务**里办：一条走岔只回滚它自己，标记催不动后继续其余，不许甩下整轮。
     */
    private void processOneInNewTx(final TBcastZeroTask task, final Long roundId, final Date now) {
        TransactionTemplate tt = new TransactionTemplate(transactionManager);
        tt.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        try {
            tt.execute(status -> {
                processOne(task, roundId, now);
                return null;
            });
        } catch (Exception e) {
            log.error("零报告催办条目经办走岔 itemNo={}，本轮不连坐其余", task.getItemNo(), e);
            // 连"标记催不动"都没能在原事务落下，则在新事务里补挂，原委写明走岔
            try {
                tt.execute(status -> {
                    markFail(task.getId(), roundId, now, "经办异常", "经办走岔：" + e.getMessage());
                    return null;
                });
            } catch (Exception ex) {
                log.error("条目催不动补挂仍失败 itemNo={}", task.getItemNo(), ex);
            }
        }
    }

    /** 单条办理：两类开不成单的当场催不动；齐活的催到并落两路痕迹。 */
    private void processOne(TBcastZeroTask task, Long roundId, Date now) {
        TBcastZeroTask fresh = taskMapper.selectById(task.getId());
        if (fresh == null || (fresh.getDelFlag() != null && fresh.getDelFlag() == 1)) {
            return;
        }
        // 双保险：已催到的不许回捞（正常 listDue 已只取候催）
        if (fresh.getStatus() != null && fresh.getStatus() != STATUS_WAIT) {
            return;
        }

        TBcastSite site = fresh.getSiteId() == null ? null
                : siteMapper.selectById(fresh.getSiteId());

        if (site == null || (site.getDelFlag() != null && site.getDelFlag() == 1)) {
            markFail(fresh.getId(), roundId, now, "底册核验", "台站早已从底册摘走，无在册台站可对");
            return;
        }
        if (site.getStatus() != null && site.getStatus() == SITE_CLOSED) {
            markFail(fresh.getId(), roundId, now, "底册核验",
                    "台站已关停摘出（" + site.getSiteName() + "），不办催办");
            return;
        }
        if (StringUtils.isEmpty(fresh.getContactPhone())) {
            markFail(fresh.getId(), roundId, now, "值机联系人核验", "值机联系人那一栏空着几季没人补，手机一路无收方");
            return;
        }

        // 催到：情形由候催转催到 + 催到那一刻，两者同时落（只落其一不算走完）
        fresh.setStatus(STATUS_DONE);
        fresh.setDoneTime(now);
        fresh.setLastRoundId(roundId);
        fresh.setBlockStage(null);
        fresh.setBlockReason(null);
        taskMapper.updateById(fresh);

        // 话分两路，送到哪路独记哪路，手机不能替站内交差
        String county = countyOf(site);
        insertChannel(fresh.getId(), roundId, CHANNEL_STATION, county + "广电局", now, fresh.getTplVer());
        insertChannel(fresh.getId(), roundId, CHANNEL_PHONE,
                "台站值机室（手机 " + fresh.getContactPhone() + "）", now, fresh.getTplVer());
    }

    /** 属地"省—市—县"取末段；缺路名时退回"县" */
    private static String countyOf(TBcastSite site) {
        String road = site.getRoadName();
        if (StringUtils.isEmpty(road)) {
            return "县";
        }
        String[] parts = road.split("—|-");
        return parts[parts.length - 1];
    }

    /** 当场转挂催不动：卡在哪一环、原委并写同一条 */
    private void markFail(Long taskId, Long roundId, Date now, String stage, String reason) {
        TBcastZeroTask t = taskMapper.selectById(taskId);
        if (t == null) {
            return;
        }
        t.setStatus(STATUS_FAIL);
        t.setDoneTime(null);
        t.setLastRoundId(roundId);
        t.setBlockStage(stage);
        t.setBlockReason(reason);
        taskMapper.updateById(t);
    }

    private void insertChannel(Long taskId, Long roundId, int channel, String receiver, Date now, String tplVer) {
        TBcastZeroChannel c = new TBcastZeroChannel();
        c.setTaskId(taskId);
        c.setRoundId(roundId);
        c.setChannel(channel);
        c.setReceiver(receiver);
        c.setSentAt(now);
        c.setTplVer(tplVer);
        c.setStatus(CHANNEL_SENT);
        c.setDelFlag(0);
        channelMapper.insert(c);
    }

    @Override
    public List<TBcastZeroChannel> listChannels(Long taskId) {
        return channelMapper.selectList(new QueryWrapper<TBcastZeroChannel>()
                .eq("task_id", taskId)
                .eq("del_flag", 0)
                .orderByAsc("channel"));
    }

    @Override
    public List<TBcastZeroRound> listRounds() {
        return roundMapper.selectList(new QueryWrapper<TBcastZeroRound>()
                .eq("del_flag", 0)
                .orderByDesc("run_at", "id"));
    }
}
