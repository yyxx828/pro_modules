package com.xajiusuo.busi.control.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 布控任务
 * Created by shirenjing on 2019/6/22.
 */
@ApiModel
@Entity
@Table(name = "t_controltask_16", catalog = "village")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class ControlTask extends BaseEntity {

    @ApiModelProperty(required = true,value="任务名称",dataType = "string")
    private String taskName;
    @ApiModelProperty(required = true,value="布控类型 1：人员布控 2：车辆布控 3:wifi布控",dataType = "string")
    private String controlType;
    @ApiModelProperty( required = true, value = "布控开始时间", dataType = "date")
    @JsonFormat( timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date controlStartTime;
    @ApiModelProperty( required = true, value = "布控结束时间", dataType = "date")
    @JsonFormat( timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date controlEndTime;
    @ApiModelProperty(required = true,value="0:未布控 1：布控中 2：布控结束",dataType = "string")
    private String controlStatus;

    @Transient
    private int nums;  //子表数据条数
//    @Transient
//    private List<ControlIdentify> identifyList;  //标识


    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getControlType() {
        return controlType;
    }

    public String getControlTypes() {
        String type = controlType;
        switch (controlType){
            case "1":
                type =  "人员布控";
                break;
            case "2":
                type =  "车辆布控";
                break;
            case "3":
                type =  "wifi布控";
                break;
        }
        return type;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    public Date getControlStartTime() {
        return controlStartTime;
    }

    public void setControlStartTime(Date controlStartTime) {
        this.controlStartTime = controlStartTime;
    }

    public Date getControlEndTime() {
        return controlEndTime;
    }

    public void setControlEndTime(Date controlEndTime) {
        this.controlEndTime = controlEndTime;
    }

    public String getControlStatus() {
            return controlStatus;
    }

    public String getControlStatuses() {
        String type = controlStatus;
        if("0".equals(controlStatus)){
            type =  "未布控";
        }else if("1".equals(controlStatus)){
            type = "布控中";
        }else if("2".equals(controlStatus)){
            type = "布控结束";
        }
        return type;
    }

    public void setControlStatus(String controlStatus) {
        this.controlStatus = controlStatus;
    }

    public int getNums() {
        return nums;
    }

    public void setNums(int nums) {
        this.nums = nums;
    }

//    public List<ControlIdentify> getIdentifyList() {
//        return identifyList;
//    }
//
//    public void setIdentifyList(List<ControlIdentify> identifyList) {
//        this.identifyList = identifyList;
//    }
}
