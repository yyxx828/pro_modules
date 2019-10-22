package com.xajiusuo.busi.flow.dao;

import com.xajiusuo.busi.flow.entity.FlowDef;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.busi.flow.entity.FlowNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by fanhua on 18-1-23.
 */
public interface FlowNodeDao extends BaseDao<FlowNode,Integer>,JpaSpecificationExecutor<FlowNode> {

    /**
     * @author fanhua
     * 通过flowDef获取流程节点带分页列表集合
     * @param flowDef
     * @return
     */
    Page<FlowNode> findByFlowDef(FlowDef flowDef, Pageable pageable);

    /***
     * 获取流程的第N个节点流程
     * @param flowDef 流程
     * @param orders 对应顺序
     * @return
     */
    FlowNode findByFlowDefAndOrders(FlowDef flowDef, int orders);
}
