package com.xajiusuo.busi.entranceandexit.controller;

import com.xajiusuo.busi.entranceandexit.service.GateEntranceInOutService;
import com.xajiusuo.busi.entranceandexit.vo.PersonEarlyWarningVo;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.DateUtils;
import com.xajiusuo.jpa.util.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by shirenjing on 2019/6/21.
 */
@Slf4j
@Api(description = "出入口门禁采集信息进出表")
@RestController
@RequestMapping(value = "/api/village/gateEntranceInOut")
public class GateEntranceInOutController extends BaseController {

    @Autowired
    private GateEntranceInOutService gateEntranceInOutService;

    /**
     * 数据整合测试
     *
     * @return
     */
    @ApiOperation(value = "数据整合测试", httpMethod = "GET")
    @RequestMapping(value = "/text", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean text() {
//        gateEntranceInOutService.gateLogIntegration(); //出入口门禁
//        gateEntranceInOutService.buildLogIntegration();  //楼宇门禁

        gateEntranceInOutService.earlwarning();  //预警
        return getResponseBean(Result.QUERY_SUCCESS);
    }

    /**
     * @Author shirenjing
     * @Description
     * @Date 16:00 2019/6/26
     * @Param []
     * @return
     **/
    @ApiOperation(value = "测试预警昼伏夜出", httpMethod = "GET")
    @RequestMapping(value = "/textNightOut", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", required = true, value = "开始时间", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "endTime", required = true, value = "结束时间", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "hours", value = "时间范围，用逗号隔开,", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "count", value = "次数,", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flag", value = "0:出入口门禁 1：楼宇门禁,", paramType = "query", dataType = "int")
    })
    @ResponseBody
    public ResponseBean textNightOut(String startTime,String endTime,String hours,String count,int flag) {
        try{
            Integer[] hs = new Integer[2];
            if(hours.contains(",")){
                hs[0] = Integer.parseInt(hours.split(",")[0]);
                hs[1] = Integer.parseInt(hours.split(",")[1]);
            }
            List<PersonEarlyWarningVo> result = gateEntranceInOutService.queryWarnNightOut(DateUtils.parseDatetime(startTime),DateUtils.parseDatetime(endTime),Integer.parseInt(count),flag,hs);
            return getResponseBean(Result.QUERY_SUCCESS.setData(result));
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

}


