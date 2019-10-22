package com.xajiusuo.busi.buildingaccess.controller;

import com.xajiusuo.busi.buildingaccess.entity.BuildingEntranceGuardLog;
import com.xajiusuo.busi.buildingaccess.entity.ResultVo;
import com.xajiusuo.busi.buildingaccess.service.BuildingEntranceGuardLogService;
import com.xajiusuo.busi.entranceandexit.service.GateEntranceInOutService;
import com.xajiusuo.busi.villageMessage.entity.Building;
import com.xajiusuo.busi.villageMessage.entity.Unit;
import com.xajiusuo.busi.villageMessage.service.BuildingService;
import com.xajiusuo.busi.villageMessage.service.UnitService;
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
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections.map.LinkedMap;
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
@Api(description = "楼宇门禁采集信息")
@RestController
@RequestMapping(value = "/api/village/buildingEntranceGuardLog")
public class BuildingEntranceGuardLogController extends BaseController {

    @Autowired
    private BuildingEntranceGuardLogService buildingEntranceGuardLogService;
    @Autowired
    private BuildingService buildingService;
    @Autowired
    private GateEntranceInOutService gateEntranceInOutService;
    @Autowired
    private UnitService unitService;

    /**
     * 楼宇门禁采集信息列表查询
     *
     * @param page
     * @param buildingEntranceGuardLog
     * @return
     */
    @ApiOperation(value = "楼宇门禁采集信息列表查询", httpMethod = "GET")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean query(Pageable page, BuildingEntranceGuardLog buildingEntranceGuardLog) {
        page = transPage(page, new Sort(Sort.Direction.DESC, "passtime"), true);
        Page<BuildingEntranceGuardLog> ulist = buildingEntranceGuardLogService.query(buildingEntranceGuardLog, page);
        return Result.QUERY_SUCCESS.toBean(ulist);
    }

    /**
     * 根据时间统计楼栋的刷卡量
     *
     * @param times
     * @param flag
     * @return
     */
    @ApiOperation(value = "根据时间统计楼栋的刷卡量", httpMethod = "GET")
    @RequestMapping(value = "/tjBuildingEntranceGuard", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "times", required = false, value = "时间用,号分割", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flag", value = "按天：day, 按周：week, 按月：month", example = "day", paramType = "query", dataType = "string")

    })
    @ResponseBody
    public ResponseBean tjBuildingEntranceGuard(String times, String flag) {
        Map<String, List> resultMap = new HashMap<>();
        Map<String, List<ResultVo>> map = buildingEntranceGuardLogService.tjBuildingEntranceGuard(times, flag);
        if (map != null) {
            List date = new ArrayList<>(map.keySet());
            List buildingData = new ArrayList<>();
            List<Building> buildingList = buildingService.getAll();
            List buldingNum = new ArrayList<>();
            for (Building building : buildingList) {
                Map buildMap = new LinkedMap();
                buildMap.put("name", building.getBuildingname());
                buildMap.put("countArr", new int[date.size()]);
                buildingData.add(buildMap);
                Map numMap = new LinkedMap();
                numMap.put("name", building.getBuildingname());
                numMap.put("value", building.getBuildingno());
                buldingNum.add(numMap);
            }
            for (Object o : buildingData) {
                Map buildingDataMap = (Map) o;
                for (int i = 0; i < date.size(); i++) {
                    List<ResultVo> list = map.get(date.get(i).toString());
                    for (ResultVo resultVo : list) {
                        if (buildingDataMap.get("name").equals(resultVo.getName())) {
                            int[] count = (int[]) buildingDataMap.get("countArr");
                            count[i] = resultVo.getCount();
                        }
                    }
                }
            }
            resultMap.put("date", date);
            resultMap.put("buildingData", buildingData);
            resultMap.put("buldingNum", buldingNum);
        }
        return Result.QUERY_SUCCESS.toBean(resultMap);
    }

    /**
     * 统计楼栋下各个单元的刷卡量
     *
     * @param buildingno
     * @param time
     * @return
     */
    @ApiOperation(value = "统计楼栋下各个单元的刷卡量", httpMethod = "GET")
    @RequestMapping(value = "/tjBuildingEntranceGuardBydy", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "buildingno", required = false, value = "楼栋编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "time", required = false, value = "时间", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flag", value = "按天：day, 按周：week, 按月：month", example = "day", paramType = "query", dataType = "string")

    })
    @ResponseBody
    public ResponseBean tjBuildingEntranceGuardBydy(String buildingno, String time, String flag) {
        return Result.QUERY_SUCCESS.toBean(buildingEntranceGuardLogService.tjBuildingEntranceGuardBydy(buildingno, time, flag));
    }

    /**
     * 楼宇门禁时间段流量统计
     *
     * @param pill
     * @param date
     * @return
     */
    @GetMapping(value = "/buildingEntranceGuardTimeDur")
    @ApiOperation(value = "楼宇门禁时间段流量统计", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pill", value = "小时颗粒度", required = true, example = "2", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flag", value = "按天：day, 按周：week, 按月：month", example = "day", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "date", value = "日期/周/月份", required = false, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "unitno", value = "单元编号", required = false, paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean buildingEntranceGuardTimeDur(String pill, String flag, String date, String unitno) {
        try {
            int p = Configs.find(CfI.C_CENSUS_ACCESSCARD_TIMECOUNT_TIMEUNIT).getInt();
            if (CommonUtils.isNotEmpty(pill)) p = Integer.parseInt(pill);
            List<Map<String, Object>> result = buildingEntranceGuardLogService.buildingEntranceGuardTimeDur(p, date, flag, unitno);
            return Result.QUERY_SUCCESS.toBean(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }

    /**
     * 楼宇门禁时间段流量详单
     *
     * @param pageable
     * @param housrs
     * @param flag
     * @param date
     * @return
     */
    @GetMapping(value = "/queryBuildingEntranceGuardByTimeDur")
    @ApiOperation(value = "楼宇门禁时间段流量详单", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为a.passTime,desc),", example = "a.passTime,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "housrs", required = true, value = "时间段，用逗号隔开", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flag", value = "按天：day, 按周：week, 按月：month", required = true, example = "day", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "date", value = "日期/周/月份开", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "unitno", required = true, value = "单元编号", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean queryBuildingEntranceGuardByTimeDur(Pageable pageable, String housrs, String flag, String date, String unitno) {
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
            Page<BuildingEntranceGuardLog> result = buildingEntranceGuardLogService.queryBuildingEntranceGuardByTimeDur(pageable, date, hs, flag, unitno);
            return Result.QUERY_SUCCESS.toBean(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }


    @GetMapping(value = "/countNightoutForBEG")
    @ApiOperation(value = "楼宇门禁昼伏夜出放统计", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "time", required = false, value = "时间用,号分割", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean countNightoutForBEG(String time) {
        try {
            List<Map> resultMap = new ArrayList<>();
            List result = gateEntranceInOutService.countNightoutForBEG(time);
            for (Object o : result) {
                Object[] oo = (Object[]) o;
                Map map = new HashMap<>();
                map.put("cardno", oo[0] + "," + (null == oo[2] ? "" : oo[2]));
                map.put("nums", oo[1]);
                resultMap.add(map);
            }
            return Result.QUERY_SUCCESS.toBean(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }

    @GetMapping(value = "/getDefaultNumber")
    @ApiOperation(value = "获取默认的楼宇和单元编号", httpMethod = "GET")
    @ResponseBody
    public ResponseBean getDefaultNumber() {
        try {
            return Result.QUERY_SUCCESS.toBean(buildingEntranceGuardLogService.getDefaultNumber());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.QUERY_FAIL.toBean();
    }

}
