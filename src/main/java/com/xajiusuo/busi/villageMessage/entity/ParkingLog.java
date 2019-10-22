package com.xajiusuo.busi.villageMessage.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleInfo;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author GaoYong
 * @Date 19-6-20 下午4:26
 * @Description
 */
@Entity
@ApiModel(value = "parkinglog", description = "车位租赁记录")
@Table(name ="T_PARKINGLOG_19")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ParkingLog extends BaseEntity{


//    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = true,targetEntity = Parking.class )
//    @JoinColumn(name = "parkingno", referencedColumnName = "parkingno")
    private String     parkingno;//    '车位编号';

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = true, targetEntity = MotorVehicleInfo.class)
    @JoinColumn(name = "plateno", referencedColumnName = "plateNo")
    private MotorVehicleInfo plateno;//    '车牌号';
//    private String     plateno;//    '车牌号';

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date starttime;//    '开始时间';

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date     endtime;//    '结束时间';


    public String getParkingno() {
        return parkingno;
    }

    public void setParkingno(String parkingno) {
        this.parkingno = parkingno;
    }


    public MotorVehicleInfo getPlateno() {
        return plateno;
    }

    public void setPlateno(MotorVehicleInfo plateno) {
        this.plateno = plateno;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }
}
