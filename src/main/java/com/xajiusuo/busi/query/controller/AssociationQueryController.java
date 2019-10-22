package com.xajiusuo.busi.query.controller;

import com.google.common.base.Objects;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleInfo;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleInfoVo;
import com.xajiusuo.busi.query.entity.TypeEnum;
import com.xajiusuo.busi.query.service.QueryService;
import com.xajiusuo.busi.query.service.impl.QueryServiceImpl;
import com.xajiusuo.busi.villageMessage.entity.House;
import com.xajiusuo.busi.villageMessage.entity.Owner;
import com.xajiusuo.busi.villageMessage.service.HouseService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(description = "关联查询")
@RestController
@RequestMapping(value = "/api/village/query")
public class AssociationQueryController extends BaseController {

    @Autowired
    QueryService repository;

    @Autowired
    HouseService houseRepository;

    @GetMapping(value = "/getOwner")
    @ApiOperation(value = "通过id获取业主基本信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "业主id", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean getOwner(String id) {
        try {
            return getResponseBean(Result.QUERY_SUCCESS.setData(repository.getOwner(id)));
        } catch (Exception e) {
            log.error("通过id获取业主基本信息失败");
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @GetMapping(value = "/getInfo")
    @ApiOperation(value = "通过id获取基本信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "id", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean getInfo(String id) {
        try {
            if (!id.contains("_")) return getResponseBean(Result.QUERY_FAIL);
            String flag = id.split("_")[0];
            if (Objects.equal(TypeEnum.HOUSE.name(), flag)) {
                return getResponseBean(Result.QUERY_SUCCESS.setData(repository.getHouse(id.split("_")[1])));
            } else if (Objects.equal(TypeEnum.OWNER.name(), flag)) {
                return getResponseBean(Result.QUERY_SUCCESS.setData(repository.getOwner(id.split("_")[1])));
            }
            return getResponseBean(Result.QUERY_SUCCESS.setData(repository.getCar(id.split("_")[1])));
        } catch (Exception e) {
            log.error("通过id获取基本信息失败");
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @RequestMapping(value = "/queryOwnerList", method = RequestMethod.POST)
    @ApiOperation(value = "业主基本信息列表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(lastModifyTime,desc),", example = "lastModifyTime,desc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean queryOwnerList(Pageable pageable, @RequestBody Owner owner) {
        try {
            return getResponseBean(Result.QUERY_SUCCESS.setData(repository.queryOwnerList(owner, pageable)));
        } catch (Exception e) {
            log.error("通过业主基本信息列表信息失败");
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @GetMapping(value = "/getHouse")
    @ApiOperation(value = "通过id获取房屋基本信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "房屋id", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean getHouse(String id) {
        try {
            return getResponseBean(Result.QUERY_SUCCESS.setData(repository.getHouse(id)));
        } catch (Exception e) {
            log.error("通过id获取房屋基本信息信息失败");
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @RequestMapping(value = "/queryHouseList", method = RequestMethod.POST)
    @ApiOperation(value = "房屋基本信息列表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(lastModifyTime,desc),", example = "lastModifyTime,desc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean queryHouseList(Pageable pageable, @RequestBody House house) {
        try {

            return getResponseBean(Result.QUERY_SUCCESS.setData(houseRepository.getsqlpage(pageable, house)));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @GetMapping(value = "/getMotorvehicle")
    @ApiOperation(value = "通过id获取车辆基本信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "車輛id", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean getMotorvehicle(String id) {
        try {
            return getResponseBean(Result.QUERY_SUCCESS.setData(repository.getCar(id)));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }


    @RequestMapping(value = "/queryMotorvehicleList", method = RequestMethod.POST)
    @ApiOperation(value = "车辆基本信息列表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(lastModifyTime,desc),", example = "lastModifyTime,desc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean queryHouseList(Pageable pageable, @RequestBody MotorVehicleInfo info) {
        try {
            return getResponseBean(Result.QUERY_SUCCESS.setData(repository.queryMotorvehicleList(info, pageable)));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }


    @GetMapping(value = "/getAssociationQuery")
    @ApiOperation(value = "通过id获取关联信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "id", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flag", required = true, value = "查询的数据的类型标志", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean getAssociationQuery(TypeEnum flag, String id) {
        try {
            return getResponseBean(Result.QUERY_SUCCESS.setData(repository.getAssociationQuery(flag, id)));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @GetMapping(value = "/getBuildingEntranceCardLog")
    @ApiOperation(value = "楼宇门禁信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(lastModifyTime,desc),", example = "passTime,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "idCard", required = true, value = "证件号码", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean getBuildingEntranceCardLog(Pageable pageable, String idCard) {
        try {
            return getResponseBean(Result.QUERY_SUCCESS.setData(repository.getBuildingEntranceCardLog(idCard, pageable)));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @GetMapping(value = "/getGateEntranceGuardLog")
    @ApiOperation(value = "出入口门禁信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(lastModifyTime,desc),", example = "passTime,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "idNumber", required = true, value = "证件号码", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean getGateEntranceGuardLog(Pageable pageable, String idNumber) {
        try {
            pageable = PageRequest.of(((Pageable) pageable).getPageNumber() - 1, ((Pageable) pageable).getPageSize(), ((Pageable) pageable).getSort());
            return getResponseBean(Result.QUERY_SUCCESS.setData(repository.getGateEntranceGuardLog(idNumber, pageable)));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @GetMapping(value = "/getHouseLogByHouseId")
    @ApiOperation(value = "通过房屋编号 查找房屋租赁信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(lastModifyTime,desc),", example = "lastModifyTime,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "houseId", required = true, value = "房屋編號", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean getHouseLogByHouseId(Pageable pageable, String houseId) {
        try {
            House house = new House();
            house.setHouseno(houseId);
            return getResponseBean(Result.QUERY_SUCCESS.setData(repository.getHouseLogByHouseId(house, pageable)));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }


    @GetMapping(value = "/getHouseLogByIdCard")
    @ApiOperation(value = "通过证件号 查找房屋租赁信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idCard", required = true, value = "证件号", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean getHouseLogByIdCard(String idCard) {
        try {
            return getResponseBean(Result.QUERY_SUCCESS.setData(repository.getHouseLogByIdCard(idCard)));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @GetMapping(value = "/getMotorVehicleLog")
    @ApiOperation(value = "通过车牌号 查找車輛出入信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(passTime,desc),", example = "lastModifyTime,desc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "plateno", required = true, value = "车牌号", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean getMotorVehicleLog(String plateno, Pageable pageable) {
        try {
            return getResponseBean(Result.QUERY_SUCCESS.setData(repository.getMotorVehicleLog(plateno, pageable)));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }


}
