package com.xajiusuo.busi.villageMessage.controller;

import com.xajiusuo.busi.diction.controller.DictionController;
import com.xajiusuo.busi.diction.service.DictionService;
import com.xajiusuo.busi.log.service.LogSysService;
import com.xajiusuo.busi.villageMessage.entity.Owner;
import com.xajiusuo.busi.villageMessage.entity.Parking;
import com.xajiusuo.busi.villageMessage.service.ParkingLogService;
import com.xajiusuo.busi.villageMessage.service.ParkingService;
import com.xajiusuo.busi.villageMessage.untilmodel.ModelExcel;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.down.FileContentType;
import com.xajiusuo.jpa.param.e.Configs;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:31
 * @Description
 */
@Api(description = "停车位基本信息")
@RestController
@RequestMapping(value ="/api/village/parking")
public class ParkingController extends BaseController{
    /**
     *@Description:增加日志
     *@Author: gaoyong
     *@date: 19-6-10 下午5:35
    */
    private final Logger log = LoggerFactory.getLogger(ParkingController.class);


    @Autowired
    private DictionController dictionController;

    @Autowired
    private LogSysService logSysService;

    @Autowired
    private DictionService dictionService;

    @Autowired
    private ParkingLogService parkingLogService;

    @Autowired
    private ParkingService parkingService;

    @ApiOperation(value = "车位基本信息分页展示及查询")
    @RequestMapping(value = "/pageParking", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="villageid",required = false,value="小区id", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="idnumber",required = false,value="身份证号", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean pageParking(Pageable pageable, Parking parking,String startTime, String endTime,String idnumber) {
        Page<Parking> page = null;
        try {
            Owner owner = new Owner();
            owner.setIdnumber(idnumber);
            parking.setOwner(owner);
            page = this.parkingService.getsqlpage(pageable,parking);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.OPERATE_SUCCESS.toBean(page);//时间有点问题
    }

    @ApiOperation(value = "车位基本信息新增/修改")
    @RequestMapping(value = "/addEditParking", method = RequestMethod.POST)
    @ApiImplicitParams({
//            @ApiImplicitParam(name = "villageid" , required = true, value = "小区id", paramType = "query", dataType = "String"),
    })
    @ResponseBody
    public ResponseBean addEditParking(@RequestBody Parking parking){
            try{
                this.parkingService.saveOrUpdate(parking);
                log.info("车位信息更新");
                return getResponseBean(Result.OPERATE_SUCCESS);
            }catch(Exception e){
                e.printStackTrace();
                log.error(e.getMessage());
                return getResponseBean(Result.OPERATE_FAIL);
            }

    }
    @ApiOperation(value = "停车位类型字典下拉")
    @RequestMapping(value = "getParkingtypeDiction", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean getParkingtypeDiction(){
        try{
            //停车为属性字典下拉码值
            String getParkingtype = Configs.find(CfI.C_PARKING_PARKINGTYPE_DICTION).getValue();
            log.info("获取"+getParkingtype+"的字典信息");
            return dictionController.listDictions(getParkingtype);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

    @ApiOperation(value ="车位基本信息删除")
    @RequestMapping(value="/delParking", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value="车位信息id", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean delParking(String id){
        try{
            Parking parking = this.parkingService.get(id);
            List parkingNoExist = this.parkingLogService.getList("parkingno","delFlag",false,null,null,null);//已租赁的停车位
            if(parkingNoExist.contains(parking.getParkingno())){
                return getResponseBean(Result.find(CfI.R_PRAKINGNO_DELETE_FAIL));
            }
            this.parkingService.delete(id);
            log.info("车位基本信息删除");
            return getResponseBean(Result.OPERATE_SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

    @ApiOperation(value = "车位基本信息查看")
    @RequestMapping(value = "/viewParking", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id" , required = true, value = "车位ID", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean viewParking(String id){
        try{
            Parking owner = this.parkingService.getOne(id);
            log.info("车位基本信息查看");
            return getResponseBean(Result.OPERATE_SUCCESS.setData(owner));
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }


    @ApiOperation(value = "车位标识查重校验")
    @RequestMapping(value = "checkbs", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name ="villageid", required = false, value="小区ID", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name ="bs", required = true, value="标识(parkingno;'车位编号)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name ="bsValue", required = true, value="标识值", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean checkbs(String villageid,String bs,String bsValue){
        try{
            Long count = this.parkingService.chekBs(villageid,bs,bsValue);
            log.info("自定义字段查重校验");
            return getResponseBean(Result.OPERATE_SUCCESS.setData(count));
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

    @ApiOperation(value = "停车位下拉搜索")
    @RequestMapping(value = "getSelectPParking",method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "villageid" , required = false, value = "小区编号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "parkingno", required = false, value = "停车位编号", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean getSelectPParking(String villageid,String parkingno){
        List list = null;
        try{
            list = this.parkingService.getSelectParking(villageid,parkingno);
            log.info("停车位下拉搜索");
            return getResponseBean(Result.OPERATE_SUCCESS.setData(list));
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }


    @ApiOperation(value = "停车位导入模板下载")
    @RequestMapping(value = "downModelExcel", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean downModelExcel(HttpServletRequest request){
        ModelExcel modelExcel = new ModelExcel();
        try {

            //停车位字典下拉码值
            String getParkingtype = Configs.find(CfI.C_PARKING_PARKINGTYPE_DICTION).getValue();
            String fileName = "停车场信息导入模板(标*的列为必填列).xls"; //模板名称
            String[] handers = {"小区编号*","停车场编号*", "业主身份证号*","停车位类型","机动车ID","出售（租）时间" ,"到期时间"};
            //下拉框数据
            List<String[]> downData = new ArrayList();
            List list1 = this.parkingService.getDictionForUpload(getParkingtype);
            String [] str1  = (String[]) list1.toArray(new String[list1.size()]);
            downData.add(str1);
            String [] downRows = {"3"}; //下拉的列序号数组(序号从0开始)

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

    @ApiOperation(value = "房屋信息导入")
    @RequestMapping(value = "uploadHouseExcel",method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean uploadHouseExcel(@RequestParam(name = "file",required=true) MultipartFile file){
        InputStream in;
        try{
            in = file.getInputStream();
            List result = this.parkingService.saveUpdate(in);
            if(result.size()>0){
                return getResponseBean(Result.OPERATE_SUCCESS.setData(result));
            }else{
                return getResponseBean(Result.OPERATE_SUCCESS);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.find(CfI.R_EXCELLOG_ALL_CHECK_FAIL));
        }
    }

}
