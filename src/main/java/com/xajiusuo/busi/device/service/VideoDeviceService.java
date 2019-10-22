package com.xajiusuo.busi.device.service;

import com.xajiusuo.busi.device.entity.VideoDevice;
import com.xajiusuo.busi.device.vo.VideoDeviceVo;
import com.xajiusuo.jpa.config.BaseService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * @author lizhidong
 * @date 2019-6-10 11:21
 */
public interface VideoDeviceService extends BaseService<VideoDevice, String> {

    String save(HttpServletRequest request, VideoDevice videoDevice);

    @Deprecated
    String update(HttpServletRequest request, VideoDevice videoDevice);

    void delete(HttpServletRequest request, String id);

    VideoDevice getVideoDeviceInfo(String deviceId);

    List<VideoDevice> getVideoDevicesByDeviceIds(Set<String> deviceIds);

    /**
     * 根据设备编码获得视频监控设备完整信息
     *
     * @param deviceId 设备编码
     * @return
     */
    VideoDeviceVo getVideoDeviceFullByDeviceId(String deviceId);

    /**
     * 获取小区内全部视频监控设备
     *
     * @param villageId 小区编号
     * @return
     */
    List<VideoDeviceVo> findAllVideoDevice(String villageId);
}
