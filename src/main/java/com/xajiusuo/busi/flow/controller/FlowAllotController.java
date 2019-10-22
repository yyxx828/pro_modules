package com.xajiusuo.busi.flow.controller;

import com.xajiusuo.busi.flow.conf.FlowBusiConf;
import com.xajiusuo.busi.flow.entity.FlowAllot;
import com.xajiusuo.busi.flow.entity.FlowNode;
import com.xajiusuo.busi.flow.service.FlowAllotService;
import com.xajiusuo.busi.flow.service.FlowDefService;
import com.xajiusuo.busi.flow.service.FlowNodeService;
import com.xajiusuo.busi.out.configuration.RemoteConfigurationProperties;
import com.xajiusuo.busi.user.controller.DepartController;
import com.xajiusuo.busi.user.controller.DutyController;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 杨勇 on 2018/5/23.
 */
@Slf4j
@Api(description = "流程分配管理")
@RestController
@RequestMapping(value = "/api/village/flowAllot")
@EnableConfigurationProperties(RemoteConfigurationProperties.class)
public class FlowAllotController extends BaseController {

    @Autowired
    private FlowAllotService flowAllotService;

    @Autowired
    private FlowDefService flowDefRepository;

    @Autowired
    private FlowNodeService flowNodeRepository;

    @Autowired
    private DepartController departController;

    @Autowired
    private DutyController dutyController;

    /***
     * @return
     * @desc 流程分配列表
     * @author 杨勇 19-2-18
     */
    @ApiOperation(value = "流程分配列表[FLOW - 1]", notes = "查询流程分配表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页数量,必须大于0(不写默认为20)", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为lastModifyTime,asc),", example = "id,asc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "busi", value = "业务标识", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flowType", value = "分配类型(0指定人,1部门+角色, 2角色 9是默认,从左到右最近匹配)", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "did", value = "部门ID", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pid", value = "职务ID", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "userId", value = "用户ID", paramType = "query", dataType = "int"),
    })
    @RequestMapping(value = "/flowAllotPager", method = RequestMethod.GET)
    public ResponseBean flowAllotPager(Pageable page, FlowAllot entity) {
        Page<FlowAllot> pager = flowAllotService.queryFlowAllotPage(page,entity);
        return getResponseBean(Result.QUERY_SUCCESS.setData( pager));
    }

    /***
     * @return
     * @desc 流程分配查看
     * @author 杨勇 19-2-19 下午3:09
     */
    @ApiOperation(value = "流程分配查看[FLOW - 2]", notes = "流程分配查看", httpMethod = "POST")
    @RequestMapping(value = "/flowAllotView/{id}", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = false, value = "流程分配ID", paramType = "path", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean flowAllotView(@PathVariable(value = "id") Integer id) {
        FlowAllot entity = null;
        if(id != null && id != 0){
            entity = flowAllotService.getOne(id);
        }
        return getResponseBean(Result.QUERY_SUCCESS.setData( entity));
    }

    /***
     * @return
     * @desc 流程分配操作
     * @author 杨勇 19-2-19 下午3:09
     */
    @ApiOperation(value = "流程分配操作[FLOW - 3]", notes = "流程分配操作", httpMethod = "POST")
    @RequestMapping(value = "/flowAllotEdit/{id}", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = false, value = "流程分配ID", paramType = "path", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean flowAllotEdit(@PathVariable(value = "id") Integer id) {
        Map<String,Object> map = new HashMap<String,Object>();
        FlowAllot entity = null;
        if(id != null && id != 0){
            entity = flowAllotService.getOne(id);
        }

        map.put("entity",entity);//分配实体
        map.put("flow",flowDefRepository.findBy("flevel",0));//所有流程列表
        map.put("departs",departController.departTree().getData());//所有部门列表
        map.put("dutys",dutyController.dutyAll().getData());//所有职务列表
        map.put("users",departController.departUser().getData());//用户树
        map.put("busi", FlowBusiConf.getBusiMap());//业务表列表
        return Result.QUERY_SUCCESS.setData(map).toBean();
    }

    /***
     * @return
     * @desc 流程分配
     * @author 杨勇 19-2-19 下午3:09
     */
    @ApiOperation(value = "流程分配[FLOW - 4]", notes = "流程分配", httpMethod = "POST")
    @RequestMapping(value = "/flowToAllot", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean flowToAllot(@RequestBody FlowAllot entity) {
        //查询是否存在重复流程配置
        if (entity.getFlowType() != 1 && entity.getFlowType() != 0 && entity.getFlowType() != 2 && entity.getFlowType() != 9) {//0指定人,1角色,2部门+角色,9全局
            return getResponseBean(Result.SAVE_FAIL);
        }

        Result r = flowAllotService.findSameAllot(entity);
        if(r != null){
            return getResponseBean(r);
        }
        r = flowAllotService.allotName(entity);
        if(r != null){
            return getResponseBean(r);
        }
        entity.setRuns(true);

        flowAllotService.save(entity);
        return getResponseBean(Result.OPERATE_SUCCESS.setData(entity));
    }

    /***
     * @return
     * @desc 流程分配启禁用
     * @author 杨勇 19-2-19 下午3:09
     */
    @ApiOperation(value = "流程分配启禁用[FLOW - 5]", notes = "流程分配启禁用", httpMethod = "POST")
    @RequestMapping(value = "/flowAllotUse/{run}/{id}", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = false, value = "空为新增,否则为修改", paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "run", required = true, value = "流程启用开关on启用,off关闭", paramType = "path", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean flowAllotUse(@PathVariable(value = "id") Integer id, @PathVariable(value = "run") String run) {
        FlowAllot entity = flowAllotService.getOne(id);

        if(entity.getRuns() == null){//默认开启
            entity.setRuns(true);
        }
        Result r = Result.find(CfI.R_FLOW_RUNNOALTER_SUCCESS);
        if(entity.getRuns()){
            if("off".equals(run)){
                entity.setRuns(false);
                flowAllotService.save(entity);
                r = Result.find(CfI.R_FLOW_RUNOFF_SUCCESS);
            }
        }else{
            if("on".equals(run)){
                entity.setRuns(true);
                flowAllotService.save(entity);
                r = Result.find(CfI.R_FLOW_RUNON_SUCCESS);
            }
        }
        return getResponseBean(r.setData(entity));
    }

    /***
     * @return
     * @desc 流程分配删除
     * @author 杨勇 19-2-19 下午3:09
     */
    @ApiOperation(value = "流程分配删除[FLOW - 6]", notes = "流程分配删除", httpMethod = "DELETE")
    @RequestMapping(value = "/flowAllotDelete/{id}", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = false, value = "流程分配ID", paramType = "path", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean flowAllotDelete(@PathVariable(value = "id") Integer id) {
        FlowAllot entity = null;
        if(id != null && id != 0){
            flowAllotService.delete(id);
        }
        return getResponseBean(Result.DELETE_SUCCESS);
    }

    /***
     * @return
     * @desc 流程对象节点
     * @author 杨勇 18-1-26 下午3:48
     * @param fdid 流程定义ID
     */
    @ApiOperation(value = "流程对象节点[FLOW - 7]", httpMethod = "POST")
    @RequestMapping(value = "/flowDefNodeList", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fdid", required = true, value = "流程对象id", paramType = "query", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean flowDefNodeList(Integer fdid) {
        List<FlowNode> list = flowNodeRepository.executeNativeQuerySql(MessageFormat.format( "select * from {0} e where fdId = ? order by orders", SqlUtils.tableName(FlowNode.class)), fdid);
        return getResponseBean(Result.QUERY_SUCCESS.setData(list));
    }
}
