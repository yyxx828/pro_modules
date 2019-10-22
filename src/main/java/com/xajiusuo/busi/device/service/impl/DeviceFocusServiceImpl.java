package com.xajiusuo.busi.device.service.impl;

import com.xajiusuo.busi.device.dao.DeviceFocusDao;
import com.xajiusuo.busi.device.entity.Device;
import com.xajiusuo.busi.device.entity.DeviceFocus;
import com.xajiusuo.busi.device.entity.VideoDevice;
import com.xajiusuo.busi.device.enums.ApeType;
import com.xajiusuo.busi.device.service.DeviceFocusService;
import com.xajiusuo.busi.device.service.DeviceService;
import com.xajiusuo.busi.device.service.VideoDeviceService;
import com.xajiusuo.busi.device.vo.VideoDeviceVo;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lizhidong
 * @date 2019-6-26 15:13
 */
@Service
public class DeviceFocusServiceImpl extends BaseServiceImpl<DeviceFocus, String> implements DeviceFocusService {

    @Autowired
    private DeviceFocusDao deviceFocusDao;

    @Autowired
    private DeviceService deviceService;
    @Autowired
    private VideoDeviceService videoDeviceService;

    @Override
    public BaseDao<DeviceFocus, String> getBaseDao() {
        return deviceFocusDao;
    }

    @Override
    public void deleteByDeviceId(String deviceId) {
        this.deviceFocusDao.deleteByDeviceId(deviceId);
    }

    @Override
    public List getFocusInfoByApeType(String villageId, Integer apeType) {
        Set<String> deviceIds = getFocusDeviceIdsByApeType(villageId, apeType);
        List result;
        switch (ApeType.valueBy(apeType)) {
            case VIDEO:
                List<Device> devices = this.deviceService.getDevicesByApeIds(deviceIds);
                List<VideoDevice> videoDevices = this.videoDeviceService.getVideoDevicesByDeviceIds(deviceIds);
                result = VideoDeviceVo.convert(devices, videoDevices, null, null);
                break;
            default:
                result = this.deviceService.getDevicesByApeIds(deviceIds);
                break;
        }
        return result;
    }

    @Override
    public Set<String> getFocusDeviceIdsByApeType(String villageId, Integer apeType) {
        return Optional.ofNullable(this.deviceFocusDao.findByVillageIdAndApeType(villageId, apeType)).map(n -> n.stream().map(DeviceFocus::getDeviceId).collect(Collectors.toSet())).orElse(null);
    }

    /**
     * 判断一个设备是否被关注
     *
     * @param deviceId
     * @return
     */
    @Override
    public boolean isFocusByDeviceId(String deviceId) {
        return Optional.ofNullable(this.deviceFocusDao.findBy("deviceId", deviceId)).map(n -> n.size() > 0).orElse(false);
    }
}
