package com.fc.v2.service.bcast;

import com.fc.v2.model.auto.TBcastZeroTask;

import java.util.ArrayList;
import java.util.List;

/**
 * 一轮收口交回的三个数与本轮名单。三个数由服务**回库逐条点出**，调用方不得照屏誊。
 *
 * @author fuce
 * @date 2026-10-01
 */
public class ZeroRoundResult {

    /** 轮次id */
    private Long roundId;
    /** 轮次代号 */
    private String roundNo;
    /** 还欠开口：库里仍候催(status=0)的条数（未到档、被封盘压住都算） */
    private int pendingCount;
    /** 本轮催到：本轮由候催转催到的条数 */
    private int doneCount;
    /** 催不动：本轮当场挂起的条数 */
    private int failCount;
    /** 本轮够档捞起的条数（催到+催不动） */
    private int pickedCount;
    /** 是否撞保障期封盘 */
    private boolean frozen;
    /** 收口留言；整轮一条没捞着为"本轮无到期"，属正常收口 */
    private String message;
    /** 本轮实际捞起的条目 */
    private final List<TBcastZeroTask> picked = new ArrayList<TBcastZeroTask>();

    public Long getRoundId() {
        return roundId;
    }

    public void setRoundId(Long roundId) {
        this.roundId = roundId;
    }

    public String getRoundNo() {
        return roundNo;
    }

    public void setRoundNo(String roundNo) {
        this.roundNo = roundNo;
    }

    public int getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(int pendingCount) {
        this.pendingCount = pendingCount;
    }

    public int getDoneCount() {
        return doneCount;
    }

    public void setDoneCount(int doneCount) {
        this.doneCount = doneCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public int getPickedCount() {
        return pickedCount;
    }

    public void setPickedCount(int pickedCount) {
        this.pickedCount = pickedCount;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TBcastZeroTask> getPicked() {
        return picked;
    }
}
