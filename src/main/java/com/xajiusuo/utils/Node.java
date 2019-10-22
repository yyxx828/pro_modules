package com.xajiusuo.utils;

import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.busi.user.entity.Userinfo;

import java.util.List;

/**
 * @author yhl
 * @Date 2018/03/05
 */
public class Node {
    private Integer id;
    private Integer parentid;
    private String title;
    private String parentTitle;
    private Boolean expand=false;
    private Boolean disabled=false;
    private Boolean disableCheckbox=false;
    private Boolean selected=false;
    private Boolean checked=false;
    private Boolean isDepart=true;
    private List<Node> children;

    public Integer getId() {
        return id;
    }

    public Node setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getParentid() {
        return parentid;
    }

    public Node setParentid(Integer parentid) {
        this.parentid = parentid;
        return this;
    }

    public String getParentTitle() {
        return parentTitle;
    }

    public Node setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
        return this;
    }

    public Integer getChildNum(){
        return CommonUtils.isNotEmpty(children)?children.size():0;
    }

    public String getTitle() {
        return title;
    }

    public Node setTitle(String title) {
        this.title = title;
        return this;
    }

    public Boolean getExpand() {
        return expand;
    }

    public Node setExpand(Boolean expand) {
        this.expand = expand;
        return this;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public Node setDisabled(Boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public Boolean getDisableCheckbox() {
        return disableCheckbox;
    }

    public Node setDisableCheckbox(Boolean disableCheckbox) {
        this.disableCheckbox = disableCheckbox;
        return this;
    }

    public Boolean getSelected() {
        return selected;
    }

    public Node setSelected(Boolean selected) {
        this.selected = selected;
        return this;
    }

    public Boolean getChecked() {
        return checked;
    }

    public Node setChecked(Boolean checked) {
        this.checked = checked;
        return this;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node setChildren(List<Node> children) {
        this.children = children;
        return this;
    }

    public Node userToNode(Userinfo userinfo, Integer parentId) {
        return new Node().setTitle(userinfo.getFullname()
                + "(" + userinfo.getPoliceNum()
                + ")").setId(userinfo.getId()).setParentid(parentId);
    }
    public Boolean getIsDepart() {
        return isDepart;
    }

    public Node setIsDepart(Boolean depart) {
        isDepart = depart;
        return this;
    }
}
