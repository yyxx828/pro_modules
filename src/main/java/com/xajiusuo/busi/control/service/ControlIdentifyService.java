package com.xajiusuo.busi.control.service;

import com.xajiusuo.busi.control.entity.ControlIdentify;
import com.xajiusuo.busi.control.entity.ControlIdentifyVo;
import com.xajiusuo.busi.control.entity.ControlTask;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by shirenjing on 2019/6/22.
 */
public interface ControlIdentifyService extends BaseService<ControlIdentify,String> {

    /**
     * @Author shirenjing
     * @Description 批量新增
     * @Date 15:30 2019/6/22
     * @Param [identifyList]
     * @return
     **/
    void batchSaveIdentify(List<ControlIdentify> identifyList);

    /**
     * @Author shirenjing
     * @Description 批量新增
     * @Date 15:30 2019/6/22
     * @Param [identifyList]
     * @return
     **/
    void batchSaveIdentify(ControlTask task, List<ControlIdentifyVo> identifies);

    /**
     * @Author shirenjing
     * @Description 根据任务id删除
     * @Date 15:32 2019/6/22
     * @Param [taskId]
     * @return
     **/
    void deleteByTaskId(String taskId);

    /**
     * @Author shirenjing
     * @Description 根据任务
     * @Date 15:32 2019/6/22
     * @Param [taskId]
     * @return
     **/
    void deleteByTask(ControlTask task);

    /**
     * @Author shirenjing
     * @Description 标识信息列表
     * @Date 17:15 2019/6/22
     * @Param [pageable, taskid]
     * @return
     **/
    Page<ControlIdentify> queryPageIdentify(Pageable pageable,String taskid);
}
