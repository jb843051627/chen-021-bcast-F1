package com.fc.v2.service.impl;

import com.fc.v2.mapper.auto.TBcastSiteMapper;
import com.fc.v2.mapper.auto.TBcastZeroChannelMapper;
import com.fc.v2.mapper.auto.TBcastZeroRoundMapper;
import com.fc.v2.mapper.auto.TBcastZeroSkipMapper;
import com.fc.v2.mapper.auto.TBcastZeroTaskMapper;
import com.fc.v2.model.auto.TBcastSite;
import com.fc.v2.model.auto.TBcastZeroChannel;
import com.fc.v2.model.auto.TBcastZeroSkip;
import com.fc.v2.model.auto.TBcastZeroTask;
import com.fc.v2.service.bcast.ZeroRoundResult;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StreamUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 零报告催办一轮的端到端规则测试（H2 内存库 + MyBatis-Plus，手工装配，不引整站上下文）。
 */
class TBcastZeroTaskServiceImplTest {

    private TBcastZeroTaskMapper taskMapper;
    private TBcastZeroSkipMapper skipMapper;
    private TBcastSiteMapper siteMapper;
    private TBcastZeroRoundMapper roundMapper;
    private TBcastZeroChannelMapper channelMapper;
    private TBcastZeroTaskServiceImpl service;

    private static Timestamp at(String s) {
        return Timestamp.valueOf(s);
    }

    @BeforeEach
    void setUp() throws Exception {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:zero;MODE=MySQL;DB_CLOSE_DELAY=-1");
        ds.setUsername("sa");
        ds.setPassword("");

        String ddl = StreamUtils.copyToString(new ClassPathResource("sql/bcast_h2.sql").getInputStream(),
                StandardCharsets.UTF_8);
        try (java.sql.Connection c = ds.getConnection(); java.sql.Statement st = c.createStatement()) {
            for (String sql : ddl.split(";")) {
                if (!sql.trim().isEmpty()) {
                    st.execute(sql);
                }
            }
        }

        MybatisConfiguration conf = new MybatisConfiguration();
        conf.setMapUnderscoreToCamelCase(true);
        conf.addMapper(TBcastSiteMapper.class);
        conf.addMapper(TBcastZeroSkipMapper.class);
        conf.addMapper(TBcastZeroTaskMapper.class);
        conf.addMapper(TBcastZeroRoundMapper.class);
        conf.addMapper(TBcastZeroChannelMapper.class);
        MybatisSqlSessionFactoryBean fb = new MybatisSqlSessionFactoryBean();
        fb.setDataSource(ds);
        fb.setConfiguration(conf);
        fb.setGlobalConfig(new GlobalConfig());
        SqlSessionFactory factory = fb.getObject();
        SqlSessionTemplate tpl = new SqlSessionTemplate(factory);

        taskMapper = tpl.getMapper(TBcastZeroTaskMapper.class);
        skipMapper = tpl.getMapper(TBcastZeroSkipMapper.class);
        siteMapper = tpl.getMapper(TBcastSiteMapper.class);
        roundMapper = tpl.getMapper(TBcastZeroRoundMapper.class);
        channelMapper = tpl.getMapper(TBcastZeroChannelMapper.class);

        service = new TBcastZeroTaskServiceImpl();
        ReflectionTestUtils.setField(service, "taskMapper", taskMapper);
        ReflectionTestUtils.setField(service, "skipMapper", skipMapper);
        ReflectionTestUtils.setField(service, "siteMapper", siteMapper);
        ReflectionTestUtils.setField(service, "roundMapper", roundMapper);
        ReflectionTestUtils.setField(service, "channelMapper", channelMapper);
        ReflectionTestUtils.setField(service, "transactionManager", new DataSourceTransactionManager(ds));
    }

    private void seedSite(long id, String name, String road, int status) {
        TBcastSite s = new TBcastSite();
        s.setId(id);
        s.setSiteNo("TZ" + id);
        s.setSiteName(name);
        s.setRoadName(road);
        s.setStatus(status);
        s.setDelFlag(0);
        siteMapper.insert(s);
    }

    private long skipSeq = 1000;

    private void seedSkip(String date, int type) {
        TBcastZeroSkip k = new TBcastZeroSkip();
        k.setId(skipSeq++);
        k.setSkipDate(java.sql.Date.valueOf(date));
        k.setSkipType(type);
        k.setStatus(0);
        k.setDelFlag(0);
        skipMapper.insert(k);
    }

    private TBcastZeroTask task(long id, long siteId, String due, String amount, String phone, int status) {
        TBcastZeroTask t = new TBcastZeroTask();
        t.setId(id);
        t.setItemNo("ITEM-" + id);
        t.setSiteId(siteId);
        t.setBizType(0);
        t.setDueAt(at(due));
        t.setAmount(amount == null ? null : new BigDecimal(amount));
        t.setContactPhone(phone);
        t.setTplVer("v3");
        t.setContent("零报告催办 " + id);
        t.setStatus(status);
        t.setDelFlag(0);
        return t;
    }

    /** 2026-10 月历：国庆假 1..7、10-10 调休补班、10-12 省令停办、10-15/16 保障期封盘 */
    private void seedOctoberCalendar() {
        for (int d = 1; d <= 7; d++) {
            seedSkip(String.format("2026-10-%02d", d), 0);
        }
        seedSkip("2026-10-10", 3);
        seedSkip("2026-10-12", 1);
        seedSkip("2026-10-15", 2);
        seedSkip("2026-10-16", 2);
    }

    @Test
    void fullRound_picksAllDue_doneFailAndTwoTraces_othersLeftWaiting() {
        seedOctoberCalendar();
        seedSite(0, "苗岭转播台", "长岭省—河湾区—苗岭县", 0);
        seedSite(1, "滨河北微波站", "长岭省—河湾区—滨河北", 1);
        seedSite(2, "青云山发射台", "长岭省—河湾区—青云山县", 0);

        // 08:00 已过档
        taskMapper.insert(task(2001, 0, "2026-10-09 08:00:00", "0", "13900000001", 0));
        // 10:00 未到档
        taskMapper.insert(task(2002, 0, "2026-10-09 10:00:00", "0", "13900000002", 0));
        // 提前2工作日，10-13 09:00 -> 开窗 10-09 09:00，踩线到档
        taskMapper.insert(task(2003, 0, "2026-10-13 09:00:00", "2", "13900000003", 0));
        // 提前2工作日但开窗 10:00，未到
        taskMapper.insert(task(2004, 0, "2026-10-13 10:00:00", "2", "13900000004", 0));
        // 10-15 到期、不提前，本轮未到档
        taskMapper.insert(task(2005, 0, "2026-10-15 08:00:00", "0", "13900000005", 0));
        // 台站关停 -> 催不动
        taskMapper.insert(task(2006, 1, "2026-10-09 08:00:00", "0", "13900000006", 0));
        // 联系人空缺 -> 催不动
        taskMapper.insert(task(2007, 0, "2026-10-09 08:00:00", "0", null, 0));
        // 设备核点事由在册 -> 催到
        TBcastZeroTask check = task(2008, 2, "2026-10-09 08:30:00", "0", "13900000008", 0);
        check.setBizType(1);
        taskMapper.insert(check);
        // 昨日已催到 -> 不回捞
        TBcastZeroTask done = task(2009, 0, "2026-10-08 08:00:00", "0", "13900000009", 1);
        done.setDoneTime(at("2026-10-08 09:05:00"));
        taskMapper.insert(done);

        // 捞单只认到档：2001/2003/2006/2007/2008 五条（须在跑轮之前点，跑过后它们就不再是候催）
        assertEquals(5, service.listDue(at("2026-10-09 09:00:00")).size());

        ZeroRoundResult r = service.runOnce(at("2026-10-09 09:00:00"));

        // 收口三数回库点：催到3、催不动2、还欠开口3（2002/2004/2005）
        assertEquals(3, r.getDoneCount());
        assertEquals(2, r.getFailCount());
        assertEquals(5, r.getPickedCount());
        assertEquals(3, r.getPendingCount());
        assertFalse(r.isFrozen());

        // 催到：情形+那一刻同时落
        TBcastZeroTask t2001 = taskMapper.selectById(2001L);
        assertEquals(1, t2001.getStatus());
        assertNotNull(t2001.getDoneTime());
        assertEquals(r.getRoundId(), t2001.getLastRoundId());
        // 两路痕迹各自独立
        List<TBcastZeroChannel> ch = channelMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<TBcastZeroChannel>()
                        .eq("task_id", 2001L).orderByAsc("channel"));
        assertEquals(2, ch.size(), "站内/手机各一路，手机不能替站内");
        assertEquals(0, ch.get(0).getChannel());
        assertTrue(ch.get(0).getReceiver().contains("广电局"));
        assertEquals(1, ch.get(1).getChannel());
        assertTrue(ch.get(1).getReceiver().contains("13900000001"));

        // 台站关停：催不动 + 卡环 + 原委，清单照挂（未删）
        TBcastZeroTask t2006 = taskMapper.selectById(2006L);
        assertEquals(2, t2006.getStatus());
        assertEquals("底册核验", t2006.getBlockStage());
        assertTrue(t2006.getBlockReason().contains("关停"));
        assertEquals(0, t2006.getDelFlag());
        assertNull(t2006.getDoneTime());

        // 联系人空缺：催不动
        TBcastZeroTask t2007 = taskMapper.selectById(2007L);
        assertEquals(2, t2007.getStatus());
        assertEquals("值机联系人核验", t2007.getBlockStage());

        // 未到档/昨日已催到：原样
        assertEquals(0, taskMapper.selectById(2002L).getStatus());
        assertEquals(0, taskMapper.selectById(2004L).getStatus());
        assertEquals(0, taskMapper.selectById(2005L).getStatus());
        assertEquals(1, taskMapper.selectById(2009L).getStatus());
        assertEquals(0, channelMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<TBcastZeroChannel>()
                        .eq("task_id", 2009L).eq("round_id", r.getRoundId())));

        // 催不动的条目现行清单里照样挂着
        assertTrue(service.selectActiveList().stream().anyMatch(x -> x.getId() == 2006L));
        assertTrue(service.selectActiveList().stream().anyMatch(x -> x.getId() == 2007L));

        // 历轮收口落了一行
        assertEquals(1, roundMapper.selectCount(null));
    }

    @Test
    void notReached_normalRoundSaysNoDue() {
        seedOctoberCalendar();
        seedSite(0, "苗岭转播台", "长岭省—苗岭县", 0);
        taskMapper.insert(task(3001, 0, "2026-10-09 10:00:00", "0", "13900000001", 0));

        ZeroRoundResult r = service.runOnce(at("2026-10-09 09:00:00"));

        assertEquals(0, r.getPickedCount());
        assertEquals(0, r.getDoneCount());
        assertEquals(0, r.getFailCount());
        assertEquals(1, r.getPendingCount(), "未到档的仍候催，算还欠开口");
        assertTrue(r.getMessage().contains("本轮无到期"));
        assertEquals(0, taskMapper.selectById(3001L).getStatus());
        assertNull(taskMapper.selectById(3001L).getDoneTime());
        assertEquals(1, roundMapper.selectCount(null), "空轮也正常落收口，不当事故");
    }

    @Test
    void freezeDay_holdsDueItemsForNextRound() {
        seedOctoberCalendar();
        seedSite(0, "苗岭转播台", "长岭省—苗岭县", 0);
        // 10-15 08:00 到期、无提前，到 09:00 已踩档，但 10-15 是保障期封盘日
        taskMapper.insert(task(4001, 0, "2026-10-15 08:00:00", "0", "13900000001", 0));

        // 捞单这一层认得它已到档
        assertEquals(1, service.listDue(at("2026-10-15 09:00:00")).size());

        ZeroRoundResult r = service.runOnce(at("2026-10-15 09:00:00"));
        assertTrue(r.isFrozen());
        assertEquals(0, r.getPickedCount());
        assertEquals(0, r.getDoneCount());
        assertTrue(r.getMessage().contains("封盘"));
        // 手里有已到档的也不动
        assertEquals(0, taskMapper.selectById(4001L).getStatus());
        assertNull(taskMapper.selectById(4001L).getDoneTime());
    }

    @Test
    void alreadyDone_isNotPickedAgain() {
        seedOctoberCalendar();
        seedSite(0, "苗岭转播台", "长岭省—苗岭县", 0);
        TBcastZeroTask done = task(5001, 0, "2026-10-08 08:00:00", "0", "13900000001", 1);
        done.setDoneTime(at("2026-10-08 09:00:00"));
        taskMapper.insert(done);

        ZeroRoundResult r = service.runOnce(at("2026-10-09 09:00:00"));
        assertEquals(0, r.getDoneCount());
        assertEquals(0, r.getFailCount());
        assertEquals(1, taskMapper.selectById(5001L).getStatus());
    }

    @Test
    void oneFailure_doesNotStopTheRest() {
        seedSite(0, "甲台", "长岭省—甲县", 0);
        seedSite(1, "关停台", "长岭省—关停", 1);
        taskMapper.insert(task(6001, 0, "2026-10-09 08:00:00", "0", "13900000001", 0));
        taskMapper.insert(task(6002, 1, "2026-10-09 08:00:00", "0", "13900000002", 0));
        taskMapper.insert(task(6003, 0, "2026-10-09 08:00:00", "0", "13900000003", 0));

        ZeroRoundResult r = service.runOnce(at("2026-10-09 09:00:00"));
        assertEquals(2, r.getDoneCount(), "头一条之后、走岔一条，第三条仍要办到底");
        assertEquals(1, r.getFailCount());
        assertEquals(1, taskMapper.selectById(6001L).getStatus());
        assertEquals(2, taskMapper.selectById(6002L).getStatus());
        assertEquals(1, taskMapper.selectById(6003L).getStatus());
    }

    @Test
    void missingSite_isFailWithReason() {
        seedSite(0, "甲台", "长岭省—甲县", 0);
        // siteId=99 底册里没有
        taskMapper.insert(task(7001, 99, "2026-10-09 08:00:00", "0", "13900000001", 0));

        ZeroRoundResult r = service.runOnce(at("2026-10-09 09:00:00"));
        assertEquals(1, r.getFailCount());
        TBcastZeroTask t = taskMapper.selectById(7001L);
        assertEquals(2, t.getStatus());
        assertTrue(t.getBlockReason().contains("底册摘走"));
    }

    @Test
    void reconciliationCounts_matchDbRecount() {
        seedSite(0, "甲台", "长岭省—甲县", 0);
        seedSite(1, "关停台", "长岭省—关停", 1);
        taskMapper.insert(task(8001, 0, "2026-10-09 08:00:00", "0", "13900000001", 0));
        taskMapper.insert(task(8002, 1, "2026-10-09 08:00:00", "0", "13900000002", 0));
        taskMapper.insert(task(8003, 0, "2026-10-09 12:00:00", "0", "13900000003", 0));

        ZeroRoundResult r = service.runOnce(at("2026-10-09 09:00:00"));

        long wait = taskMapper.selectCount(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<TBcastZeroTask>()
                .eq("status", 0).eq("del_flag", 0));
        long done = taskMapper.selectCount(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<TBcastZeroTask>()
                .eq("status", 1).eq("last_round_id", r.getRoundId()));
        long fail = taskMapper.selectCount(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<TBcastZeroTask>()
                .eq("status", 2).eq("last_round_id", r.getRoundId()));
        assertEquals(wait, r.getPendingCount());
        assertEquals(done, r.getDoneCount());
        assertEquals(fail, r.getFailCount());
    }
}
