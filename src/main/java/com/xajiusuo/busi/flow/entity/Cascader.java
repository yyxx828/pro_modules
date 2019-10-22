package com.xajiusuo.busi.flow.entity;

import java.util.Set;

/**
 * Created by fanhua on 2018/1/26.
 *
 * 级联选择封装
 * @author fanhua
 */
public class Cascader {
    private int value;//组件索引
    private String label;//组件内容
    private Set<Cascader> children;//子组件
    private Boolean loading;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Set<Cascader> getChildren() {
        return children;
    }

    public void setChildren(Set<Cascader> children) {
        this.children = children;
    }

    public Boolean getLoading() {
        return loading;
    }

    public void setLoading(Boolean loading) {
        this.loading = loading;
    }
}
