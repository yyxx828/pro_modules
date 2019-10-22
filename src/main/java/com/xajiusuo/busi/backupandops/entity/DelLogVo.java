package com.xajiusuo.busi.backupandops.entity;

/**
 * Created by wangdou on 2019/6/17
 */
public class DelLogVo {
    private String ljmc;
    private String des;
    private String delTime;

    public DelLogVo() {
    }

    public DelLogVo(String ljmc, String des, String delTime) {
        this.ljmc = ljmc;
        this.des = des;
        this.delTime = delTime;
    }

    public String getLjmc() {
        return ljmc;
    }

    public void setLjmc(String ljmc) {
        this.ljmc = ljmc;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getDelTime() {
        return delTime;
    }

    public void setDelTime(String delTime) {
        this.delTime = delTime;
    }
}
