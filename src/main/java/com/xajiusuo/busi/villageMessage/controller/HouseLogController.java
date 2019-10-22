package com.xajiusuo.busi.villageMessage.controller;

import com.xajiusuo.busi.diction.service.DictionService;
import com.xajiusuo.busi.log.service.LogSysService;
import com.xajiusuo.busi.villageMessage.entity.House;
import com.xajiusuo.busi.villageMessage.entity.HouseLog;
import com.xajiusuo.busi.villageMessage.entity.Owner;
import com.xajiusuo.busi.villageMessage.service.HouseLogService;
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
@Api(description = "房屋租赁流水信息")
@RestController
@RequestMapping(value ="/api/village/houseLog")
public class HouseLogController extends BaseController{
    /**
     *@Description:增加日志
     *@Author: gaoyong
     *@date: 19-6-10 下午5:35
    */
    private final Logger log = LoggerFactory.getLogger(HouseLogController.class);

    private String dateFormat = "yyyy-MM-dd";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    @Autowired
    private LogSysService logSysService;

    @Autowired
    private DictionService dictionService;

    @Autowired
    private HouseLogService houseLogService;

    @ApiOperation(value = "房屋租赁流水基本信息分页展示及查询")
    @RequestMapping(value = "/pageHouseLog", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean pageHouseLog(Pageable pageable, String buildingid, String unitid, String houseid, String idnumber) {
        try {
            Page<HouseLog> page = this.houseLogService.getsqlpage(pageable,buildingid,unitid,houseid,idnumber);
            return getResponseBean(Result.OPERATE_SUCCESS.setData(page));
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

    @ApiOperation(value = "房屋租赁基本信息新增/(续租)")
    @RequestMapping(value = "/addEditHouseLog", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean addEditHouseLog(HttpServletRequest request,String id, String houseno, String idnumber, String startTime, String endTime){
        try {
            House house = new House();
            house.setHouseno(houseno); //房屋
            Owner owner = new Owner();
            owner.setIdnumber(idnumber);//业主

            HouseLog houseLog = new HouseLog();
            houseLog.setId(id);
            houseLog.setHouseno(house);
            houseLog.setIdnumber(owner);
            houseLog.setStarttime(simpleDateFormat.parse(startTime));
            houseLog.setEndtime(simpleDateFormat.parse(endTime));

            if(this.houseLogService.chekBs(idnumber,houseLog.getHouseno().getHouseno(),houseLog.getStarttime(),houseLog.getEndtime())>0L){
            return getResponseBean(Result.find(CfI.R_HOUSE_HASRENT_FAIL));
        }
        if(!"".equals(houseLog.getId()) && null!= houseLog.getId()){
        }else{
            houseLog.setId(null);
        }
        this.houseLogService.saveOrUpdate(houseLog);
            return getResponseBean(Result.OPERATE_SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }

    }

    @ApiOperation(value ="房屋租赁基本信息删除")
    @RequestMapping(value="/delHouseLog", method = RequestMethod.POST)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", required = true, value="房屋租赁流水信息id", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean delHouseLog(String id){
        try{
            this.houseLogService.delete(id);
            return getResponseBean(Result.DELETE_SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.DELETE_FAIL);
        }
    }

    @ApiOperation(value = "租赁信息基本信息查看")
    @RequestMapping(value = "/viewHouseLog", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id" , required = true, value = "房屋租赁流水信息id", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean viewHouseLog(String id){
        try{
            HouseLog HouseLog = this.houseLogService.getOne(id);
            log.info("租赁信息基本信息查看");
            return getResponseBean(Result.OPERATE_SUCCESS.setData(HouseLog));
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

    @Deprecated
    @ApiOperation(value = "租赁信息模板导入模板下载")
    @RequestMapping(value = "downModelExcel", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean downModelExcel(HttpServletRequest request){
        ModelExcel modelExcel = new ModelExcel();
        try {
        String fileName = "房屋租赁导入模板(标*的列为必填列).xls"; //模板名称
        String[] handers = {"证件号码*","房屋编号*","开始时间(2012-01-01)","到期时间(2012-11-01)"}; //列标题
        List<String[]> downData = new ArrayList();
        String [] downRows = {};
                modelExcel.createExcelTemplate(fileName, handers, downData, downRows);
                fileDownOrShow(fileName, FileContentType.down,new FileInputStream(new File(fileName)));
                if(new File(fileName).exists()) new File(fileName).delete();
                log.info("下载模板");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
        return getResponseBean(Result.OPERATE_SUCCESS);
    }
    @Deprecated
    @ApiOperation(value = "租赁信息导入")
    @RequestMapping(value = "uploadHouseLogExcel",method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean uploadHouseLogExcel(@RequestParam(name = "file",required=true) MultipartFile file){
        try{
            System.out.println(file.getOriginalFilename());
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.find(CfI.R_EXCELLOG_ALL_CHECK_FAIL));
        }
        return getResponseBean(Result.OPERATE_SUCCESS.setData(""));
    }


}
