package com.xajiusuo.busi.motorVehicle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by liujiankang on 19-7-1.
 */
@ApiModel
@Entity
@Table(name = "T_MOTORVEHICLELNWARN_39", catalog = "village")
public class MotorVehicleWarn extends BaseEntity {

    @ApiModelProperty(required = false, value = "车牌号", dataType = "string")
    private String plateno;
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
    private Integer timeRangeEend;
    @ApiModelProperty(required = false, value = "0:长时间停放；1:昼伏夜出 2：频繁出入", dataType = "integer")
    private String analyType;

    public MotorVehicleWarn(){}

    public MotorVehicleWarn(String plateno, Integer nums, String ids, Date dataRangeStart, Date dataRangeEnd, Integer timeRangeStart, Integer timeRangeEend, String analyType){
        this.plateno = plateno;
        this.nums = nums;
        this.ids = ids;
        this.dataRangeStart = dataRangeStart;
        this.dataRangeEnd = dataRangeEnd;
        this.timeRangeStart = timeRangeStart;
        this.timeRangeEend = timeRangeEend;
        this.analyType = analyType;
    }
    public String getPlateno() {
        return plateno;
    }

    public void setPlateno(String plateno) {
        this.plateno = plateno;
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

    public Integer getTimeRangeEend() {
        return timeRangeEend;
    }

    public void setTimeRangeEend(Integer timeRangeEend) {
        this.timeRangeEend = timeRangeEend;
    }

    public String getAnalyType() {
        return analyType;
    }

    public void setAnalyType(String analyType) {
        this.analyType = analyType;
    }
}

