package com.fc.v2.service.bcast;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * 零报告催办——开口档判定（捞单与回写共用的第二块纯逻辑）。
 *
 * 开口档（应开口那一日收到哪个钟点为止）**写死在条目上，不许为凑一批顺延**；
 * 事前唯一能调的是"最可提前几个工作日开口"。开窗时刻 = 从开口档的日期向前倒找
 * amount 个工作日，钟点仍用开口档上写死的那个钟点（跨夜段不入数，钟点不随倒找挪到前一夜）。
 *
 * @author fuce
 * @date 2026-10-01
 */
public final class ZeroOpening {

    private ZeroOpening() {
    }

    /**
     * 折成 LocalDateTime，保留库内墙面钟点。MyBatis 取回的本就是 java.sql.Timestamp，
     * 直接 {@link Timestamp#toLocalDateTime()} 不再做时区换算（与库里 yyyy-MM-dd HH:mm:ss 逐字对齐）；
     * 对非 Timestamp 的 Date 走系统默认时区兜底。
     */
    public static LocalDateTime toLdt(Date d) {
        if (d instanceof Timestamp) {
            return ((Timestamp) d).toLocalDateTime();
        }
        return d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
    }

    /** 最可提前工作日数：空当 0，负数也按 0（不许事后开口） */
    public static int leadDays(BigDecimal amount) {
        if (amount == null) {
            return 0;
        }
        int n = amount.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        return Math.max(n, 0);
    }

    /**
     * 本条最早开窗时刻。
     *
     * @param dueAt 写死的开口档（日期+止点钟点）
     * @param amount 最可提前几个工作日
     */
    public static LocalDateTime windowStart(Date dueAt, BigDecimal amount, Map<LocalDate, Integer> types) {
        LocalDateTime due = toLdt(dueAt);
        LocalDate startDay = WorkdayCalendar.shiftBackWorkdays(due.toLocalDate(), leadDays(amount), types);
        return LocalDateTime.of(startDay, due.toLocalTime());
    }

    /**
     * 开口那一档踩过去了没有：此刻已到/已过最早开窗时刻即算踩过（踩线算到档）。
     * 只认这一件事，不掺封盘、不掺底册/联系人——那些是开窗之后的下一层。
     */
    public static boolean reached(Date dueAt, BigDecimal amount, Date now, Map<LocalDate, Integer> types) {
        if (dueAt == null || now == null) {
            return false;
        }
        LocalDateTime start = windowStart(dueAt, amount, types);
        return !toLdt(now).isBefore(start);
    }
}
