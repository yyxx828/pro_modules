package com.xajiusuo.busi.entranceandexit.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by wangdou on 2019/7/1
 */
public class AccessCardVo {
    @ApiModelProperty(required = false, value = "id", dataType = "String")
    private String id;
    @ApiModelProperty(required = false, value = "小区编号", dataType = "String")
    private String villageid;
    @ApiModelProperty(required = false, value = "门禁卡类型", dataType = "String")
    private String entrancecardtype;
    @ApiModelProperty(required = false, value = "门禁卡编号", dataType = "String")
    private String entrancecardno;
    @ApiModelProperty(required = false, value = "1有效0无效", dataType = "String")
    private String isvalid;
    @ApiModelProperty(required = false, value = "采集时间", dataType = "date")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date passtime;
    @ApiModelProperty(required = false, value = "授权范围0全部1出入口2楼宇", dataType = "String")
    private int authscope;
    @ApiModelProperty(required = false, value = "证件号码", dataType = "String")
    private String personidnumber;
    @ApiModelProperty(required = false, value = "业主姓名", dataType = "String")
    private String personname;

    public AccessCardVo() {
    }

    public AccessCardVo(String id, String villageid, String entrancecardtype, String entrancecardno, String isvalid, Date passtime, int authscope, String personidnumber, String personname) {
        this.id = id;
        this.villageid = villageid;
        this.entrancecardtype = entrancecardtype;
        this.entrancecardno = entrancecardno;
        this.isvalid = isvalid;
        this.passtime = passtime;
        this.authscope = authscope;
        this.personidnumber = personidnumber;
        this.personname = personname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVillageid() {
        return villageid;
    }

    public void setVillageid(String villageid) {
        this.villageid = villageid;
    }

    public String getEntrancecardtype() {
        return entrancecardtype;
    }

    public void setEntrancecardtype(String entrancecardtype) {
        this.entrancecardtype = entrancecardtype;
    }

    public String getEntrancecardno() {
        return entrancecardno;
    }

    public void setEntrancecardno(String entrancecardno) {
        this.entrancecardno = entrancecardno;
    }

    public String getIsvalid() {
        return isvalid;
    }

    public void setIsvalid(String isvalid) {
        this.isvalid = isvalid;
    }

    public Date getPasstime() {
        return passtime;
    }

    public void setPasstime(Date passtime) {
        this.passtime = passtime;
    }

    public int getAuthscope() {
        return authscope;
    }

    public void setAuthscope(int authscope) {
        this.authscope = authscope;
    }

    public String getPersonidnumber() {
        return personidnumber;
    }

    public void setPersonidnumber(String personidnumber) {
        this.personidnumber = personidnumber;
    }

    public String getPersonname() {
        return personname;
    }

    public void setPersonname(String personname) {
        this.personname = personname;
    }
}
