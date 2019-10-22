package com.xajiusuo.busi.flow.service.impl;

import com.xajiusuo.busi.flow.dao.FlowNodeDao;
import com.xajiusuo.busi.flow.entity.FlowDef;
import com.xajiusuo.busi.flow.entity.FlowIns;
import com.xajiusuo.busi.flow.entity.FlowNode;
import com.xajiusuo.busi.flow.service.FlowNodeService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.util.SqlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanhua on 18-1-22.
 */
@Service
public class FlowNodeServiceImpl extends BaseServiceImpl<FlowNode, Integer> implements FlowNodeService {

    @Autowired
    private FlowNodeDao flowNodeDao;

    @Override
    public BaseDao<FlowNode, Integer> getBaseDao() {
        return flowNodeDao;
    }

    @Override
    public List<FlowNode> getByFlowId(FlowDef flowDef) {
        List<Object> list = new ArrayList(0);
        list.add(flowDef);
        StringBuffer sb = new StringBuffer(MessageFormat.format("select * from {0} f where 1 = 1 and f.fdId = ? and (f.spType is not null or f.orders = 1) order by orders",tableName()));
        return flowNodeDao.executeNativeQuerySql(sb.toString(), list.toArray());
    }

    @Override
    public List<FlowNode> getByOrder(int order, FlowDef flowDef) {
        List<Object> list = new ArrayList(0);
        list.add(flowDef);
        list.add(order);
        StringBuffer sb = new StringBuffer(MessageFormat.format( "select * from {0}  f where 1 = 1 and f.orders >= ? and f.fdId = ? order by orders",tableName()));
        return flowNodeDao.executeNativeQuerySql(sb.toString(), list.toArray());
    }

    @Override
    public FlowNode findByFlowDefAndOrders(FlowDef flowDef, int orders) {
        return flowNodeDao.findByFlowDefAndOrders(flowDef, orders);
    }

    @Override
    public Page<FlowNode> findByFlowDef(FlowDef flowDef, Pageable pageable) {
        return flowNodeDao.findByFlowDef(flowDef, pageable);
    }

    @Override
    public FlowNode getNextNode(FlowIns ins) {
        if(ins.getFlowNode() == null){
            return null;
        }
        FlowDef def = ins.getFlowDef();//获取流程对象中使用的定义流程
        List<FlowNode> list = getByFlowId(def);
        for(FlowNode n : list){
            if(n.getId().equals(ins.getFlowNode().getId())){
                if(list.size() > list.indexOf(n) + 1){
                    return list.get(list.indexOf(n) + 1);
                }
            }

        }
        return null;
    }
}
