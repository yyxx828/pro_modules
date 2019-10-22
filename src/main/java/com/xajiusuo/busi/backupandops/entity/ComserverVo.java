package com.xajiusuo.busi.backupandops.entity;

import java.util.List;

/**
 * Created by wangdou on 2019/6/18
 */
public class ComserverVo {
    private List<ResultVo> dbObject;
    private List<ResultVo> webObject;

    public ComserverVo() {
    }

    public ComserverVo(List<ResultVo> dbObject, List<ResultVo> webObject) {
        this.dbObject = dbObject;
        this.webObject = webObject;
    }

    public List<ResultVo> getDbObject() {
        return dbObject;
    }

    public void setDbObject(List<ResultVo> dbObject) {
        this.dbObject = dbObject;
    }

    public List<ResultVo> getWebObject() {
        return webObject;
    }

    public void setWebObject(List<ResultVo> webObject) {
        this.webObject = webObject;
    }
}

