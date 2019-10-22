package com.xajiusuo.busi.wifilog.entity;

/**
 * Created by shirenjing on 2019/6/24.
 */
public class WifiQueryVo {
    private String[] passtimes;
    private String mac; //抓取Mac
    private String apeID; //设备编码
    private String Name; //名称
    private String IPAddr; //IP地址
    private String PlaceCode; //安装地点行政区划代码
    private String OrgCode;  //管辖单位代码

    public String[] getPasstimes() {
        return passtimes;
    }

    public void setPasstimes(String[] passtimes) {
        this.passtimes = passtimes;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getApeID() {
        return apeID;
    }

    public void setApeID(String apeID) {
        this.apeID = apeID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getIPAddr() {
        return IPAddr;
    }

    public void setIPAddr(String IPAddr) {
        this.IPAddr = IPAddr;
    }

    public String getPlaceCode() {
        return PlaceCode;
    }

    public void setPlaceCode(String placeCode) {
        PlaceCode = placeCode;
    }

    public String getOrgCode() {
        return OrgCode;
    }

    public void setOrgCode(String orgCode) {
        OrgCode = orgCode;
    }
}
