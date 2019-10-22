package com.xajiusuo.busi.villageMessage.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@ApiModel(value = "houselog", description = "房屋租赁记录")
@Table(name ="T_HOUSELOG_19")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class HouseLog extends BaseEntity{


    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = true,targetEntity = Owner.class )
    @JoinColumn(name = "idnumber", referencedColumnName = "idnumber")
    private Owner     idnumber;//    '证件号码';
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = true, targetEntity = House.class)
    @JoinColumn(name = "houseno", referencedColumnName = "houseno")
    private House     houseno;//    '房屋编号';

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date starttime;//    '开始时间';

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date     endtime;//    '结束时间';


    public Owner getIdnumber() {
        return idnumber;
    }

    public void setIdnumber(Owner idnumber) {
        this.idnumber = idnumber;
    }

    public House getHouseno() {
        return houseno;
    }

    public void setHouseno(House houseno) {
        this.houseno = houseno;
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
