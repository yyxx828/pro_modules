package com.xajiusuo.busi.device.service.impl;

import com.xajiusuo.busi.device.dao.BuildingEntranceDeviceDao;
import com.xajiusuo.busi.device.entity.BuildingEntranceDevice;
import com.xajiusuo.busi.device.service.BuildingEntranceDeviceService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author lizhidong
 * @date 2019-6-19 17:13
 */
@Service
public class BuildingEntranceDeviceServiceImpl extends BaseServiceImpl<BuildingEntranceDevice, String> implements BuildingEntranceDeviceService {

    @Autowired
    private BuildingEntranceDeviceDao buildingEntranceDeviceDao;

    @Override
    public BaseDao<BuildingEntranceDevice, String> getBaseDao() {
        return buildingEntranceDeviceDao;
    }

    /**
     * 通过设备编码获取门禁设备信息
     *
     * @param apeId 设备编码
     * @return
     */
    @Override
    public BuildingEntranceDevice getBEDevice(String apeId) {
        return Optional.ofNullable(this.buildingEntranceDeviceDao.findByDeviceIdAndDelFlagFalse(apeId)).map(n -> (n.size() > 0) ? n.get(0) : null).orElse(null);
    }
}
