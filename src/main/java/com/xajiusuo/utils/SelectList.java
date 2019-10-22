package com.xajiusuo.utils;

/**
 * Created by Administrator on 2018/12/3.
 */
public class SelectList {
    private String value;
    private String label;
    private String orders;
    private String name;
    private String desc;


    public SelectList(){
    }

    public SelectList(String val){
        this(val, val, val);
    }

    public SelectList(String value, String label, String name){
        this.value = value;
        this.name = name;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
