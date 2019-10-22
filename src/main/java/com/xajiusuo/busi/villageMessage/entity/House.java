package com.xajiusuo.busi.villageMessage.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.*;

/**
 * @author GaoYong
 * @Date 19-6-5 下午1:59
 * @Description
 */
@Entity
@ApiModel(value ="house", description = "房屋基本信息")
@Table(name="T_HOUSE_19")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class House extends BaseEntity{

    

    private String   houseno;  //'房屋编号';

    private String   standardaddr;  //'标准地址';

    private String   standardaddrcode;  //'标准地址编码';

    private String   detailaddr;  //'附属详址';

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "housetype",referencedColumnName = "keys")
    private Diction   housetype;  //'户型';

    private String   housearea;  //'面积';

    private String   roomnum;  //'房屋间数';

    private String   housecertificateno;  //'房屋产权证号';

//    private String   ownertype;  //'ownertype';
//
//    private String   owneridtype;  //'产权人证件类别';
//
    private String   owneridnumber;  //'产权人证件号码1';
    private String   owneridnumber1;  //'产权人证件号码1';
//
//    private String   ownerid;  //'产权人id';
//
    private String   ownername;  //'产权人姓名'
    private String   ownername1;  //'产权人姓名1';
//
    private String   ownerphone;  //'产权人联系电话';
    private String   ownerphone1;  //'产权人联系电话1';

    private String   floor;  //'所属楼层';

    private String   unitid;  //'单元编号';

    private String   unitname;  //'单元名称';

    private String   buildingid;  //'楼栋编号';

    private String   buildingname;  //'楼栋名称';

    private String   villageid;  //'小区编号';

    private String   villagename;  //'小区名称';

    private String   people_number;  //'居住人数';

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "house_attrubute",referencedColumnName = "keys")
    private Diction   house_attrubute;  //''自住，出租，商用 1：自住，2：出租，9：其他';

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "rental",referencedColumnName = "keys")
    private Diction   rental;  //'租赁状态：[0=>''无'',1=>''待租'',2=>''已租'']';

    private String   position;  //'房屋位置';

    private String   room_name;  //'房间名称';

    public String getHouseno() {
        return houseno;
    }

    public void setHouseno(String houseno) {
        this.houseno = houseno;
    }

    public String getStandardaddr() {
        return standardaddr;
    }

    public void setStandardaddr(String standardaddr) {
        this.standardaddr = standardaddr;
    }

    public String getStandardaddrcode() {
        return standardaddrcode;
    }

    public void setStandardaddrcode(String standardaddrcode) {
        this.standardaddrcode = standardaddrcode;
    }

    public String getDetailaddr() {
        return detailaddr;
    }

    public void setDetailaddr(String detailaddr) {
        this.detailaddr = detailaddr;
    }

    public Diction getHousetype() {
        return housetype;
    }

    public void setHousetype(Diction housetype) {
        this.housetype = housetype;
    }

    public String getHousearea() {
        return housearea;
    }

    public void setHousearea(String housearea) {
        this.housearea = housearea;
    }

    public String getRoomnum() {
        return roomnum;
    }

    public void setRoomnum(String roomnum) {
        this.roomnum = roomnum;
    }

    public String getHousecertificateno() {
        return housecertificateno;
    }

    public void setHousecertificateno(String housecertificateno) {
        this.housecertificateno = housecertificateno;
    }

    public String getOwneridnumber() {
        return owneridnumber;
    }

    public void setOwneridnumber(String owneridnumber) {
        this.owneridnumber = owneridnumber;
    }

    public String getOwneridnumber1() {
        return owneridnumber1;
    }

    public void setOwneridnumber1(String owneridnumber1) {
        this.owneridnumber1 = owneridnumber1;
    }

    public String getOwnername() {
        return ownername;
    }

    public void setOwnername(String ownername) {
        this.ownername = ownername;
    }

    public String getOwnername1() {
        return ownername1;
    }

    public void setOwnername1(String ownername1) {
        this.ownername1 = ownername1;
    }

    public String getOwnerphone() {
        return ownerphone;
    }

    public void setOwnerphone(String ownerphone) {
        this.ownerphone = ownerphone;
    }

    public String getOwnerphone1() {
        return ownerphone1;
    }

    public void setOwnerphone1(String ownerphone1) {
        this.ownerphone1 = ownerphone1;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getUnitid() {
        return unitid;
    }

    public void setUnitid(String unitid) {
        this.unitid = unitid;
    }

    public String getUnitname() {
        return unitname;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

    public String getBuildingid() {
        return buildingid;
    }

    public void setBuildingid(String buildingid) {
        this.buildingid = buildingid;
    }

    public String getBuildingname() {
        return buildingname;
    }

    public void setBuildingname(String buildingname) {
        this.buildingname = buildingname;
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


    public String getPeople_number() {
        return people_number;
    }

    public void setPeople_number(String people_number) {
        this.people_number = people_number;
    }

    public Diction getHouse_attrubute() {
        return house_attrubute;
    }

    public void setHouse_attrubute(Diction house_attrubute) {
        this.house_attrubute = house_attrubute;
    }

    public Diction getRental() {
        return rental;
    }

    public void setRental(Diction rental) {
        this.rental = rental;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }
}
