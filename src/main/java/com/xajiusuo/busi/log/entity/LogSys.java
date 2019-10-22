package com.xajiusuo.busi.log.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by hadoop on 18-3-9.
 */
@Entity
@ApiModel(value = "logsys", description = "系统日志")
@Table(name = "I_LOGSYS_11", catalog = "village")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LogSys extends BaseEntity {

    private String entityId;//对应实体类ID,可根据实体ID反查

    @ApiModelProperty(value = "地址")
    private String url;//访问地址
    private String operDesc;//操作描述例如:登陆,退出,定时任务,查询,新增修改,删除,发布等
    private String methods;//请求方式 get post
    private String params;//请求参数
    private String types;//请求类型 查询,操作,新增,修改

    private String ip;//用户操作ip
    private Integer depId;//部门ID
    private String depName;//部门名称

    private String itemTag;//模块标识:例如 skryxx
    private String itemName;//模块名称 例如 涉恐人员信息

    private String errInfo;//异常信息

    private String status;//状态 ,成功,失败,异常
    private String msg;//请求描述
    private Integer rowNums;//条数

//    private long duration;//持续时长

    public LogSys(){

    }


    public LogSys(String id){
        this.setId(id);
    }


    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOperDesc() {
        return operDesc;
    }

    public void setOperDesc(String operDesc) {
        this.operDesc = operDesc;
    }

    public String getMethods() {
        return methods;
    }

    public void setMethods(String methods) {
        this.methods = methods;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getDepId() {
        return depId;
    }

    public void setDepId(Integer depId) {
        this.depId = depId;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public String getItemTag() {
        return itemTag;
    }

    public void setItemTag(String itemTag) {
        this.itemTag = itemTag;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


    public String getErrInfo() {
        return errInfo;
    }

    public void setErrInfo(String errInfo) {
        this.errInfo = errInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getRowNums() {
        return rowNums;
    }

    public void setRowNums(Integer rowNums) {
        this.rowNums = rowNums;
    }
}
