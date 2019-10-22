package com.xajiusuo.busi.device.service;

import com.xajiusuo.busi.device.entity.BuildingEntranceDevice;
import com.xajiusuo.jpa.config.BaseService;

/**
 * @author lizhidong
 * @date 2019-6-19 17:11
 */
public interface BuildingEntranceDeviceService extends BaseService<BuildingEntranceDevice, String> {

    /**
     * 通过设备编码获取门禁设备信息
     *
     * @param apeId 设备编码
     * @return
     */
    BuildingEntranceDevice getBEDevice(String apeId);


}
