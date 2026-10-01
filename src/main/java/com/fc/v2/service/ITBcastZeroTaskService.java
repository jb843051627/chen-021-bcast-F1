package com.fc.v2.service;

import java.util.Date;
import java.util.List;

import com.fc.v2.model.auto.TBcastZeroChannel;
import com.fc.v2.model.auto.TBcastZeroRound;
import com.fc.v2.model.auto.TBcastZeroTask;
import com.fc.v2.service.bcast.ZeroRoundResult;

/**
 * 零报告催办到期单 Service接口（scheduling-job 形状：周期执行，无前台增删改入口）。
 *
 * 捞单（listDue/runOnce）与回写共用同一把尺（WorkdayCalendar + ZeroOpening）。
 *
 * @author fuce
 * @date 2026-09-14
 */
public interface ITBcastZeroTaskService {

    /** 按主键回查条目 */
    TBcastZeroTask selectTBcastZeroTaskById(Long id);

    /** 现行清单（不藏不掖：候催/催到/催不动都挂着，未删） */
    List<TBcastZeroTask> selectActiveList();

    /**
     * 档已踩过的候催条目（status=0、未删、此刻已到/已过最早开窗时刻）。
     * 只认"开口档踩过去没有"这一件事；封盘冻结、台站摘走、联系人空缺是 runOnce 里的下一层，不在此剔除。
     */
    List<TBcastZeroTask> listDue(Date at);

    /**
     * 执行一轮：够着的每一条逐条办到底，一桩走岔不甩其余；收口三个数回库逐条点出。
     * 整轮一条没捞着也正常收口（留言"本轮无到期"）。
     */
    ZeroRoundResult runOnce(Date at);

    /** 某条目的两路送达痕迹 */
    List<TBcastZeroChannel> listChannels(Long taskId);

    /** 历轮收口（新到旧） */
    List<TBcastZeroRound> listRounds();
}
