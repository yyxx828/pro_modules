package com.xajiusuo.busi.out.vo;

import com.xajiusuo.busi.user.entity.Userinfo;

/**
 * @Author zlm
 * @Date 2018/1/17
 * @Description 用户信息
 */
public class LoginUser {

    private Integer id;
    private String username;
    private String fullname;
    private String copyright;
    private Integer did;
    private String dname;
    private String dutyname;
    private Integer duid;
    private String ocodes;
    private String cardid;
    private Integer gid;
    private String gname;


    public LoginUser(){}

    public LoginUser(Userinfo userinfo, String copyright) {
        this.id=userinfo.getId();
        this.username=userinfo.getUsername();
        this.fullname=userinfo.getFullname();
        this.did=userinfo.getDepartId();
        this.dname=userinfo.getDepart().getDname();
        this.dutyname=userinfo.getDutyName();
        this.duid=userinfo.getDutyId();
        this.cardid=userinfo.getCardId();
        this.gid=userinfo.getOperGroupId();
        this.gname=userinfo.getOperGroupName();
        this.copyright=copyright;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public Integer getDid() {
        return did;
    }

    public void setDid(Integer did) {
        this.did = did;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getDutyname() {
        return dutyname;
    }

    public void setDutyname(String dutyname) {
        this.dutyname = dutyname;
    }

    public Integer getDuid() {
        return duid;
    }

    public void setDuid(Integer duid) {
        this.duid = duid;
    }

    public String getOcodes() {
        return ocodes;
    }

    public void setOcodes(String ocodes) {
        this.ocodes = ocodes;
    }

    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }


}








