package com.xajiusuo.busi.entranceandexit.controller;

import com.xajiusuo.busi.buildingaccess.service.BuildingEntranceGuardLogService;
import com.xajiusuo.busi.entranceandexit.entity.PersonWarn;
import com.xajiusuo.busi.entranceandexit.entity.PersonWarnVo;
import com.xajiusuo.busi.entranceandexit.service.GateEntranceGuardLogService;
import com.xajiusuo.busi.entranceandexit.service.PersonWarnService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.DateUtils;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shirenjing on 2019/6/27.
 */
@Slf4j
@Api(description = "人员预警")
@RestController
@RequestMapping(value = "/api/village/personWarn")
public class PersonWarnController extends BaseController {

    @Autowired
    private PersonWarnService personWarnService;
    @Autowired
    private GateEntranceGuardLogService gateEntranceGuardLogService;  //出入口门禁
    @Autowired
    private BuildingEntranceGuardLogService buildingEntranceGuardLogService;  //楼宇门禁



    @ApiOperation(value = "查看人员预警参数", httpMethod = "GET")
    @RequestMapping(value = "/getPersonWarnParam/{warnType}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "warnType", required = true, value = "预警类型：0:昼伏夜出 1：频繁出入", paramType = "path", dataType = "int")
    })
    @ResponseBody
    public ResponseBean getPersonWarnParam(@PathVariable(value = "warnType") int warnType) {
        try{
            Map<String, Object> map = new HashMap<>();
            if(warnType==0){//昼伏夜出
                map = personWarnService.getNightOutParams();
            }else{//频繁出入
                map = personWarnService.getOftenOutInParams();
            }
            return Result.QUERY_SUCCESS.toBean(map);
        }catch (Exception e){
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }

    @PostMapping(value = "/updateWarnParams")
    @ApiOperation(value = "修改人员预警参数", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "warnType", required = true, value = "预警类型：0:昼伏夜出 1：频繁出入", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "dataRange", required = true, value = "数据范围", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "timeRange", required = false, value = "时间范围", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "nums", required = true, value = "次数", paramType = "query", dataType = "int")
    })
    @ResponseBody
    public ResponseBean updateWarnParams( int warnType, int dataRange, String timeRange, int nums) {
        try{
            if(warnType==0){
                personWarnService.updateNightOutParams(dataRange, timeRange, nums);
            }else{
                personWarnService.updateOftenOutInParams(dataRange, nums);
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
    public ResponseBean queryPagePersonWarn(Pageable pageable,PersonWarnVo personWarnVo) {
        pageable = transPage(pageable,new Sort(Sort.Direction.DESC, "lastModifyTime"),true);
        try {
            Page<PersonWarn> page = personWarnService.queryPagePersonWarn(pageable,personWarnVo);
            return Result.QUERY_SUCCESS.toBean(page);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }
    @GetMapping(value = "/queryPersonWarnList")
    @ApiOperation(value = "查看人员预警详单", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "详单id",  paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flag", required = true, value = "0:出入口门禁 1：楼宇门禁",  paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean queryPersonWarnList(String ids,String flag) {
        try {
            List<Map> result = null;
            String[] idArr = ids.split(",");
            if("0".equals(flag)) result = personWarnService.queryGateLogByIds(idArr);
            if("1".equals(flag)) result = personWarnService.queryBuildLogByIds(idArr);
            return Result.QUERY_SUCCESS.toBean(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }


    @GetMapping(value = "/testss")
    @ApiOperation(value = "testss", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "详单id",  paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flag", required = true, value = "0:出入口门禁 1：楼宇门禁",  paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean testss(String analyType, Integer flag, String dataRange1, String dataRange2, String timeRange1, String timeRange2) {
        Date[] dataRange = new Date[]{DateUtils.parse(dataRange1,"yyyy-MM-dd HH:mm:ss"),DateUtils.parse(dataRange2,"yyyy-MM-dd HH:mm:ss")};
        Integer[] timeRange = null;
        if(CommonUtils.isNotEmpty(timeRange1) && CommonUtils.isNotEmpty(timeRange2)){
            timeRange = new Integer[]{Integer.parseInt(timeRange1),Integer.parseInt(timeRange2)};
        }
        try {
            Map<String,PersonWarn> map = personWarnService.queryListPersonWarn(analyType,flag, dataRange,timeRange);
            return Result.QUERY_SUCCESS.toBean(map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }








}
