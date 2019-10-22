package com.xajiusuo.busi.device.controller;

import com.xajiusuo.busi.device.entity.BuildingEntranceDevice;
import com.xajiusuo.busi.device.service.BuildingEntranceDeviceService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author lizhidong
 * @date 2019-6-19 17:21
 */
@Api(description = "楼宇门禁设备信息接口")
@RestController
@RequestMapping("/api/village/buildingEntranceDevice")
public class BuildingEntranceDeviceController extends BaseController {

    @Autowired
    private BuildingEntranceDeviceService buildingEntranceDeviceService;

    @ApiOperation(value = "楼宇门禁设备信息新增", httpMethod = "POST")
    @PostMapping("/save")
    @ResponseBody
    public ResponseBean save(@RequestBody BuildingEntranceDevice buildingEntranceDevice) {
        return getResponseBean(Result.SAVE_SUCCESS.setData(this.buildingEntranceDeviceService.save(buildingEntranceDevice)));
    }

    @ApiOperation(value = "楼宇门禁设备信息修改", httpMethod = "POST")
    @PostMapping("/update")
    @ResponseBody
    public ResponseBean update(@RequestBody BuildingEntranceDevice buildingEntranceDevice) {
        return getResponseBean(Result.SAVE_SUCCESS.setData(this.buildingEntranceDeviceService.update(buildingEntranceDevice)));
    }

    @ApiOperation(value = "楼宇门禁设备信息删除", httpMethod = "GET")
    @ApiImplicitParam(name = "id", value = "ID", required = true, paramType = "query", dataType = "string")
    @GetMapping("/delete/{id}")
    @ResponseBody
    public ResponseBean delete(@PathVariable("id") String id) {
        this.buildingEntranceDeviceService.delete(id);
        return getResponseBean(Result.DELETE_SUCCESS);
    }

    @ApiOperation(value = "楼宇门禁设备信息查看", httpMethod = "GET")
    @ApiImplicitParam(name = "deviceId", value = "设备编码", required = true, paramType = "path", dataType = "string")
    @GetMapping("/view/{deviceId}")
    @ResponseBody
    public ResponseBean view(@PathVariable("deviceId") String deviceId) {
        try {
            return getResponseBean(Result.QUERY_SUCCESS.setData(this.buildingEntranceDeviceService.getBEDevice(deviceId)));
        } catch (Exception e) {
            return getResponseBean(Result.QUERY_FAIL);
        }
    }
}
