package com.xajiusuo.busi.sync.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

/**
 * Created by 杨勇 on 19-6-27.
 */
@Entity
@ApiModel(value = "InDataLog", description = "数据导入记录")
@Table(name = "T_InDataLog_10", catalog = "village")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","rules"})
public class InDataLog extends BaseEntity {

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "scid")
    @ApiModelProperty(value = "同步配置", required = true)
    private SyncConf syncConf;

    @ApiModelProperty(value = "总数量", dataType = "int")
    private Integer allNum;

    @ApiModelProperty(value = "成功数量", dataType = "int")
    private Integer successNum;

    @ApiModelProperty(value = "异常数量", dataType = "int")
    private Integer errNum;

    @ApiModelProperty(value = "异常信息", dataType = "string")
    private String errInfo;

    public SyncConf getSyncConf() {
        return syncConf;
    }

    public void setSyncConf(SyncConf syncConf) {
        this.syncConf = syncConf;
    }

    public Integer getAllNum() {
        return allNum;
    }

    public void setAllNum(Integer allNum) {
        this.allNum = allNum;
    }

    public void addAllNum(){
        if(allNum == null)
            allNum = 0;
        allNum++;
    }

    public Integer getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(Integer successNum) {
        this.successNum = successNum;
    }

    public Integer getErrNum() {
        return errNum;
    }

    public void setErrNum(Integer errNum) {
        this.errNum = errNum;
    }

    public String getErrInfo() {
        return errInfo;
    }

    public void setErrInfo(String errInfo) {
        if(errInfo != null && errInfo.length() > 380){
            errInfo = errInfo.substring(0,379) + "...";
        }
        this.errInfo = errInfo;
    }

    public void numinit(Integer scId){
        allNum = 0;
        successNum = 0;
        errNum = 0;
        syncConf = new SyncConf(scId);
    }
}
