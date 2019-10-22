package com.xajiusuo.busi.flow.dao;

import com.xajiusuo.busi.flow.entity.FlowDef;
import com.xajiusuo.busi.flow.entity.FlowIns;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by fanhua on 18-1-23.
 */
public interface FlowInsDao extends BaseDao<FlowIns,Integer>,JpaSpecificationExecutor<FlowIns> {
    /**
     * @author fanhua
     * 通过flowDef获取流程节点带分页列表集合
     * @param flowDef
     * @return
     */
    Page<FlowIns> findByFlowDef(FlowDef flowDef, Pageable pageable);
}
