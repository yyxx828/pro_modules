package com.xajiusuo.busi.flow.entity;

/**
 * Created by fanhua on 18-1-22.流程节点VO
 */
public class FlowNodeVo extends FlowNode{
    private String deptName;
    private String roleNames;
    private String userNames;

    public FlowNodeVo(FlowNode flowNode) {
        super(flowNode);
    }

    public FlowNodeVo() {
    }

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public String getUserNames() {
        return userNames;
    }

    public void setUserNames(String userNames) {
        this.userNames = userNames;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
