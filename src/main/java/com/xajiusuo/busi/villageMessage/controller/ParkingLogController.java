package com.xajiusuo.busi.villageMessage.controller;

import com.xajiusuo.busi.diction.service.DictionService;
import com.xajiusuo.busi.log.service.LogSysService;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleInfo;
import com.xajiusuo.busi.villageMessage.entity.ParkingLog;
import com.xajiusuo.busi.villageMessage.service.ParkingLogService;
import com.xajiusuo.busi.villageMessage.untilmodel.ModelExcel;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.down.FileContentType;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.utils.CfI;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:31
 * @Description
 */
@Api(description = "车位租赁流水信息")
@RestController
@RequestMapping(value ="/api/village/parkingLog")
public class ParkingLogController extends BaseController{
    /**
     *@Description:增加日志
     *@Author: gaoyong
     *@date: 19-6-10 下午5:35
    */
    private final Logger log = LoggerFactory.getLogger(ParkingLogController.class);

    private String dateFormat = "yyyy-MM-dd";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    @Autowired
    private LogSysService logSysService;

    @Autowired
    private DictionService dictionService;

    @Autowired
    private ParkingLogService parkingLogService;

    @ApiOperation(value = "车位租赁流水基本信息分页展示及查询")
    @RequestMapping(value = "/pageParkingLog", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean pageParkingLog(Pageable pageable, String parkingno,String plateno) {
        try {
            MotorVehicleInfo motorVehicleInfo = new MotorVehicleInfo();
            motorVehicleInfo.setPlateNo(plateno);

            ParkingLog parkingLog = new ParkingLog();
            parkingLog.setParkingno(parkingno);
            parkingLog.setPlateno(motorVehicleInfo);
            Page<ParkingLog> page = this.parkingLogService.getsqlpage(pageable,parkingLog);
            return Result.OPERATE_SUCCESS.toBean(page);
        }catch (Exception e){
            e.printStackTrace();
            return Result.OPERATE_FAIL.toBean();
        }
    }

    @ApiOperation(value = "车位租赁基本信息新增/修改")
    @RequestMapping(value = "/addEditParkingLog", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean addEditParkingLog(HttpServletRequest request,String id,String parkingno,String plateno,String startTime,String endTime){
        try {
            MotorVehicleInfo motorVehicleInfo = new MotorVehicleInfo();
            motorVehicleInfo.setPlateNo(plateno);

            ParkingLog parkingLog = new ParkingLog();
            parkingLog.setId(id);
            parkingLog.setParkingno(parkingno);
            parkingLog.setPlateno(motorVehicleInfo);
            parkingLog.setStarttime(simpleDateFormat.parse(startTime));
            parkingLog.setEndtime(simpleDateFormat.parse(endTime));
            if(this.parkingLogService.chekBs(parkingLog.getParkingno(),parkingLog.getStarttime(),parkingLog.getEndtime())>0L){
                return getResponseBean(Result.find(CfI.R_PARKINGLOG_PARKINGNO_CHECK_FAIL));
            }
            if(!"".equals(parkingLog.getId()) && null!= parkingLog.getId()){
            }else{
                parkingLog.setId(null);
            }

            this.parkingLogService.saveUpdate(parkingLog);
            return Result.OPERATE_SUCCESS.toBean();
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.OPERATE_FAIL.toBean();
        }

    }

    @ApiOperation(value ="车位租赁基本信息删除")
    @RequestMapping(value="/delParkingLog", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value="车位租赁流水信息id", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean delParkingLog(String id){
        try{
            this.parkingLogService.delete(id);
            return Result.DELETE_SUCCESS.toBean();
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.DELETE_FAIL.toBean();
        }
    }

    @ApiOperation(value = "车位信息基本信息查看")
    @RequestMapping(value = "/viewParkingLog", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id" , required = true, value = "车位租赁流水信息id", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean viewParkingLog(String id){
        try{
            ParkingLog parkingLog = this.parkingLogService.getOne(id);
            log.info("车位租赁信息基本信息查看");
            return Result.OPERATE_SUCCESS.toBean(parkingLog);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.OPERATE_FAIL.toBean();
        }
    }

    @Deprecated
    @ApiOperation(value = "车位租赁信息模板导入模板下载")
    @RequestMapping(value = "downModelExcel", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean downModelExcel(HttpServletRequest request){
        ModelExcel modelExcel = new ModelExcel();
        try {
        String fileName = "车位租赁导入模板(标*的列为必填列).xls"; //模板名称
        String[] handers = {"车位编号*","车牌号*","开始时间(2012-01-01)","到期时间(2012-11-01)"}; //列标题
        List<String[]> downData = new ArrayList();
        String [] downRows = {};
                modelExcel.createExcelTemplate(fileName, handers, downData, downRows);
                fileDownOrShow(fileName, FileContentType.down,new FileInputStream(new File(fileName)));
                if(new File(fileName).exists()) new File(fileName).delete();
                log.info("下载模板");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.OPERATE_FAIL.toBean();
        }
        return Result.OPERATE_SUCCESS.toBean();
    }

    @Deprecated
    @ApiOperation(value = "车位信息导入")
    @RequestMapping(value = "uploadParkingLogExcel",method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean uploadParkingLogExcel(@RequestParam(name = "file",required=true) MultipartFile file){
        try{
            System.out.println(file.getOriginalFilename());
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.NOPOWER_FAIL.toBean();
        }
        return Result.OPERATE_SUCCESS.toBean();
    }


}
