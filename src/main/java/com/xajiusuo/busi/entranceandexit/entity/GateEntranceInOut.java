package com.xajiusuo.busi.entranceandexit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 出入口门禁采集信息进出表
 * Created by shirenjing on 2019/6/21.
 */
@ApiModel
@Entity
@Table(name = "T_GATEENTRANCEINOUT_16", catalog = "village")
public class GateEntranceInOut implements Serializable {
    @Id
    @GeneratedValue(
            generator = "uuid"
    )
    @GenericGenerator(
            name = "uuid",
            strategy = "uuid"
    )
    @ApiModelProperty(
            required = false,
            value = "主键",
            dataType = "string"
    )
    private String id;
    private String entranceCardno;
    @ApiModelProperty(required = true,value="进时间",dataType = "Integer")
    private Integer intime;
    @ApiModelProperty(required = true,value="出时间",dataType = "Integer")
    private Integer outtime;
    @ApiModelProperty(required = true,value="状态",dataType = "Integer")
    private Integer status;
    @ApiModelProperty(required = true,value="停留时长",dataType = "Integer")
    private Integer stoptime;
    @ApiModelProperty(required = true,value="进源数据ID",dataType = "String")
    private String inid;
    @ApiModelProperty(required = true,value="出源数据ID",dataType = "String")
    private String outid;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(required = true,value="进日期",dataType = "Date")
    private Date indata;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(required = true,value="出日期",dataType = "Date")
    private Date outdata;
    private boolean flag; //0:出入口门禁 1：楼宇门禁

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntranceCardno() {
        return entranceCardno;
    }

    public void setEntranceCardno(String entranceCardno) {
        this.entranceCardno = entranceCardno;
    }

    public Integer getIntime() {
        return intime;
    }

    public void setIntime(Integer intime) {
        this.intime = intime;
    }

    public Integer getOuttime() {
        return outtime;
    }

    public void setOuttime(Integer outtime) {
        this.outtime = outtime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStoptime() {
        return stoptime;
    }

    public void setStoptime(Integer stoptime) {
        this.stoptime = stoptime;
    }

    public String getInid() {
        return inid;
    }

    public void setInid(String inid) {
        this.inid = inid;
    }

    public String getOutid() {
        return outid;
    }

    public void setOutid(String outid) {
        this.outid = outid;
    }

    public Date getIndata() {
        return indata;
    }

    public void setIndata(Date indata) {
        this.indata = indata;
    }

    public Date getOutdata() {
        return outdata;
    }

    public void setOutdata(Date outdata) {
        this.outdata = outdata;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
