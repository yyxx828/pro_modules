package com.xajiusuo.busi.villageMessage.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author GaoYong
 * @Date 19-6-10 上午11:05
 * @Description
 */
@Entity
@ApiModel(value ="parking" ,description = "小区停车场基本信息")
@Table(name = "T_PARKING_19")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Parking extends BaseEntity {

    private String   parkingno;// '停车场编号';

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "parkingtype",referencedColumnName = "keys")
    private Diction parkingtype;// '停车位类型';

    private String   motorvehicleid;// '机动车id';

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "ownerid", referencedColumnName = "idnumber")
    private Owner   owner;// '车主id';

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date begintime;// '出售（租）时间';

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date   endtime;// '到期时间';

    private String   villageid;// '小区编号';

    public String getParkingno() {
        return parkingno;
    }

    public void setParkingno(String parkingno) {
        this.parkingno = parkingno;
    }

    public Diction getParkingtype() {
        return parkingtype;
    }

    public void setParkingtype(Diction parkingtype) {
        this.parkingtype = parkingtype;
    }

    public String getMotorvehicleid() {
        return motorvehicleid;
    }

    public void setMotorvehicleid(String motorvehicleid) {
        this.motorvehicleid = motorvehicleid;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Date getBegintime() {
        return begintime;
    }

    public void setBegintime(Date begintime) {
        this.begintime = begintime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public String getVillageid() {
        return villageid;
    }

    public void setVillageid(String villageid) {
        this.villageid = villageid;
    }
}
