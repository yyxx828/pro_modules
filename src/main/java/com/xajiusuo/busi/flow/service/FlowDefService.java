package com.xajiusuo.busi.flow.service;

import com.xajiusuo.busi.flow.entity.FlowDef;
import com.xajiusuo.busi.flow.entity.FlowNode;
import com.xajiusuo.busi.flow.entity.FlowNodeVo;
import com.xajiusuo.jpa.config.BaseService;
import com.xajiusuo.jpa.param.e.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by fanhua on 18-1-22.
 */
public interface FlowDefService extends BaseService<FlowDef, Integer> {
    /**
     * @desc 获取Parent为所传流程并且flevel为使用的流程列表
     * @author fanhua
     * @param flowDef
     * @return List<FlowDef>
     */
    List<FlowDef> findByParentAndLevl(FlowDef flowDef);

    /**
     * @desc 获取Parent为所传流程并且flevel为使用的流程列表（带分页）
     * @author fanhua
     */
    Page<FlowDef> findByParentAndFlevel(FlowDef flowDef, int flevel, Pageable pageable);

    /**
     * @desc 通过获取flevel流程列表
     * @author fanhua
     * @param flevel
     * @param pageable
     * @return List<FlowDef>
     */
    Page<FlowDef> findByFlevel(int flevel, Pageable pageable);

    /**
     * @desc 通过获取flevel和是否启用获取流程列表
     * @author fanhua
     * @param flevel
     * @param initMode
     * @param pageable
     * @return List<FlowDef>
     */
    Page<FlowDef> findByFlevelAndInitMode(int flevel, Boolean initMode, Pageable pageable);

    /***
     * @desc 获取默认流程对象
     * @author 杨勇 18-1-29 下午2:06
     * @return
     */
    FlowDef getDefault();

    /***
     * 通过流程定义获取流程使用记录,如果存在则不再生成
     * @param def 流程对象定义
     * @param uid 操作用户
     * @return
     */
    FlowDef findFlowIns(FlowDef def,Integer uid);

    /**
     * @desc 所有流程列表（对内）
     * @author fanhua
     * @param pageable
     */
    Page<FlowDef> findAll(Pageable pageable);

    /**
     * @desc 通过flowName获取FlowDef集合
     * @author fanhua
     * @param flowName
     * @return List<FlowDef>
     */
    List<FlowDef> findByFname(String flowName);

    /**
     * @desc setRoles和UerS
     * @author fanhua
     * @param flowNode
     * @param flowNodeVo
     * @return List<FlowDef>
     */
    FlowNodeVo setRolesAndUsers(FlowNode flowNode, FlowNodeVo flowNodeVo);

    /**
     * @author fanhua
     * 查询流程使用流程
     * @param fname
     * @return ModelMap
     */
    Page<FlowDef> query(Pageable pageable, String fname);

    /***
     * 流程定义删除
     * @param id
     * @return
     */
    Result flowDefDelete(int id);
}
