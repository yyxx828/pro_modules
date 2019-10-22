package com.xajiusuo.busi.flow.entity;

/**
 * Created by fanhua on 18-1-22.流程节点VO
 */
public class FlowDefVo extends FlowDef{
    private String userName;

    public FlowDefVo(FlowDef flowDef) {
        super(flowDef);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
