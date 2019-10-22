package com.xajiusuo.busi.wifilog.controller;

import com.xajiusuo.busi.wifilog.entity.WifiCountVo;
import com.xajiusuo.busi.wifilog.entity.WifiLog;
import com.xajiusuo.busi.wifilog.service.WifiLogService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.util.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.xajiusuo.jpa.param.e.Result;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by liujiankang on 19-6-10.
 */
@Api(description = "Wifi探针采集信息管理")
@RestController
@RequestMapping(value = "/api/village/MotorVehicleLog")
public class WifiLogController extends BaseController {

    @Autowired
    private WifiLogService wifiLogService;

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @GetMapping(value = "/queryWifiList")
    @ApiOperation(value = "Wifi探针采集信息列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(PASSTIME,desc),", example = "PASSTIME,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "sbbm",required = false, value = "设备编码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "ipAddress",required = false, value = "IP地址", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "placecode",required = false, value = "安装地点行政区划代码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "orgcode",required = false, value = "管辖单位代码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "dateTime",required = false, value = "数据时间", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean queryInfoList(Pageable pageable,String sbbm,String ipAddress,String placecode,String orgcode ,String dateTime) {
        pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(Sort.Direction.DESC, "PASSTIME"));
        try {
            Page<WifiLog> page = wifiLogService.queryPageLog(pageable,sbbm,ipAddress,placecode,orgcode,dateTime);
            return getResponseBean(Result.QUERY_SUCCESS.setData(page));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @GetMapping(value = "/countByMac")
    @ApiOperation(value = "wifi探针统计Top 10 按MAC次数", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", required = false, value = "数据开始时间", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "endTime",required = false, value = "数据结束时间", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean countByMac(String startTime,String endTime) {

        if(null==startTime||startTime==""||startTime.equals("null")){
            startTime="1970-01-01 01:01:01";
            endTime=format.format(new Date());
        }
        try {
            List<WifiCountVo> list = wifiLogService.countByMac(startTime,endTime);
            return getResponseBean(Result.QUERY_SUCCESS.setData(list));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @GetMapping(value = "/countByApe")
    @ApiOperation(value = "wifi探针统计Top 10 按设备采集数量", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", required = false, value = "数据开始时间", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "endTime",required = false, value = "数据结束时间", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean countByApe(String startTime,String endTime) {

        if(null==startTime||startTime==""||startTime.equals("null")){
            startTime="1970-01-01 01:01:01";
            endTime=format.format(new Date());
        }
        try {
            List<WifiCountVo> list = wifiLogService.countByApe(startTime,endTime);
            return getResponseBean(Result.QUERY_SUCCESS.setData(list));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

}
