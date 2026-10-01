package com.fc.v2.service.bcast;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 工作日尺与开口档判定的纯逻辑单测（2026-10 月历：国庆假、调休补班、省令停办、保障期封盘）。
 */
class WorkdayCalendarTest {

    private static final LocalDate OCT09 = LocalDate.of(2026, 10, 9);   // 周五
    private static final LocalDate OCT10 = LocalDate.of(2026, 10, 10);  // 周六·调休补班
    private static final LocalDate OCT11 = LocalDate.of(2026, 10, 11);  // 周日
    private static final LocalDate OCT12 = LocalDate.of(2026, 10, 12);  // 周一·省令停办
    private static final LocalDate OCT13 = LocalDate.of(2026, 10, 13);  // 周二
    private static final LocalDate OCT15 = LocalDate.of(2026, 10, 15);  // 周四·保障期封盘

    private Map<LocalDate, Integer> calendar() {
        Map<LocalDate, Integer> m = new HashMap<>();
        for (int d = 1; d <= 7; d++) {
            m.put(LocalDate.of(2026, 10, d), WorkdayCalendar.TYPE_HOLIDAY);
        }
        m.put(OCT10, WorkdayCalendar.TYPE_MAKEUP_WORKDAY);
        m.put(OCT12, WorkdayCalendar.TYPE_PROVINCE_OFF);
        m.put(OCT15, WorkdayCalendar.TYPE_FREEZE);
        m.put(LocalDate.of(2026, 10, 16), WorkdayCalendar.TYPE_FREEZE);
        return m;
    }

    @Test
    void workdayCountingExcludesNightsHolidaysProvinceOff() {
        Map<LocalDate, Integer> cal = calendar();
        assertTrue(WorkdayCalendar.isWorkday(OCT09, cal), "普通周五是工作日");
        assertTrue(WorkdayCalendar.isWorkday(OCT10, cal), "调休补班的周六按工作日");
        assertFalse(WorkdayCalendar.isWorkday(OCT11, cal), "无记载周日不入数");
        assertFalse(WorkdayCalendar.isWorkday(OCT12, cal), "省令停办整日不入数");
        assertFalse(WorkdayCalendar.isWorkday(LocalDate.of(2026, 10, 1), cal), "国家法定假不入数");
    }

    @Test
    void freezeDayStillCountsAsWorkday() {
        Map<LocalDate, Integer> cal = calendar();
        assertTrue(WorkdayCalendar.isWorkday(OCT15, cal), "保障期封盘日仍算工作日");
        assertTrue(WorkdayCalendar.isFrozen(OCT15, cal), "但被封盘占住");
        assertFalse(WorkdayCalendar.isFrozen(OCT09, cal));
    }

    @Test
    void shiftBackSkipsNonWorkdays() {
        Map<LocalDate, Integer> cal = calendar();
        // 从 10-13 倒找 2 个工作日：跨过省令停办(12)、周日(11)，落到调休补班(10)、再到 09
        assertEquals(OCT09, WorkdayCalendar.shiftBackWorkdays(OCT13, 2, cal));
        assertEquals(OCT10, WorkdayCalendar.shiftBackWorkdays(OCT13, 1, cal));
        assertEquals(OCT13, WorkdayCalendar.shiftBackWorkdays(OCT13, 0, cal));
    }

    @Test
    void remainingWorkdaysCountsOnlyWholeFutureWorkdays() {
        Map<LocalDate, Integer> cal = calendar();
        // (10-09, 10-13] 间：10 上班、13 上班 = 2；11 周日、12 省令不计
        assertEquals(2, WorkdayCalendar.workdaysRemaining(OCT09, OCT13, cal));
        assertEquals(0, WorkdayCalendar.workdaysRemaining(OCT13, OCT13, cal), "档在今天报0(跨夜不入数)");
        assertEquals(1, WorkdayCalendar.workdaysRemaining(OCT09, OCT10, cal));
    }

    @Test
    void openingThresholdUsesLeadWorkdaysAndWallClock() {
        Map<LocalDate, Integer> cal = calendar();
        Date now = at("2026-10-09 09:00:00");

        // 无提前：08:00 档已踩过，10:00 档未踩过
        assertTrue(ZeroOpening.reached(at("2026-10-09 08:00:00"), new BigDecimal("0"), now, cal));
        assertFalse(ZeroOpening.reached(at("2026-10-09 10:00:00"), new BigDecimal("0"), now, cal));

        // 提前 2 工作日：开窗落在 10-09；09:00 档踩线算到，10:00 档未到
        assertTrue(ZeroOpening.reached(at("2026-10-13 09:00:00"), new BigDecimal("2"), now, cal));
        assertFalse(ZeroOpening.reached(at("2026-10-13 10:00:00"), new BigDecimal("2"), now, cal));

        // 09:01 再看，10:00 档仍未到，但 09:00 档当然已过
        Date later = at("2026-10-09 09:01:00");
        assertTrue(ZeroOpening.reached(at("2026-10-13 09:00:00"), new BigDecimal("2"), later, cal));
        assertFalse(ZeroOpening.reached(at("2026-10-13 10:00:00"), new BigDecimal("2"), later, cal));
    }

    private static java.util.Date at(String s) {
        return Timestamp.valueOf(s);
    }
}
