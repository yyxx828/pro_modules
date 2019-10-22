package com.xajiusuo.busi.buildingaccess.entity;

/**
 * Created by Administrator on 2019/6/20.
 */
public class ResultVo {
    private String number;
    private String name;
    private int count;

    public ResultVo() {
    }

    public ResultVo(String number, String name, int count) {
        this.number = number;
        this.name = name;
        this.count = count;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
