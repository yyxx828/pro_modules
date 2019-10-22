package com.xajiusuo.busi.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseIntEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * @author zlm 2018/1/18
 *  职位信息
 */
@Entity
@ApiModel(value = "duty", description = "职位信息")
@Table(name = "I_DUTY_11", catalog = "village")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler"})
public class Duty extends BaseIntEntity {

    @ApiModelProperty(value = "职位名称", dataType = "String", allowableValues = "range[1,100]", example = "民警", required = true)
    private String dutyname;
    @ApiModelProperty(value = "职位描述", dataType = "String", example = "普通民警", allowableValues = "range[0,200]")
    private String ddesc;
    @ApiModelProperty(value = "职位级别", dataType = "Integer", example = "1", required = true)
    private Integer dlevel;
    @ApiModelProperty(value = "备注", dataType = "String", example = "备注", allowableValues = "range[0,100]")
    private String remark;

    public Duty(){
    }

    public Duty(Integer id){
        this.setId(id);
    }

    @Size(min = 2, max = 100)
    public String getDutyname() {
        return dutyname;
    }

    public void setDutyname(String dutyname) {
        this.dutyname = dutyname;
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


}
