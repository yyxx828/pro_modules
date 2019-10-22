package com.xajiusuo.busi.flow.dao;

import com.xajiusuo.busi.flow.entity.FlowDef;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by fanhua on 18-1-22.
 */
public interface FlowDefDao extends BaseDao<FlowDef,Integer>,JpaSpecificationExecutor<FlowDef> {
    /**
     * @desc 通过获取flevel获取流程列表
     * @author fanhua
     * @param flevel
     * @param pageable
     * @return Page<FlowDef>
     */
    Page<FlowDef> findByFlevel(int flevel, Pageable pageable);

    /**
     * @desc 通过获取flevel和是否启用获取流程列表
     * @author fanhua
     * @param flevel
     * @param initMode
     * @param pageable
     * @return Page<FlowDef>
     */
    Page<FlowDef> findByFlevelAndInitMode(int flevel, Boolean initMode, Pageable pageable);

    /**
     * @desc 所有流程列表（对内）
     * @author fanhua
     * @param pageable
     * @return Page<FlowDef>
     */
    Page<FlowDef> findAll(Pageable pageable);

    /**
     * @desc 获取Parent为所传流程并且flevel为使用的流程列表（带分页）
     * @author fanhua
     * @param flowDef
     * @param flevel
     * @param pageable
     * @return Page<FlowDef>
     */
    Page<FlowDef> findByParentAndFlevel(FlowDef flowDef, int flevel, Pageable pageable);
}
