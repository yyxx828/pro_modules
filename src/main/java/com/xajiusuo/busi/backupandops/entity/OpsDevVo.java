package com.xajiusuo.busi.backupandops.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by wangdou on 2019/6/24
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OpsDevVo {
    private int id;
    private String name;
    private int count;
    private String onLineCount;
    private List<NameAndInt> childType;

    public static class NameAndInt {
        private String name;
        private int count;

        public NameAndInt() {
        }

        public NameAndInt(String name, int count) {
            this.name = name;
            this.count = count;
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

    public OpsDevVo() {
    }

    public OpsDevVo(int id, String name, int count, String onLineCount, List<NameAndInt> childType) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.onLineCount = onLineCount;
        this.childType = childType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getOnLineCount() {
        return onLineCount;
    }

    public void setOnLineCount(String onLineCount) {
        this.onLineCount = onLineCount;
    }

    public List<NameAndInt> getChildType() {
        return childType;
    }

    public void setChildType(List<NameAndInt> childType) {
        this.childType = childType;
    }
}
