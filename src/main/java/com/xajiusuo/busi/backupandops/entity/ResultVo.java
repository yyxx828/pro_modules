package com.xajiusuo.busi.backupandops.entity;

/**
 * Created by wangdou on 2019/6/24
 */
public class ResultVo {
    private String name;
    private String value;

    public ResultVo() {
    }

    public ResultVo(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
