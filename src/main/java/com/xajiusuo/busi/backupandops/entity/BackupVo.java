package com.xajiusuo.busi.backupandops.entity;

/**
 * Created by wangdou on 2019/6/17
 */
public class BackupVo {
    private String ip;
    private String pathName;
    private String fileSize;
    private String backupTime;

    public BackupVo() {
    }

    public BackupVo(String ip, String pathName, String fileSize, String backupTime) {
        this.ip = ip;
        this.pathName = pathName;
        this.fileSize = fileSize;
        this.backupTime = backupTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getBackupTime() {
        return backupTime;
    }

    public void setBackupTime(String backupTime) {
        this.backupTime = backupTime;
    }
}
