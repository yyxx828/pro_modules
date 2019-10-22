package com.xajiusuo.busi.note.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xajiusuo.jpa.config.BaseEntity;
import com.xajiusuo.busi.mail.send.conf.TipsConf;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "S_NOTE_11", catalog = "village")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Note extends BaseEntity {

    @Transient
    public static final TipsConf conf = TipsConf.TIPS_NOTICE;

    private String noteTitle;

    private String author;

    private String content;

    private String type;

    private Integer experDays;

    private Boolean release;//是否发布

    private Boolean roof;//置顶

    @Transient
    private Boolean must = false;//强制提交

    public Note(String noteTitle, String author, Integer experDays, String type) {
        this.noteTitle = noteTitle;
        this.author = author;
        this.experDays = experDays;
        this.type = type;
    }

    public Note() {
    }

    public Note(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getExperDays() {
        return experDays;
    }

    public void setExperDays(Integer experDays) {
        this.experDays = experDays;
    }

    public Boolean getRelease() {
        return release;
    }

    public void setRelease(Boolean release) {
        this.release = release;
    }

    public Boolean getMust() {
        return must;
    }

    public void setMust(Boolean must) {
        this.must = must;
    }

    public Boolean getRoof() {
        return roof;
    }

    public void setRoof(Boolean roof) {
        this.roof = roof;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static TipsConf getConf() {
        return conf;
    }
}

