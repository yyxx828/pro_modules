package com.xajiusuo.busi.motorVehicle.controller;

import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleWarn;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleWarnVo;
import com.xajiusuo.busi.motorVehicle.service.MotorVehicleWarnService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liujiankang on 2019/6/28.
 */
@Slf4j
@Api(description = "机动车（停车场）采集信息管理")
@RestController
@RequestMapping(value = "/api/village/MotorVehicleWarn")
public class MotorVehicleWarnController extends BaseController {

    @Autowired
    private MotorVehicleWarnService motorVehiclelnWarnService;

    @ApiOperation(value = "查看车辆预警参数", httpMethod = "GET")
    @RequestMapping(value = "/getMotorVehicleParam/{warnType}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "warnType", required = true, value = "预警类型：0:昼伏夜出 1：频繁出入", paramType = "path", dataType = "int")
    })
    @ResponseBody
    public ResponseBean getMotorVehicleParam(@PathVariable(value = "warnType") int warnType) {
        try{
            Map<String, Object> map = new HashMap<>();
            if(warnType==0){//长时间停放
                map = motorVehiclelnWarnService.getOftenStopParams();
            }else if(warnType==1){//昼伏夜出
                map = motorVehiclelnWarnService.getNightOutParams();
            }else if(warnType==2){//频繁出入
                map = motorVehiclelnWarnService.getOftenOutInParams();
            }
            return Result.QUERY_SUCCESS.toBean(map);
        }catch (Exception e){
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }

    @PostMapping(value = "/updateMotorVehicWarnleParams")
    @ApiOperation(value = "修改车辆预警参数", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "warnType", required = true, value = "预警类型：0:昼伏夜出 1：频繁出入", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "dataRange", required = true, value = "数据范围", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "timeRange", required = false, value = "时间范围", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "nums", required = true, value = "次数", paramType = "query", dataType = "int")
    })
    @ResponseBody
    public ResponseBean updateMotorVehicWarnleParams( int warnType, int dataRange, String timeRange, int nums) {
        try{
            if(warnType==0){//长时间停放
                motorVehiclelnWarnService.updateOftenStopParams(dataRange);
            }else if(warnType==1){//昼伏夜出
                motorVehiclelnWarnService.updateNightOutParams(dataRange,timeRange,nums);
            }else if(warnType==2){//频繁出入
                motorVehiclelnWarnService.updateOftenOutInParams(dataRange,nums);
            }
            return Result.UPDATE_SUCCESS.toBean();
        }catch (Exception e){
            e.printStackTrace();
            return Result.UPDATE_FAIL.toBean();
        }
    }


    @GetMapping(value = "/queryPagePersonWarn")
    @ApiOperation(value = "人员预警信息列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(lastModifyTime,desc),", example = "lastModifyTime,desc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean queryPagePersonWarn(Pageable pageable, MotorVehicleWarnVo personWarnVo) {
        pageable = transPage(pageable,new Sort(Sort.Direction.DESC, "lastModifyTime"),true);
        try {
            Page<MotorVehicleWarn> page = motorVehiclelnWarnService.queryPagePersonWarn(pageable,personWarnVo);
            return Result.QUERY_SUCCESS.toBean(page);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }



}
