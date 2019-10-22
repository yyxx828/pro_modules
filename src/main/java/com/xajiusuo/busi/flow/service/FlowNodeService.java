package com.xajiusuo.busi.flow.service;

import com.xajiusuo.busi.flow.entity.FlowDef;
import com.xajiusuo.jpa.config.BaseService;
import com.xajiusuo.busi.flow.entity.FlowIns;
import com.xajiusuo.busi.flow.entity.FlowNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by fanhua on 18-1-22.
 */
public interface FlowNodeService extends BaseService<FlowNode, Integer> {
    /**
     * @author fanhua
     * 通过flowDef获取流程节点列表集合
     * @param flowDef
     * @return List<FlowNode>
     */
    List<FlowNode> getByFlowId(FlowDef flowDef);

    /**
     * @author fanhua
     * 获取FlowNode中order小于等于传入数字的列表集合
     * @param order
     * @param flowDef
     * @return
     */
    List<FlowNode> getByOrder(int order, FlowDef flowDef);

    /**
     * @author fanhua
     * 通过flowDef和order获取流程节点
     * @param orders
     * @param flowDef
     * @return
     */
    FlowNode findByFlowDefAndOrders(FlowDef flowDef, int orders);

    /**
     * @author fanhua
     * 通过flowDef获取流程节点带分页列表集合
     * @param flowDef
     * @return Page<FlowNode>
     */
    Page<FlowNode> findByFlowDef(FlowDef flowDef, Pageable pageable);

    /***
     * @desc 获取流程实体下一个节点
     * @author 杨勇 18-1-30 上午10:33
     * @param ins
     * @return
     */
    FlowNode getNextNode(FlowIns ins);
}
