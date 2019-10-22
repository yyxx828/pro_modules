package com.xajiusuo.busi.villageMessage.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.jpa.config.BaseEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.*;

/**
 * @author GaoYong
 * @Date 19-6-5 下午1:59
 * @Description     //    @JoinColumn(name ="deviceid",referencedColumnName = "apeId")
 */
@Entity
@ApiModel(value ="owner", description = "业主基本信息")
@Table(name="T_OWNER_19")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class Owner extends BaseEntity{

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "idtype",referencedColumnName = "keys")
    private Diction idtypeDiction;  // '证件类型';

    private String idnumber;  // '证件号码';

    private String name;  // '姓名';

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "sex",referencedColumnName = "keys")
    private Diction sexDiction;  // '性别';

    private String birthday;  // '出生日期';

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "nation",referencedColumnName = "keys")
    private Diction nationDiction;  // '民族';

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "politicsstatus",referencedColumnName = "keys")
    private Diction politicsstatus;  // '政治面貌';

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "educationstatus",referencedColumnName = "keys")
    private Diction educationstatus;  // '文化程度';

    private String phone;  // '联系电话';

    private String job;  // '从事职业';

    private String workplace;  // '服务处所';

    private String photo;  // '照片';

    private String idphoto;  // '证件照片';

    private String idaddr;  // '户籍地址';

    private String houseid;  // '房屋编号';

    private String houseaddr;// '房屋地址'

    private String villageid; // 小区编号(暂时不用了(多个小区技术负责统一加))

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "identify_type",referencedColumnName = "keys")
    private Diction identifyTypeDiction;  // '人员类型（业主、家人、租客、临时客人）1:业主，2：家属，3：租户，4：访客，9：其他';


    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH},optional = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "identify_country",referencedColumnName = "keys")
    private Diction identifyCountryDiction; //所属国家

    public Diction getIdtypeDiction() {
        return idtypeDiction;
    }

    public void setIdtypeDiction(Diction idtypeDiction) {
        this.idtypeDiction = idtypeDiction;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Diction getSexDiction() {
        return sexDiction;
    }

    public void setSexDiction(Diction sexDiction) {
        this.sexDiction = sexDiction;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Diction getNationDiction() {
        return nationDiction;
    }

    public void setNationDiction(Diction nationDiction) {
        this.nationDiction = nationDiction;
    }

    public Diction getPoliticsstatus() {
        return politicsstatus;
    }

    public void setPoliticsstatus(Diction politicsstatus) {
        this.politicsstatus = politicsstatus;
    }

    public Diction getEducationstatus() {
        return educationstatus;
    }

    public void setEducationstatus(Diction educationstatus) {
        this.educationstatus = educationstatus;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getIdphoto() {
        return idphoto;
    }

    public void setIdphoto(String idphoto) {
        this.idphoto = idphoto;
    }

    public String getIdaddr() {
        return idaddr;
    }

    public void setIdaddr(String idaddr) {
        this.idaddr = idaddr;
    }

    public String getVillageid() {
        return villageid;
    }

    public void setVillageid(String villageid) {
        this.villageid = villageid;
    }

    public Diction getIdentifyTypeDiction() {
        return identifyTypeDiction;
    }

    public void setIdentifyTypeDiction(Diction identifyTypeDiction) {
        this.identifyTypeDiction = identifyTypeDiction;
    }

    public Diction getIdentifyCountryDiction() {
        return identifyCountryDiction;
    }

    public void setIdentifyCountryDiction(Diction identifyCountryDiction) {
        this.identifyCountryDiction = identifyCountryDiction;
    }


        public String getHouseid() {
        return houseid;
    }

    public void setHouseid(String houseid) {
        this.houseid = houseid;
    }


    public String getHouseaddr() {
        return houseaddr;
    }

    public void setHouseaddr(String houseaddr) {
        this.houseaddr = houseaddr;
    }
}
