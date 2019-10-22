package com.xajiusuo.busi.device.service.impl;

import com.xajiusuo.busi.backupandops.service.OpsService;
import com.xajiusuo.busi.device.dao.VideoDeviceDao;
import com.xajiusuo.busi.device.entity.Device;
import com.xajiusuo.busi.device.entity.VideoDevice;
import com.xajiusuo.busi.device.enums.ApeType;
import com.xajiusuo.busi.device.service.DeviceFocusService;
import com.xajiusuo.busi.device.service.DeviceService;
import com.xajiusuo.busi.device.service.VideoDeviceService;
import com.xajiusuo.busi.device.vo.VideoDeviceVo;
import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.utils.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lizhidong
 * @date 2019-6-10 11:20
 */
@Service
public class VideoDeviceServiceImpl extends BaseServiceImpl<VideoDevice, String> implements VideoDeviceService {

    @Autowired
    private VideoDeviceDao videoDeviceDao;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeviceFocusService deviceFocusService;
    @Autowired
    private OpsService opsService;

    @Override
    public BaseDao<VideoDevice, String> getBaseDao() {
        return videoDeviceDao;
    }

    @Override
    public String save(HttpServletRequest request, VideoDevice videoDevice) {
        if (null == videoDevice)
            return null;
        UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
        if (null != userInfoVo) {
            videoDevice.setCreateUID(userInfoVo.getUserId());
            videoDevice.setCreateName(userInfoVo.getFullName());
            videoDevice.setLastModifyUID(userInfoVo.getUserId());
        }
        videoDevice.setCreateTime(new Date());
        videoDevice.setLastModifyTime(new Date());
        videoDevice.setDelFlag(false);
        return this.videoDeviceDao.save(videoDevice).getId();
    }

    @Override
    public String update(HttpServletRequest request, VideoDevice videoDevice) {
        if (null == videoDevice)
            return null;
        if (StringUtils.isNotEmpty(videoDevice.getId())) {
            VideoDevice oldVideoDevice = this.videoDeviceDao.getOne(videoDevice.getId());
            UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
            if (null != userInfoVo) {
                videoDevice.setLastModifyUID(userInfoVo.getUserId());
            }
            videoDevice.setLastModifyTime(new Date());
            videoDevice.setCreateUID(oldVideoDevice.getCreateUID());
            videoDevice.setCreateName(oldVideoDevice.getCreateName());
            videoDevice.setCreateTime(oldVideoDevice.getCreateTime());
            videoDevice.setDelFlag(oldVideoDevice.getDelFlag());
            return this.videoDeviceDao.update(videoDevice).getId();
        } else {
            throw new IllegalArgumentException("只能修改已存在信息！");
        }
    }

    @Override
    public void delete(HttpServletRequest request, String id) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("请传递正确的ID！");
        }
        this.videoDeviceDao.findById(id).map(n -> {
            this.videoDeviceDao.delete(n);
            return null;
        });
    }

    @Override
    public VideoDevice getVideoDeviceInfo(String deviceId) {
        return
                Optional.ofNullable(deviceId).map(n -> {
                    List<VideoDevice> videoDevices = this.videoDeviceDao.findByDeviceIdAndDelFlagFalse(deviceId);
                    if (null != videoDevices && videoDevices.size() > 0)
                        return videoDevices.get(0);
                    else
                        return null;
                }).orElse(null);
    }

    @Override
    public List<VideoDevice> getVideoDevicesByDeviceIds(Set<String> deviceIds) {
        return this.videoDeviceDao.findByDeviceIdInAndDelFlagFalse(deviceIds);
    }

    /**
     * 根据设备编码获得视频监控设备完整信息
     *
     * @param deviceId 设备编码
     * @return
     */
    @Override
    public VideoDeviceVo getVideoDeviceFullByDeviceId(String deviceId) {
        return
                Optional.ofNullable(deviceId).map(n -> {
                    Device device = this.deviceService.getDeviceByApeId(deviceId);
                    VideoDevice videoDevice = getVideoDeviceInfo(deviceId);
                    Integer isFocus = deviceFocusService.isFocusByDeviceId(deviceId) ? 1 : 0;
                    Integer lineState = Optional.ofNullable(this.opsService.getDevStatus(device.getIpAddr())).map(m -> (null == m.get(device.getIpAddr()) ? 1 : Integer.parseInt(m.get(device.getIpAddr()) + ""))).orElse(1);
                    return new VideoDeviceVo(device, videoDevice, lineState, isFocus);
                }).orElse(null);
    }

    /**
     * 获取小区内全部视频监控设备
     *
     * @param villageId 小区编号
     * @return
     */
    @Override
    public List<VideoDeviceVo> findAllVideoDevice(String villageId) {
        List<Device> devices = this.deviceService.getDevicesByApeType(villageId, ApeType.VIDEO.getValue(), null);
        List<VideoDevice> videoDevices = this.videoDeviceDao.findByDelFlagFalse();
        Set<String> isFocusDeviceIds = this.deviceFocusService.getFocusDeviceIdsByApeType(villageId, ApeType.VIDEO.getValue());
        Map<String, Object> lineStates = this.opsService.getDevStatus(StringUtils.join(devices.stream().map(Device::getIpAddr).collect(Collectors.toList()), ","));
        lineStates.forEach((k, v) -> {
            if ("网络断开".equals(v)) {
                lineStates.put(k, 0);
            } else {
                lineStates.put(k, 1);
            }
        });
        return VideoDeviceVo.convert(devices, videoDevices, lineStates, isFocusDeviceIds);
    }
}
