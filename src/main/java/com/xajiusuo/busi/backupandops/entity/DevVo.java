package com.xajiusuo.busi.backupandops.entity;

/**
 * Created by Administrator on 2019/6/18.
 */
public class DevVo {
    private String ip;
    private String status;

    public DevVo() {
    }

    public DevVo(String ip, String status) {
        this.ip = ip;
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
