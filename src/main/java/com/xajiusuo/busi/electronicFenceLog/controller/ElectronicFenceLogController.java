package com.xajiusuo.busi.electronicFenceLog.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xajiusuo.busi.electronicFenceLog.entity.ElectronicFenceLog;
import com.xajiusuo.busi.electronicFenceLog.service.ElectronicFenceLogService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author GaoYong
 * @Date 19-6-12 上午11:58
 * @Description
 */
@Api(description = "周界报警信息系统")
@RestController
@RequestMapping(value ="/api/village/electronicfencelog")
public class ElectronicFenceLogController extends BaseController {
    private final Logger log = LoggerFactory.getLogger(ElectronicFenceLogController.class);

    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private ElectronicFenceLogService electronicFenceLogService;

    @ApiOperation(value = "周界报警信息按时间段查询")
    @RequestMapping(value = "/pageElectronicfencelog" ,method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "villageid", required = false, value = "小区编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name ="starttime", required = false, value ="开始时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "endtime", required = false,value = "结束时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "page", required =true, value = "当前页面,从1开始", allowableValues = "range[0,infinity]", paramType = "query", dataType= "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1,infinity]", paramType= "query", dataType= "int"),
            @ApiImplicitParam(name = "sort",value = "排序说明(默认id.asc)", example = "id,asc" ,paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean pageElectronicfencelog(Pageable pageable,ElectronicFenceLog electronicFenceLog, String locname, String starttime, String endtime){
        Page<ElectronicFenceLog> page = null;
        try{
            String sql = "select  a.id,a.villageid,a.villagename,a.loc,a.type,a.alert_date,a.deviceid,a.display_flag,b.longitude,b.latitude,b.createtime as createdate,a.alertcount  from t_electronicfencelog_19 a, T_DEVICE_20 b where 1=1 and a.deviceid = b.apeid and b.delflag =0 ";

            if(null != electronicFenceLog.getVillageid() && !"".equals(electronicFenceLog.getVillageid())){
                sql += " and b.villageid = '"+electronicFenceLog.getVillageid()+"'";
            }
            if(null != electronicFenceLog.getLoc() && !"".equals(electronicFenceLog.getLoc())){
                sql += " and a.loc like '%"+electronicFenceLog.getLoc()+"%' ";
            }
            if(null != locname && !"".equals(locname)){
                sql += " and b.name like '%"+locname+"%' ";
            }
            if(null != starttime && !"" .equals(starttime)){

                sql += " and to_char(a.alert_date,'"+dateFormat+"') >= '"+starttime+"' ";
            }
            if(null != endtime && !"".equals(endtime)){
                sql += " and to_char(a.alert_date,'"+dateFormat+"') <= '" +endtime+"' " ;
            }
            page = this.electronicFenceLogService.executeNativeQuerySqlForPage(pageable,sql, null);
            log.info("电子栅栏报警信息分页查询");
        }catch(Exception e){
            e.printStackTrace();
            getResponseBean(Result.OPERATE_FAIL);
            log.error(e.getMessage());
        }
        return getResponseBean(Result.OPERATE_SUCCESS.setData(page));
    }

    @ApiOperation(value = "周界报警信息给定时间内按区域统计报警次数")
    @RequestMapping(value ="/countElectronicfencelog", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name ="villageid", required = false, value = "小区编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "starttime", required = true, value = "开始时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "endtime", required = true, value = "结束时间(yyyy-MM-dd HH:mm:ss)", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean countElectronicfencelog(String villageid, String starttime, String endtime){
//        String sql = "select  a.id,a.villageid,a.villagename,a.loc,a.type,a.alert_date,a.deviceid,a.display_flag,b.longitude,b.latitude,b.createtime as createdate,a.alertcount,b.region  from t_electronicfencelog_19 a, T_DEVICE_20 b where 1=1 and a.villageid = '"+villageid+"' and a.deviceid = b.apeid and b.delflag =0 ";
        String sql = "select  a.id,a.villageid,a.villagename,a.loc,a.type,a.alert_date,a.deviceid,b.name,a.display_flag,b.longitude,b.latitude,b.createtime as createdate,a.alertcount  from t_electronicfencelog_19 a, T_DEVICE_20 b where 1=1 and a.villageid = '"+villageid+"' and a.deviceid = b.apeid  and b.delflag =0 ";
        JSONArray result = null;
        try{
            String sqlResult = "select deviceid,name,count(*) from ( "+sql+" ) where 1=1 ";
            if(null != starttime && !"".equals(starttime)){
                sqlResult += " and to_char(alert_date,'"+dateFormat+"') >= '"+starttime+"' ";
            }
            if(null != endtime && !"".equals(endtime)){
                sqlResult += " and to_char(alert_date,'"+dateFormat+"') <= '"+endtime+"' ";
            }
            sqlResult += " group by deviceid,name";
            result =  this.electronicFenceLogService.countElectronicfencelog(sqlResult);
            log.info("给定时间内按区域统计报警次数");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
        return getResponseBean(Result.OPERATE_SUCCESS.setData(result));
    }

    @ApiOperation(value = "周界报警信息给定时间某个区域,不同时间颗粒的统计(月周天小时)")
    @RequestMapping(value = "countRegionTime", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "villageid", required = true, value = "小区编号", paramType= "query", dataType="String"),
            @ApiImplicitParam(name = "starttime", required = true, value = "开始时间(yyyy-MM-dd HH:mm:ss)", paramType= "query", dataType = "string"),
            @ApiImplicitParam(name = "endtime", required = true, value ="结束时间(yyyy-MM-dd HH:mm:ss)", paramType ="query", dataType = "string"),
            @ApiImplicitParam(name = "deviceid", required = true, value = "设备ID", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "timeBs", required = true, value = "时间标识(yrzx)", paramType="query", dataType="string"),
            @ApiImplicitParam(name = "timePill", required = false, value = "(暂时是一个小时,前台不许要传已经默认)小时时选择时间颗粒1-24", allowableValues = "range[1,24]" ,paramType = "query", dataType = "int")

    })
    @ResponseBody
    public ResponseBean countRegionTime(String villageid, String starttime, String endtime, String deviceid,String timeBs, String timePill){
        String _timePill = "1";
        if(null !=timePill && !"".equals(timePill)) _timePill = timePill;
//        String sql = "select  a.id,a.villageid,a.villagename,a.loc,a.type,a.alert_date,a.deviceid,a.display_flag,b.longitude,b.latitude,b.createtime as createdate,a.alertcount,b.region  from t_electronicfencelog_19 a, T_DEVICE_20 b where 1=1 and a.villageid = '"+villageid+"' and b.region = '"+region+"' and a.deviceid = b.apeid and b.delflag =0 ";
        String sql = "select  a.id,a.villageid,a.villagename,a.loc,a.type,a.alert_date,a.deviceid,a.display_flag,b.longitude,b.latitude,b.createtime as createdate,a.alertcount  from t_electronicfencelog_19 a, T_DEVICE_20 b where 1=1 and a.villageid = '"+villageid+"' and a.deviceid = '"+deviceid+"' and a.deviceid = b.apeid and b.delflag =0 ";
        JSONObject result = null;
        String sqlResult  = "select * from  ("+sql+") where 1=1";
        try{
            if(null !=starttime && !"".equals(starttime)){
                sqlResult += " and to_char(alert_date,'"+dateFormat+"') >= '"+starttime+"'";
            }
            if(null != endtime && !"".equals(endtime)){
                sqlResult += " and to_char(alert_date,'"+dateFormat+"') <= '"+endtime+"'";
            }
            result = this.electronicFenceLogService.countRegionTime(sqlResult,timeBs,_timePill);
            log.info("按照时间颗粒度统计");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return getResponseBean(Result.OPERATE_SUCCESS.setData(result));
    }




}
