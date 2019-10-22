package com.xajiusuo.busi.entranceandexit.entity;

/**
 * Created by Administrator on 2019/6/19.
 */
public class MjkAdnSkCount {
    private String mkcount;
    private String skcount;

    public MjkAdnSkCount() {
    }

    public MjkAdnSkCount(String mkcount, String skcount) {
        this.mkcount = mkcount;
        this.skcount = skcount;
    }

    public String getMkcount() {
        return mkcount;
    }

    public void setMkcount(String mkcount) {
        this.mkcount = mkcount;
    }

    public String getSkcount() {
        return skcount;
    }

    public void setSkcount(String skcount) {
        this.skcount = skcount;
    }
}
