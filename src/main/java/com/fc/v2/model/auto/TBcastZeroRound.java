package com.fc.v2.model.auto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 零报告催办轮次收口对象 t_bcast_zero_round
 *
 * @author fuce
 * @date 2026-10-01
 */
@TableName("t_bcast_zero_round")
@ApiModel(value = "TBcastZeroRound", description = "零报告催办轮次收口")
public class TBcastZeroRound implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键")
    private Long id;

    /** 轮次代号 */
    @TableField("round_no")
    @ApiModelProperty(value = "轮次代号")
    private String roundNo;

    /** 这一轮开口的钟点 */
    @TableField("run_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "这一轮开口的钟点")
    private Date runAt;

    /** 本轮所对那一日 */
    @TableField("win_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "本轮所对那一日")
    private Date winDate;

    /** 本轮是否撞保障期封盘 0否 1是 */
    @TableField("frozen")
    @ApiModelProperty(value = "本轮是否撞保障期封盘 0否 1是")
    private Integer frozen;

    /** 本轮够档捞起条数 */
    @TableField("picked_count")
    @ApiModelProperty(value = "本轮够档捞起条数")
    private Integer pickedCount;

    /** 收口:还欠开口条数 */
    @TableField("pending_count")
    @ApiModelProperty(value = "收口:还欠开口条数")
    private Integer pendingCount;

    /** 收口:本轮催到条数 */
    @TableField("done_count")
    @ApiModelProperty(value = "收口:本轮催到条数")
    private Integer doneCount;

    /** 收口:催不动条数 */
    @TableField("fail_count")
    @ApiModelProperty(value = "收口:催不动条数")
    private Integer failCount;

    /** 收口留言(整轮无到期写本轮无到期) */
    @TableField("message")
    @ApiModelProperty(value = "收口留言")
    private String message;

    @TableField("del_flag")
    @ApiModelProperty(value = "删除标记 0正常 1删除")
    private Integer delFlag;

    @TableField(value = "create_by", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建者")
    private String createBy;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @TableField(value = "update_by", fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新者")
    private String updateBy;

    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoundNo() {
        return roundNo;
    }

    public void setRoundNo(String roundNo) {
        this.roundNo = roundNo;
    }

    public Date getRunAt() {
        return runAt;
    }

    public void setRunAt(Date runAt) {
        this.runAt = runAt;
    }

    public Date getWinDate() {
        return winDate;
    }

    public void setWinDate(Date winDate) {
        this.winDate = winDate;
    }

    public Integer getFrozen() {
        return frozen;
    }

    public void setFrozen(Integer frozen) {
        this.frozen = frozen;
    }

    public Integer getPickedCount() {
        return pickedCount;
    }

    public void setPickedCount(Integer pickedCount) {
        this.pickedCount = pickedCount;
    }

    public Integer getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(Integer pendingCount) {
        this.pendingCount = pendingCount;
    }

    public Integer getDoneCount() {
        return doneCount;
    }

    public void setDoneCount(Integer doneCount) {
        this.doneCount = doneCount;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
