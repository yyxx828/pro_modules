package com.xajiusuo.busi.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xajiusuo.jpa.config.BaseIntEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.sql.Clob;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author zlm
 * @Date 2018/1/17
 * @Description 用户信息
 */
@Entity
@ApiModel(value = "userinfo", description = "用户信息")
@Table(name = "I_USERINFO_11", catalog = "village")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler"})
public class Userinfo extends BaseIntEntity {

    @ApiModelProperty(required = true,value="用户名",dataType = "String")
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(required = true,value="密码",dataType = "String")
    private String password;
    @ApiModelProperty(required = true,value="真实姓名",dataType = "String")
    private String fullname;
    @ApiModelProperty(required = true,value="性别",dataType = "Boolean")
    private Boolean sex;
    @ApiModelProperty(required = true,value="身份证号码",dataType = "String")
    private String cardId;
    @ApiModelProperty(required = false,value="key",dataType = "String")
    private String key;
    @ApiModelProperty(required = false,value="警号",dataType = "String")
    private String policeNum;
    @ApiModelProperty(required = true,value="用户级别",dataType = "Integer")
    private Integer ulevel;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ApiModelProperty(required = false,value="生日",dataType = "java.util.Date")
    private Date birthday;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(required = false,value="入职日期",dataType = "Date")
    private Date hiredate;
    @ApiModelProperty(required = false,value="备注",dataType = "String")
    private String remark;
    @ApiModelProperty(required = false,value="单位电话",dataType = "String")
    private String workPhone;
    @ApiModelProperty(required = false,value="移动电话",dataType = "String")
    private String mobilePhone;
    @ApiModelProperty(required = false,value="电子邮箱",dataType = "String")
    private String email;
    @ApiModelProperty(value = "注册审核标识", dataType = "Boolean",hidden = true)
    private Boolean regflag;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(columnDefinition = "CLOB", nullable = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Clob gzCode;//公章图片
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(columnDefinition = "CLOB", nullable = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Clob qmCode;//签名图片
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(value = "机构", dataType = "Depart", example = "false",  hidden = true)
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = true,targetEntity = Depart.class )
    @JoinColumn(name = "departid")
    private Depart depart;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(value = "职位", dataType = "Duty", example = "false",  hidden = true)
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = true)
    @JoinColumn(name = "dutyid")
    private Duty duty;
    @ApiModelProperty(value = "是否启用", dataType = "Boolean", example = "true", required = true, hidden = true)
    private Boolean able;

    //权限组
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "ogid")
    private OperGroup operGroup;
    @Transient
    public Set<String> ocodeList = new HashSet<String>();
    @Transient
    private String gzcodeStr;
    @Transient
    private String qmcodeStr;

    public Userinfo(){}

    public Userinfo(String username, String fullname) {
        this.username = username;
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getHiredate() {
        return hiredate;
    }

    public void setHiredate(Date hiredate) {
        this.hiredate = hiredate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPoliceNum() {
        return policeNum;
    }

    public void setPoliceNum(String policeNum) {
        this.policeNum = policeNum;
    }

    public Boolean getSex() {
        return sex;
    }

    public String getSexName(){
        return sex == null ? "" : (sex ? "男" : "女");
    }
    public String getAbleName(){
        return able != null && able  ? "启用" : "禁用" ;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public Integer getUlevel() {
        return ulevel;
    }

    public void setUlevel(Integer ulevel) {
        this.ulevel = ulevel;
    }

    public Duty getDuty() {
        return duty;
    }

    public void setDuty(Duty duty) {
        this.duty = duty;
    }

    public Boolean getAble() {
        return able;
    }

    public void setAble(Boolean able) {
        this.able = able;
    }

    public Integer getDepartId(){
        return depart == null ? null : depart.getId();
    }

    public String getDepartNames(){
        return depart == null ? "" : depart.getDepartNames();
    }

    public String getDepartName(){
        return depart == null ? "" : depart.getDname();
    }

    public Integer getDutyId(){
        return duty == null ? null : duty.getId();
    }
    public Integer getOperGroupId(){
        return operGroup == null ? null : operGroup.getId();
    }
    public String getOperGroupName(){
        return operGroup == null ? "" : operGroup.getGname();
    }

    public String getDutyName(){
        return duty == null ? "" : duty.getDutyname();
    }

    public Depart getDepart() {
        return depart;
    }

    public void setDepart(Depart depart) {
        this.depart = depart;
    }

    public OperGroup getOperGroup() {
        return operGroup;
    }

    public void setOperGroup(OperGroup operGroup) {
        this.operGroup = operGroup;
    }

    public Set<String> getOcodeList(){
        return  ocodeList;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getUlevelName(){
        String name = "";
        switch (ulevel){
            case 0:
                name = "开发级别";
                break;
            case 1:
                name = "一级";
                break;
            case 2:
                name = "二级";
                break;
            case 3:
                name = "三级";
                break;
            case 4:
                name = "四级";
                break;
            case 5:
                name = "五级";
                break;
    }

        return name;
    }

    public void setOcodeList(Set<String> ocodeList) {
        this.ocodeList = ocodeList;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Clob getGzCode() {
        return gzCode;
    }

    public void setGzCode(Clob gzCode) {
        this.gzCode = gzCode;
    }

    public Clob getQmCode() {
        return qmCode;
    }

    public void setQmCode(Clob qmCode) {
        this.qmCode = qmCode;
    }

    public String getGzcodeStr() throws Exception{
        return gzCode != null ? gzCode.getSubString(1,(int)gzCode.length()) : gzcodeStr;
    }

    public void setGzcodeStr(String gzcodeStr){
        this.gzcodeStr = gzcodeStr;
    }

    public String getQmcodeStr() throws Exception{
        return qmCode != null ? qmCode.getSubString(1,(int)qmCode.length()) : qmcodeStr;
    }

    public void setQmcodeStr(String qmcodeStr) {
        this.qmcodeStr = qmcodeStr;
    }

    public Boolean getRegflag() {
        return regflag;
    }

    public void setRegflag(Boolean regflag) {
        this.regflag = regflag;
    }


    public Userinfo u(){
        if(gzCode == null || qmCode == null)
            return this;
        Userinfo u = new Userinfo();
        BeanUtils.copyProperties(this,u);
        u.setGzCode(null);
        u.setQmCode(null);
        return u;
    }
}








