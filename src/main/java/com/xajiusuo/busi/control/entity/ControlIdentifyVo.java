package com.xajiusuo.busi.control.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

/**
 * 布控任务标识
 * Created by shirenjing on 2019/6/22.
 */
public class ControlIdentifyVo extends BaseEntity {

    private String taskid; //任务id
    private String identify; //布控标识 证件编号/车牌号/手机mac地址
    private String idType; //标识类型

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }
}
