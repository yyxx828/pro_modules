package com.xajiusuo.busi.device.service;

import com.xajiusuo.busi.device.entity.DeviceFocus;
import com.xajiusuo.jpa.config.BaseService;

import java.util.List;
import java.util.Set;

/**
 * @author lizhidong
 * @date 2019-6-26 15:13
 */
public interface DeviceFocusService extends BaseService<DeviceFocus, String> {

    void deleteByDeviceId(String deviceId);

    List getFocusInfoByApeType(String villageId, Integer apeType);

    Set<String> getFocusDeviceIdsByApeType(String villageId, Integer apeType);

    /**
     * 判断一个设备是否被关注
     *
     * @param deviceId
     * @return
     */
    boolean isFocusByDeviceId(String deviceId);

}
