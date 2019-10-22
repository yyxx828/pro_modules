package com.xajiusuo.busi.device.controller;

import com.xajiusuo.busi.device.entity.DeviceFocus;
import com.xajiusuo.busi.device.service.DeviceFocusService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author lizhidong
 * @date 2019-6-26 15:17
 */
@Api(description = "设备关注信息接口")
@RestController
@RequestMapping("/api/village/deviceFocus")
public class DeviceFocusController extends BaseController {

    @Autowired
    private DeviceFocusService deviceFocusService;

    @ApiOperation(value = "保存设备关注信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "villageId", value = "小区编号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "deviceId", value = "设备ID", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "apeType", value = "设备类型", required = true, paramType = "query", dataType = "int")
    })
    @GetMapping("/save")
    @ResponseBody
    public ResponseBean save(@RequestParam String villageId, @RequestParam String deviceId, @RequestParam Integer apeType) {
        try {
            return getResponseBean(Result.SAVE_SUCCESS.setData(this.deviceFocusService.save(new DeviceFocus(villageId, deviceId, apeType))));
        } catch (Exception e) {
            return getResponseBean(Result.SAVE_FAIL);
        }
    }

    @ApiOperation(value = "删除设备关注信息", httpMethod = "GET")
    @ApiImplicitParam(name = "deviceId", value = "设备ID", required = true, paramType = "path", dataType = "string")
    @GetMapping("/delete/{deviceId}")
    @ResponseBody
    public ResponseBean delete(@PathVariable("deviceId") String deviceId) {
        try {
            this.deviceFocusService.deleteByDeviceId(deviceId);
            return getResponseBean(Result.DELETE_SUCCESS);
        } catch (Exception e) {
            return getResponseBean(Result.DELETE_FAIL);
        }
    }

    @ApiOperation(value = "按照类型获取已关注的设备信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "villageId", value = "小区编号", required = true, paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "apeType", value = "设备类型", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping("/getFocusInfoByApeType/{villageId}/{apeType}")
    @ResponseBody
    public ResponseBean getFocusInfoByApeType(@PathVariable("villageId") String villageId, @PathVariable("apeType") Integer apeType) {
        try {
            return getResponseBean(Result.QUERY_SUCCESS.setData(this.deviceFocusService.getFocusInfoByApeType(villageId, apeType)));
        } catch (Exception e) {
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @ApiOperation(value = "按照类型获取已关注的设备ID", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "villageId", value = "小区编号", required = true, paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "apeType", value = "设备类型", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping("/getFocusDeviceIdsByApeType/{villageId}/{apeType}")
    @ResponseBody
    public ResponseBean getFocusDeviceIdsByApeType(@PathVariable("villageId") String villageId, @PathVariable("apeType") Integer apeType) {
        try {
            return getResponseBean(Result.QUERY_SUCCESS.setData(this.deviceFocusService.getFocusDeviceIdsByApeType(villageId, apeType)));
        } catch (Exception e) {
            return getResponseBean(Result.QUERY_FAIL);
        }
    }
}
