package com.xajiusuo.busi.motorVehicle.controller;

import com.xajiusuo.busi.motorVehicle.entity.*;
import com.xajiusuo.busi.motorVehicle.service.MotorVehicleInOutService;
import com.xajiusuo.busi.motorVehicle.service.MotorVehicleLogService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by shirenjing on 2019/6/6.
 */
@Slf4j
@Api(description = "机动车（停车场）采集信息管理")
@RestController
@RequestMapping(value = "/api/village/MotorVehicleLog")
public class MotorVehicleLogController extends BaseController {

    @Autowired
    private MotorVehicleLogService motorVehicleLogService;
    @Autowired
    private MotorVehicleInOutService motorVehicleInOutService;

    /**
     * @Author shirenjing
     * @Description 机动车采集信息列表
     * @Date 15:19 2019/6/6
     * @Param [pageable, motorVehicleLogVo]
     * @return
     **/
    @GetMapping(value = "/queryLogList")
    @ApiOperation(value = "机动车采集信息列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为passTime,desc),", example = "passTime,desc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean queryLogList(Pageable pageable, MotorVehicleLogVo motorVehicleLogVo) {
        pageable = transPage(pageable,new Sort(Sort.Direction.DESC, "lastModifyTime"),true);
        try {
            Page<MotorVehicleLog> page = motorVehicleLogService.queryPageLog(pageable,motorVehicleLogVo);
            return getResponseBean(Result.QUERY_SUCCESS.setData(page));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @Author shirenjing
     * @Description
     * @Date 17:36 2019/6/6
     * @Param [motorVehicleLogVo]
     * @return
     **/
    @GetMapping(value = "/countAllVehicle")
    @ApiOperation(value = "统计车辆总数和总出入次数", httpMethod = "GET")
    @ResponseBody
    public ResponseBean countAllVehicle(MotorVehicleLogVo motorVehicleLogVo) {
        try {
            Map<String, Object> result = motorVehicleLogService.countAllVehicle(motorVehicleLogVo);
            return getResponseBean(Result.QUERY_SUCCESS.setData(result));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }


    /**
     * @Author shirenjing
     * @Description
     * @Date 17:57 2019/6/6
     * @Param [motorVehicleLogVo]
     * @return
     **/
    @GetMapping(value = "/countVehicleGoCome")
    @ApiOperation(value = "机动车出入流量统计", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flag", value = "按天：day, 按周：week, 按月：month", example = "day", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean countVehicleGoCome(String flag,MotorVehicleLogVo motorVehicleLogVo) {
        try {
            Map<String,List> map = new HashMap<>();
            List<MotorVehicleCountVo> result = null;
            if("day".equals(flag)) result = motorVehicleLogService.countGoComeDay(motorVehicleLogVo);
            if("week".equals(flag)) result = motorVehicleLogService.countGoComeWeek(motorVehicleLogVo);
            if("month".equals(flag)) result = motorVehicleLogService.countGoComeMonth(motorVehicleLogVo);
            if(result!=null && result.size()>0){
                List date = new ArrayList<>();
                List dataRange = new ArrayList<>();
                List inNums = new ArrayList<>();
                List outNums = new ArrayList<>();
                for (MotorVehicleCountVo vo:result) {
                    if("0".equals(vo.getPassType())){
                        date.add(vo.getDays());
                        dataRange.add(getTimeDur(flag,vo.getDays()));
                        outNums.add(vo.getNums());
                    }else{
                        inNums.add(vo.getNums());
                    }

                }
                map.put("date",date);
                map.put("dataRange",dataRange);
                map.put("inNums",inNums);
                map.put("outNums",outNums);
            }
            return getResponseBean(Result.QUERY_SUCCESS.setData(map));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    public String getTimeDur(String flag,String date){
        String result = "";
        if("day".equals(flag)){
            result = date+" 00:00:00"+","+date+" 23:59:59";
        }
        if("month".equals(flag)){
            String start = date+"-01";
            String end = DateUtils.dateAddd2s(DateUtils.addMonth(DateUtils.parse(start,"yyyy-MM-dd"),1),-1);
            result = start+" 00:00:00"+","+end+" 23:59:59";
        }
        if("week".equals(flag)){
            String[] arr = date.split("-");
            int year = Integer.parseInt(arr[0]);
            int w = Integer.parseInt(arr[1]);
            String start = DateTools.getStartDayOfWeekNo(year,w);
            String end = DateTools.getEndDayOfWeekNo(year,w);
            result = start+" 00:00:00"+","+end+" 23:59:59";
        }
        return result;
    }


    /**
     * @Author shirenjing
     * @Description
     * @Date 17:57 2019/6/6
     * @Param [motorVehicleLogVo]
     * @return
     **/
    @GetMapping(value = "/countVehicleGoComeTimeDur1")
    @ApiOperation(value = "机动车时间段流量统计(已废弃)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pill", value = "小时颗粒度", required = false, example = "2", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "times", value = "时间范围，用逗号隔开", required = false, paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean countVehicleGoComeTimeDur1(String pill,String times) {
        int p = Configs.find(CfI.C_CENSUS_MOTORVEEHICLE_TIMECOUNT_TIMEUNIT).getInt();
        if(CommonUtils.isNotEmpty(pill)) p = Integer.parseInt(pill);
        try {
            String[] timeArr = null;
            if(times!=null && !"".equals(times)){
                timeArr = times.split(",");
            }else{
                timeArr = new String[]{DateUtils.currentDatetime()};
            }
            List<Map<String,Object>> result = motorVehicleLogService.countGoComeHour(p,timeArr);
            return getResponseBean(Result.QUERY_SUCCESS.setData(result));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @Author shirenjing
     * @Description
     * @Date 17:57 2019/6/6
     * @Param [motorVehicleLogVo]
     * @return
     **/
    @GetMapping(value = "/countVehicleGoComeTimeDur")
    @ApiOperation(value = "机动车时间段流量统计", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pill", value = "小时颗粒度", required = false, example = "2", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flag", value = "按天：day, 按周：week, 按月：month", example = "day", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "date", value = "日期/周/月份", required = false, paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean countVehicleGoComeTimeDur(String pill,String flag, String date) {
        int p = Configs.find(CfI.C_CENSUS_MOTORVEEHICLE_TIMECOUNT_TIMEUNIT).getInt();
        if(CommonUtils.isNotEmpty(pill)) p = Integer.parseInt(pill);
        try {
            String[] timeArr = null;
            if(date!=null && !"".equals(date)){
                String times = getTimeDur(flag,date);
                timeArr = times.split(",");
            }
            List<Map<String,Object>> result = motorVehicleLogService.countGoComeHour(p,timeArr);
            return getResponseBean(Result.QUERY_SUCCESS.setData(result));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @GetMapping(value = "/getDefaultTimeUnit")
    @ApiOperation(value = "机动车时间段流量--默认时间颗粒度", httpMethod = "GET")
    @ResponseBody
    public ResponseBean getDefaultTimeUnit() {
        try {
            int p = Configs.find(CfI.C_CENSUS_MOTORVEEHICLE_TIMECOUNT_TIMEUNIT).getInt();
            Map<String,Object> map = new HashMap<>();
            map.put("timeUnit",p);
            return getResponseBean(Result.QUERY_SUCCESS.setData(map));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @Author shirenjing
     * @Description
     * @Date 15:58 2019/6/14
     * @Param [housrs, times]
     * @return
     **/
    @GetMapping(value = "/queryLogByTimeDur")
    @ApiOperation(value = "机动车时间段流量详单", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为passTime,desc),", example = "passTime,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "housrs", required = true, value = "时间段，用逗号隔开", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flag", value = "按天：day, 按周：week, 按月：month", required = true, example = "day", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "date", value = "日期/周/月份开", required = true, paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean queryLogByTimeDur(Pageable pageable,String housrs,String flag,String date) {
        try {
            Integer[] hs = new Integer[2];
            String[] ts = new String[2];
            if(housrs.contains(",")){
                hs[0] = Integer.parseInt(housrs.split(",")[0]);
                hs[1] = Integer.parseInt(housrs.split(",")[1]);
            }else{
                hs[0] = Integer.parseInt(housrs);
                hs[1] = Integer.parseInt(housrs);
            }
            String times = getTimeDur(flag,date);
            if(times.contains(",")){
                ts[0] = times.split(",")[0];
                ts[1] = times.split(",")[1];
            }else{
                ts[0] = times;
                ts[1] = times;
            }
            Page<MotorVehicleLog> result = motorVehicleLogService.queryLogPageByHours(pageable,ts,hs);
            return getResponseBean(Result.QUERY_SUCCESS.setData(result));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @Author shirenjing
     * @Description
     * @Date 15:58 2019/6/14
     * @Param [housrs, times]
     * @return
     **/
    @GetMapping(value = "/queryLogByTimeDur1")
    @ApiOperation(value = "机动车时间段流量详单(已废弃)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "housrs", required = true, value = "时间段，用逗号隔开", example = "passTime,desc", paramType = "query", dataType = "integer"),
            @ApiImplicitParam(name = "times", required = true, value = "时间范围，用逗号隔开", example = "passTime,desc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean queryLogByTimeDur1(String housrs,String times) {
        try {
            Integer[] hs = new Integer[2];
            String[] ts = new String[2];
            if(housrs.contains(",")){
                hs[0] = Integer.parseInt(housrs.split(",")[0]);
                hs[1] = Integer.parseInt(housrs.split(",")[1]);
            }else{
                hs[0] = Integer.parseInt(housrs);
                hs[1] = Integer.parseInt(housrs);
            }
            if(times.contains(",")){
                ts[0] = times.split(",")[0];
                ts[1] = times.split(",")[1];
            }else{
                ts[0] = times;
                ts[1] = times;
            }
            List<MotorVehicleLog> result = motorVehicleLogService.queryLogByHours(ts,hs);
            return getResponseBean(Result.QUERY_SUCCESS.setData(result));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @Author shirenjing
     * @Description 
     * @Date 16:31 2019/6/17
     * @Param [motorVehicleLogVo]
     * @return
     **/
    @GetMapping(value = "/countLongtimestop")
    @ApiOperation(value = "机动车长时间停放统计", httpMethod = "GET")
    @ResponseBody
    public ResponseBean countLongtimestop(MotorVehicleLogVo motorVehicleLogVo) {
        try {
            List<MotorVehicleCountVo> result = motorVehicleInOutService.queryLongtimestop(motorVehicleLogVo);
            return getResponseBean(Result.QUERY_SUCCESS.setData(result));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @Author shirenjing
     * @Description 
     * @Date 16:53 2019/6/17
     * @Param [motorVehicleLogVo]
     * @return
     **/
    @GetMapping(value = "/countNightout")
    @ApiOperation(value = "机动车昼伏夜出放统计", httpMethod = "GET")
    @ResponseBody
    public ResponseBean countNightout(MotorVehicleLogVo motorVehicleLogVo) {
        try {
            List<MotorVehicleCountVo> result = motorVehicleInOutService.queyrNightOut(motorVehicleLogVo);
            return getResponseBean(Result.QUERY_SUCCESS.setData(result));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @Author liujiankang
     * @Description 机动车长时间停放查询
     * @Date 10:19 2019/6/17
     * @Param [plateNo, startTime,endTime,timeSpan]
     * @return
     **/
    @GetMapping(value = "/queryByLongtime")
    @ApiOperation(value = "机动车长时间停放查询", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "plateNo", required = false, value = "车牌号",  paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "dateTime", required = false, value = "数据时间",  paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "timeSpan",required = true, value = "时长" ,paramType = "query", dataType = "int")
    })
    @ResponseBody
    public ResponseBean queryByLongtime(Pageable pageable, String plateNo,String dateTime,int timeSpan) {
        if(pageable.getSort().isEmpty()){
            pageable.getSort().and(new Sort(Sort.Direction.DESC, "stoptime"));
        }
        try {
            Page<MotorVehicleInOut> page = motorVehicleInOutService.queryPageByLongtime(pageable,plateNo,dateTime,timeSpan);
            return getResponseBean(Result.QUERY_SUCCESS.setData(page));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }



    /**
     * @Author liujiankang
     * @Description 机动车频繁出入查询
     * @Date 10:19 2019/6/17
     * @Param [plateNo,startDate,endDate,startTime,endTime,times]
     * @return
     **/
    @GetMapping(value = "/queryPageBytimes")
    @ApiOperation(value = "机动车频繁出入查询", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(times,desc),", example = "times,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "plateNo", required = false, value = "车牌号",  paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "dateTime", required = false, value = "数据时间",  paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "startTime", required = true, value = "数据结束时间",  paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "endTime", required = true, value = "数据结束时间",  paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "times",required = true, value = "次数" ,paramType = "query", dataType = "int")
    })
    @ResponseBody
    public ResponseBean queryPageBytimes(Pageable pageable, String plateNo,String dateTime,String startTime,String endTime,int times) {

        if(pageable.getSort().isEmpty()){
            pageable.getSort().and(new Sort(Sort.Direction.DESC, "times"));
        }
        try {
            Page<InOutNewVo> page = motorVehicleInOutService.queryPageBytimes(pageable,plateNo,dateTime,startTime,endTime,times);
            return getResponseBean(Result.QUERY_SUCCESS.setData(page));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }


    /**
     * @Author liujiankang
     * @Description 机动车昼伏夜出查询
     * @Date 10:19 2019/6/18
     * @Param [plateNo,startDate,endDate,startTime,endTime,times]
     * @return
     **/
    @GetMapping(value = "/queryPageByMoringAndNight")
    @ApiOperation(value = "机动车昼伏夜出查询", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(times,desc),", example = "times,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "plateNo", required = false, value = "车牌号",  paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "dateTime", required = false, value = "数据时间",  paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "startTime", required = true, value = "数据结束时间",  paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "endTime", required = true, value = "数据结束时间",  paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "times",required = true, value = "次数" ,paramType = "query", dataType = "int")
    })
    @ResponseBody
    public ResponseBean queryPageByMoringAndNight(Pageable pageable, String plateNo,String dateTime,String startTime,String endTime,int times) {

        if(pageable.getSort().isEmpty()){
            pageable.getSort().and(new Sort(Sort.Direction.DESC, "times"));
        }
        try {
            Page<InOutNewVo> page = motorVehicleInOutService.queryPageByMoringAndNight(pageable,plateNo,dateTime,startTime,endTime,times);
            return getResponseBean(Result.QUERY_SUCCESS.setData(page));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }


    /**
     * @Author liujiankang
     * @Description 根据ids查询出入列表
     * @Date 10:19 2019/6/18
     * @Param [plateNo,startDate,endDate,startTime,endTime,times]
     * @return
     **/
    @GetMapping(value = "/queryPageByids")
    @ApiOperation(value = "根据ids查询出入列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(indata,desc),", example = "indata,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "ids", required = true, value = "ids",  paramType = "query", dataType = "string")

    })
    @ResponseBody
    public ResponseBean queryPageByids(Pageable pageable, String ids) {

        if(pageable.getSort().isEmpty()){
            pageable.getSort().and(new Sort(Sort.Direction.DESC, "indata"));
        }
        try {
            Page<MotorVehicleInOut> page = motorVehicleInOutService.queryPageByIds(pageable,ids);
            return getResponseBean(Result.QUERY_SUCCESS.setData(page));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }


    /**
     * @Author shirenjing
     * @Description
     * @Date 16:31 2019/6/17
     * @Param [motorVehicleLogVo]
     * @return
     **/
    @GetMapping(value = "/countGoCome7Day")
    @ApiOperation(value = "近七天机动车出入统计", httpMethod = "GET")
    @ResponseBody
    public ResponseBean countGoCome7Day() {
        try {
            Map<String,List> map = new HashMap<>();
            String endDate = DateUtils.format(new Date(),"yyyy-MM-dd");
            String startDate = DateUtils.dateAdds2s(endDate,-6);
            List<MotorVehicleCountVo> result = motorVehicleLogService.countGoCome7Day(startDate,endDate);
            if(result!=null && result.size()>0){
                List date = new ArrayList<>();
//                List dataRange = new ArrayList<>();
                List inNums = new ArrayList<>();
                List outNums = new ArrayList<>();
                for (MotorVehicleCountVo vo:result) {
                    if("0".equals(vo.getPassType())){
                        date.add(vo.getDays());
//                        dataRange.add(getTimeDur("day",vo.getDays()));
                        outNums.add(vo.getNums());
                    }else{
                        inNums.add(vo.getNums());
                    }

                }
                map.put("date",date);
//                map.put("dataRange",dataRange);
                map.put("inNums",inNums);
                map.put("outNums",outNums);
            }
            return getResponseBean(Result.QUERY_SUCCESS.setData(map));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }


    @GetMapping(value = "/countGoComeCurrent")
    @ApiOperation(value = "今日安防-机动车出入总量", httpMethod = "GET")
    @ResponseBody
    public ResponseBean countGoComeCurrent() {
        try {
            Map<String,List> map = new HashMap<>();
            String endDate = DateUtils.format(new Date(),"yyyy-MM-dd");
            List<MotorVehicleCountVo> result = motorVehicleLogService.countGoCome7Day(endDate,endDate);
            if(result!=null && result.size()>0){
                List date = new ArrayList<>();
//                List dataRange = new ArrayList<>();
                List inNums = new ArrayList<>();
                List outNums = new ArrayList<>();
                for (MotorVehicleCountVo vo:result) {
                    if("0".equals(vo.getPassType())){
                        date.add(vo.getDays());
//                        dataRange.add(getTimeDur("day",vo.getDays()));
                        outNums.add(vo.getNums());
                    }else{
                        inNums.add(vo.getNums());
                    }
                }
                map.put("date",date);
//                map.put("dataRange",dataRange);
                map.put("inNums",inNums);
                map.put("outNums",outNums);
            }
            return getResponseBean(Result.QUERY_SUCCESS.setData(map));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }






}
