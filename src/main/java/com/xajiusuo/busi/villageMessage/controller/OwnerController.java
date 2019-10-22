package com.xajiusuo.busi.villageMessage.controller;

import com.alibaba.fastjson.JSONObject;
import com.xajiusuo.busi.buildingaccess.service.BuildingEntranceCardService;
import com.xajiusuo.busi.diction.controller.DictionController;
import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.busi.entranceandexit.service.GateEntranceCardService;
import com.xajiusuo.busi.log.service.LogSysService;
import com.xajiusuo.busi.villageMessage.entity.HouseOwner;
import com.xajiusuo.busi.villageMessage.entity.Owner;
import com.xajiusuo.busi.villageMessage.service.HouseOwnerService;
import com.xajiusuo.busi.villageMessage.service.OwnerService;
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
@Api(description = "业主基本信息")
@RestController
@RequestMapping(value ="/api/village/owner")
public class OwnerController extends BaseController{
    /**
     *@Description:增加日志
     *@Author: gaoyong
     *@date: 19-6-10 下午5:35
    */
    private final Logger log = LoggerFactory.getLogger(OwnerController.class);


    @Autowired
    private LogSysService logSysService;

    @Autowired
    DictionController dictionController;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private BuildingEntranceCardService buildingEntranceCardService;


    @Autowired
    private GateEntranceCardService gateEntranceCardService;

    @Autowired
    private HouseOwnerService houseOwnerService;

    @ApiOperation(value = "业主基本信息分页展示及查询")
    @RequestMapping(value = "/pageOwner", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="villageid",required = false,value="小区id", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="startTime",required = false,value = "出生日期开始日期", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name="endTime",required = false,value = "出生日期开始日期", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean pageOwner(Pageable pageable, Owner owner,String startTime, String endTime,String identifyTypeKeys) {
        try {
            Diction identifyType =  new Diction();
            identifyType.setKeys(identifyTypeKeys);
            owner.setIdentifyTypeDiction(identifyType);
            Page<Owner> page = this.ownerService.getsqlpage(pageable,owner,startTime,endTime);
            return getResponseBean(Result.OPERATE_SUCCESS.setData(page));
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

    @ApiOperation(value = "业主基本信息新增/修改")
    @RequestMapping(value = "/addEditOwner", method = RequestMethod.POST)
    @ApiImplicitParams({
//            @ApiImplicitParam(name = "villageid" , required = true, value = "小区id", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean addEditOwner(@RequestBody Owner owner){
            try {

               if(!"".equals(owner.getId()) && null != owner.getId()){
               }else{
                   owner.setId(null);
               }
                this.ownerService.saveOrUpdate(owner);

                String ownerid = owner.getIdnumber();//业主id
                String ownernumber = owner.getIdnumber();//业主身份证号
                if(this.houseOwnerService.saveUpdataHouseOwner(ownernumber,owner.getHouseid())){
                    log.info("业主信息新增成功");
                    return getResponseBean(Result.OPERATE_SUCCESS);
                }else{
                    this.ownerService.delete(ownerid);
                    log.error("业主信息新增失败");
                    return getResponseBean(Result.OPERATE_FAIL);
                }
            }catch (Exception e){
                e.printStackTrace();
                log.error(e.getMessage());
                return getResponseBean(Result.OPERATE_FAIL);
            }

    }

    @ApiOperation(value ="业主基本信息删除/及关联的房屋信息")
    @RequestMapping(value="/delOwner", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value="业主信息id", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean delOwner(String id){
        try{
            Owner owner = this.ownerService.get(id);
            List bulidingCarNoExist = this.buildingEntranceCardService.getList("personidnumber","delFlag",false,null,null,null);//楼宇门禁以关联业主
            List gateEntranceCardNoExist = this.gateEntranceCardService.getList("personidnumber","delFlag",false,null,null,null); //出入口正在 使用业主信息
            if(bulidingCarNoExist.contains(owner.getIdnumber()) || gateEntranceCardNoExist.contains(owner.getIdnumber())){
                return getResponseBean(Result.find(CfI.R_OWNER_DELETE_FAIL));
            }

            this.ownerService.delete(id);
             List<HouseOwner> list =   this.houseOwnerService.findBy("ownerid",owner.getIdnumber());
            String [] ids = new String[list.size()];
            for(int i = 0;i<list.size();i++){
                ids[i] = list.get(i).getId();
            }
            this.houseOwnerService.delete(ids);
        }catch (Exception e){
            e.printStackTrace();
            log.error("业主基本信息删除失败");
            log.error(e.getMessage());
            return getResponseBean(Result.DELETE_FAIL);
        }
        return getResponseBean(Result.DELETE_SUCCESS);
    }

    @ApiOperation(value = "业主基本信息查看")
    @RequestMapping(value = "/viewOwner", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idnumber" , required = true, value = "业主身份证号", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean viewOwner(String idnumber){
        Owner owner = null;
        List<Owner> owners = this.ownerService.findBy("idnumber",idnumber);
        if(owners.size()>0){
            owner = owners.get(0);
        }
        log.info("业主基本信息查看");
        return Result.OPERATE_SUCCESS.toBean(owner);
    }

    @ApiOperation(value = "所有人员模糊搜索select下拉查询")
    @RequestMapping(value = "getSelectOwner",method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "villageid" , required = false, value = "小区ID", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "nameOrIdNumber" , required = false, value = "人员姓名", paramType = "query", dataType = "String"),
//            @ApiImplicitParam(name = "idNumber" , required = false, value = "身份证号", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean getSelectOwner(String villageid,String nameOrIdNumber){
        try{
            List list = this.ownerService.getSelectOwner(villageid,nameOrIdNumber);
            return getResponseBean(Result.OPERATE_SUCCESS.setData(list));
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }


    @ApiOperation(value = "某个房屋的人员选择框select下拉查询")
    @RequestMapping(value = "getSelectHouseOwner",method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "villageid" , required = true, value = "小区编号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "houseno", required = true, value = "房屋编号", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean getSelectHouseOwner(String villageid,String houseno){
        List list = null;
        try{
            list = this.ownerService.getSelectHouseOwner(villageid,houseno);
            log.info("房屋下人员信息下拉菜单");
            return getResponseBean(Result.OPERATE_SUCCESS.setData(list));
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

    @ApiOperation(value = "业主标识查重校验")
    @RequestMapping(value = "checkbs", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name ="villageid", required = false, value="小区ID", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name ="bs", required = true, value="标识(idnumber;'业主身份证号)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name ="bsValue", required = true, value="标识值", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean checkbs(String villageid,String bs,String bsValue){
        try{
            Long count = this.ownerService.chekBs(villageid, bs, bsValue);
            log.info("自定义字段查重校验");
            return getResponseBean(Result.OPERATE_SUCCESS.setData(count));
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

    @ApiOperation(value = "业主信息所有字典字典信息查询")
    @RequestMapping(value = "getSelectDiction", method = RequestMethod.GET)
    @ApiImplicitParams({
//            @ApiImplicitParam(name = "pid", required = true, value="字典跟节点", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean getSelectDiction(){
        //证件类型
        String idtype = Configs.find(CfI.C_OWNER_IDTYPE_DICTION).getValue();
        //性别
        String sex = Configs.find(CfI.C_OWNER_SEX_DICTION).getValue();
        //民族
        String nation = Configs.find(CfI.C_OWNER_NATION_DICTION).getValue();
        //政治面貌
        String politicsstatus = Configs.find(CfI.C_OWNER_POLITICSSTATUS_DICTION).getValue();
        //文化程度
        String  educationstatus = Configs.find(CfI.C_OWNER_EDUCATIONSTATUS_DICTION).getValue();
        //人员类型
        String identify_type = Configs.find(CfI.C_OWNER_IDENTIFY_TYPE_DICTION).getValue();
        //所属国家
        String identify_country = Configs.find(CfI.C_OWNER_IDENTIFY_COUNTRY_DICTION).getValue();

        try{
                JSONObject jsonObject = new JSONObject();
                Object idtypeDiction = dictionController.listDictions(idtype).getData();
                log.info("获取"+idtype+"的字典信息");
                Object sexDiction = dictionController.listDictions(sex).getData();
                log.info("获取"+sex+"的字典信息");
                Object nationDiction = dictionController.listDictions(nation).getData();
                log.info("获取"+nation+"的字典信息");
                Object politicsstatusDiction = dictionController.listDictions(politicsstatus).getData();
                log.info("获取"+politicsstatus+"的字典信息");
                Object educationstatusDiction = dictionController.listDictions(educationstatus).getData();
                log.info("获取"+educationstatus+"的字典信息");
                Object identifyTypeDiction = dictionController.listDictions(identify_type).getData();
                log.info("获取"+identify_type+"的字典信息");
                Object identifyCountryDiction = dictionController.listDictions(identify_country).getData();
                log.info("获取"+identify_country+"的字典信息");

                jsonObject.put("idtype",idtypeDiction);
                jsonObject.put("sex",sexDiction);
                jsonObject.put("nation",nationDiction);
                jsonObject.put("politicsstatus",politicsstatusDiction);
                jsonObject.put("educationstatus",educationstatusDiction);
                jsonObject.put("identifyType",identifyTypeDiction);
                jsonObject.put("identifyCountry",identifyCountryDiction);
                return getResponseBean(Result.OPERATE_SUCCESS.setData(jsonObject));
            }catch (Exception e){
                e.printStackTrace();
                log.error(e.getMessage());
                return getResponseBean(Result.OPERATE_FAIL);
            }
    }
    @ApiOperation(value = "业主信息导入模板下载")
    @RequestMapping(value = "downModelExcel", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean downModelExcel(HttpServletRequest request){
        //证件类型
        String idtype = Configs.find(CfI.C_OWNER_IDTYPE_DICTION).getValue();
        //性别
        String sex = Configs.find(CfI.C_OWNER_SEX_DICTION).getValue();
        //民族
        String nation = Configs.find(CfI.C_OWNER_NATION_DICTION).getValue();
        //政治面貌
        String politicsstatus = Configs.find(CfI.C_OWNER_POLITICSSTATUS_DICTION).getValue();
        //文化程度
        String  educationstatus = Configs.find(CfI.C_OWNER_EDUCATIONSTATUS_DICTION).getValue();
        //人员类型
        String identify_type = Configs.find(CfI.C_OWNER_IDENTIFY_TYPE_DICTION).getValue();
        //所属国家
        String identify_country = Configs.find(CfI.C_OWNER_IDENTIFY_COUNTRY_DICTION).getValue();

        ModelExcel modelExcel = new ModelExcel();
        try {
        String fileName = "业主信息导入模板(标*的列为必填列).xls"; //模板名称
        String[] handers = {"小区编号*","房屋编号*",
                            "证件类型*","证件号码","姓名","性别*","出生日期",
                            "民族*","政治面貌*","文化程度*","联系电话",
                            "从事职业","服务处所","户籍地址","人员类型*","所属国家*"}; //列标题
        //下拉框数据
        List<String[]> downData = new ArrayList();
        List list = this.ownerService.getDictionForUpload(idtype); //证件类型
        String [] str  = (String[]) list.toArray(new String[list.size()]);
        List list1 = this.ownerService.getDictionForUpload(sex);//性别
        String [] str1  = (String[]) list1.toArray(new String[list1.size()]);
        List list2 = this.ownerService.getDictionForUpload(nation);//民族
        String [] str2  = (String[]) list2.toArray(new String[list2.size()]);;
        List list3 = this.ownerService.getDictionForUpload(politicsstatus);//政治面貌
        String [] str3  = (String[]) list3.toArray(new String[list3.size()]);
        List list4 = this.ownerService.getDictionForUpload(educationstatus);//文化程度
        String [] str4  = (String[]) list4.toArray(new String[list4.size()]);
        List list5 = this.ownerService.getDictionForUpload(identify_type);//人员类型
        String [] str5  = (String[]) list5.toArray(new String[list5.size()]);
        List list6 = this.ownerService.getDictionForUpload(identify_country);//所属国家
        String [] str6  = (String[]) list6.toArray(new String[list5.size()]);
        downData.add(str);
        downData.add(str1);
        downData.add(str2);
        downData.add(str3);
        downData.add(str4);
        downData.add(str5);
        downData.add(str6);
        String [] downRows = {"2","5","7","8","9","14","15"}; //下拉的列序号数组(序号从0开始)

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

    @ApiOperation(value = "业主信息导入")
    @RequestMapping(value = "uploadOwnerExcel",method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean uploadOwnerExcel(@RequestParam(name = "file",required=true) MultipartFile file){
        InputStream in;
        try{
            in = file.getInputStream();
            List result = this.ownerService.saveUpdate(in);
            if(result.size()>0){
                return getResponseBean(Result.OPERATE_SUCCESS.setData(result));
            }else {
                return getResponseBean(Result.OPERATE_SUCCESS);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.find(CfI.R_EXCELLOG_ALL_CHECK_FAIL));
        }
    }


}
