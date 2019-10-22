package com.xajiusuo.busi.control.controller;

import com.xajiusuo.busi.buildingaccess.entity.BuildingEntranceGuardLog;
import com.xajiusuo.busi.buildingaccess.service.BuildingEntranceGuardLogService;
import com.xajiusuo.busi.control.entity.*;
import com.xajiusuo.busi.control.service.ControlIdentifyService;
import com.xajiusuo.busi.control.service.ControlTaskService;
import com.xajiusuo.busi.diction.controller.DictionController;
import com.xajiusuo.busi.entranceandexit.entity.GateEntranceGuardLog;
import com.xajiusuo.busi.entranceandexit.service.GateEntranceGuardLogService;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleLog;
import com.xajiusuo.busi.motorVehicle.service.MotorVehicleLogService;
import com.xajiusuo.busi.wifilog.entity.WifiLog;
import com.xajiusuo.busi.wifilog.entity.WifiQueryVo;
import com.xajiusuo.busi.wifilog.service.WifiLogService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Configs;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.DateUtils;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.utils.CfI;
import com.xajiusuo.utils.UserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by shirenjing on 2019/6/22.
 */
@Slf4j
@Api(description = "布控")
@RestController
@RequestMapping(value = "/api/village/controlTask")
public class ControlTaskController extends BaseController {
    @Autowired
    private ControlTaskService controlTaskService;
    @Autowired
    private ControlIdentifyService controlIdentifyService;
    @Autowired
    private GateEntranceGuardLogService gateEntranceGuardLogService; //出入口门禁
    @Autowired
    private BuildingEntranceGuardLogService buildingEntranceGuardLogService; //楼宇门禁
    @Autowired
    private MotorVehicleLogService motorVehicleLogService; //停车场采集
    @Autowired
    private WifiLogService wifiLogService; // wifi探针信息采集
    @Autowired
    private DictionController dictionController; //字典信息


    /*{
        "controlEndTime": "2019-07-15 06:15:44",
            "controlStartTime": "2019-07-08 06:15:44",
            "controlStatus": "1",
            "controlType": "1",
            "id": "402881da6bd02e5c016bd03be2790000",
            "taskName": "0708人员布控"
    }*/
    @PostMapping(value = "/saveOrUpdateTask")
    @ApiOperation(value = "布控任务新增/修改", httpMethod = "POST")
    @ResponseBody
    public ResponseBean saveOrUpdateTask(@RequestBody ControlTaskVo controlTask) {
        ControlTask task = null;
        String option = "update";
        if(CommonUtils.isEmpty(controlTask.getId())){
            option = "save";
        }else{
            task = controlTaskService.getOne(controlTask.getId());
        }
        controlTask.setControlStatus("0"); //未布控
        String otaskName = ""; //任务名称
        if(task!=null){
            otaskName = task.getTaskName();
        }
        if(!otaskName.equals(controlTask.getTaskName())){ //任务名称去重
            List<ControlTask> taskList = controlTaskService.findBy("taskName",controlTask.getTaskName());
            if(taskList!=null && taskList.size()>0){
                return getResponseBean(Result.find(CfI.R_CATCH_TASKREPEAT_FAIL));
            }
        }
        try{
            if("update".equals(option)) controlIdentifyService.deleteByTaskId(task.getId());
            ControlTask saveTask = controlTaskService.saveOrUpateTask(controlTask.getEntity());
            controlIdentifyService.batchSaveIdentify(saveTask,controlTask.getIdentifyVoList());
            if("save".equals(option)){
                return getResponseBean(Result.SAVE_SUCCESS);
            }else{
                return getResponseBean(Result.UPDATE_SUCCESS);
            }
        }catch (Exception e){
            e.printStackTrace();
            if("save".equals(option)){
                return getResponseBean(Result.SAVE_FAIL);
            }else{
                return getResponseBean(Result.UPDATE_FAIL);
            }
        }

    }

    @GetMapping(value = "/queryTaskList")
    @ApiOperation(value = "布控任务列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(lastModifyTime,desc),", example = "lastModifyTime,desc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean queryTaskList(Pageable pageable, ControlTaskQueryVo controlTaskQueryVo) {
        if(pageable.getSort().isUnsorted()){
            pageable.getSort().and(new Sort(Sort.Direction.DESC, "lastModifyTime"));
        }

        try {
            Page<ControlTask> page = controlTaskService.queryTaskPage(pageable,controlTaskQueryVo);
            page.getContent().forEach(task -> task.setNums(controlIdentifyService.getSize("task",task,null,null)));
            return getResponseBean(Result.QUERY_SUCCESS.setData(page));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @GetMapping(value = "/checkRepeat")
    @ApiOperation(value = "任务名称是否重复", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskName", required = true, value = "任务名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "id", required = false, value = "主键编号", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean checkRepeat(String taskName, String id) {
        boolean res = controlTaskService.findSame("taskName",taskName, id, null);
        return Result.find(res ? CfI.R_CATCH_TASKRISEPEAT_SUCCESS : CfI.R_CATCH_TASKREPEAT_SUCCESS).toBean(res);
    }

    @GetMapping(value = "/updateStopControl/{id}")
    @ApiOperation(value = "手动布控/撤控", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "任务id", paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "controlStatus", required = true, value = "0:未布控 1：布控中 2：布控结束", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean updateStopControl(@PathVariable(value = "id") String id,String controlStatus) {
        try{
            ControlTask task = controlTaskService.getOne(id);
            if(task!=null){
                task.setControlStatus(controlStatus);
            }
            controlTaskService.update(task);
            return getResponseBean(Result.OPERATE_SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.OPERATE_FAIL);
        }

    }

    @RequestMapping(value = "/viewTask/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "查看任务参数", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "任务id", paramType = "path", dataType = "string")
    })
    @ResponseBody
    public ResponseBean viewTask(@PathVariable(value = "id") String id) {
        ControlTaskVo taskVo = new ControlTaskVo();
        ControlTask task = controlTaskService.getOne(id);
        if(task!=null){
            taskVo.setId(task.getId());
            taskVo.setTaskName(task.getTaskName());
            taskVo.setControlType(task.getControlTypes());
            taskVo.setControlStartTime(task.getControlStartTime());
            taskVo.setControlEndTime(task.getControlEndTime());
            taskVo.setControlStatus(task.getControlStatuses());
            List<ControlIdentify> ids = controlIdentifyService.getList("task",task,null,null,null);
            for (ControlIdentify controlId:ids) {
                ControlIdentifyVo vo = new ControlIdentifyVo();
                vo.setIdentify(controlId.getIdentify());
                vo.setIdType(controlId.getIdTypeDiction().getVal());
                taskVo.addIdList(vo);
            }
        }
        return Result.QUERY_SUCCESS.toBean(taskVo);

    }

    @RequestMapping(value = "/preUpdateTask/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "进入修改任务信息页面", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "任务id", paramType = "path", dataType = "string")
    })
    @ResponseBody
    public ResponseBean preUpdateTask(@PathVariable(value = "id") String id) {
        ControlTaskVo taskVo = new ControlTaskVo();
        ControlTask task = controlTaskService.getOne(id);
        if(task!=null){
            taskVo.setId(task.getId());
            taskVo.setTaskName(task.getTaskName());
            taskVo.setControlType(task.getControlType());
            taskVo.setControlStartTime(task.getControlStartTime());
            taskVo.setControlEndTime(task.getControlEndTime());
            taskVo.setControlStatus(task.getControlStatus());
            List<ControlIdentify> ids = controlIdentifyService.getList("task",task,null,null,null);
            for (ControlIdentify controlId:ids) {
                ControlIdentifyVo vo = new ControlIdentifyVo();
                vo.setIdentify(controlId.getIdentify());
                vo.setIdType(controlId.getIdTypeDiction().getKeys());
                taskVo.addIdList(vo);
            }
        }
        return Result.QUERY_SUCCESS.toBean(taskVo);

    }

    @GetMapping(value = "/queryIdentifyList")
    @ApiOperation(value = "布控标识列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(lastModifyTime,desc),", example = "lastModifyTime,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "taskid", required = true, value = "任务id,", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean queryIdentifyList(Pageable pageable, String taskid) {
        if(pageable.getSort().isUnsorted()){
            pageable.getSort().and(new Sort(Sort.Direction.DESC, "lastModifyTime"));
        }
        try {
            Page<ControlIdentify> page = controlIdentifyService.queryPageIdentify(pageable,taskid);
            return getResponseBean(Result.QUERY_SUCCESS.setData(page));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }


    @GetMapping(value = "/queryControlDetailGate")
    @ApiOperation(value = "布控详情-出入口门禁信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(passtime,desc),", example = "passtime,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "taskId", required = true, value = "任务id", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "passtimes", required = false, value = "数据时间范围，用逗号隔开", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "idcard", required = false, value = "证件编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "passtype", required = false, value = "0出1进", paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean queryControlDetailGate(Pageable pageable, String taskId,String passtimes,String idcard,String passtype) {
        if(pageable.getSort().isUnsorted()){
            pageable.getSort().and(new Sort(Sort.Direction.DESC, "lastModifyTime"));
        }
        String[] passtimeArr = null;
        if(CommonUtils.isNotEmpty(passtimes)){
            passtimeArr = passtimes.split(",");
        } else{
            ControlTask task = controlTaskService.getOne(taskId);
            passtimeArr = new String[]{DateUtils.format(task.getControlStartTime(),"yyyy-MM-dd HH:mm:ss"),
                    DateUtils.format(task.getControlEndTime(),"yyyy-MM-dd HH:mm:ss")};
        }
        try {
            Page<GateEntranceGuardLog> page = gateEntranceGuardLogService.queryPageControltask(pageable,taskId,passtimeArr,idcard,passtype);
            return getResponseBean(Result.QUERY_SUCCESS.setData(page));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }


    @GetMapping(value = "/queryControlDetailBuild")
    @ApiOperation(value = "布控详情-楼宇门禁信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(passtime,desc),", example = "passtime,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "taskId", required = true, value = "任务id", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "passtimes", required = false, value = "数据时间范围，用逗号隔开", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "idcard", required = false, value = "证件编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "passtype", required = false, value = "0出1进", paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean queryControlDetailBuild(Pageable pageable, String taskId,String passtimes,String idcard,String passtype) {
        if(pageable.getSort().isUnsorted()){
            pageable.getSort().and(new Sort(Sort.Direction.DESC, "lastModifyTime"));
        }
        String[] passtimeArr = null;
        if(CommonUtils.isNotEmpty(passtimes)){
            passtimeArr = passtimes.split(",");
        } else{
            ControlTask task = controlTaskService.getOne(taskId);
            passtimeArr = new String[]{DateUtils.format(task.getControlStartTime(),"yyyy-MM-dd HH:mm:ss"),
                    DateUtils.format(task.getControlEndTime(),"yyyy-MM-dd HH:mm:ss")};
        }
        try {
            Page<BuildingEntranceGuardLog> page = buildingEntranceGuardLogService.queryPageControltask(pageable,taskId,passtimeArr,idcard,passtype);
            return getResponseBean(Result.QUERY_SUCCESS.setData(page));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @GetMapping(value = "/queryControlDetailCar")
    @ApiOperation(value = "布控详情-停车场采集信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(passtime,desc),", example = "passtime,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "taskId", required = true, value = "任务id", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "passtimes", required = false, value = "数据时间范围，用逗号隔开", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "plateNo", required = false, value = "车牌号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "passtype", required = false, value = "0出1进", paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean queryControlDetailCar(Pageable pageable, String taskId,String passtimes, String plateNo,String passtype) {
        if(pageable.getSort().isUnsorted()){
            pageable.getSort().and(new Sort(Sort.Direction.DESC, "lastModifyTime"));
        }
        String[] passtimeArr = null;
        if(CommonUtils.isNotEmpty(passtimes)){
            passtimeArr = passtimes.split(",");
        } else{
            ControlTask task = controlTaskService.getOne(taskId);
            passtimeArr = new String[]{DateUtils.format(task.getControlStartTime(),"yyyy-MM-dd HH:mm:ss"),
                    DateUtils.format(task.getControlEndTime(),"yyyy-MM-dd HH:mm:ss")};
        }
        try {
            Page<MotorVehicleLog> page = motorVehicleLogService.queryPageControltask(pageable,taskId,passtimeArr,plateNo,passtype);
            return getResponseBean(Result.QUERY_SUCCESS.setData(page));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }


    @GetMapping(value = "/queryControlDetailWif")
    @ApiOperation(value = "布控详情-wifi探针采集信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(passtime,desc),", example = "passtime,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "taskId", required = true, value = "任务id", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "passtimes", required = false, value = "数据时间范围，用逗号隔开", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "mac", required = false, value = "mac地址", paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean queryControlDetailWif(Pageable pageable, String taskId,String passtimes, String mac) {
        if(pageable.getSort().isUnsorted()){
            pageable.getSort().and(new Sort(Sort.Direction.DESC, "lastModifyTime"));
        }
        String[] passtimeArr = null;
        if(CommonUtils.isNotEmpty(passtimes)){
            passtimeArr = passtimes.split(",");
        } else{
            ControlTask task = controlTaskService.getOne(taskId);
            passtimeArr = new String[]{DateUtils.format(task.getControlStartTime(),"yyyy-MM-dd HH:mm:ss"),
                    DateUtils.format(task.getControlEndTime(),"yyyy-MM-dd HH:mm:ss")};
        }
        WifiQueryVo wifiQueryVo = new WifiQueryVo();
        wifiQueryVo.setPasstimes(passtimeArr);
        wifiQueryVo.setMac(mac);
        try {
            Page<WifiLog> page = wifiLogService.queryPageControltask(pageable,taskId,wifiQueryVo);
            return getResponseBean(Result.QUERY_SUCCESS.setData(page));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @ApiOperation(value = "布控人员类型字典下拉")
    @RequestMapping(value = "getPersonTypeDiction", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean getPersonTypeDiction() {
        //布控人员类型字典下拉码值
        try {
            String rental = Configs.find(CfI.C_CATCHPERSON_TPYE_DICTION).getValue();
            return dictionController.listDictions(rental);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.QUERY_FAIL.toBean();
        }
    }

    @ApiOperation(value = "布控车辆类型字典下拉")
    @RequestMapping(value = "getCarTypeDiction", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean getCarTypeDiction() {
        //布控车辆类型字典下拉码值
        try {
            String rental = Configs.find(CfI.C_CATCHCAR_TPYE_DICTION).getValue();
            return dictionController.listDictions(rental);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.QUERY_FAIL.toBean();
        }
    }

    @ApiOperation(value = "布控wifi类型字典下拉")
    @RequestMapping(value = "getWifiTypeDiction", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean getWifiTypeDiction() {
        //布控wifi类型字典下拉码值
        try {
            String rental = Configs.find(CfI.C_CATCHWIFI_TPYE_DICTION).getValue();
            return dictionController.listDictions(rental);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.QUERY_FAIL.toBean();
        }
    }

    @RequestMapping(value = "/deleteTask/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机动车基本信息", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "机动车信息id", paramType = "path", dataType = "string")
    })
    @ResponseBody
    public ResponseBean deleteTask(@PathVariable(value = "id") String id) {
        try {
            ControlTask task = controlTaskService.getOne(id);
            controlIdentifyService.deleteByTask(task);
            controlTaskService.delete(task);
            return Result.DELETE_SUCCESS.toBean();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.DELETE_FAIL.toBean();
        }
    }



}
