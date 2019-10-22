package com.xajiusuo.busi.motorVehicle.controller;

import com.xajiusuo.busi.diction.controller.DictionController;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleInfo;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleInfoVo;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleOwnerVo;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleVo;
import com.xajiusuo.busi.motorVehicle.service.MotorVehicleInfoService;
import com.xajiusuo.busi.villageMessage.untilmodel.ModelExcel;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.down.FileContentType;
import com.xajiusuo.jpa.param.e.Configs;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.utils.CfI;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.PictureData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by shirenjing on 2019/6/15.
 */
@Slf4j
@Api(description = "机动车基本信息管理")
@RestController
@RequestMapping(value = "/api/village/MotorVehicleInfo")
public class MotorVehicleInfoController extends BaseController {

    @Autowired
    private MotorVehicleInfoService motorVehicleInfoService;

    private ModelExcel modelExcel = null;  //excelG工具类
    @Autowired
    private DictionController dictionController; //字典信息

    @GetMapping(value = "/queryInfoList")
    @ApiOperation(value = "机动车基本信息列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(lastModifyTime,desc),", example = "lastModifyTime,desc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean queryInfoList(Pageable pageable, MotorVehicleInfoVo motorVehicleInfoVo) {
        pageable = transPage(pageable, new Sort(Sort.Direction.DESC, "lastModifyTime"), true);
        try {
            Page<MotorVehicleVo> page = motorVehicleInfoService.queryPageInfo(pageable, motorVehicleInfoVo);
            return Result.QUERY_SUCCESS.toBean(page);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }

    /*{
        "id": "402881da6bb5ea25016bb5fd8cb70001",
            "motorVehicleID": "201907030001",
            "ownerID": "",
            "ownerIDNumber": "410105199407039090",
            "ownerName": "李一",
            "plateClassDiction": {
        "keys": "k0101"
    },
        "plateNo": "陕A111113",
            "storageUrl1": "",
            "storageUrl2": "",
            "storageUrl3": "",
            "vehicleBrandDiction": {
        "keys": "k0301"
    },
        "vehicleClassDiction": {
        "keys": "k0201"
    },
        "vehicleColor": "黑",
            "vehicleModel": "234234",
            "vehicleStatusDiction": {
        "keys": "k0402"
    },
        "villageID": "10001"
    }*/
    @PostMapping(value = "/saveOrUpdateInfo")
    @ApiOperation(value = "机动车基本信息新增/修改", httpMethod = "POST")
    @ResponseBody
    public ResponseBean saveOrUpdateInfo(@RequestBody MotorVehicleInfo entity) {
        MotorVehicleInfo info = null;
        String option = "update";
        if (CommonUtils.isNotEmpty(entity.getId())) {
            info = motorVehicleInfoService.getOne(entity.getId());
        }
        String oldccbh = ""; //车辆编号
        String oldcp = "";//车牌号
        if (info != null) {
            oldccbh = info.getMotorVehicleID();
            oldcp = info.getPlateNo();
        }
        if (!oldccbh.equals(entity.getMotorVehicleID())) { //车辆编号去重
            List<MotorVehicleInfo> ccbhList = motorVehicleInfoService.queryByField("motorVehicleID", entity.getMotorVehicleID());
            if (ccbhList != null && ccbhList.size() > 0) {
                return Result.find(CfI.R_CAR_CCBHREPEAT_FAIL).toBean();
            }
        }
        if (!oldcp.equals(entity.getPlateNo())) { //车牌号去重
            List<MotorVehicleInfo> oldcpList = motorVehicleInfoService.queryByField("plateNo", entity.getPlateNo());
            if (oldcpList != null && oldcpList.size() > 0) {
                return getResponseBean(Result.find(CfI.R_CAR_CPREPEAT_FAIL));
            }
        }

//        if(file1 != null){
//            entity.setStorageUrl1(file1.getOriginalFilename());
//        }
//        if(file2 != null){
//            entity.setStorageUrl2(file2.getOriginalFilename());
//        }
//        if(file3 != null){
//            entity.setStorageUrl3(file3.getOriginalFilename());
//        }
        return motorVehicleInfoService.saveUpdate(entity).toBean();
    }

    @GetMapping(value = "/checkRepeat")
    @ApiOperation(value = "验证车辆编号、车牌号是否重复", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fieldname", required = true, value = "去重字段名 plateNo：车牌号 motorVehicleID：车辆编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "fieldvalue", required = true, value = "字段值", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "id", required = false, value = "主键编号", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean checkRepeat(String fieldname, String fieldvalue, String id) {
        boolean res = motorVehicleInfoService.findSame(fieldname, fieldvalue, id, null);
        if ("motorVehicleID".equals(fieldname)) {
            return Result.find(res ? CfI.R_CAR_BHREPEAT_SUCCESS : CfI.R_CAR_CCBHREPEAT_SUCCESS).toBean(res);
        } else {
            return Result.find(res ? CfI.R_CAR_REPEAT_SUCCESS : CfI.R_CAR_CPREPEAT_SUCCESS).toBean(res);
        }
    }

    @RequestMapping(value = "/viewInfo/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "查看机动车基本信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "机动车信息id", paramType = "path", dataType = "string")
    })
    @ResponseBody
    public ResponseBean viewInfo(@PathVariable(value = "id") String id) {
        try {
            MotorVehicleInfo motorVehicleInfo = motorVehicleInfoService.queryTranslateById(id);
            return getResponseBean(Result.QUERY_SUCCESS.setData(motorVehicleInfo));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @RequestMapping(value = "/viewInfoByplateNo/{plateNo}", method = RequestMethod.GET)
    @ApiOperation(value = "查看机动车基本信息（根据车牌号）", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "plateNo", required = true, value = "车牌号", paramType = "path", dataType = "string")
    })
    @ResponseBody
    public ResponseBean viewInfoByplateNo(@PathVariable(value = "plateNo") String plateNo) {
        try {
            MotorVehicleInfo motorVehicleInfo = motorVehicleInfoService.queryTranslateByPlateNo(plateNo);
            return getResponseBean(Result.QUERY_SUCCESS.setData(motorVehicleInfo));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @RequestMapping(value = "/deleteInfo/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机动车基本信息", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "机动车信息id", paramType = "path", dataType = "string")
    })
    @ResponseBody
    public ResponseBean deleteInfo(@PathVariable(value = "id") String id) {
        try {
            motorVehicleInfoService.delete(id);
            return Result.DELETE_SUCCESS.toBean();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.DELETE_FAIL.toBean();
        }
    }

    @RequestMapping(value = "/batchdeleteInfo", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除删除机动车基本信息", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "机动车信息ids(用逗号分隔)", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean batchdeleteInfo(@RequestParam(value = "ids") String ids) {
        try {
            motorVehicleInfoService.delete(ids.split(","));
            return Result.DELETE_SUCCESS.toBean();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.DELETE_FAIL.toBean();
        }
    }

    @GetMapping(value = "/getMotorVehicleOwner")
    @ApiOperation(value = "获取车主信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fieldname", required = true, value = "字段名 name：车主姓名 idnumber：车主证件编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "fieldvalue", required = true, value = "字段值", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean getMotorVehicleOwner(String fieldname, String fieldvalue) {
        try {
            List<MotorVehicleOwnerVo> list = motorVehicleInfoService.queryMotorVehicleOwner(fieldname, fieldvalue);
            return getResponseBean(Result.QUERY_SUCCESS.setData(list));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }

    }

    @GetMapping(value = "/getMotorVehicleOwnerNew")
    @ApiOperation(value = "获取车主信息new", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "value", required = true, value = "字段值", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean getMotorVehicleOwnerNew(String value) {
        try {
            List<MotorVehicleOwnerVo> list = motorVehicleInfoService.queryMotorVehicleOwner("idnumber", value);
            if (list == null || list.size() == 0) {
                list = motorVehicleInfoService.queryMotorVehicleOwner("name", value);
            }
            return getResponseBean(Result.QUERY_SUCCESS.setData(list));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }

    }

    @RequestMapping(value = "/preUpdateInfo/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "进入修改车辆信息页面", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "车辆id", paramType = "path", dataType = "String"),
    })
    public ResponseBean preUpdateInfo(@PathVariable(value = "id") String id) {
        try {
            MotorVehicleInfo info = motorVehicleInfoService.getOne(id);
            return getResponseBean(Result.QUERY_SUCCESS.setData(info));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    @RequestMapping(value = "/downTemplete", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "机动车信息模板下载", httpMethod = "GET")
    public ResponseBean downTemplete() {
        modelExcel = new ModelExcel();
        try {
            String[] handers = new String[]{"车辆编号*", "号牌种类", "车牌号*", "车辆类型", "车辆品牌", "车辆型号", "车身颜色", "近景照片", "车牌照片",
                    "远景照片", "车辆状态", "车主姓名*", "车主证件号*", "小区编号*"};
            List<String[]> downData = new ArrayList<>();
            String[] plateClassD = motorVehicleInfoService.getDictionForUpload(Configs.find(CfI.C_CAR_PLATECLASS_DICTION).getValue());
            String[] vehicleClassD = motorVehicleInfoService.getDictionForUpload(Configs.find(CfI.C_CAR_VEHICLECLASS_DICTION).getValue());
            String[] vehicleBrandD = motorVehicleInfoService.getDictionForUpload(Configs.find(CfI.C_CAR_VEHICLEBRAND_DICTION).getValue());
            String[] vehicleStatusD = motorVehicleInfoService.getDictionForUpload(Configs.find(CfI.C_CAR_VEHICLESTATUS_DICTION).getValue());
            downData.add(plateClassD);
            downData.add(vehicleClassD);
            downData.add(vehicleBrandD);
            downData.add(vehicleStatusD);
            String[] downRows = new String[]{"1", "3", "4", "10"};
            String fileName = "车辆信息导入模板.xls";

            modelExcel.createExcelTemplate(fileName, handers, downData, downRows);
            fileDownOrShow(fileName, FileContentType.xls, new FileInputStream(new File(fileName)));
            if (new File(fileName).exists()) new File(fileName).delete();
            return Result.OPERATE_SUCCESS.toBean();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.OPERATE_FAIL.toBean();
        }

    }


    @ApiOperation(value = "车辆信息导入")
    @RequestMapping(value = "uploadCarExcel", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean uploadCarExcel(@RequestParam(name = "file", required = true) MultipartFile file) {
        InputStream in;
        try {
            in = file.getInputStream();
            List result = motorVehicleInfoService.uploadCarExcel(in);
//            Map<String, PictureData> ss = new ModelExcel().getPic("xls",in);
//            for (Map.Entry<String, PictureData> entry:ss.entrySet()) {
//                String key = entry.getKey();
//                PictureData value = entry.getValue();
//                System.out.println(key);
//                System.out.println(value);
//            }
            return Result.OPERATE_SUCCESS.toBean(result);
//            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.find(CfI.R_EXCELLOG_ALL_CHECK_FAIL).toBean();
        }
    }

    @ApiOperation(value = "号牌种类字典下拉")
    @RequestMapping(value = "getPlateClassDiction", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean getPlateClassDiction() {
        //号牌种类字典下拉码值
        try {
            String rental = Configs.find(CfI.C_CAR_PLATECLASS_DICTION).getValue();
            return dictionController.listDictions(rental);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.QUERY_FAIL.toBean();
        }
    }

    @ApiOperation(value = "车辆类型字典下拉")
    @RequestMapping(value = "getVehicleClassDiction", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean getVehicleClassDiction() {
        //车辆类型字典下拉码值
        try {
            String rental = Configs.find(CfI.C_CAR_VEHICLECLASS_DICTION).getValue();
            return dictionController.listDictions(rental);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.QUERY_FAIL.toBean();
        }
    }

    @ApiOperation(value = "车辆品牌字典下拉")
    @RequestMapping(value = "getVehicleBrandDiction", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean getVehicleBrandDiction() {
        //车辆品牌字典下拉码值
        try {
            String rental = Configs.find(CfI.C_CAR_VEHICLEBRAND_DICTION).getValue();
            return dictionController.listDictions(rental);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.QUERY_FAIL.toBean();
        }
    }

    @ApiOperation(value = "车辆状态字典下拉")
    @RequestMapping(value = "getVehicleStatusDiction", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean getVehicleStatusDiction() {
        //车辆状态字典下拉码值
        try {
            String rental = Configs.find(CfI.C_CAR_VEHICLESTATUS_DICTION).getValue();
            return dictionController.listDictions(rental);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.QUERY_FAIL.toBean();
        }
    }

    @ApiOperation(value = "获取车辆信息下拉")
    @RequestMapping(value = "getCarsByPlateNo", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "plateNo", required = true, value = "车牌号", paramType = "query", dataType = "String"),
    })
    @ResponseBody
    public ResponseBean getCarsByPlateNo(String plateNo) {
        try {
            List<MotorVehicleInfo> result = motorVehicleInfoService.queryInfoByPlateNo(plateNo);
            return Result.QUERY_SUCCESS.toBean(result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.QUERY_FAIL.toBean();
        }
    }

}
