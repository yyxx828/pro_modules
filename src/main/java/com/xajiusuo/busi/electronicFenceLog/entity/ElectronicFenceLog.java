package com.xajiusuo.busi.electronicFenceLog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.busi.device.entity.Device;
import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author GaoYong
 * @Date 19-6-12 上午11:27
 * @Description
 */
@Entity
@ApiModel(value ="electronicfencelog", description = "周界报警系统业务实体")
@Table(name="T_ELECTRONICFENCELOG_19")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ElectronicFenceLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(
            generator = "uuid"
    )
    @GenericGenerator(
            name = "uuid",
            strategy = "uuid"
    )
    private   String    id;// 唯一标识
    private   String   villageid;// '小区编号';
    private   String   villagename;// '小区姓名';
    private   String   loc;// '栅栏位置';
    private   String   type;// '报警类型';

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alert_date;// '报警时间';

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = true, targetEntity = Device.class)
    @JoinColumn(name ="deviceid",referencedColumnName = "apeId")
    private Device deviceid;// '设备编码';

    private   String   longitude;// '经度';
    private   String   latitude;// '纬度';

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private   Date      createdate;//创建时间
    private   String   display_flag;// '是否弹出';
    private   String   alertcount;// '报警次数';


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

    public String getVillagename() {
        return villagename;
    }

    public void setVillagename(String villagename) {
        this.villagename = villagename;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getAlert_date() {
        return alert_date;
    }

    public void setAlert_date(Date alert_date) {
        this.alert_date = alert_date;
    }

    public Device getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(Device deviceid) {
        this.deviceid = deviceid;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public String getDisplay_flag() {
        return display_flag;
    }

    public void setDisplay_flag(String display_flag) {
        this.display_flag = display_flag;
    }

    public String getAlertcount() {
        return alertcount;
    }

    public void setAlertcount(String alertcount) {
        this.alertcount = alertcount;
    }


}
