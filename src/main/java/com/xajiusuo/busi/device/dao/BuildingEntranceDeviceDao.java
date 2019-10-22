package com.xajiusuo.busi.device.dao;

import com.xajiusuo.busi.device.entity.BuildingEntranceDevice;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author lizhidong
 * @date 2019-6-19 17:13
 */
public interface BuildingEntranceDeviceDao  extends BaseDao<BuildingEntranceDevice, String>, JpaSpecificationExecutor<BuildingEntranceDevice> {

    List<BuildingEntranceDevice> findByDeviceIdAndDelFlagFalse(String deviceId);
}
