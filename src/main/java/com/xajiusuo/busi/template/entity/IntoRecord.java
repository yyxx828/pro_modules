package com.xajiusuo.busi.template.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseIntEntity;
import com.xajiusuo.utils.MD5FileUtil;
import io.swagger.annotations.ApiModel;

import javax.persistence.*;


/**
 * Created by 杨勇 on 19-1-14.
 */
@Entity
@ApiModel(value = "IntoRecord", description = "导入记录")
@Table(name = "T_INTORECORD_11", catalog = "mailMana")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class IntoRecord extends BaseIntEntity {

    private String fileName;//文件名称

    private Long fileSize;//文件大小

    private String md5;//文件MD5

    private Integer status;//导入状态0,进行 1成功,2中止,3,重复.-1失败

    private Integer allNum;//总条数

    private Integer successNum;//成功条数

    private Integer errorNum;//错误条数

    private Integer startNum;//开始行树

    private Integer endNum;//截至行树

    private String createname;//创建人

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "bsid")
    private Business business;//业务表

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "itid")
    private IntoTemp intoTemp;//模板

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }


    public String getFileSizes() {
        if(fileSize == null){
            return "0B";
        }
        return MD5FileUtil.l2s(fileSize);
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusString() {
        if(status == null){
            return "无状态";
        }
        switch (status){
            case -1 :
                return "失败";
            case 0 :
                return "进行";
            case 1 :
                return "成功";
            case 2 :
                return "中止";
            case 3 :
                return "重复";
        }
        return "无状态";
    }

    public void setStatus(Integer status) {
        if(status == 0){
            this.allNum = 0;
            this.successNum = 0;
            this.errorNum = 0;
            this.startNum = 0;
            this.endNum = 0;
        }
        this.status = status;
    }

    public Integer getAllNum() {
        return allNum;
    }

    public void setAllNum(Integer allNum) {
        this.allNum = allNum;
    }

    public Integer getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(Integer successNum) {
        this.successNum = successNum;
    }

    public Integer getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(Integer errorNum) {
        this.errorNum = errorNum;
    }

    public Integer getStartNum() {
        return startNum;
    }

    public void setStartNum(Integer startNum) {
        this.startNum = startNum;
    }

    public Integer getEndNum() {
        return endNum;
    }

    public void setEndNum(Integer endNum) {
        this.endNum = endNum;
    }

    public String getCreatename() {
        return createname;
    }

    public void setCreatename(String createname) {
        this.createname = createname;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public IntoTemp getIntoTemp() {
        return intoTemp;
    }

    public void setIntoTemp(IntoTemp intoTemp) {
        this.intoTemp = intoTemp;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
