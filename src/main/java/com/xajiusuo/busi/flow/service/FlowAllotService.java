package com.xajiusuo.busi.flow.service;

import com.xajiusuo.jpa.config.BaseService;
import com.xajiusuo.busi.flow.conf.FlowBusiConf;
import com.xajiusuo.busi.flow.entity.FlowAllot;
import com.xajiusuo.jpa.param.e.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Created by 杨勇  on 2018/5/25.
 */
public interface FlowAllotService extends BaseService<FlowAllot, Integer> {

    void batchSaveOrUpdate(List<FlowAllot> logs);

    /***
     * 分页查询
     * @param pageable
     * @param entity
     * @return
     */
    Page<FlowAllot> findPageByEntity(Pageable pageable, FlowAllot entity);


    /***
     * 分配列表查询
     * @param page
     * @param entity
     * @return
     */
    Page<FlowAllot> queryFlowAllotPage(Pageable page, FlowAllot entity);

    /***
     * 分配重复过滤
     * @param entity
     * @return
     */
    Result findSameAllot(FlowAllot entity);

    /***
     * 通过id填充名称
     * @param entity
     */
    Result allotName(FlowAllot entity);

    /***
     * 通过业务和用户获取流程定义ID,和下一级审批信息
     * key:flowDefId,flowIns(包含下一级审批人信息和待审批返回格式一致)
     * @param conf 业务标识
     * @param uid 当前用户
     * @param condition 特殊条件,没有null
     * @return
     */
    Map<String,Object> flowInfoMatch(FlowBusiConf conf, Integer uid, String condition);
}
