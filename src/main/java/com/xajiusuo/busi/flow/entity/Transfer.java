package com.xajiusuo.busi.flow.entity;

/**
 * Created by yhl on 2018/1/26.
 *
 * 穿梭框封装
 * @author fanhua
 */
public class Transfer {
    private int key;//组件索引
    private String label;//索引名称
    private Boolean disabled;//禁用

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}
