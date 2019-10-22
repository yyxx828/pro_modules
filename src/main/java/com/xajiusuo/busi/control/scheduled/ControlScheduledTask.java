package com.xajiusuo.busi.control.scheduled;

import com.xajiusuo.busi.control.entity.ControlTask;
import com.xajiusuo.busi.control.service.ControlTaskService;
import com.xajiusuo.jpa.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 布控定时任务
 * Created by shirenjing on 2019/6/22.
 */
@Component
public class ControlScheduledTask {

    @Autowired
    private ControlTaskService controlTaskService;

    /**
     * @Author shirenjing
     * @Description 定时更新开始布控和布控结束的任务
     * @Date 17:42 2019/6/22
     * @Param []
     * @return
     **/
//    @Scheduled(cron = "0 */2 * * * ?")
    public void updateTaskStatus() {

        String current = DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss");

        //布控中
        List<ControlTask> startTasks = controlTaskService.queryTaskList(current,"control");
        controlTaskService.batchSave(startTasks);
        //布控结束
        List<ControlTask> endTasks = controlTaskService.queryTaskList(current,"nocontrol");
        controlTaskService.batchSave(endTasks);

    }
}
