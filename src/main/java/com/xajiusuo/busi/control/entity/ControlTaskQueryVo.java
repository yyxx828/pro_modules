package com.xajiusuo.busi.control.entity;

/**
 * Created by shirenjing on 2019/6/22.
 */
public class ControlTaskQueryVo {

    private String taskName; //查询任务名称
    private String identify;  //查询标识
    private String taskType;  //查询任务类型
    private String[] taskTimes;  //查询任务布控时间
    private String taskstatus; //任务状态

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String[] getTaskTimes() {
        return taskTimes;
    }

    public void setTaskTimes(String[] taskTimes) {
        this.taskTimes = taskTimes;
    }

    public String getTaskstatus() {
        return taskstatus;
    }

    public void setTaskstatus(String taskstatus) {
        this.taskstatus = taskstatus;
    }
}
