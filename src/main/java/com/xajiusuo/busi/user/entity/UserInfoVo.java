package com.xajiusuo.busi.user.entity;

import com.xajiusuo.jpa.param.entity.UserSession;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Mingle Wang
 * @Description:
 * @Date:Created in 13:39 2018/5/24
 * @Modified By:
 */
public class UserInfoVo implements UserSession{

    private Integer userId;         //用户id
    private String userName;        //用户名
    private String fullName;        //用户真实姓名
    private String cardId;          //证件id
    private Integer departmentId;   //部门id
    private String departmentName;  //部门名称
    private Integer roleId;         //角色id
    private String roleName;        //角色名称
    private String ocodes;          //用户权限 用户操作code集合，字符串，以逗号分隔
    private String gname;           //用户权限名称
    private List<String> ocodeList = new ArrayList<String>();

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getOcodes() {
        return ocodes;
    }

    public void setOcodes(String ocodes) {
        this.ocodes = ocodes;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public List<String> getOcodeList() {
        if(ocodes!=null && !"".equals(ocodes)){
            String[] ocodeArr = ocodes.split(",");
            for (String code:ocodeArr) {
                ocodeList.add(code);
            }
        }
        return ocodeList;
    }

    public void setOcodeList(List<String> ocodeList) {
        this.ocodeList = ocodeList;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("userId:"+userId+"\n");
        sb.append("userName:"+userName+"\n");
        sb.append("fullName:"+fullName+"\n");
        sb.append("cardId:"+cardId+"\n");
        sb.append("departmentId:"+departmentId+"\n");
        sb.append("departmentName:"+departmentName+"\n");
        sb.append("roleId:"+roleId+"\n");
        sb.append("roleName:"+roleName+"\n");
        sb.append("ocodes:"+ocodes+"\n");
        sb.append("gname:"+gname+"\n");
        return sb.toString();
    }

    @Override
    public String getFullname() {
        return fullName;
    }

    @Override
    public Integer getId() {
        return userId;
    }



}
