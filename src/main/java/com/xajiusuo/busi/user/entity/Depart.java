package com.xajiusuo.busi.user.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xajiusuo.jpa.config.BaseIntEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zlm 2018/1/18
 *  机构信息
 */
@Entity
@ApiModel(value = "depart", description = "机构信息")
@Table(name = "I_DEPART_11", catalog = "village")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler"})
public class Depart extends BaseIntEntity {
    @ApiModelProperty(value = "机构名称", required = true, dataType = "String", allowableValues = "range[2,100]", example = "默认的新机构")
    private String dname;
    @ApiModelProperty(value = "机构描述", allowableValues = "range[0,200]", dataType = "String", example = "默认的机构描述")
    private String ddesc;
    @ApiModelProperty(value = "机构层级", dataType = "Integer", example = "1",hidden = true)
    private Integer dlevel;
    @ApiModelProperty(value = "备注", allowableValues = "range[0,200]", dataType = "String", example = "默认的机构备注")
    private String remark;

    /**
     * 父级机构
     */
//    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "parentid")
    private Depart parent;


    public Depart(){
    }

    public Depart(Integer id) {
        this.setId(id);
    }

//    @NotNull
//    @Size(min = 2, max = 100)
    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    @Size(max = 100)
    public String getDdesc() {
        return ddesc;
    }

    public void setDdesc(String ddesc) {
        this.ddesc = ddesc;
    }

    public Integer getDlevel() {
        return dlevel;
    }

    public void setDlevel(Integer dlevel) {
        this.dlevel = dlevel;
    }

    @Size(max = 100)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @ApiModelProperty(value = "父级机构id", dataType = "Integer", required = true, example = "1024")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Integer getParentid() {
        return parent == null ? null : parent.getId();
    }

    @ApiModelProperty(value = "父级机构名称", dataType = "String", notes = "不要传进")
    public String getParentDname() {
        return parent == null ? "" : parent.getDepartNames();
    }

    public Depart getParent() {
        return parent;
    }

    public void setParent(Depart parent) {
        this.parent = parent;
    }

    public String getDepartNames(){
        if(dlevel == null || dlevel == 0){
            return dname;
        }
        Depart d = this;
        StringBuilder sb = new StringBuilder();
        List<String> name = new ArrayList<String>();
        for(int i = 0 ; i < dlevel ; i++ ){
            name.add(d.getDname());
            d = d.getParent();
        }
        for(int i = name.size()-1;i >= 0;i--){
            sb.append(">"+name.get(i));
        }
        return StringUtils.isNotBlank(sb.toString()) ? sb.deleteCharAt(0).toString() : "";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (!(obj instanceof Depart)){
            return false;
        }
        Depart depart = (Depart) obj;
        return depart.getId().equals(this.getId()) &&
                Objects.equals(dname, depart.getDname()) &&
                Objects.equals(getParentid(), depart.getParentid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), dname, parent);
    }
}
