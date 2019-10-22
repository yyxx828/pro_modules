package com.xajiusuo.busi.device.controller;

import com.xajiusuo.busi.device.entity.Device;
import com.xajiusuo.busi.device.entity.FireDevice;
import com.xajiusuo.busi.device.service.DeviceService;
import com.xajiusuo.busi.device.service.FireDeviceService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * @author lizhidong
 * @date 2019-6-28 10:41
 */
@Api(description = "消防设施设备信息接口")
@RestController
@RequestMapping("/api/village/fireDevice")
public class FireDeviceController extends BaseController {

    @Autowired
    private FireDeviceService fireDeviceService;
    @Autowired
    private DeviceService deviceService;

    @ApiOperation(value = "消防设施设备信息新增", httpMethod = "POST")
    @PostMapping("/save")
    @ResponseBody
    public ResponseBean save(@RequestBody FireDevice fireDevice) {
        try {
            return getResponseBean(Result.SAVE_SUCCESS.setData(this.fireDeviceService.save(fireDevice)));
        } catch (Exception e) {
            return getResponseBean(Result.SAVE_FAIL);
        }
    }

    @ApiOperation(value = "消防设施设备信息修改", httpMethod = "POST")
    @PostMapping("/update")
    @ResponseBody
    public ResponseBean update(@RequestBody FireDevice fireDevice) {
        try {
            return getResponseBean(Result.UPDATE_SUCCESS.setData(this.fireDeviceService.update(fireDevice)));
        } catch (Exception e) {
            return getResponseBean(Result.UPDATE_FAIL);
        }
    }

    @ApiOperation(value = "消防设施设备信息查看", httpMethod = "GET")
    @ApiImplicitParam(name = "deviceId", value = "设备编码", required = true, paramType = "path", dataType = "string")
    @GetMapping("/view/{deviceId}")
    @ResponseBody
    public ResponseBean view(@PathVariable("deviceId") String deviceId) {
        try {
            return getResponseBean(Result.QUERY_SUCCESS.setData(this.fireDeviceService.getFireDeviceInfo(deviceId)));
        } catch (Exception e) {
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @ApiOperation(value = "消防设施设备信息删除", httpMethod = "GET")
    @ApiImplicitParam(name = "id", value = "ID", required = true, paramType = "path", dataType = "string")
    @GetMapping("/delete/{id}")
    @ResponseBody
    public ResponseBean delete(@PathVariable("id") String id) {
        try {
            this.fireDeviceService.delete(id);
            return getResponseBean(Result.DELETE_SUCCESS);
        } catch (Exception e) {
            return getResponseBean(Result.DELETE_FAIL);
        }
    }

    @ApiOperation(value = "获取消防设备", httpMethod = "GET")
    @ApiImplicitParam(name = "villageId", value = "小区ID", required = true, paramType = "path", dataType = "string")
    @GetMapping("/getFireDevices/{villageId}")
    @ResponseBody
    public ResponseBean getFireDevices(@PathVariable("villageId") String villageId) {
        return Result.QUERY_SUCCESS.setData(this.fireDeviceService.getFireDevices(villageId)).toBean();
    }

    @ApiOperation(value = "消防设备列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为createTime,desc),", allowMultiple = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/queryFireDevicePage")
    @ResponseBody
    public ResponseBean queryFireDevicePage(Pageable pageable, Device device) {
        return getResponseBean(Result.QUERY_SUCCESS.setData(this.fireDeviceService.queryFireDevicePage(this.request, pageable, device)));
    }
}
