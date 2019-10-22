package com.xajiusuo.busi.device.controller;

import com.xajiusuo.busi.device.entity.VideoDevice;
import com.xajiusuo.busi.device.service.VideoDeviceService;
import com.xajiusuo.busi.device.vo.VideoDeviceVo;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * @author lizhidong
 * @date 2019-6-10 11:42
 */
@Api(description = "视频监控设备信息接口")
@RestController
@RequestMapping("/api/village/videoDevice")
public class VideoDeviceController extends BaseController {

    @Autowired
    private VideoDeviceService videoDeviceService;

    @ApiOperation(value = "视频监控设备信息新增", httpMethod = "POST")
    @PostMapping("/save")
    @ResponseBody
    public ResponseBean save(@RequestBody VideoDevice videoDevice) {
        return getResponseBean(Result.SAVE_SUCCESS.setData(this.videoDeviceService.save(this.request, videoDevice)));
    }

    @ApiOperation(value = "视频监控设备信息修改", httpMethod = "POST")
    @PostMapping("/update")
    @ResponseBody
    public ResponseBean update(@RequestBody VideoDevice videoDevice) {
        return getResponseBean(Result.UPDATE_SUCCESS.setData(this.videoDeviceService.update(videoDevice)));
    }

    @ApiOperation(value = "视频监控设备信息删除", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID", required = true, paramType = "path", dataType = "string")
    })
    @GetMapping("/delete/{id}")
    @ResponseBody
    public ResponseBean delete(@PathVariable("id") String id) {

        this.videoDeviceService.delete(this.request, id);
        return getResponseBean(Result.DELETE_SUCCESS);
    }

    @ApiOperation(value = "获取视频监控设备信息(简单信息)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备编码", required = true, paramType = "path", dataType = "string")
    })
    @GetMapping("/view/{deviceId}")
    @ResponseBody
    public ResponseBean view(@PathVariable("deviceId") String deviceId) {
        return getResponseBean(Result.QUERY_SUCCESS.setData(this.videoDeviceService.getVideoDeviceInfo(deviceId)));
    }

    @ApiOperation(value = "获取视频监控设备信息(完整信息)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备编码", required = true, paramType = "path", dataType = "string")
    })
    @GetMapping("/viewFull/{deviceId}")
    @ResponseBody
    public ResponseBean viewFull(@PathVariable("deviceId") String deviceId) {
        try {
            return getResponseBean(Result.QUERY_SUCCESS.setData(this.videoDeviceService.getVideoDeviceFullByDeviceId(deviceId)));
        } catch (Exception e) {
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @ApiOperation(value = "获取指定小区内的全部视频监控设备", httpMethod = "GET")
    @ApiImplicitParam(name = "villageId", value = "小区ID", required = true, paramType = "path", dataType = "string")
    @GetMapping("/getVideoDevicesGroup/{villageId}")
    @ResponseBody
    public ResponseBean getVideoDevicesGroup(@PathVariable("villageId") String villageId) {
        try {
            return getResponseBean(Result.QUERY_SUCCESS.setData(this.videoDeviceService.findAllVideoDevice(villageId).stream().collect(Collectors.groupingBy(VideoDeviceVo::getIsFocus))));
        } catch (Exception e) {
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

}
