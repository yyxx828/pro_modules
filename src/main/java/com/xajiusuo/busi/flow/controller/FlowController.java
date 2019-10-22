package com.xajiusuo.busi.flow.controller;

import com.xajiusuo.busi.flow.entity.*;
import com.xajiusuo.busi.flow.service.FlowDefService;
import com.xajiusuo.busi.flow.service.FlowInsService;
import com.xajiusuo.busi.flow.service.FlowNodeService;
import com.xajiusuo.busi.user.entity.Depart;
import com.xajiusuo.busi.user.entity.Duty;
import com.xajiusuo.busi.user.entity.Userinfo;
import com.xajiusuo.busi.user.service.DepartService;
import com.xajiusuo.busi.user.service.DutyService;
import com.xajiusuo.busi.user.service.UserinfoService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.DateUtils;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.utils.CfI;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by hadoop on 18-1-19.
 */
@Slf4j
@Api(description = "流程管理")
@RestController
@RequestMapping(value = "/api/village/flow")
public class FlowController extends BaseController {
    @Autowired
    private FlowDefService flowDefService;

    @Autowired
    private FlowInsService flowInsService;

    @Autowired
    private FlowNodeService flowNodeService;

    @Autowired
    private UserinfoService userinfoService;

    @Autowired
    private DepartService departService;

    @Autowired
    private DutyService dutyService;

    /**
     * @desc 定义且启用流程列表
     * @author fanhua
     */
    @PostMapping(value = "/flowDefListDefined")
    @ApiOperation(value = "定义且启用流程列表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean flowDefListDefined(Pageable pageable) {
        try {
            Page<FlowDef> flowDefList = flowDefService.findByFlevelAndInitMode(0, true, pageable);
            return getResponseBean(Result.QUERY_SUCCESS.setData(flowDefList));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @desc 使用流程列表
     * @author fanhua
     */
    @GetMapping(value = "/flowDefListUse")
    @ApiOperation(value = "所有使用流程列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "fname", value = "流程名称", paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean flowDefListUse(Pageable pageable, String fname) {
        try {
            Page<FlowDef> flowDefList = flowDefService.query(pageable, fname);
            List<FlowDefVo> flowDefVos = new ArrayList<>();
            for (FlowDef flowDef : flowDefList) {
                FlowDefVo flowDefVo = new FlowDefVo(flowDef);
                if (!StringUtils.isEmpty(flowDef.getCreateUID())) {
                    Userinfo userinfo = userinfoService.getOne(flowDef.getCreateUID());
                    if(userinfo != null){
                        flowDefVo.setUserName(userinfo.getFullname());
                    }
                }
                flowDefVos.add(flowDefVo);
            }
            Page<FlowInsVo> flowDefVos1 = new PageImpl(flowDefVos, pageable, flowDefList.getTotalElements());
            return getResponseBean(Result.QUERY_SUCCESS.setData(flowDefVos1));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }

    /**
     * @desc 流程新增/修改
     * @author fanhua
     */
    @PostMapping(value = "/flowDefAddOrUpd")
    @ApiOperation(value = "流程新增/修改", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fname", required = true, value = "流程名称", paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "fdesc", required = true, value = "流程描述", paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "initMode", required = true, value = "是否启用(false禁用,true启用)", paramType = "form", dataType = "Boolean"),
            @ApiImplicitParam(name = "initUse", required = true, value = "是否默认(false否,true启用)", paramType = "form", dataType = "Boolean"),
            @ApiImplicitParam(name = "remark", value = "备注", paramType = "form", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean flowDefAddOrUpd(@RequestBody FlowDef flowDef) {
        try {
            List<FlowDef> flowDefs = flowDefService.findByFname(flowDef.getFname());
            if (flowDefs.size() > 0) {
                if (!StringUtils.isEmpty(flowDef.getId())) {
                    int count = 0;
                    for (FlowDef flowDef1 : flowDefs) {
                        if (!flowDef1.getId().equals(flowDef.getId()) && 0 == flowDef1.getFlevel()) {
                            count = count + 1;
                        }
                    }
                    if (count > 0) {
                        return Result.find(CfI.R_FLOW_FLOWNAMEEXIST_FAIL).toBean();
                    }
                } else {
                    return getResponseBean(Result.find(CfI.R_FLOW_FLOWNAMEEXIST_FAIL));
                }
            }
            if (false == flowDef.getInitUse()) {//若无默认流程设置默认流程
                List<FlowDef> flowDefList = flowDefService.findBy("initUse", true);
                if (0 == flowDefList.size()) {
                    flowDef.setInitUse(true);
                    flowDef.setInitMode(true);
                }
            } else {
                List<FlowDef> flowDefList = flowDefService.findBy("initUse", true);
                if (flowDefList.size() > 0) {
                    FlowDef oldDef = flowDefList.get(0);
                    oldDef.setInitUse(false);
                    flowDefService.save(oldDef);
                }
            }
            if (null == flowDef.getId()) {
                flowDef.setFlevel(0);//初始默认设置
                flowDef.setCreateTime(DateUtils.now());
                flowDef.setLastModifyTime(DateUtils.now());
                flowDefService.save(flowDef);
                FlowNode flowNode1 = new FlowNode();
                FlowNode flowNode2 = new FlowNode();
                flowNode1.setFlowDef(flowDef);
                flowNode1.setCreateTime(DateUtils.now());
                flowNode1.setLastModifyTime(DateUtils.now());
                flowNode1.setnName("保存");
                flowNode1.setNaName("保存");
                flowNode1.setNbName("待提交");
                flowNode1.setBackDot(true);
                flowNode1.setOrders(1);
                flowNode2.setFlowDef(flowDef);
                flowNode2.setCreateTime(DateUtils.now());
                flowNode2.setLastModifyTime(DateUtils.now());
                flowNode2.setnName("结束");
                flowNode2.setNaName("结束");
                flowNode2.setNbName("结束");
                flowNode2.setBackDot(false);
                flowNode2.setOrders(2);
                flowNodeService.save(flowNode1);
                flowNodeService.save(flowNode2);
            } else {
                flowDef.setLastModifyTime(DateUtils.now());
                flowDefService.save(flowDef);
            }
            return getResponseBean(Result.SAVE_SUCCESS.setData(flowDef));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.SAVE_FAIL);
        }
    }

    /**
     * @desc 流程删除
     * @author fanhua 修改 杨勇 19-5-27
     */
    @ApiOperation(value = "流程删除", httpMethod = "DELETE")
    @RequestMapping(value = "/flowDefDel/{id}", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "流程ID", paramType = "path", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean flowDefDelete(@PathVariable(value = "id") int id) {
        try {
            Result result = flowDefService.flowDefDelete(id);
            if(result != null){
                return getResponseBean(result);
            }
            return getResponseBean(Result.DELETE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.DELETE_FAIL);
        }
    }

    /**
     * @desc 流程查看
     * @author fanhua
     */
    @ApiOperation(value = "流程查看", httpMethod = "GET")
    @RequestMapping(value = "/flowDefView/{id}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "流程ID", paramType = "path", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean flowFefView(@PathVariable(value = "id") int id) {
        try {
            FlowDef flowDef = flowDefService.getOne(id);
            return getResponseBean(Result.QUERY_SUCCESS.setData(flowDef));
        } catch (Exception e) {
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @desc 流程节点列表
     * @author fanhua
     */
    @ApiOperation(value = "流程节点列表", httpMethod = "GET")
    @RequestMapping(value = "/flowNodeList/{flowId}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flowId", required = true, value = "流程ID", paramType = "path", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean flowNodeList(@PathVariable(value = "flowId") int flowId, Pageable pageable) {
        try {
            FlowDef flowDef = flowDefService.getOne(flowId);
            Page<FlowNode> flowNodes = flowNodeService.findByFlowDef(flowDef, pageable);
            List<FlowNodeVo> flowNodeList = new ArrayList<>();
            for (FlowNode flowNode : flowNodes) {
                FlowNodeVo flowNodeVo = new FlowNodeVo(flowNode);
                FlowNodeVo returnFlowNodeVo = flowDefService.setRolesAndUsers(flowNode, flowNodeVo);
                flowNodeList.add(returnFlowNodeVo);
            }
            return getResponseBean(Result.QUERY_SUCCESS.setData(flowNodeList));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @desc 流程实体列表
     * @author fanhua
     */
    @ApiOperation(value = "流程实体列表", httpMethod = "GET")
    @RequestMapping(value = "/flowInsList", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "fname", value = "流程名称", paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean flowInsList(Pageable pageable, String fname) {
        try {
            Page<FlowIns> flowInses = flowInsService.query(pageable, fname);
            List<FlowInsVo> flowInsVos = new ArrayList<>();
            for (FlowIns flowIns : flowInses) {
                FlowInsVo flowInsVo = new FlowInsVo();
                if (!StringUtils.isEmpty(flowIns.getCreateUID())) {
                    Userinfo userinfo = userinfoService.getOne(flowIns.getCreateUID());
                    if(userinfo != null)
                    flowInsVo.setUserName(userinfo.getFullname());
                }
                flowInsVo.setId(flowIns.getId());
                flowInsVo.setFname(flowIns.getFlowDef().getFname());
                flowInsVo.setFdesc(flowIns.getFlowDef().getFdesc());
                flowInsVo.setSysName(flowIns.getSysName());
                if (null == flowIns.getFlowNode()) {
                    flowInsVo.setnName("结束");
                } else {
                    flowInsVo.setnName(flowIns.getFlowNode().getnName());
                }
                if (!StringUtils.isEmpty(flowIns.getCreateTime())) {
                    flowInsVo.setCreateTime(flowIns.getCreateTime().toString().substring(0, 19));
                }
                if (!StringUtils.isEmpty(flowIns.getLastModifyTime())) {
                    flowInsVo.setLastModifyTime(flowIns.getLastModifyTime().toString().substring(0, 19));
                }
                flowInsVos.add(flowInsVo);
            }
            Page<FlowInsVo> flowInsVoPage = new PageImpl(flowInsVos, pageable, flowInses.getTotalElements());
            return getResponseBean(Result.QUERY_SUCCESS.setData(flowInsVoPage));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @desc 流程节点新增/修改
     * @author fanhua
     */
    @PostMapping(value = "/flowNodeAddOrUpd/{flowId}")
    @ApiOperation(value = "流程节点新增/修改", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nName", required = true, value = "节点名称", paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "nbName", required = true, value = "节点描述", paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "naName", required = true, value = "审批后名称", paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "spType", required = true, value = "0指定人 -1指定机构+职位 1本机构+职位 2上级机构+职位 3/4/5...依次往上机构+职位 99最高机构+职位", paramType = "form", dataType = "int"),
            @ApiImplicitParam(name = "depId", value = "本机构/指定机构", paramType = "form", dataType = "int"),
            @ApiImplicitParam(name = "roleIds", value = "指定职位", paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "userIds", value = "指定审批人", paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "userBack", required = true, value = "发起人是否可以撤回", paramType = "form", dataType = "Boolean"),
            @ApiImplicitParam(name = "spBack", required = true, value = "审批人是否可以撤回", paramType = "form", dataType = "Boolean"),
            @ApiImplicitParam(name = "backDot", required = true, value = "是否接受驳回", paramType = "form", dataType = "Boolean"),
            @ApiImplicitParam(name = "goEnd", required = true, value = "直接通过", paramType = "form", dataType = "Boolean"),
            @ApiImplicitParam(name = "orders", required = true, value = "流程顺序", paramType = "form", dataType = "int"),
            @ApiImplicitParam(name = "reject", required = true, value = "审批人驳回权限", paramType = "form", dataType = "Boolean"),
            @ApiImplicitParam(name = "flowId", required = true, value = "流程ID", paramType = "path", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean flowNodeAddOrUpd(@RequestBody FlowNode flowNode, @PathVariable(value = "flowId") int flowId) {
        try {
            if (null == flowNode.getId()) {
                if (0 == flowNode.getSpType()) {
                    if (StringUtils.isEmpty(flowNode.getUserIds())) {
                        return getResponseBean(Result.find(CfI.R_FLOW_USERNOTNULL_FAIL));
                    }
                } else if ("-1".equals(flowNode.getSpType())) {
                    if (StringUtils.isEmpty(flowNode.getRoleIds())) {
                        return getResponseBean(Result.find(CfI.R_FLOW_DUTYNOTNULL_FAIL));
                    }
                    if (StringUtils.isEmpty(flowNode.getDepId())) {
                        return getResponseBean(Result.find(CfI.R_FLOW_DEPTNOTNULL_FAIL));
                    }
                } else {
                    if (StringUtils.isEmpty(flowNode.getRoleIds())) {
                        return getResponseBean(Result.find(CfI.R_FLOW_DUTYNOTNULL_FAIL));
                    }
                }
                FlowDef flowDef = flowDefService.getOne(flowId);
                List<FlowNode> flowNodes = flowNodeService.getByFlowId(flowDef);
                //根据节点序号做对应处理
                if ((0 < flowNodes.size() && flowNodes.get(flowNodes.size() - 1).getOrders() + 1 < flowNode.getOrders()) || 1 >= flowNode.getOrders()) {
                    flowNode.setOrders(flowNodes.get(flowNodes.size() - 1).getOrders() + 1);
                } else {
                    List<FlowNode> flowNodeList = flowNodeService.getByOrder(flowNode.getOrders(), flowDef);
                    for (FlowNode oldNode : flowNodeList) {
                        oldNode.setOrders(oldNode.getOrders() + 1);
                        flowNodeService.update(oldNode);
                    }
                }
                flowNode.setFlowDef(flowDef);
                flowNode.setCreateTime(DateUtils.now());
                flowNode.setLastModifyTime(DateUtils.now());
                flowDef.setInsdefid(null);
                flowDefService.save(flowDef);
                flowNodeService.save(flowNode);
                return getResponseBean(Result.SAVE_SUCCESS);
            } else {
                FlowDef flowDef = flowNode.getFlowDef();
                flowDef.setInsdefid(null);
                flowDefService.save(flowDef);
                flowNode.setLastModifyTime(DateUtils.now());
                flowNodeService.save(flowNode);
                return getResponseBean(Result.SAVE_SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.SAVE_FAIL);
        }
    }

    /**
     * @desc 流程节点删除
     * @author fanhua
     */
    @ApiOperation(value = "流程节点删除", httpMethod = "DELETE")
    @RequestMapping(value = "/flowNodeDel/{id}", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "流程节点ID", paramType = "path", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean flowNodeDel(@PathVariable(value = "id") int id) {
        try {
            FlowNode flowNode = flowNodeService.getOne(id);
            List<FlowNode> flowNodes = flowNodeService.getByFlowId(flowNode.getFlowDef());
            if (!((0 < flowNodes.size() && flowNodes.get(flowNodes.size() - 1).getOrders() + 1 <= flowNode.getOrders()) || 1 >= flowNode.getOrders())) {
                List<FlowNode> flowNodeList = flowNodeService.getByOrder(flowNode.getOrders(), flowNode.getFlowDef());
                flowNodeList.remove(flowNode);
                for (FlowNode oldNode : flowNodeList) {
                    oldNode.setOrders(oldNode.getOrders() - 1);
                    flowNodeService.update(oldNode);
                }
            }
            flowNodeService.delete(id);
            return getResponseBean(Result.DELETE_SUCCESS);
        } catch (Exception e) {
            return getResponseBean(Result.DELETE_FAIL);
        }
    }

    /**
     * @desc 流程节点查看
     * @author fanhua
     */
    @ApiOperation(value = "流程节点查看", httpMethod = "GET")
    @RequestMapping(value = "/flowNodeView/{id}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "流程节点ID", paramType = "path", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean flowNodeView(@PathVariable(value = "id") int id) {
        try {
            ModelMap modelMap = new ModelMap();
            FlowNode flowNode = flowNodeService.getOne(id);
            List<FlowNode> flowNodes = flowNodeService.getByFlowId(flowNode.getFlowDef());
            FlowNodeVo flowNodeVo = new FlowNodeVo(flowNode);
            FlowNodeVo returnFlowNodeVo = flowDefService.setRolesAndUsers(flowNode, flowNodeVo);
            modelMap.put("data", returnFlowNodeVo);
            modelMap.put("order", flowNodes.get(flowNodes.size() - 1).getOrders() + 1);
            return getResponseBean(Result.QUERY_SUCCESS.setData(modelMap));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @desc 获取审批历史
     * @author fanhua
     */
    @ApiOperation(value = "审批历史", httpMethod = "GET")
    @RequestMapping(value = "/spHistoryList/{id}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "流程实体ID", paramType = "path", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean spHistoryList(@PathVariable(value = "id") int id) {
        try {
            List<SpInfo> spInfoList = flowInsService.getSpHistoryList(id);
            List<SpInfoVo> spInfoVos = new ArrayList<>();
            for (SpInfo spInfo : spInfoList) {
                SpInfoVo spInfoVo = new SpInfoVo(spInfo);
                if (!StringUtils.isEmpty(spInfo.getCreateUID())) {
                    Userinfo userinfo = userinfoService.getOne(spInfo.getCreateUID());
                    if(userinfo != null)
                    spInfoVo.setUserName(userinfo.getFullname());
                }
                spInfoVo.setSpTypeName(spInfo.getSpTypes());
                spInfoVos.add(spInfoVo);
            }
            return getResponseBean(Result.QUERY_SUCCESS.setData(spInfoVos));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @desc 获取流程进度
     * @author fanhua
     */
    @ApiOperation(value = "流程进度", httpMethod = "GET")
    @RequestMapping(value = "/flowSchedule/{id}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "流程实体ID", paramType = "path", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean flowSchedule(@PathVariable(value = "id") int id) {
        try {
            ModelMap modelMap = flowInsService.getFlowNodesForMap(id);
            return getResponseBean(Result.QUERY_SUCCESS.setData(modelMap));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @desc 获取所有定义流程列表（对内）
     * @author fanhua
     */
    @GetMapping(value = "/flowDefList")
    @ApiOperation(value = "获取所有定义流程列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean flowDefList(Pageable pageable) {
        try {
            Page<FlowDef> flowDefList = flowDefService.findByFlevel(0, pageable);
            return getResponseBean(Result.QUERY_SUCCESS.setData(flowDefList));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @desc 通过流程Id获取使用流程列表（对内）
     * @author fanhua
     */
    @PostMapping(value = "/flowDefListByParent/{id}")
    @ApiOperation(value = "通过流程Id获取使用流程列表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "id", required = true, value = "流程ID", paramType = "path", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean flowDefListByParent(@PathVariable(value = "id") int id, Pageable pageable) {
        try {
            FlowDef flowDef = flowDefService.getOne(id);
            Page<FlowDef> flowDefList = flowDefService.findByParentAndFlevel(flowDef, 1, pageable);
            return getResponseBean(Result.QUERY_SUCCESS.setData(flowDefList));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @desc 通过流程Id获取流程实体列表
     * @author fanhua
     */
    @ApiOperation(value = "通过流程Id获取流程实体列表", httpMethod = "GET")
    @RequestMapping(value = "/flowInsList/{flowId}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flowId", required = true, value = "流程ID", paramType = "path", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean flowInsList(@PathVariable(value = "flowId") int flowId, Pageable pageable) {
        try {
            FlowDef flowDef = flowDefService.getOne(flowId);
            Page<FlowIns> flowInses = flowInsService.findByFlowDef(flowDef, pageable);
            return getResponseBean(Result.QUERY_SUCCESS.setData(flowInses));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @Author fanhua
     */
    @ApiOperation(value = "用于流程中提供机构穿梭框数据", notes = "用于流程中提供机构穿梭框数据", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @RequestMapping(value = "/departAllByTransfer", method = RequestMethod.GET)
    public ResponseBean departAllByTransfer() {
        try {
            List<Depart> dlist = departService.findAllByDel(false);
            List<Transfer> transferList = new ArrayList<>();
            for (Depart depart : dlist) {
                Transfer transfer = new Transfer();
                transfer.setKey(depart.getId());
                transfer.setLabel(depart.getDname());
                transfer.setDisabled(false);
                transferList.add(transfer);
            }
            return getResponseBean(Result.QUERY_SUCCESS.setData(transferList));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @Author fanhua
     * @Date 2018/2/26 16:10
     */
    @ApiOperation(value = "用于流程中提供职位穿梭框数据", notes = "用于流程中提供职位穿梭框数据", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @RequestMapping(value = "/dutyAllByTransfer", method = RequestMethod.GET)
    public ResponseBean dutyAllByTransfer() {
        try {
            List<Duty> dutyList = dutyService.findDutysByDel(false);
            List<Transfer> transferList = new ArrayList<>();
            for (Duty duty : dutyList) {
                Transfer transfer = new Transfer();
                transfer.setKey(duty.getId());
                transfer.setLabel(duty.getDutyname());
                transfer.setDisabled(false);
                transferList.add(transfer);
            }
            return getResponseBean(Result.QUERY_SUCCESS.setData(transferList));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL.setData(e.getLocalizedMessage()));
        }
    }

    /**
     * @Author fanhua
     * @Description
     * @Date 2018/1/25 9:11
     */
    @ApiOperation(value = "用于流程中提供用户穿梭框数据", notes = "用于流程中提供用户穿梭框数据", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @RequestMapping(value = "/userAllByTransfer", method = RequestMethod.GET)
    public ResponseBean userAllByTransfer() {
        try {
            List<Userinfo> userList = userinfoService.findByDelAndAble(false, true);
            List<Transfer> transferList = new ArrayList<>();
            for (Userinfo userinfo : userList) {
                Transfer transfer = new Transfer();
                transfer.setKey(userinfo.getId());
                transfer.setLabel(userinfo.getFullname());
                transfer.setDisabled(false);
                transferList.add(transfer);
            }
            return getResponseBean(Result.QUERY_SUCCESS.setData(transferList));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL.setData(e.getLocalizedMessage()));
        }
    }

    /**
     * @Author fanhua
     */
    @ApiOperation(value = "用于流程中提供机构级联数据", notes = "用于流程中提供机构级联数据", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @RequestMapping(value = "/departAllByCascader", method = RequestMethod.GET)
    public ResponseBean departAllByCascader() {
        try {
            List<Depart> departs = departService.findByDlevelAndDel(1,false);
            List<Object> cascaders = new ArrayList<>();
            for (Depart levlDepart : departs) {
                Cascader cascader = new Cascader();
                cascader.setValue(levlDepart.getId());
                cascader.setLabel(levlDepart.getDname());
                List<Depart> children = departService.getDeptsByPId(levlDepart.getId());
                if (children.size() > 0) {
                    cascader.setChildren(new HashSet<>());
                    cascader.setLoading(false);
                }
                if (children.size() > 0) {
                    cascader.setChildren(new HashSet<>());
                    cascader.setLoading(false);
                }
                cascaders.add(cascader);
                children.clear();
            }
            return getResponseBean(Result.QUERY_SUCCESS.setData(cascaders));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @Author fanhua
     * @Date 2018/1/28 9:18
     */
    @ApiOperation(value = "查询子机构", notes = "查询子机构")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @RequestMapping(value = "/departByPid/{departId}", method = RequestMethod.GET)
    public ResponseBean departByPid(@PathVariable Integer departId) {
        try {
            List<Depart> dlist = departService.getDeptsByPId(departId);
            List<Object> cascaders = new ArrayList<>();
            for (Depart depart : dlist) {
                Cascader cascader = new Cascader();
                cascader.setValue(depart.getId());
                cascader.setLabel(depart.getDname());
                cascader.setTitle(depart.getDname());
                List<Depart> children = departService.getDeptsByPId(depart.getId());
                if (children.size() > 0) {
                    cascader.setChildren(new HashSet<>());
                    cascader.setLoading(false);
                }
                cascaders.add(cascader);
                children.clear();
            }
            return getResponseBean(Result.QUERY_SUCCESS.setData(cascaders));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @desc 修改流程节点顺序
     * @author fanhua
     */
    @PostMapping(value = "/updNodeOrders/{type}/{nodeId}")
    @ApiOperation(value = "修改流程节点顺序", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", required = true, value = "修改类型，可选值为up（orders上移）和down（orders下移）", paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "nodeId", required = true, value = "流程节点ID", paramType = "path", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean updNodeOrders(@PathVariable(value = "type") String type, @PathVariable(value = "nodeId") int nodeId) {
        try {
            FlowNode flowNode = flowNodeService.getOne(nodeId);
            FlowNode updFlowNode = new FlowNode();
            if ("up".equals(type)) {
                updFlowNode = flowNodeService.findByFlowDefAndOrders(flowNode.getFlowDef(), flowNode.getOrders() - 1);
                if (updFlowNode.getOrders() != 1) {
                    updFlowNode.setOrders(flowNode.getOrders());
                    flowNodeService.update(updFlowNode);
                    flowNode.setOrders(flowNode.getOrders() - 1);
                }
            } else {
                updFlowNode = flowNodeService.findByFlowDefAndOrders(flowNode.getFlowDef(), flowNode.getOrders() + 1);
                updFlowNode.setOrders(updFlowNode.getOrders() - 1);
                flowNodeService.update(updFlowNode);
                flowNode.setOrders(flowNode.getOrders() + 1);
            }
            flowNode.setLastModifyTime(DateUtils.now());
            flowNodeService.update(flowNode);
            FlowDef flowDef = flowNode.getFlowDef();
            flowDef.setInsdefid(null);
            flowDefService.save(flowDef);
            return getResponseBean(Result.SAVE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.SAVE_FAIL);
        }
    }

    /**
     * 本Controller的异常解析
     * @Author 杨勇
     * @Date 2018/3/5 11:19
     */
    @ExceptionHandler(Exception.class)
    public ResponseBean handleException(Exception e){
        log.error(e.getMessage(),e);
        String message;
        if (e instanceof NestedRuntimeException){
            message = ((NestedRuntimeException) e).getRootCause().getLocalizedMessage();
        }else{
            message = e.getLocalizedMessage();
        }
        return getResponseBean(Result.REQUEST_FAIL.setData(message));
    }

}
