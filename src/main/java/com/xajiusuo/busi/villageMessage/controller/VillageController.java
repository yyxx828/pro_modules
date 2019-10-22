package com.xajiusuo.busi.villageMessage.controller;

import com.alibaba.fastjson.JSONObject;
import com.xajiusuo.busi.device.service.DeviceService;
import com.xajiusuo.busi.device.vo.DeviceBaseStatVo;
import com.xajiusuo.busi.diction.service.DepartPoliceService;
import com.xajiusuo.busi.diction.service.DictionService;
import com.xajiusuo.busi.diction.service.ProvincesDataService;
import com.xajiusuo.busi.motorVehicle.service.MotorVehicleInfoService;
import com.xajiusuo.busi.villageMessage.entity.Village;
import com.xajiusuo.busi.villageMessage.service.*;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Configs;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.utils.CfI;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author GaoYong
 * @Date 19-6-5 下午2:30
 * @Description 小区基本信息
 */
@Slf4j
@Api(description = "小区基本信息")
@RestController
@RequestMapping(value ="/api/village/village")
public class VillageController extends BaseController{

    @Autowired
    private VillageService villageService;

    @Autowired
    private DictionService dictionService;

    @Autowired
    DepartPoliceService departPoliceService;

    @Autowired
    ProvincesDataService provincesDataService;

    @Autowired
    DeviceService deviceService;

    @Autowired
    MotorVehicleInfoService motorVehicleInfoService;

    @Autowired
    OwnerService ownerService;

    @Autowired
    HouseService houseService;

    @Autowired
    UnitService unitService;

    @Autowired
    BuildingService buildingService;

    @Autowired
    HouseLogService houseLogService;

    @Autowired
    ParkingService parkingService;

    @ApiOperation(value = "小区基本信息分页展示及查询")
    @RequestMapping(value = "/pageVillage", method = RequestMethod.GET)
    @ApiImplicitParams({
//            @ApiImplicitParam(name="villagename",required = false,value="小区名称", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean pageVillage(Pageable pageable,Village village) {
        Page<Village> page = null;
        try {
            page = this.villageService.getsqlpage(pageable,village);
            return Result.OPERATE_SUCCESS.toBean(page);
        }catch (Exception e){
            e.printStackTrace();
            return Result.OPERATE_FAIL.toBean();
        }
    }

    @ApiOperation(value = "小区基本信息新增/修改")
    @RequestMapping(value = "/addEditVillage", method = RequestMethod.POST)
    @ApiImplicitParams({
//            @ApiImplicitParam(name = "subbureau" , required = true, value = "所属分局", paramType = "query", dataType = "String"),
//            @ApiImplicitParam(name = "district", required = true, value = "行政区域", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean addEditVillage(@RequestBody  Village village){
        return villageService.saveUpdate(village).toBean();
    }

    @ApiOperation(value ="小区基本信息删除/关联删除小区下的所有楼宇,单元,房屋,业主信息")
    @RequestMapping(value="/delVillage", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value="小区ID", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean delVillage(String id){
        try{
            villageService.delete(id);
            log.info("小区基本信息删除");
            return Result.DELETE_SUCCESS.toBean();
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.DELETE_FAIL.toBean();
        }
    }

    @ApiOperation(value = "小区基本信息查看")
    @RequestMapping(value = "/viewVillage", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id" , required = true, value = "小区ID", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean viewVillage(String id){
        try{
            Village village = this.villageService.getOne(id);
            log.info("小区基本信息查看");
            return getResponseBean(Result.QUERY_SUCCESS.setData(village));
        }catch (Exception e){
            log.error(e.getMessage());
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @ApiOperation(value = "小区选择框select下拉查询(key为编号,value为要显示的)")
    @RequestMapping(value = "getSelectVillage",method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean getSelectVillage(){
        try{
            List list = villageService.getAll();
            log.info("小区编码等信息下拉选择");
            return getResponseBean(Result.OPERATE_SUCCESS.setData(list));
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
           return getResponseBean(Result.OPERATE_FAIL);
        }
    }
    @ApiOperation(value = "小区标识查重校验")
    @RequestMapping(value = "checkbs", method = RequestMethod.GET)
    @ApiImplicitParams({
        @ApiImplicitParam(name ="bs", required = true, value="标识(villageid;'小区编号'.villagename;小区名称)", paramType = "query", dataType = "String"),
        @ApiImplicitParam(name ="bsValue", required = true, value="标识值", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean checkbs(String bs,String bsValue){
        try{
            Long  count = this.villageService.chekBs(bs,bsValue);
            log.info("自定义字段查重校验");
            return getResponseBean(Result.OPERATE_SUCCESS.setData(count));
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

    @ApiOperation(value= "首页统计接口")
    @RequestMapping(value = "getCountVillages", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name ="villageId", required = false, value = "小区id", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean getCountVillages(String villageId){
        try {
            List result = new ArrayList<>();
            JSONObject buildingJson = new JSONObject();
            buildingJson.put("name","楼栋数");
            buildingJson.put("count",this.buildingService.getAll().size());
            buildingJson.put("icon","lds");
            buildingJson.put("color","#8cd470");
            result.add(buildingJson);
            JSONObject unitJson = new JSONObject();
            unitJson.put("name","单元数");
            unitJson.put("count",this.unitService.getAll().size());
            unitJson.put("icon","dys");
            unitJson.put("color","#8cd470");
            result.add(unitJson);
            JSONObject houseJson = new JSONObject();
            houseJson.put("name","房屋数");
            houseJson.put("count",this.houseService.getAll().size());
            houseJson.put("icon","fws");
            houseJson.put("color","#8cd470");
            result.add(houseJson);
            //机房设备 视频头
            long deviceCount = 0;
            List<DeviceBaseStatVo> list  = this.deviceService.getDeviceCount(villageId);
            for(DeviceBaseStatVo aa:list){
                if(aa.getValue() == 3)deviceCount = aa.getTotal();
            }
            JSONObject deviceJson = new JSONObject();
            deviceJson.put("name","探头数");
            deviceJson.put("count",deviceCount);
            deviceJson.put("icon","tts");
            deviceJson.put("color","#f286ab");
            result.add(deviceJson);
            JSONObject permanentJson = new JSONObject();
            permanentJson.put("name","常驻人");
            permanentJson.put("count",this.ownerService.getPeopleType(Configs.find(CfI.C_OWNER_RESIDENT_DICTION).getValue()));
            permanentJson.put("icon","zzrk");
            permanentJson.put("color","#eeab57");
            result.add(permanentJson);
            JSONObject temporaryJson = new JSONObject();
            temporaryJson.put("name","暂住人");
            temporaryJson.put("count",this.ownerService.getPeopleType(Configs.find(CfI.C_OWNER_TEMPORARY_DICTION).getValue()));
            temporaryJson.put("icon","zzrk");
            temporaryJson.put("color","#eeab57");
            result.add(temporaryJson);
            JSONObject lodgeJson = new JSONObject();
            lodgeJson.put("name","寄住人");
            lodgeJson.put("count",this.ownerService.getPeopleType(Configs.find(CfI.C_OWNER_LODGE_DICTION).getValue()));
            lodgeJson.put("icon","zzrk");
            lodgeJson.put("color","#eeab57");
            result.add(lodgeJson);
            JSONObject overseasJson = new JSONObject();
            overseasJson.put("name","境外人");
            overseasJson.put("count",this.ownerService.getOverseasPeopleCount());
            overseasJson.put("icon","jwry");
            overseasJson.put("color","#eeab57");
            result.add(overseasJson);
            //机动车
            Map<String,Integer> map = this.motorVehicleInfoService.getMotorVehicleCount(villageId);
            JSONObject moterJson = new JSONObject();
            moterJson.put("name","机动车");
            moterJson.put("count",map.get("motorVehicleCount"));
            moterJson.put("icon","jdc");
            moterJson.put("color","#3dc9e1");
            result.add(moterJson);
            JSONObject parkingJson = new JSONObject();
            parkingJson.put("name","机动停车位");
            parkingJson.put("count",this.parkingService.getAll().size());
            parkingJson.put("icon","jdctcw");
            parkingJson.put("color","#3dc9e1");
            result.add(parkingJson);

            return getResponseBean(Result.OPERATE_SUCCESS.setData(result));
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }
    @Deprecated
    @ApiOperation(value = "字典信息查询")
    @RequestMapping(value = "getSelectDiction", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", required = true, value="字典跟节点", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean getSelectDiction(String pid){
        List  list = null;
        try{
            String sql = "select * from P_DICTION_11 where 1=1 and and pid = ?";
            list = this.dictionService.executeNativeQuerySql(sql, "0",pid);
            log.info("获取"+pid+"的字典信息");
            return Result.OPERATE_SUCCESS.toBean(list);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.OPERATE_FAIL.toBean();
        }
    }

    @ApiOperation(value ="小区信息统计接口")
    @RequestMapping(value = "getCountOwners", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "villageId", required = false, value ="小区id", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean getCountOwners(String villageId){
        JSONObject result = new JSONObject();
        int ownerCount = 0;
        int tenantPercent = 0;
        int vacancyPercent = 0;
        try{
             ownerCount = this.ownerService.getAll().size();
            int tenant = this.ownerService.findBy("identify_type",Configs.find(CfI.C_OWNER_TENANT_DICTION).getValue()).size();
            tenantPercent =  (int)Math.round(((double)tenant/(double)ownerCount)*100);
            int houseCount = this.houseService.getAll().size();
            int ownerHouseCount = this.houseLogService.getCountHouseNo();
            vacancyPercent = (int)Math.round(((double)ownerHouseCount/(double)houseCount)*100);
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.OPERATE_FAIL);
        }
        result.put("allOwner",ownerCount);
        result.put("tenantPercent",tenantPercent);
        result.put("vacancyPercent",vacancyPercent);
        return getResponseBean(Result.OPERATE_SUCCESS.setData(result));
    }

    @ApiOperation(value = "小区其它信息统计")
    @RequestMapping(value = "getCountAll", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "villageId", required = false, value ="小区ID", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean getCountAll(String villageId){
        try {
            String ownerLocation = Configs.find(CfI.C_OWNER_LOCATION_DICTION).getValue();
            return  getResponseBean(Result.OPERATE_SUCCESS.setData(this.ownerService.getPeoplePercent(ownerLocation)));
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

}
//    <李志栋> 2019年07月01日 星期一 10时38分18秒
//            rvice
//
//    interface DeviceService
//
///**
// * 获取指定小区各类型技防设备数量
// *
// * @param villageId 小区编号
// * @return
// */
//            List<DeviceBaseStatVo> getDeviceCount(String villageId);

//    <史任静> 2019年07月01日 星期一 11时15分20秒
///**
// * @Author shirenjing
// * @Description 统计机动车数量
// * @Date 10:55 2019/7/1
// * @Param []
// * @return
// **/
//    Map<String,Integer> getMotorVehicleCount(String villageId);--我在
//    MotorVehicleInfoService 你刚要的接口

//    [
//    {name:'楼栋数',count:45,icon:'lds',color:"#8cd470"},
//    {name:'单元数',count:90,icon:'dys',color:"#8cd470"},
//    {name:'房屋数',count:360,icon:'fws',color:"#8cd470"},
//    {name:'探头数',count:50,icon:'tts',color:"#f286ab"},
//    {name:'常驻人口',count:1054,icon:'zzrk',color:"#eeab57"},
//    {name:'暂住人口',count:165,icon:'zzrk',color:"#eeab57"},
//    {name:'寄住人口',count:81,icon:'zzrk',color:"#eeab57"},
//    {name:'境外人员',count:2,icon:'jwry',color:"#eeab57"},
//    {name:'机动车',count:152,icon:'jdc',color:"#3dc9e1"},
//    {name:'机动车停车位',count:200,icon:'jdctcw',color:"#3dc9e1"},
//            ]


