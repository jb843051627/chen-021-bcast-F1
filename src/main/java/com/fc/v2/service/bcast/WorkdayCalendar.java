package com.fc.v2.service.bcast;

import java.time.LocalDate;
import java.util.Map;

/**
 * 零报告催办——剩余工作日与开口档的**同一把尺**。
 *
 * 全处只认这一种数法，捞单（listDue）与回写（runOnce/页面展示）都调它，不许各自另起炉灶：
 * 1. 跨夜那一段不入数：只按整日数，当天剩余的钟点不折成一天；
 * 2. 国家放的假(skip_type=0)、省里通令不办公那几日(skip_type=1)不入工作日；
 * 3. 保障期封盘(skip_type=2)那几日**仍算工作日**，只是催办被压住（见轮次服务）；
 * 4. 调休补班(skip_type=3)落在周末的，那一日改按工作日；
 * 5. 无日历记载的日子，周一至周五为工作日，周六周日不入数。
 *
 * 日历以 LocalDate（整日）为键，钟点由调用方在日历之外另行比较，避免把跨夜时段折算进来。
 *
 * @author fuce
 * @date 2026-10-01
 */
public final class WorkdayCalendar {

    /** 国家法定假：整日不入工作日 */
    public static final int TYPE_HOLIDAY = 0;
    /** 省令停办：整日不入工作日 */
    public static final int TYPE_PROVINCE_OFF = 1;
    /** 保障期封盘：仍算工作日，只压住催办 */
    public static final int TYPE_FREEZE = 2;
    /** 调休补班：周末改按工作日 */
    public static final int TYPE_MAKEUP_WORKDAY = 3;

    private WorkdayCalendar() {
    }

    /**
     * 某一日是否按工作日数。
     *
     * @param day   整日
     * @param types 日历记载 date -&gt; skip_type（只放现行记载）
     */
    public static boolean isWorkday(LocalDate day, Map<LocalDate, Integer> types) {
        Integer t = types == null ? null : types.get(day);
        if (t != null) {
            // 法定假、省令停办：整日剔出；封盘不剔；调休补班：整日收入
            return t == TYPE_FREEZE || t == TYPE_MAKEUP_WORKDAY;
        }
        int w = day.getDayOfWeek().getValue();
        return w >= 1 && w <= 5;
    }

    /** 某一日是否被保障期封盘占住（封盘日仍算工作日，只是不开口） */
    public static boolean isFrozen(LocalDate day, Map<LocalDate, Integer> types) {
        Integer t = types == null ? null : types.get(day);
        return t != null && t == TYPE_FREEZE;
    }

    /**
     * 从锚点日向前倒找 n 个工作日，落到第 n 个工作日那一日。
     * 锚点本身不计步数（n=0 直接返回锚点）；每退一步先跨一日，再跳过所有非工作日。
     */
    public static LocalDate shiftBackWorkdays(LocalDate anchor, int n, Map<LocalDate, Integer> types) {
        LocalDate cur = anchor;
        for (int i = 0; i < n; i++) {
            cur = cur.minusDays(1);
            while (!isWorkday(cur, types)) {
                cur = cur.minusDays(1);
            }
        }
        return cur;
    }

    /**
     * 数到应开口那一档还剩几个工作日。跨夜段不入数，故只数 (today, due] 之间的整日：
     * 档就在今天报 0；明天工作日到期报 1。
     */
    public static int workdaysRemaining(LocalDate today, LocalDate due, Map<LocalDate, Integer> types) {
        if (due == null || due.isBefore(today)) {
            return 0;
        }
        int cnt = 0;
        LocalDate d = today.plusDays(1);
        while (!d.isAfter(due)) {
            if (isWorkday(d, types)) {
                cnt++;
            }
            d = d.plusDays(1);
        }
        return cnt;
    }
}
