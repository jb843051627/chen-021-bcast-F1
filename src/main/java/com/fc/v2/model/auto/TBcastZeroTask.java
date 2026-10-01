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
import java.math.BigDecimal;
import java.util.Date;

/**
 * 零报告催办到期单对象 t_bcast_zero_task
 *
 * @author fuce
 * @date 2026-09-12
 */
@TableName("t_bcast_zero_task")
@ApiModel(value = "TBcastZeroTask", description = "零报告催办到期单")
public class TBcastZeroTask implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键")
    private Long id;

    /** 零报告催办条目代号 */
    @TableField("item_no")
    @ApiModelProperty(value = "零报告催办条目代号")
    private String itemNo;

    /** 所对发射台站(底册id) */
    @TableField("site_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所对发射台站(底册id)")
    private Long siteId;

    /** 事由 0零报告催办 1保障期前设备核点 */
    @TableField("biz_type")
    @ApiModelProperty(value = "事由 0零报告催办 1保障期前设备核点")
    private Integer bizType;

    /** 应开口那一日收到哪个钟点为止(开口档,写死不许顺延) */
    @TableField("due_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "应开口那一日收到哪个钟点为止")
    private Date dueAt;

    /** 最可提前几个工作日开口(事前唯一可调) */
    @TableField("amount")
    @ApiModelProperty(value = "最可提前几个工作日开口")
    private BigDecimal amount;

    /** 依照哪一版稿子播发 */
    @TableField("tpl_ver")
    @ApiModelProperty(value = "依照哪一版稿子播发")
    private String tplVer;

    /** 值机联系人手机(空着几季没人补=催不动) */
    @TableField("contact_phone")
    @ApiModelProperty(value = "值机联系人手机")
    private String contactPhone;

    /** 事由与所对台站的记要 */
    @TableField("content")
    @ApiModelProperty(value = "事由与所对台站的记要")
    private String content;

    /** 条目情形 0候催 1已催到 2催不动 */
    @TableField("status")
    @ApiModelProperty(value = "条目情形 0候催 1已催到 2催不动")
    private Integer status;

    /** 催到那一刻(status=1时必须落) */
    @TableField("done_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "催到那一刻")
    private Date doneTime;

    /** 最近一次经办的轮次id */
    @TableField("last_round_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "最近一次经办的轮次id")
    private Long lastRoundId;

    /** 催不动:卡在哪一环 */
    @TableField("block_stage")
    @ApiModelProperty(value = "催不动:卡在哪一环")
    private String blockStage;

    /** 催不动:原委(日后可按原委挑一批) */
    @TableField("block_reason")
    @ApiModelProperty(value = "催不动:原委")
    private String blockReason;

    /** 展示用(非持久):数到开口档还剩几个工作日 */
    @TableField(exist = false)
    @ApiModelProperty(value = "剩余工作日(展示)")
    private Integer remainingWorkdays;

    /** 展示用(非持久):此刻开口档是否已踩过 */
    @TableField(exist = false)
    @ApiModelProperty(value = "是否已到档(展示)")
    private Boolean reachedNow;

    /** 删除标记 0正常 1删除 */
    @TableField("del_flag")
    @ApiModelProperty(value = "删除标记 0正常 1删除")
    private Integer delFlag;

    /** 创建者 */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建者")
    private String createBy;

    /** 创建时间 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /** 更新者 */
    @TableField(value = "update_by", fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新者")
    private String updateBy;

    /** 更新时间 */
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /** 备注 */
    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public Date getDueAt() {
        return dueAt;
    }

    public void setDueAt(Date dueAt) {
        this.dueAt = dueAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public String getTplVer() {
        return tplVer;
    }

    public void setTplVer(String tplVer) {
        this.tplVer = tplVer;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Date getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(Date doneTime) {
        this.doneTime = doneTime;
    }

    public Long getLastRoundId() {
        return lastRoundId;
    }

    public void setLastRoundId(Long lastRoundId) {
        this.lastRoundId = lastRoundId;
    }

    public String getBlockStage() {
        return blockStage;
    }

    public void setBlockStage(String blockStage) {
        this.blockStage = blockStage;
    }

    public String getBlockReason() {
        return blockReason;
    }

    public void setBlockReason(String blockReason) {
        this.blockReason = blockReason;
    }

    public Integer getRemainingWorkdays() {
        return remainingWorkdays;
    }

    public void setRemainingWorkdays(Integer remainingWorkdays) {
        this.remainingWorkdays = remainingWorkdays;
    }

    public Boolean getReachedNow() {
        return reachedNow;
    }

    public void setReachedNow(Boolean reachedNow) {
        this.reachedNow = reachedNow;
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
