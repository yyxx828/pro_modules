package com.xajiusuo.busi.electroPatrol.controller;

import com.xajiusuo.busi.electroPatrol.entity.ElectroPatrolLog;
import com.xajiusuo.busi.electroPatrol.service.ElectroPatrolLogService;
import com.xajiusuo.busi.electroPatrol.vo.PatrolRate;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author lizhidong
 * @date 2019-6-12 13:48
 */
@Slf4j
@Api(description = "电子巡更采集日志接口")
@RestController
@RequestMapping("/api/village/electroPatrolLog")
public class ElectroPatrolLogController extends BaseController {

    @Autowired
    private ElectroPatrolLogService electroPatrolLogService;

    @ApiOperation(value = "临时用电子巡更日志信息新增", httpMethod = "POST")
    @PostMapping("/save")
    @ResponseBody
    public ResponseBean save(@RequestBody ElectroPatrolLog patrolLog) {
//        log.setPatrolTime(new Date());
        patrolLog.setCreateDate(new Date());
        return getResponseBean(Result.SAVE_SUCCESS.setData(this.electroPatrolLogService.save(patrolLog)));
    }

    @ApiOperation(value = "获取指定设备当天的电子巡更日志", httpMethod = "GET")
    @ApiImplicitParam(name = "apeId", value = "设备编码", required = true, dataType = "string", paramType = "path")
    @GetMapping("/getTodayLogByApeId/{apeId}")
    @ResponseBody
    public ResponseBean getTodayLogByApeId(@PathVariable("apeId") String apeId) {
        return getResponseBean(Result.QUERY_SUCCESS.setData(this.electroPatrolLogService.getTodayLogByApeId(apeId)));
    }

    @ApiOperation(value = "获取指定小区昨日电子巡更日志", httpMethod = "GET")
    @ApiImplicitParam(name = "villageId", value = "小区编号", required = true, dataType = "string", paramType = "path")
    @GetMapping("/getYesterdayLogByVillageId/{villageId}")
    @ResponseBody
    public ResponseBean getYesterdayLogByVillageId(@PathVariable("villageId") String villageId) {
        return getResponseBean(Result.QUERY_SUCCESS.setData(this.electroPatrolLogService.getBeforeDayLogByVillageId(villageId, 1)));
    }

    @ApiOperation(value = "获取指定小区昨日电子巡更情况及巡更率", httpMethod = "GET")
    @ApiImplicitParam(name = "villageId", value = "小区编号", required = true, dataType = "string", paramType = "path")
    @GetMapping("/getYesterdayPartrolRateByVillageId/{villageId}")
    @ResponseBody
    public ResponseBean getYesterdayPartrolRateByVillageId(@PathVariable("villageId") String villageId) {
        return getResponseBean(Result.QUERY_SUCCESS.setData(PatrolRate.generate(this.electroPatrolLogService.getBeforeDayLogByVillageId(villageId, 1))));
    }

    @ApiOperation(value = "获取指定小区昨日电子巡更情况统计", httpMethod = "GET")
    @ApiImplicitParam(name = "villageId", value = "小区编号", required = true, dataType = "string", paramType = "path")
    @GetMapping("/getYesterdayPartrolStatByVillageId/{villageId}")
    @ResponseBody
    public ResponseBean getYesterdayPartrolStatByVillageId(@PathVariable("villageId") String villageId) {
        return getResponseBean(Result.QUERY_SUCCESS.setData(this.electroPatrolLogService.getYesterdayPartrolStatByVillageId(villageId, 1)));
    }

    @ApiOperation(value = "获取电子巡更日志列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(patrolTime,desc),", allowMultiple = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/queryPage")
    @ResponseBody
    public ResponseBean queryPage(Pageable pageable, ElectroPatrolLog electroPatrolLog) {
        pageable = transPage(pageable, new Sort(Sort.Direction.DESC, "patrolTime"), false);
        return getResponseBean(Result.QUERY_SUCCESS.setData(this.electroPatrolLogService.queryPatrolLog(this.request, pageable, electroPatrolLog)));
    }

    @ApiOperation(value = "电子巡更点位巡更次数统计", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "timeFrame", value = "日/周/月", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "startTime", value = "开始时间yyyy-MM-dd", required = false, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "endTime", value = "结束时间yyyy-MM-dd", required = false, paramType = "query", dataType = "string")
    })
    @GetMapping("/getPartrolStat/{villageId}")
    @ResponseBody
    public ResponseBean getPartrolStat(@PathVariable("villageId") String villageId, @RequestParam String timeFrame,
                                       @RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime) {
        return getResponseBean(Result.QUERY_SUCCESS.setData(this.electroPatrolLogService.getPartrolStat(villageId, timeFrame, startTime, endTime)));
    }
}
