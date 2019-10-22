package com.xajiusuo.busi.villageMessage.controller;

import com.xajiusuo.busi.diction.controller.DictionController;
import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.busi.diction.service.DictionService;
import com.xajiusuo.busi.log.service.LogSysService;
import com.xajiusuo.busi.villageMessage.entity.House;
import com.xajiusuo.busi.villageMessage.entity.HouseOwner;
import com.xajiusuo.busi.villageMessage.service.HouseOwnerService;
import com.xajiusuo.busi.villageMessage.service.HouseService;
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
@Api(description = "房屋基本信息")
@RestController
@RequestMapping(value ="/api/village/house")
public class HouseController extends BaseController{
    /**
     *@Description:增加日志
     *@Author: gaoyong
     *@date: 19-6-10 下午5:35
    */
    private final Logger log = LoggerFactory.getLogger(HouseController.class);

    @Autowired
    private DictionController dictionController;

    @Autowired
    private LogSysService logSysService;

    @Autowired
    private DictionService dictionService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private HouseOwnerService houseOwnerService;

    @ApiOperation(value = "房屋基本信息分页展示及查询")
    @RequestMapping(value = "/pageHouse", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="villageid",required = false,value="小区编号", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="buildingid", required = false,value = "楼栋编号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name="unitid", required = false,value = "单元编号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean pageHouse(Pageable pageable, House house , String houseType,String houseAttrubuteKeys, String rentalKeys) {
        try {
            Diction houseTypeD = new Diction();
            houseTypeD.setKeys(houseType);
            house.setHousetype(houseTypeD);

            Diction houseAttrubuteD = new Diction();
            houseAttrubuteD.setKeys(houseAttrubuteKeys);
            house.setHouse_attrubute(houseAttrubuteD);

            Diction rentalD = new Diction();
            rentalD.setKeys(rentalKeys);
            house.setRental(rentalD);
            Page<House> page = this.houseService.getsqlpage(pageable,house);
            return getResponseBean(Result.OPERATE_SUCCESS.setData(page));
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

    @ApiOperation(value = "房屋基本信息新增/修改")
    @RequestMapping(value = "/addEditHouse", method = RequestMethod.POST)
    @ApiImplicitParams({
//            @ApiImplicitParam(name = "villageid" , required = true, value = "小区编号", paramType = "query", dataType = "String"),
//            @ApiImplicitParam(name ="buildingid", required = true, value="楼栋编号", paramType = "query", dataType = "String"),
//            @ApiImplicitParam(name ="unitid", required = true, value="单元编号", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean addEditHouse(@RequestBody House house){
      try{
          if(!"".equals(house.getId()) && null != house.getId()){
          }else {
              house.setId(null);
          }
        this.houseService.saveOrUpdate(house);
          return getResponseBean(Result.OPERATE_SUCCESS);
      }catch (Exception e){
          e.printStackTrace();
          return getResponseBean(Result.OPERATE_FAIL);
      }
    }

    @ApiOperation(value ="房屋基本信息删除//已有人员信息不能删除")
    @RequestMapping(value="/delHouse", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value="房屋ID", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean delHouse(String id){
        try{
            House house = this.houseService.get(id);
            List<HouseOwner> list =   this.houseOwnerService.findBy("houseid",house.getHouseno());
            if(list.size()>0){
                log.info("此房屋下存在业主信息,不能删除");
                return getResponseBean(Result.find(CfI.R_HOUSE_DELETE_FAIL));
            }else{
                this.houseService.delete(id);
                log.info("房屋基本信息删除");
                return getResponseBean(Result.OPERATE_SUCCESS);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

    @ApiOperation(value = "房屋基本信息查看")
    @RequestMapping(value = "/viewHouse", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id" , required = true, value = "房屋ID", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean viewHouse(String id){
        try{
            House house  = this.houseService.getOne(id);
            log.info("房屋基本信息查看");
            return getResponseBean(Result.OPERATE_SUCCESS.setData(house));
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

    @ApiOperation(value = "房屋选择框select下拉查询")
    @RequestMapping(value = "getSelectHouse",method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "villageid" , required = true, value = "小区编号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "buildingid", required = true, value = "楼栋编号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "unitid", required = true, value = "单元编号", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean getSelectHouse(String villageid,String buildingid,String unitid){
        try{
            List list = this.houseService.getSelectHouse(villageid,buildingid, unitid);
            log.info("获取房屋下拉菜单");
            return getResponseBean(Result.OPERATE_SUCCESS.setData(list));
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

    @ApiOperation(value = "房屋标识查重校验")
    @RequestMapping(value = "checkbs", method = RequestMethod.GET)
    @ApiImplicitParams({
//            @ApiImplicitParam(name ="villageid", required = true, value="小区编号", paramType = "query", dataType = "String"),
//            @ApiImplicitParam(name ="buildingid", required = true, value="楼宇编号", paramType = "query", dataType = "String"),
//            @ApiImplicitParam(name = "unitid", required = true, value = "单元编号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name ="bs", required = true, value="标识(houseno;'房屋编号)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name ="bsValue", required = true, value="标识值", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean checkbs(String villageid,String buildingid,String unitid,String bs,String bsValue){
        Long count = 0L;
        try{
//            String sql = " select count(*) from T_HOUSE_19 where 1=1 and delflag = 0  and "+bs+"="+bsValue;
//            String sql = " select count(*) from T_HOUSE_19 where 1=1 and delflag = 0 and villageid = '"+villageid+"' and buildingid = '"+buildingid+"' and unitid = '"+unitid+"'  and "+bs+"="+bsValue;
            count = this.houseService.chekBs(villageid,buildingid,unitid,bs,bsValue);
            log.info("自定义字段查重校验");
            return getResponseBean(Result.OPERATE_SUCCESS.setData(count));
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }


    @ApiOperation(value = "房屋户型字典下拉")
    @RequestMapping(value = "getHousetypeDiction", method = RequestMethod.GET)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "pid", required = true, value="字典跟节点", paramType = "query", dataType = "String")
//    })
    @ResponseBody
    public ResponseBean getHousetypeDiction(){
        try{
            //房屋属性字典下拉码值
            String houseType = Configs.find(CfI.C_HOUSE_HOUSETYPE_DICTION).getValue();
            log.info("获取"+houseType+"的字典信息");
            return dictionController.listDictions(houseType);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

    @ApiOperation(value = "房屋类型字典下拉")
    @RequestMapping(value = "getHouseAttrubuteDiction", method = RequestMethod.GET)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "pid", required = true, value="字典跟节点", paramType = "query", dataType = "String")
//    })
    @ResponseBody
    public ResponseBean getHouseAttrubuteDiction(){
            try{
                //房屋属性字典下拉码值
                String house_attrubute = Configs.find(CfI.C_HOUSE_ATTRUBUTE_DICTION).getValue();
                log.info("获取"+house_attrubute+"的字典信息");
                return dictionController.listDictions(house_attrubute);
            }catch (Exception e){
                e.printStackTrace();
                log.error(e.getMessage());
                return getResponseBean(Result.OPERATE_FAIL);
            }
    }


    @ApiOperation(value = "租赁状态字典下拉")
    @RequestMapping(value = "getRentalDiction", method = RequestMethod.GET)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "pid", required = true, value="字典跟节点", paramType = "query", dataType = "String")
//    })
    @ResponseBody
    public ResponseBean getRentalDiction(){
        //租赁状态字典下拉码值
        try{
            String rental = Configs.find(CfI.C_HOUSE_RENTAL_DICTION).getValue();
            log.info("获取"+rental+"的字典信息");
            return dictionController.listDictions(rental);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

    @ApiOperation(value = "房屋信息导入模板下载")
    @RequestMapping(value = "downModelExcel", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean downModelExcel(HttpServletRequest request){
        ModelExcel modelExcel = new ModelExcel();
        try {

            //房屋属性字典下拉码值
            String house_attrubute = Configs.find(CfI.C_HOUSE_ATTRUBUTE_DICTION).getValue();

            //租赁状态字典下拉码值
            String rental = Configs.find(CfI.C_HOUSE_RENTAL_DICTION).getValue();

            String fileName = "房屋信息导入模板(标*的列为必填列).xls"; //模板名称
            String[] handers = {"房屋编号*","标准地址","标准地址编码","附属详址","户型",
                        "面积","房屋间数","房屋产权证号","产权人身份证号","产权人姓名",
                    "产权人联系电话","产权人身份证号","产权人姓名","产权人联系电话","所属楼层",
                    "所属单元*","单元名称","所属楼栋*","楼栋名称","小区编号*",
                    "小区名称","居住人数","房屋状态","租赁状态","房屋位置","房间名称"};
            //下拉框数据
            List<String[]> downData = new ArrayList();
            List list1 = this.houseService.getDictionForUpload(house_attrubute);
            String [] str1  = (String[]) list1.toArray(new String[list1.size()]);
            List list2 = this.houseService.getDictionForUpload(rental);
            String [] str2  = (String[]) list2.toArray(new String[list2.size()]);

            downData.add(str1);
            downData.add(str2);;
            String [] downRows = {"22","23"}; //下拉的列序号数组(序号从0开始)

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
            List result = this.houseService.saveUpdate(in);
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
