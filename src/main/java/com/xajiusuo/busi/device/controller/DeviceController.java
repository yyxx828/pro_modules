package com.xajiusuo.busi.device.controller;

import com.xajiusuo.busi.device.entity.Device;
import com.xajiusuo.busi.device.service.DeviceService;
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
 * @date 2019-6-6 16:21
 */
@Api(description = "技防设备信息接口")
@RestController
@RequestMapping("/api/village/device")
public class DeviceController extends BaseController {

    @Autowired
    private DeviceService deviceService;

    @ApiOperation(value = "设备信息新增", httpMethod = "POST")
    @PostMapping("/save")
    @ResponseBody
    public ResponseBean save(@RequestBody Device device) {
        if (this.deviceService.existsDevice(device.getApeId(), null)) {
            return getResponseBean(Result.SAVE_FAIL.setData("设备编码已存在"));
        }
        return getResponseBean(Result.SAVE_SUCCESS.setData(this.deviceService.save(this.request, device)));
    }

    @ApiOperation(value = "设备信息修改", httpMethod = "POST")
    @PostMapping("/update")
    @ResponseBody
    public ResponseBean update(@RequestBody Device device) {
        if (this.deviceService.existsDevice(device.getApeId(), device.getId())) {
            return getResponseBean(Result.SAVE_FAIL.setData("设备编码已存在"));
        }
        return getResponseBean(Result.UPDATE_SUCCESS.setData(this.deviceService.update(device)));
    }

    @ApiOperation(value = "设备信息删除", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备ID", required = true, paramType = "path", dataType = "string")
    })
    @GetMapping("/delete/{id}")
    @ResponseBody
    public ResponseBean delete(@PathVariable("id") String id) {
        this.deviceService.delete(this.request, id);
        return getResponseBean(Result.DELETE_SUCCESS);
    }

    @ApiOperation(value = "获取设备信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备ID", required = true, paramType = "path", dataType = "string")
    })
    @GetMapping("/view/{id}")
    @ResponseBody
    public ResponseBean view(@PathVariable("id") String id) {
        return getResponseBean(Result.QUERY_SUCCESS.setData(this.deviceService.getEm().find(Device.class, id)));
    }

    @ApiOperation(value = "设备列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为createTime,desc),", allowMultiple = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/queryPage")
    @ResponseBody
    public ResponseBean queryPage(Pageable pageable, Device device) {
        return getResponseBean(Result.QUERY_SUCCESS.setData(this.deviceService.querPage(this.request, pageable, device)));
    }

    @ApiOperation(value = "获取指定小区内指定类型的设备信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "villageId", value = "小区ID", required = true, paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "apeType", value = "设备类型", required = false, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "name", value = "设备名称", required = false, paramType = "query", dataType = "string")
    })
    @GetMapping("/getDevicesByApeType/{villageId}")
    @ResponseBody
    public ResponseBean getDevicesByApeType(@PathVariable("villageId") String villageId, @RequestParam(value = "apeType", required = false) Integer apeType, @RequestParam(value = "name", required = false) String name) {
        return getResponseBean(Result.QUERY_SUCCESS.setData(this.deviceService.getDevicesByApeType(villageId, apeType, name)));
    }

    @ApiOperation(value = "获取指定小区内多个类型的设备信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "villageId", value = "小区ID", required = true, paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "apeTypes", value = "设备类型，多个类型用,分割", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/getDevicesByApeTypes/{villageId}")
    @ResponseBody
    public ResponseBean getDevicesByApeTypes(@PathVariable("villageId") String villageId, @RequestParam String apeTypes) {
        return getResponseBean(Result.QUERY_SUCCESS.setData(this.deviceService.getDevicesByApeTypes(villageId, apeTypes)));
    }

    @ApiOperation(value = "验证设备是否已存在", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "apeId", value = "设备编码", required = true, paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "id", value = "ID", required = false, paramType = "query", dataType = "string")
    })
    @GetMapping("/existsDevice/{apeId}")
    @ResponseBody
    public ResponseBean existsDevice(@PathVariable("apeId") String apeId, @RequestParam(required = false) String id) {
        return getResponseBean(Result.VALIDATE_SUCCESS.setData(this.deviceService.existsDevice(apeId, id)));
    }
}
