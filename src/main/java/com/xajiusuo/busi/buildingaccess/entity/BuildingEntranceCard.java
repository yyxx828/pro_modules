package com.xajiusuo.busi.buildingaccess.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 楼宇门禁基本信息表
 * Created by wangdou on 2019/6/6
 */
@ApiModel
@Entity
@Table(name = "T_BUILDINGENTRANCECARD_33", catalog = "village")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BuildingEntranceCard extends BaseEntity {

    @ApiModelProperty(required = false, value = "小区编号", dataType = "String")
    private String villageid;
    @ApiModelProperty(required = false, value = "门禁卡类型", dataType = "String")
    private String entrancecardtype;
    @ApiModelProperty(required = false, value = "门禁卡编号", dataType = "String")
    private String entrancecardno;
    @ApiModelProperty(required = false, value = "1有效0无效", dataType = "String")
    private String isvalid;
    @ApiModelProperty(required = false, value = "证件号码", dataType = "String")
    private String personidnumber;
    @ApiModelProperty(required = false, value = "采集时间", dataType = "date")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date passtime;

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

    public String getPersonidnumber() {
        return personidnumber;
    }

    public void setPersonidnumber(String personidnumber) {
        this.personidnumber = personidnumber;
    }
}
