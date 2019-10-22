package com.xajiusuo.busi.control.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 布控任务
 * Created by shirenjing on 2019/6/22.
 */

public class ControlTaskVo extends BaseEntity {

    private String taskName;  //任务名称
    private String controlType;  //布控类型 1：人员布控 2：车辆布控 3:wifi布控
    @JsonFormat( timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date controlStartTime;  //布控开始时间
    @JsonFormat( timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date controlEndTime;  //布控结束时间
    private String controlStatus;  //0:未布控 1：布控中 2：布控结束
    private List<ControlIdentifyVo> identifyVoList = null;

    public void addIdList(ControlIdentifyVo vo){
        if(identifyVoList==null) identifyVoList = new ArrayList<>();
        identifyVoList.add(vo);
    }


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
        switch (controlType){
            case "1":
                controlType =  "人员布控";
                break;
            case "2":
                controlType =  "车辆布控";
                break;
            case "3":
                controlType =  "wifi布控";
                break;
        }
        return controlType;
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
        if("0".equals(controlStatus)){
            return "未布控";
        }else if("1".equals(controlStatus)){
            return "布控中";
        }else if("2".equals(controlStatus)){
            return "布控结束";
        }else{
            return controlStatus;
        }
    }

    public void setControlStatus(String controlStatus) {
        this.controlStatus = controlStatus;
    }

    public List<ControlIdentifyVo> getIdentifyVoList() {
        return identifyVoList;
    }

    public void setIdentifyVoList(List<ControlIdentifyVo> identifyVoList) {
        this.identifyVoList = identifyVoList;
    }

    public ControlTask getEntity(){
        ControlTask task = new ControlTask();
        task.setId(id);
        task.setTaskName(taskName);
        task.setControlType(controlType);
        task.setControlStatus(controlStatus);
        task.setControlStartTime(controlStartTime);
        task.setControlEndTime(controlEndTime);
        return task;
    }
}
