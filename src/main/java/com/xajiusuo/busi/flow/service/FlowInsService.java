package com.xajiusuo.busi.flow.service;

import com.xajiusuo.busi.flow.entity.FlowDef;
import com.xajiusuo.busi.flow.entity.SpInfo;
import com.xajiusuo.jpa.config.BaseService;
import com.xajiusuo.busi.flow.entity.FlowIns;
import com.xajiusuo.busi.flow.entity.FlowNode;
import com.xajiusuo.busi.user.entity.Userinfo;
import com.xajiusuo.jpa.param.e.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.ModelMap;

import java.util.List;

/**
 * Created by fanhua on 18-1-22.
 */
public interface FlowInsService extends BaseService<FlowIns, Integer> {
    /***
     * @desc 流程保存,若保存,则不予处理
     * @author 杨勇 18-1-29 上午11:46
     * @param def 流程对象id
     * @param bsid 业务实体ID
     * @param uid 用户信息
     */
    FlowIns saveFlow(FlowDef def, String bsid, Integer uid, String sysId, String sysName);

    /***
     * @desc 获取流程节点对应的审批人
     * @author 杨勇 18-2-5 下午1:54
     * @param ins
     */
    public void setInsUsers(FlowIns ins);

    /**
     * @author fanhua
     * 通过flowDef获取流程实体带分页列表集合
     * @param flowDef
     * @return
     */
    Page<FlowIns> findByFlowDef(FlowDef flowDef, Pageable pageable);

    /**
     * @author fanhua
     * 获取流程实体带分页列表集合
     * @return
     */
    Page<FlowIns> findAll(Pageable pageable);

    /***
     * @desc 流程提交
     * @author 杨勇 18-1-30 下午4:30
     * @param ins
     * @param uid
     */
    int submitFlow(FlowIns ins, Integer uid,String uids);

    /***
     * @desc 检查是否有审批权限
     * @author 杨勇 18-1-31 下午5:18
     * @param ins
     * @param user
     * @return
     */
    Result checkAccess(FlowIns ins, Userinfo user);

    /***
     * @desc 流程驳回操作
     * @author 杨勇 18-2-2 上午11:04
     * @param ins
     * @param spInfo
     * @param uid
     * @return
     */
    FlowNode flowReject(FlowIns ins, String spInfo, Integer uid);

    /***
     * @desc 流程同意操作
     * @author 杨勇 18-2-2 上午11:04
     * @param ins
     * @param spInfo
     * @param uid
     * @param nextUids
     * @return
     */
    FlowNode flowAgree(FlowIns ins, String spInfo, Integer uid, String nextUids);

    /***
     * @desc 流程不同意操作
     * @author 杨勇 18-2-2 上午11:04
     * @param ins
     * @param spInfo
     * @param uid
     * @return
     */
    FlowNode flowRefuse(FlowIns ins, String spInfo, Integer uid);

    /***
     * @desc 提交者撤回
     * @author 杨勇 18-2-5 上午11:15
     * @param ins
     * @param user
     * @return
     */
    Result flowFillerRecall(FlowIns ins, Userinfo user);

    /**
     * @author 杨勇 2017-05-14
     * 流程直接通过
     * @param uid
     * @return ModelMap
     */
    FlowNode flowAdopt(FlowIns ins, String spInfo, Integer uid);

    /***
     * @desc 流程超时自动退回上一级,需要从业务触发
     * @author 杨勇 18-2-5 上午11:16
     *
     */
    Result flowAutoBack(FlowIns ins, boolean begin);

    /**
     * @author fanhua
     * 通过流程实体ID获取flowNodes和当前流程实体
     * @param id
     * @return ModelMap
     */
    ModelMap getFlowNodesForMap(Integer id);

    /**
     * @author fanhua
     * 通过流程实体ID获取审批历史
     * @param id
     * @return ModelMap
     */
    List<SpInfo> getSpHistoryList(Integer id);


    /**
     * @author fanhua
     * 查询流程实体列表
     * @param fname
     * @return ModelMap
     */
    Page<FlowIns> query(Pageable pageable, String fname);

    /**
     * @author 杨勇
     * 获取当前用户id审批流程
     * @param uid
     * @return ModelMap
     */
    List<FlowIns> getFlowInsByUser(Integer uid,String sysId);


    /**
     * @author 杨勇
     * 获取当前用户id审批流程id
     * @param uid
     * @return ModelMap
     */
    public List<Integer> getFlowInsIdByUser(Integer uid,String sysId);

    /**
     * @author 杨勇
     * 用户流程统计
     * @param uid
     * @return ModelMap
     */
    public List<Integer> getFlowInsIdByUser(Integer uid,String sysId,Integer type);

    /**
     * @author 杨勇
     * 用户流程数量统计
     * @param uid
     * @return ModelMap
     */
    public Integer[] getFlowInsIdCountByUser(Integer uid,String sysId);


}
