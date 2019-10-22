package com.xajiusuo.busi.villageMessage.controller;

import com.xajiusuo.busi.villageMessage.entity.House;
import com.xajiusuo.busi.villageMessage.entity.Unit;
import com.xajiusuo.busi.villageMessage.service.HouseService;
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
@Api(description = "单元基本信息")
@RestController
@RequestMapping(value ="/api/village/unit")
public class UnitController extends BaseController{

    @Autowired
    private UnitService unitService;

    @Autowired
    private HouseService houseService;

    @ApiOperation(value = "单元基本信息分页展示及查询")
    @RequestMapping(value = "/pageUnit", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="villageid",required = false,value="小区编号", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="buildingid", required = false,value = "楼栋编号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean pageUnit(Pageable pageable, Unit unit) {

        try {
            Page<Unit> page = this.unitService.getsqlpage(pageable,unit);
            return getResponseBean(Result.OPERATE_SUCCESS.setData(page));
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.DELETE_FAIL);
        }
    }

    @ApiOperation(value = "单元基本信息新增/修改")
    @RequestMapping(value = "/addEditUnit", method = RequestMethod.POST)
    @ApiImplicitParams({

    })
    @ResponseBody
    public ResponseBean addEditUnit(@RequestBody Unit unit){
        try {
            if(!"".equals(unit.getId()) && null!=unit.getId()){
            }else{
                unit.setId(null);
            }
            this.unitService.saveOrUpdate(unit);
            return getResponseBean(Result.OPERATE_SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.OPERATE_FAIL);
        }

    }

    @ApiOperation(value ="单元基本信息删除/关联删除单元下的所有房屋,业主信息")
    @RequestMapping(value="/delUnit", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value="单元ID", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean delUnit(String id){
        try{
            Unit unit = this.unitService.getOne(id);
            List<House> list =   this.houseService.findBy("unitid",unit.getUnitno());
            if(list.size()>0){
                log.info("此单元下存在房屋信息,不能删除");
                return getResponseBean(Result.find(CfI.R_UNIT_DELETE_FAIL));
            }
            unit.setDelFlag(true);
            this.unitService.delete(unit);
            log.info("单元基本信息删除");
//            this.villageService.u   //暂时先屏蔽
            return getResponseBean(Result.OPERATE_SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_SUCCESS);
        }
    }

    @ApiOperation(value = "单元基本信息查看")
    @RequestMapping(value = "/viewUnit", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id" , required = true, value = "单元ID", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean viewUnit(String id){
        try{
           Unit unit = this.unitService.getOne(id);
            log.info("单元基本信息查看");
            return getResponseBean(Result.OPERATE_SUCCESS.setData(unit));
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

    @ApiOperation(value = "单元选择框select下拉查询")
    @RequestMapping(value = "getSelectUnit",method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "villageid" , required = true, value = "小区编号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "buildingid", required = true, value = "楼栋编号", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean getSelectUnit(String villageid,String buildingid){
        try{
            List list = this.unitService.getSelectUnit(villageid,buildingid);
            return getResponseBean(Result.OPERATE_SUCCESS.setData(list));
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.DELETE_FAIL);
        }
    }

    @ApiOperation(value = "单元标识查重校验")
    @RequestMapping(value = "checkbs", method = RequestMethod.GET)
    @ApiImplicitParams({
//            @ApiImplicitParam(name ="villageid", required = true, value="小区编号", paramType = "query", dataType = "String"),
//            @ApiImplicitParam(name ="buildingid", required = true, value="楼宇编号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name ="bs", required = true, value="标识(unitno;'单元编号'.unit_name;单元名称)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name ="bsValue", required = true, value="标识值", paramType = "query", dataType = "String")
    })
    @ResponseBody
    public ResponseBean checkbs(String villageid,String buildingid,String bs,String bsValue){
        try{
//            String sql = " select count(*) from T_UNIT_19 where 1=1 and delflag = 0  and "+bs+"="+bsValue;
//            String sql = " select count(*) from T_UNIT_19 where 1=1 and delflag = 0 and villageid = '"+villageid+"' and buildingid = '"+buildingid+"'  and "+bs+"="+bsValue;
            Long  count = this.unitService.chekBs(villageid,buildingid,bs,bsValue);
            log.info("自定义字段查重校验");
            return getResponseBean(Result.OPERATE_SUCCESS.setData(count));
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return getResponseBean(Result.OPERATE_FAIL);
        }
    }

}
