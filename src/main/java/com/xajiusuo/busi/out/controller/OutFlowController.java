package com.xajiusuo.busi.out.controller;

import com.xajiusuo.busi.flow.conf.FlowBusiConf;
import com.xajiusuo.busi.flow.entity.FlowDef;
import com.xajiusuo.busi.flow.entity.FlowIns;
import com.xajiusuo.busi.flow.entity.FlowNode;
import com.xajiusuo.busi.flow.entity.SpInfo;
import com.xajiusuo.busi.flow.service.FlowAllotService;
import com.xajiusuo.busi.flow.service.FlowDefService;
import com.xajiusuo.busi.flow.service.FlowInsService;
import com.xajiusuo.busi.flow.service.FlowNodeService;
import com.xajiusuo.busi.user.entity.Userinfo;
import com.xajiusuo.busi.user.service.UserinfoService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.CfI;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author 杨勇
 * @Date 2019/1/17
 */
@Slf4j
@Api(description = "流程调用接口")
@RestController
@RequestMapping(value="/api/out/flow")
public class OutFlowController extends BaseController{

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private UserinfoService userinfoService;

    @Autowired
    private FlowDefService flowDefRepository;

    @Autowired
    private FlowInsService flowInsRepository;

    @Autowired
    private FlowNodeService flowNodeRepository;

    @Autowired
    private FlowAllotService flowAllotService;

    /***
     * @desc 流程对象列表
     * @author 杨勇 18-1-26 下午3:48
     * @return
     */
    @ApiOperation(value = "流程定义列表[FLOW - 1]", httpMethod = "POST")
    @RequestMapping(value = "/flowDefList", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean flowDefList() {
        return Result.QUERY_SUCCESS.setData(flowDefRepository.findBy("flevel",0)).toBean();

    }

    /***
     * @desc 流程对象节点
     * @author 杨勇 18-1-26 下午3:48
     * @param fdid
     * @return
     */
    @ApiOperation(value = "流程对象节点[FLOW - 2]", httpMethod = "POST")
    @RequestMapping(value = "/flowDefNodeList", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fdid", required = true,value = "流程对象id", paramType = "query", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean flowDefNodeList(Integer fdid) {
        List<FlowNode> list = flowNodeRepository.executeNativeQuerySql(MessageFormat.format("select * from {0} e where fdId = ? order by orders", SqlUtils.tableName(FlowNode.class)), fdid);
        return Result.QUERY_SUCCESS.setData(list).toBean();
    }

    /***
     * @desc 流程分配回调
     * @author 杨勇 18-1-26 下午3:49
     * @return
     */
    @ApiOperation(value = "流程分配回调[FLOW - 3]", httpMethod = "POST")
    @RequestMapping(value = "/flowDefTask", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fdid", value = "流程对象id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "fiid", value = "流程实体id", paramType = "query", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean flowDefTask(Integer fdid, Integer fiid) {
        return Result.SUBMIT_SUCCESS.toBean();
    }

    /***
     * @desc 审批信息获取
     * @author 杨勇 18-1-26 下午3:49
     * @param fdid 流程对象ID
     * @param fiid 流程实体ID
     * @return
     */
    @ApiOperation(value = "审批信息获取[FLOW - 4]", httpMethod = "POST")
    @RequestMapping(value = "/flowBusiStatusPrepared", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fdid", value = "流程对象id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "fiid", value = "流程实体id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "uid", required = true,value = "发起人", paramType = "query", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean flowBusiStatusPrepared(Integer fdid, Integer fiid,Integer uid) {
        if (uid == null) {
            return Result.find(CfI.R_FLOW_USERNOTNULL_FAIL).toBean();
        }

        FlowDef def = null;
        FlowNode nextNode = null;
        FlowNode flowNode = null;
        FlowIns ins = new FlowIns();
        FlowIns temp = new FlowIns();
        if (fiid != null) {//获取流程对应的流程定义
            ins = flowInsRepository.getOne(fiid);
            if (ins != null) {
                if(ins.saveStatus()){
                    flowNode = flowNodeRepository.findByFlowDefAndOrders(ins.getFlowDef(),1);
                    ins.setFlowNode(flowNode);
                    flowInsRepository.update(ins);
                }else{
                    flowNode = ins.getFlowNode();
                }
                //获取流程下一级审批信息
                nextNode = flowNodeRepository.getNextNode(ins);
            } else {
                return Result.find(CfI.R_FLOW_NOTFOUND_FAIL).toBean();
            }
        } else if (fdid != null) {
            def = flowDefRepository.getOne(fdid);
            if (def == null) {//流程定义不存在
                return Result.find(CfI.R_FLOW_NOTFOUND_FAIL).toBean();
            }
            if (!def.getInitMode()) {//流程禁用
                return Result.find(CfI.R_FLOW_NOTUSE_FAIL).toBean();
            }
            def = flowDefRepository.findFlowIns(def, uid);//获取使用流程
            List<FlowNode> list = flowNodeRepository.getByFlowId(def);
            flowNode = list.get(0);
            if (list.size() > 1) {
                nextNode = list.get(1);
            }
            list.clear();
        } else {
            def = flowDefRepository.getDefault();
            if (def != null) {
                List<FlowNode> nList = flowNodeRepository.getByFlowId(def);
                flowNode = nList.get(0);
                if (nList.size() > 1) {
                    nextNode = nList.get(1);
                }
                nList.clear();
            } else {
                return Result.find(CfI.R_FLOW_NOTDEFAULT_FAIL).toBean();
            }
        }
        BeanUtils.copyProperties(ins, temp);
        temp.setFlowNode(nextNode);
        if (temp.getFlowDef() == null) {
            temp.setFlowDef(def);
        }
        temp.setCreateUID(uid);
        flowInsRepository.setInsUsers(temp);//审批信息获取
        temp.setFlowNode(flowNode);
        temp.setNextNode(nextNode);
        temp.statusing();
        return Result.QUERY_SUCCESS.setData(temp).toBean();
    }

    /***
     * @desc 流程保存或提交操作
     * @author 杨勇 18-2-5 下午4:40
     * @param fiid
     * @param fdid
     * @param bsid
     * @param uid
     * @param uids
     * @param type
     * @return
     */
    @ApiOperation(value = "流程保存提交[FLOW - 5]", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fiid", value = "流程实体id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "fdid", value = "流程对象id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "bsid", value = "业务实体id", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "uid", value = "提交用户id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "uids", value = "审批人ids", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "type", value = "操作类型0保存,1提交",required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sysId", value = "应用系统ID", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sysName", value = "应用系统NAME", paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/flowBusiSaveOrSubmit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean flowBusiSaveOrSubmit(Integer fiid,Integer fdid, String bsid,Integer uid,String uids,Integer type,String sysId,String sysName){
        FlowIns ins = null;
        int res = 0;
        if (fiid == null) {//流程实体ID
            FlowDef def = null;
            if (fdid == null) {//获取默认定义流程
                def = flowDefRepository.getDefault();
            } else {//获取指定流程
                def = flowDefRepository.getOne(fdid);
                if (def == null) {//流程定义不存在
                    return Result.find(CfI.R_FLOW_NOTFOUND_FAIL).toBean();
                }
                if (!def.getInitMode()) {//流程禁用
                    return Result.find(CfI.R_FLOW_NOTUSE_FAIL).toBean();
                }
            }
            FlowDef defIns = flowDefRepository.findFlowIns(def, uid);//获取流程使用对象
            ins = flowInsRepository.saveFlow(defIns, bsid, uid,sysId,sysName);//流程保存,若存在则不予处理
            if (type != null && type == 1) {//提交
                res = flowInsRepository.submitFlow(ins, uid,uids);
            }
        } else {
            ins = flowInsRepository.getOne(fiid);
            if(ins != null){
                if (type != null && type == 1) {//提交
                    res = flowInsRepository.submitFlow(ins, uid,uids);
                }
            }
        }
        if(res != -1){//提交成功
            if(res == 1){
                return Result.SUBMIT_SUCCESS.setData(ins).toBean();
            }
            return Result.SAVE_SUCCESS.setData(ins).toBean();
        }else{//提交失败,已经提交
            return Result.SUBMIT_FAIL.toBean();
        }
    }

    /***
     * @desc 流程审批职位进行占位流程,避免并发产生异常
     * @author 杨勇 18-2-5 下午4:42
     * @param fiid
     * @param uid
     * @return
     */
    @ApiOperation(value = "流程审批占位[FLOW - 6]", httpMethod = "POST")
    @RequestMapping(value = "/flowBusiApprovaling", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fiid", value = "流程实体id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "uid", value = "审批用户id", paramType = "query", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean flowBusiApprovaling(Integer fiid, Integer uid){
        FlowIns ins = flowInsRepository.getOne(fiid);
        Userinfo user = userinfoService.getOne(uid);

        Result result = flowInsRepository.checkAccess(ins,user);
        if(result != null){
            return result.toBean();
        }
        ins.setHostId(uid);//流程占位
        ins.setStatus(3);
        ins.statusing();

        flowInsRepository.update(ins);
        return Result.find(CfI.R_FLOW_FLOWAPPROLING_SUCCESS).setData(ins).toBean();
    }

    /***
     * @desc 流程审批操作
     * @author 杨勇 18-2-5 下午4:42
     * @param fiid
     * @param uid
     * @param type
     * @param spInfo
     * @param nextUids
     * @return
     */
    @ApiOperation(value = "流程审批操作[FLOW - 7]", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fiid", value = "流程实体id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "uid", value = "审批用户id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "type", value = "操作类型1驳回,2同意,-1不同意,9直接通过", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "spInfo", value = "审批信息", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "nextUids", value = "下一级审批人", paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/flowBusiApproval", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean flowBusiApproval(Integer fiid, Integer uid,Integer type,String spInfo,String nextUids){
        if(type == null || type < -1 || type > 2){//不再操作类型内
            return Result.REQUEST_FAIL.toBean();
        }
        //权限判断
        FlowIns ins = flowInsRepository.getOne(fiid);
        Userinfo user = userinfoService.getOne(uid);

        Result result = flowInsRepository.checkAccess(ins,user);
        if(result != null){
            return result.toBean();
        }
        FlowNode node = null;
        if(type == 1){//驳回
            node = flowInsRepository.flowReject(ins,spInfo,uid);
//            if(ins.getFlowNode().getReject() != null && ins.getFlowNode().getReject()){
//                node = flowInsRepository.flowReject(ins,spInfo,uid);
//            }else{
//                return getResponseBean(Message.CODE_FAIL,"message.flow.noRejectPower");
//            }
        }else if(type == 2){//同意
            node = flowInsRepository.flowAgree(ins,spInfo,uid,nextUids);
        }else if(type == -1){//不同意
            node = flowInsRepository.flowRefuse(ins,spInfo,uid);
        }else if(type == 9){//直接通过
            if(ins.getFlowNode().getGoEnd() != null && ins.getFlowNode().getGoEnd()){
                node = flowInsRepository.flowAdopt(ins,spInfo,uid);
            }else{
                return Result.find(CfI.R_FLOW_NOADOPTPOWER_FAIL).toBean();
            }
        }
        ins.setCurSpUids();
        flowInsRepository.update(ins);
        return Result.find(CfI.R_FLOW_FLOWAPPROL_SUCCESS).setData(ins).toBean();
    }

    /***
     * @desc 流程手动撤回,包含审批人撤回,提交人撤回
     * @author 杨勇 18-2-5 下午4:56
     * @param fiid
     * @param uid
     * @return
     */
    @ApiOperation(value = "流程手动撤回[FLOW - 8]", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fiid", value = "流程实体id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "uid", value = "提交用户id", paramType = "query", dataType = "int"),
    })
    @RequestMapping(value = "/flowBusiRecall", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean flowBusiRecall(Integer fiid, Integer uid){
        Integer type = 0;//提交人撤回
        if(type != 1 && type != 0){
            return Result.find(CfI.R_FLOW_TYPEERR_FAIL).toBean();
        }
        Userinfo user = userinfoService.getOne(uid);
        if(uid == null || user == null){//用户id为空
            return Result.find(CfI.R_USER_NOTEXIST_FAIL).toBean();
        }
        FlowIns ins = flowInsRepository.getOne(fiid);
        if(ins == null){
            return Result.find(CfI.R_FLOW_NOTFOUND_FAIL).toBean();
        }
        Result msg = flowInsRepository.flowFillerRecall(ins,user);
        if(msg == null){
            return Result.OPERATE_SUCCESS.toBean();
        }else{
            return msg.toBean();
        }
    }

    /**
     * @desc 获取审批历史
     * @author fanhua
     */
    @ApiOperation(value = "流程审批记录[FLOW - 9]", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "流程实体ID", paramType = "path", dataType = "int"),
    })
    @RequestMapping(value = "/spHistoryList/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean spHistoryList(@PathVariable(value = "id") int id) {
        try {
            List<SpInfo> spInfoList = flowInsRepository.getSpHistoryList(id);
            Map<Integer,String> map = new HashMap<Integer,String>();
            for(SpInfo info:spInfoList){
                info.setOrder(spInfoList.indexOf(info) + 1);
                String name = map.get(info.getCreateUID());
                if(name == null){
                    try{
                        name = userinfoService.getOne(info.getCreateUID()).getFullname();
                        map.put(info.getCreateUID(),name);
                    }catch (Exception e){}
                }
                info.setSpUserName(name);
            }
            return getResponseBean(Result.QUERY_SUCCESS.setData(spInfoList));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @desc 获取流程进度
     * @author fanhua
     */
    @ApiOperation(value = "流程审批进度[FLOW -10]", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "流程实体ID", paramType = "path", dataType = "int"),
    })
    @RequestMapping(value = "/flowSchedule/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean flowSchedule(@PathVariable(value = "id") int id) {
        ModelMap modelMap = flowInsRepository.getFlowNodesForMap(id);
        return Result.QUERY_SUCCESS.setData(modelMap).toBean();
    }


    /**
     * @desc 用户待审流程
     * @author 杨勇 18-2-5 下午4:56
     */
    @ApiOperation(value = "用户待审流程[FLOW -11]", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户实体ID", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sysId", value = "应用系统ID", paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/flowBusiListByUser", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean flowBusiListByUser(Integer uid,String sysId) {
        List<FlowIns> list = flowInsRepository.getFlowInsByUser(uid,sysId);
        return Result.QUERY_SUCCESS.setData(list).toBean();
    }

    /**
     * @desc 获取流程进度id
     * @author 杨勇 18-2-5 下午4:56
     */
    @ApiOperation(value = "审批用户待审流程id[FLOW -12]", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "审批用户ID", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sysId", value = "应用系统ID", paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/flowBusiIdListByUser", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean flowBusiIdListByUser(Integer uid,String sysId) {
        List<Integer> list = flowInsRepository.getFlowInsIdByUser(uid,sysId);
        return getResponseBean(Result.QUERY_SUCCESS.setData(list));
    }

    /**
     * @desc 获取当前用户流程列表包含 所有/待审批/审批中/已审批
     * @author 杨勇 18-6-6 下午2:56
     */
    @ApiOperation(value = "审批用户流程统计[FLOW -13]", httpMethod = "POST")
    @RequestMapping(value = "/flowInsData", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "审批用户ID", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sysId", required = true, value = "应用系统ID", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "type", value = "状态类型(null:全部,1待审,2审核中,3已审)", paramType = "query", dataType = "String"),
    })
    @ResponseBody
    public ResponseBean flowInsData(Integer uid,String sysId,Integer type) {
        List<Integer> list = flowInsRepository.getFlowInsIdByUser(uid,sysId,type);
        return getResponseBean(Result.QUERY_SUCCESS.setData(list));
    }


    /**
     * @desc 获取当前用户流程列表包含 所有/待审批/审批中/已审批
     * @author 杨勇 18-6-6 下午2:56
     */
    @ApiOperation(value = "审批用户流程数量全部统计[FLOW -14]", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "审批用户ID", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sysId", required = true, value = "应用系统ID", paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/flowInsAllData", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean flowInsAllData(Integer uid,String sysId) {
        Map<String,List<Integer>> map = new HashMap<String,List<Integer>>();
        map.put("approving",flowInsRepository.getFlowInsIdByUser(uid,sysId,1));
        map.put("approved",flowInsRepository.getFlowInsIdByUser(uid,sysId,2));
        map.put("finsh",flowInsRepository.getFlowInsIdByUser(uid,sysId,3));
        return getResponseBean(Result.QUERY_SUCCESS.setData(map));
    }

    /**
     * @desc 获取当前用户流程列表包含 所有/待审批/审批中/已审批
     * @author 杨勇 18-6-6 下午2:56
     */
    @ApiOperation(value = "用户流程数量统计[FLOW -15]", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "审批用户实体ID", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sysId", required = true, value = "应用系统ID", paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/flowInsCountData", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean flowInsCountData(Integer uid,String sysId) {
        Integer[] list = flowInsRepository.getFlowInsIdCountByUser(uid,sysId);
        Map<String,Integer> map = new HashMap<String,Integer>();
        map.put("approving",list[0]);
        map.put("approved",list[1]);
        map.put("finsh",list[2]);
        return getResponseBean(Result.QUERY_SUCCESS.setData(map));
    }


    /***
     * @desc 流程超时退回起点发起人
     * @author 杨勇 18-2-5 下午4:56
     * @param fiid
     * @return
     */
    @ApiOperation(value = "流程超时退回起点[FLOW - 16]", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fiid", value = "流程实体id", paramType = "query", dataType = "int"),
    })
    @RequestMapping(value = "/flowBusiBack", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean flowBusiBack(Integer fiid){
        FlowIns ins = flowInsRepository.getOne(fiid);
        if(ins == null){
            return Result.find(CfI.R_FLOW_NOTFOUND_FAIL).toBean();
        }
        Result result = flowInsRepository.flowAutoBack(ins, true);
        if(result == null){
            return Result.OPERATE_SUCCESS.setData(ins).toBean();
        }else{
            return result.toBean();
        }
    }

    /***
     * @desc 流程超时退回上级
     * @author 杨勇 18-2-5 下午4:56
     * @param fiid
     * @return
     */
    @ApiOperation(value = "流程超时退回上级[FLOW - 17]", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fiid", value = "流程实体id", paramType = "query", dataType = "int"),
    })
    @RequestMapping(value = "/flowBusiBackPrev", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean flowBusiBackPrev(Integer fiid){
        FlowIns ins = flowInsRepository.getOne(fiid);
        if(ins == null){
            return Result.find(CfI.R_FLOW_NOTFOUND_FAIL).toBean();
        }
        Result msg = flowInsRepository.flowAutoBack(ins, false);
        if(msg == null){
            return Result.OPERATE_SUCCESS.setData(ins).toBean();
        }else{
            return msg.toBean();
        }
    }

    /***
     * @desc 通过业务和用户获取流程定义ID,和下一级审批信息
     * @author 杨勇 19-5-9 下午1:56
     * @param busi MultiQuery,OnekeyCheck,DataCompare
     * @param uid 用户id
     * @param condition 第三条件,无则为空
     * @return
     */
    @ApiOperation(value = "通过业务和用户获取流程定义ID,和下一级审批信息[FLOW - 17]", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "busi",required = true,allowableValues = "MultiQuery,OnekeyCheck,DataCompare",value = "业务标识[MultiQuery:综合查询],[OnekeyCheck:一键核查],[DataCompare:数据对比]", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "uid", required = true,value = "用户id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "condition", value = "第三条件,无则为空", paramType = "query", dataType = "string"),
    })
    @RequestMapping(value = "/flowInfoMatch", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean flowInfoMatch(String busi, Integer uid,String condition){
        FlowBusiConf cf = FlowBusiConf.getConf(busi);
        if(cf == null){
            return Result.find(CfI.R_FLOW_BUSINOEXIST_FAIL).toBean();
        }
        Map<String, Object> m = flowAllotService.flowInfoMatch(cf,uid,condition);
        if(m.get("flowDefId") != null){
            return Result.OPERATE_SUCCESS.setData(m.get("flowDefId")).toBean();
        }else{
            return Result.find(CfI.R_FLOW_NOTFOUND_FAIL).toBean();
        }
    }

}