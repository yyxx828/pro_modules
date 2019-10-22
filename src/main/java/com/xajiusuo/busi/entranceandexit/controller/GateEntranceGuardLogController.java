package com.xajiusuo.busi.entranceandexit.controller;

import com.xajiusuo.busi.entranceandexit.entity.GateEntranceGuardLog;
import com.xajiusuo.busi.entranceandexit.entity.GuardCardCountVo;
import com.xajiusuo.busi.entranceandexit.service.GateEntranceGuardLogService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Configs;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.DateUtils;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.utils.CfI;
import com.xajiusuo.utils.DateTools;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangdou on 2019/6/6
 */
@Slf4j
@Api(description = "出入口门禁采集信息")
@RestController
@RequestMapping(value = "/api/village/gateEntranceGuardLog")
public class GateEntranceGuardLogController extends BaseController {

    @Autowired
    private GateEntranceGuardLogService gateEntranceGuardLogService;

    /**
     * 出入口门禁采集信息列表查询
     *
     * @param page
     * @param gateEntranceGuardLog
     * @return
     */
    @ApiOperation(value = "出入口门禁采集信息列表查询", httpMethod = "GET")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean query(Pageable page, GateEntranceGuardLog gateEntranceGuardLog) {
        page = transPage(page, new Sort(Sort.Direction.DESC, "passtime"), true);
        Page<GateEntranceGuardLog> ulist = gateEntranceGuardLogService.query(gateEntranceGuardLog, page);
        return Result.QUERY_SUCCESS.toBean(ulist);
    }


    /**
     * 根据时间统计出入口量
     *
     * @param times
     * @param flag
     * @return
     */
    @ApiOperation(value = "根据时间统计出入口量", httpMethod = "GET")
    @RequestMapping(value = "/tjGateEntranceCard", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "times", required = false, value = "时间用,号分割", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flag", value = "按天：day, 按周：week, 按月：month", example = "day", paramType = "query", dataType = "string")

    })
    @ResponseBody
    public ResponseBean tjGateEntranceCard(String times, String flag) {
        Map<String, List> map = null;
        try {
            map = new HashMap<>();
            List<GuardCardCountVo> result = gateEntranceGuardLogService.tjGateEntranceCard(times, flag);
            if (result != null && result.size() > 0) {
                List date = new ArrayList<>();
                List dataRange = new ArrayList<>();
                List inNums = new ArrayList<>();
                List outNums = new ArrayList<>();
                for (GuardCardCountVo vo : result) {
                    if ("0".equals(vo.getPassType())) {
                        date.add(vo.getDays());
                        dataRange.add(getTimeDur(flag, vo.getDays()));
                        outNums.add(vo.getNums());
                    } else {
                        inNums.add(vo.getNums());
                    }

                }
                map.put("date", date);
                map.put("dataRange", dataRange);
                map.put("inNums", inNums);
                map.put("outNums", outNums);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
        return Result.QUERY_SUCCESS.toBean(map);
    }

    public String getTimeDur(String flag, String date) {
        String result = "";
        if ("day".equals(flag)) {
            result = date + " 00:00:00" + "," + date + " 23:59:59";
        }
        if ("month".equals(flag)) {
            String start = date + "-01";
            String end = DateUtils.dateAddd2s(DateUtils.addMonth(DateUtils.parse(start, "yyyy-MM-dd"), 1), -1);
            result = start + " 00:00:00" + "," + end + " 23:59:59";
        }
        if ("week".equals(flag)) {
            String[] arr = date.split("-");
            int year = Integer.parseInt(arr[0]);
            int w = Integer.parseInt(arr[1]);
            String start = DateTools.getStartDayOfWeekNo(year, w);
            String end = DateTools.getEndDayOfWeekNo(year, w);
            result = start + " 00:00:00" + "," + end + " 23:59:59";
        }
        return result;
    }

    /**
     * 统计门禁卡和刷卡数量
     *
     * @return
     */
    @ApiOperation(value = "统计门禁卡和刷卡数量", httpMethod = "GET")
    @RequestMapping(value = "/tjMjkAdnSkCount", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean tjMjkAdnSkCount() {
        try {
            return Result.QUERY_SUCCESS.toBean(gateEntranceGuardLogService.tjMjkAdnSkCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.QUERY_FAIL.toBean();
    }

    /**
     * 出入口时间段流量统计
     *
     * @param pill
     * @param date
     * @return
     */
    @GetMapping(value = "/gateEntranceCardTimeDur")
    @ApiOperation(value = "出入口时间段流量统计", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pill", value = "小时颗粒度", required = true, example = "2", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flag", value = "按天：day, 按周：week, 按月：month", example = "day", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "date", value = "日期/周/月份", required = false, paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean gateEntranceCardTimeDur(String pill, String flag, String date) {
        try {
            int p = Configs.find(CfI.C_CENSUS_ACCESSCARD_TIMECOUNT_TIMEUNIT).getInt();
            if (CommonUtils.isNotEmpty(pill)) p = Integer.parseInt(pill);
            String[] timeArr = null;
            if (date != null && !"".equals(date)) {
                String times = getTimeDur(flag, date);
                timeArr = times.split(",");
            }
            List<Map<String, Object>> result = gateEntranceGuardLogService.gateEntranceCardTimeDur(p, timeArr);
            return Result.QUERY_SUCCESS.toBean(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }

    /**
     * 出入口时间段流量详单
     *
     * @param pageable
     * @param housrs
     * @param flag
     * @param date
     * @return
     */
    @GetMapping(value = "/queryGateEntranceCardByTimeDur")
    @ApiOperation(value = "出入口时间段流量详单", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为passTime,desc),", example = "passTime,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "housrs", required = true, value = "时间段，用逗号隔开", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flag", value = "按天：day, 按周：week, 按月：month", required = true, example = "day", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "date", value = "日期/周/月份开", required = true, paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean queryGateEntranceCardByTimeDur(Pageable pageable, String housrs, String flag, String date) {
        try {
            Integer[] hs = new Integer[2];
            String[] ts = new String[2];
            if (housrs.contains(",")) {
                hs[0] = Integer.parseInt(housrs.split(",")[0]);
                hs[1] = Integer.parseInt(housrs.split(",")[1]);
            } else {
                hs[0] = Integer.parseInt(housrs);
                hs[1] = Integer.parseInt(housrs);
            }
            String times = getTimeDur(flag, date);
            if (times.contains(",")) {
                ts[0] = times.split(",")[0];
                ts[1] = times.split(",")[1];
            } else {
                ts[0] = times;
                ts[1] = times;
            }
            Page<GateEntranceGuardLog> result = gateEntranceGuardLogService.queryGateEntranceCardByTimeDur(pageable, ts, hs);
            return Result.QUERY_SUCCESS.toBean(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }


    @GetMapping(value = "/getAccessCardTimeUnit")
    @ApiOperation(value = "门禁卡时间段流量--默认时间颗粒度", httpMethod = "GET")
    @ResponseBody
    public ResponseBean getAccessCardTimeUnit() {
        try {
            int p = Configs.find(CfI.C_CENSUS_ACCESSCARD_TIMECOUNT_TIMEUNIT).getInt();
            Map<String, Object> map = new HashMap<>();
            map.put("timeUnit", p);
            return Result.QUERY_SUCCESS.toBean(map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }

    @GetMapping(value = "/nearly7daysByGate")
    @ApiOperation(value = "统计出入口近7天的出入量", httpMethod = "GET")
    @ResponseBody
    public ResponseBean nearly7daysByGate() {
        try {
           Map<String,List> map = gateEntranceGuardLogService.nearly7daysByGate();
            return Result.QUERY_SUCCESS.toBean(map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }

    @GetMapping(value = "/todayByGate")
    @ApiOperation(value = "统计今日的出入量", httpMethod = "GET")
    @ResponseBody
    public ResponseBean todayByGate() {
        try {
            Map<String,List> map = gateEntranceGuardLogService.todayByGate();
            return Result.QUERY_SUCCESS.toBean(map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }


}
