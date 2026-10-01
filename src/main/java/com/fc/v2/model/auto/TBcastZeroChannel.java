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
 * 零报告催办两路送达痕迹对象 t_bcast_zero_channel
 *
 * @author fuce
 * @date 2026-10-01
 */
@TableName("t_bcast_zero_channel")
@ApiModel(value = "TBcastZeroChannel", description = "零报告催办两路送达痕迹")
public class TBcastZeroChannel implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键")
    private Long id;

    /** 所属催办条目id */
    @TableField("task_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所属催办条目id")
    private Long taskId;

    /** 所属轮次id */
    @TableField("round_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所属轮次id")
    private Long roundId;

    /** 路别 0站内(县广电局收) 1手机(台站值机室收) */
    @TableField("channel")
    @ApiModelProperty(value = "路别 0站内 1手机")
    private Integer channel;

    /** 收方 */
    @TableField("receiver")
    @ApiModelProperty(value = "收方")
    private String receiver;

    /** 送抵那一刻 */
    @TableField("sent_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "送抵那一刻")
    private Date sentAt;

    /** 本路所依稿子版次 */
    @TableField("tpl_ver")
    @ApiModelProperty(value = "本路所依稿子版次")
    private String tplVer;

    /** 送达情形 0已送抵 1未达 */
    @TableField("status")
    @ApiModelProperty(value = "送达情形 0已送抵 1未达")
    private Integer status;

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

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getRoundId() {
        return roundId;
    }

    public void setRoundId(Long roundId) {
        this.roundId = roundId;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public String getTplVer() {
        return tplVer;
    }

    public void setTplVer(String tplVer) {
        this.tplVer = tplVer;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
