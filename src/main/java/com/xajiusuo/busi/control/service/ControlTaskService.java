package com.xajiusuo.busi.control.service;

import com.xajiusuo.busi.control.entity.ControlTask;
import com.xajiusuo.busi.control.entity.ControlTaskQueryVo;
import com.xajiusuo.busi.mail.send.conf.TipsConf;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by shirenjing on 2019/6/22.
 */
public interface ControlTaskService extends BaseService<ControlTask,String> {

    /**
     * @Author shirenjing
     * @Description 布控任务列表查询
     * @Date 14:55 2019/6/22
     * @Param [pageable, controlTaskVo]
     * @return
     **/
    Page<ControlTask> queryTaskPage(Pageable pageable, ControlTaskQueryVo controlTaskVo);

    /**
     * @Author shirenjing
     * @Description 
     * @Date 16:34 2019/6/22
     * @Param [controlTask]
     * @return
     **/
    ControlTask saveOrUpateTask(ControlTask controlTask);
    
    /**
     * @Author shirenjing
     * @Description 
     * @Date 18:03 2019/6/22
     * @Param [tasks]
     * @return
     **/
    void batchSave(List<ControlTask> tasks);

    /**
     * @Author shirenjing
     * @Description 查询当前时间在布控时间内的 和 已经过了布控时间的数据
     * @Date 17:47 2019/6/22
     * @Param [current, flag；control:布控 nocontrol:布控结束]
     * @return
     **/
    List<ControlTask> queryTaskList(String current,String flag);

    /**
     * @Author shirenjing
     * @Description 数据一来，查询布控任务中标数据（从中间库导入数据时调用）
     * @Date 18:15 2019/6/24
     * @Param []
     * @return
     **/
    void controlTaskSendMessge();

}
