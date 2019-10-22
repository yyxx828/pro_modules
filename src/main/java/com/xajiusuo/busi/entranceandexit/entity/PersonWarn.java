package com.xajiusuo.busi.entranceandexit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 人员预警信息
 * Created by shirenjing on 2019/6/26.
 */
@ApiModel
@Entity
@Table(name = "T_PERSONWARNING_16", catalog = "village")
public class PersonWarn extends BaseEntity{

    @ApiModelProperty(required = false, value = "门禁卡编号", dataType = "string")
    private String entrancecardno;
    @ApiModelProperty(required = false, value = "人员证件编号", dataType = "string")
    private String pIdCard;
    @ApiModelProperty(required = false, value = "人员姓名", dataType = "string")
    private String pName;
    @ApiModelProperty(required = false, value = "中标次数", dataType = "string")
    private Integer nums;
    @ApiModelProperty(required = false, value = "中标详单id", dataType = "string")
    private String ids;
    @ApiModelProperty(required = false, value = "数据范围开始时间", dataType = "date")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dataRangeStart;
    @ApiModelProperty(required = false, value = "数据范围结束时间", dataType = "date")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dataRangeEnd;
    @ApiModelProperty(required = false, value = "开始小时", dataType = "integer")
    private Integer timeRangeStart;
    @ApiModelProperty(required = false, value = "结束小时", dataType = "integer")
    private Integer timeRangeEnd;
    @ApiModelProperty(required = false, value = "1:昼伏夜出 2：频繁出入", dataType = "integer")
    private String analyType;
    @ApiModelProperty(required = false, value = "0:出入口门禁 1：楼宇门禁", dataType = "integer")
    private Integer flag; //

    public PersonWarn() {
    }

    public PersonWarn(String entrancecardno, String pIdCard, String pName, Integer nums, String ids, Date dataRangeStart, Date dataRangeEnd, Integer timeRangeStart, Integer timeRangeEnd, String analyType, Integer flag) {
        this.entrancecardno = entrancecardno;
        this.pIdCard = pIdCard;
        this.pName = pName;
        this.nums = nums;
        this.ids = ids;
        this.dataRangeStart = dataRangeStart;
        this.dataRangeEnd = dataRangeEnd;
        this.timeRangeStart = timeRangeStart;
        this.timeRangeEnd = timeRangeEnd;
        this.analyType = analyType;
        this.flag = flag;
    }

    public String getEntrancecardno() {
        return entrancecardno;
    }

    public void setEntrancecardno(String entrancecardno) {
        this.entrancecardno = entrancecardno;
    }

    public String getpIdCard() {
        return pIdCard;
    }

    public void setpIdCard(String pIdCard) {
        this.pIdCard = pIdCard;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public Integer getNums() {
        return nums;
    }

    public void setNums(Integer nums) {
        this.nums = nums;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public Date getDataRangeStart() {
        return dataRangeStart;
    }

    public void setDataRangeStart(Date dataRangeStart) {
        this.dataRangeStart = dataRangeStart;
    }

    public Date getDataRangeEnd() {
        return dataRangeEnd;
    }

    public void setDataRangeEnd(Date dataRangeEnd) {
        this.dataRangeEnd = dataRangeEnd;
    }

    public Integer getTimeRangeStart() {
        return timeRangeStart;
    }

    public void setTimeRangeStart(Integer timeRangeStart) {
        this.timeRangeStart = timeRangeStart;
    }

    public Integer getTimeRangeEnd() {
        return timeRangeEnd;
    }

    public void setTimeRangeEnd(Integer timeRangeEnd) {
        this.timeRangeEnd = timeRangeEnd;
    }

    public String getAnalyType() {
        return analyType;
    }

    public String getAnalyTypes() {  //1:昼伏夜出 2：频繁出入
        if("1".equals(analyType)){
            return "昼伏夜出";
        }
        if("2".equals(analyType)){
            return "频繁出入";
        }
        return "";
    }

    public void setAnalyType(String analyType) {
        this.analyType = analyType;
    }

    public Integer getFlag() {
        return flag;
    }
    public String getFlags() {  //0:出入口门禁 1：楼宇门禁
        if(flag==0){
            return "出入口门禁";
        }
        if(flag==1){
            return "楼宇门禁";
        }
        return "";
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }
}
