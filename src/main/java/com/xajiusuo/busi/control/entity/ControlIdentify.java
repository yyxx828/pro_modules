package com.xajiusuo.busi.control.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

/**
 * 布控任务标识
 * Created by shirenjing on 2019/6/22.
 */
@ApiModel
@Entity
@Table(name = "t_controlidentify_16", catalog = "village")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class ControlIdentify extends BaseEntity {

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true)
    @JoinColumn(name = "taskid")
    ControlTask task;
    @ApiModelProperty(required = true,value="布控标识 证件编号/车牌号/手机mac地址",dataType = "String")
    private String identify;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = true)
    @JoinColumn(name="idType",referencedColumnName = "keys")
    private Diction idTypeDiction;  //标识类型 人员：盗窃 贩毒 车辆：违章 wifi:丢失

    public ControlIdentify() {
    }

    public ControlIdentify(ControlTask task, String identify, Diction idTypeDiction) {
        this.task = task;
        this.identify = identify;
        this.idTypeDiction = idTypeDiction;
    }

    public ControlTask getTask() {
        return task;
    }

    public void setTask(ControlTask task) {
        this.task = task;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public Diction getIdTypeDiction() {
        return idTypeDiction;
    }

    public void setIdTypeDiction(Diction idTypeDiction) {
        this.idTypeDiction = idTypeDiction;
    }
}
