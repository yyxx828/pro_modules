package com.xajiusuo.busi.flow.entity;

import com.xajiusuo.jpa.config.BaseIntEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 流程分配
 * Created by 杨勇  on 2018/5/25.
 */
@ApiModel
@Entity
@Table(name = "F_FLOWALLOT_11", catalog = "village")
public class FlowAllot  extends BaseIntEntity {

  private String busi;//适用业务 flowType=9可不填
  private String busiName;//使用业务名称 flowType=9可不填
  private Integer flowType;//流程类型0指定人,1部门+角色 2角色 9是默认

  private Integer did;//部门编号
  private String depName;//部门名称
  private Integer pid;//职位编号
  private String dutyName;//职位名称
  private Integer userId;//指定人
  private String userName;//用户名称

  private Integer flowDefId;//对应流程id
  private String flowName;//流程名称

  private String condition;//其它条件,非开发预设选项请勿填写

  private Boolean runs;//是否启用

  public String getBusi() {
    return busi;
  }

  public void setBusi(String busi) {
    this.busi = busi;
  }

  public String getBusiName() {
    return busiName;
  }

  public void setBusiName(String busiName) {
    this.busiName = busiName;
  }

  public Boolean getRuns() {
    return runs;
  }

  public void setRuns(Boolean runs) {
    this.runs = runs;
  }

  public Integer getFlowType() {
    return flowType;
  }

  public void setFlowType(Integer flowType) {
    this.flowType = flowType;
  }

  public Integer getDid() {
    return did;
  }

  public void setDid(Integer did) {
    this.did = did;
  }

  public String getDepName() {
    return depName;
  }

  public void setDepName(String depName) {
    this.depName = depName;
  }

  public Integer getPid() {
    return pid;
  }

  public void setPid(Integer pid) {
    this.pid = pid;
  }

  public String getDutyName() {
    return dutyName;
  }

  public void setDutyName(String dutyName) {
    this.dutyName = dutyName;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public Integer getFlowDefId() {
    return flowDefId;
  }

  public void setFlowDefId(Integer flowDefId) {
    this.flowDefId = flowDefId;
  }

  public String getFlowName() {
    return flowName;
  }

  public void setFlowName(String flowName) {
    this.flowName = flowName;
  }
}
