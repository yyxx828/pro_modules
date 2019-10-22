package com.xajiusuo.busi.villageMessage.controller;

import com.xajiusuo.busi.villageMessage.entity.Building;
import com.xajiusuo.busi.villageMessage.entity.Unit;
import com.xajiusuo.busi.villageMessage.service.BuildingService;
import com.xajiusuo.busi.villageMessage.service.UnitService;
import com.xajiusuo.jpa.config.BaseController;
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

import java.util.List;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:31
 * @Description
 */
@Slf4j
@Api(description = "楼栋基本信息")
@RestController
@RequestMapping(value ="/api/village/building")
public class BuildingController extends BaseController{

    @Autowired
    private BuildingService buildingService;

    @Autowired
    UnitService unitService;

    @ApiOperation(value = "楼栋基本信息分页展示及查询")
    @RequestMapping(value = "/pageBuilding", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="villageid",required = false,value="小区编号", paramType = "false", dataType="String"),
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean pageBuilding(Pageable pageable, Building building) {
        Page<Building> page = null;
        try {
            page = this.buildingService.getsqlpage(pageable,building);
        }catch (Exception e){
            e.printStackTrace();
            getResponseBean(Result.OPERATE_FAIL);
        }
        return getResponseBean(Result.OPERATE_SUCCESS.setData(page));
    }

    @ApiOperation(value = "楼栋基本信息新增/修改")
    @RequestMapping(value = "/addEditBuilding", method = RequestMethod.POST)
    @ApiImplicitParams({
//            @ApiImplicitParam(name = "villageid" , required = true, value = "小区编号", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean addEditBuilding(@RequestBody Building building){
        try {
            if(!"".equals(building.getId()) && null != building.getId()){

            }else{
                building.setId(null);
            }
            this.buildingService.saveOrUpdate(building);
        }catch (Exception e){
            e.printStackTrace();
            getResponseBean(Result.OPERATE_FAIL);
        }
        return getResponseBean(Result.OPERATE_SUCCESS);

    }

    @ApiOperation(value ="楼栋基本信息删除/关联删除楼栋下的所有单元,房屋,业主信息")
    @RequestMapping(value="/delBuilding", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value="楼栋ID", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean delBuilding(String id){
        try{
            Building building = this.buildingService.getOne(id);

            List<Unit> list = this.unitService.findBy("buildingid",building.getBuildingno());
            if(list.size()>0){
                log.info("此楼栋下存在单元信息,不能删除");
                return getResponseBean(Result.find(CfI.R_BUILDING_DELETE_FAIL));
            }
            building.setDelFlag(true);
            this.buildingService.delete(building);
            log.info("楼栋基本信息删除");
//            this.villageService.u   //暂时先屏蔽
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
        return getResponseBean(Result.OPERATE_SUCCESS);
    }

    @ApiOperation(value = "楼栋基本信息查看")
    @RequestMapping(value = "/viewBuilding", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id" , required = true, value = "楼栋ID", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean viewBuilding(String id){
        try{
            Building building = this.buildingService.getOne(id);
            log.info("楼栋基本信息查看");
            return getResponseBean(Result.OPERATE_SUCCESS.setData(building));
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

    @ApiOperation(value = "楼栋选择框select下拉查询")
    @RequestMapping(value = "getSelectBuilding",method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "villageid" , required = true, value = "小区编号", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean getSelectBuilding(String villageid){
        List list = null;
        try{
            list = this.buildingService.getSelectBulid(villageid);
        }catch (Exception e){
            e.printStackTrace();

        }
        return getResponseBean(Result.OPERATE_SUCCESS.setData(list));
    }

    @ApiOperation(value = "楼栋标识查重校验")
    @RequestMapping(value = "checkbs", method = RequestMethod.GET)
    @ApiImplicitParams({
//            @ApiImplicitParam(name ="villageid", required = true, value="小区编号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name ="bs", required = true, value="标识(buildingno;'楼宇编号'.buildingname;楼宇名称)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name ="bsValue", required = true, value="标识值", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean checkbs(String villageid,String bs,String bsValue){
        Long count = 0L;
        try{
//            String sql = " select count(*) from T_BUILDING_19 where 1=1 and delflag = 0 and villageid = '"+villageid+"'  and "+bs+"="+bsValue;
            count = this.buildingService.chekBs(villageid,bs,bsValue);
            log.info("自定义字段查重校验");
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.DELETE_FAIL.OPERATE_FAIL);
        }
        return getResponseBean(Result.OPERATE_SUCCESS.setData(count));
    }
}
