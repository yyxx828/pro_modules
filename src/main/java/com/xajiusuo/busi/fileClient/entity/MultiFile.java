package com.xajiusuo.busi.fileClient.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.io.File;
import java.io.Serializable;

/**
 * Created by hadoop on 19-7-9.
 */
@Entity
@ApiModel(value = "MultiFile", description = "文件管理")
@Table(name = "T_MultiFile_10", catalog = "village")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","rules"})
public class MultiFile implements Serializable{

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid",strategy = "uuid")
    @ApiModelProperty(required = false,value = "主键",dataType = "string")
    private String id;

    @ApiModelProperty(required = false,value = "文件名称",dataType = "string")
    private String fileName;

    @ApiModelProperty(required = false,value = "存储路径",dataType = "string")
    private String filePath;

    @ApiModelProperty(required = false,value = "文件类型",dataType = "string")
    private String contentType;

    @ApiModelProperty(required = false,value = "文件周期分类",dataType = "string")
    private String type;

    @ApiModelProperty(required = false,value = "文件校验吗",dataType = "string")
    private String md5;

    @ApiModelProperty(required = false,value = "文件大小",dataType = "long")
    private long fileSize;

    @Transient
    private Integer statusCode;

    @Transient
    private String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getParentPath(){
        return new File(getFilePath()).getParentFile().getName();
    }

    public String getFileSaveName(){
        return new File(getFilePath()).getName();
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setData(MultiFile data){
        BeanUtils.copyProperties(data,this);
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
