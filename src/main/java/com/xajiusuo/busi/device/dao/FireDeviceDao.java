package com.xajiusuo.busi.device.dao;

import com.xajiusuo.busi.device.entity.FireDevice;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author lizhidong
 * @date 2019-6-24 11:07
 */
public interface FireDeviceDao extends BaseDao<FireDevice, String>, JpaSpecificationExecutor<FireDevice> {

    List<FireDevice> findByDeviceIdAndDelFlagFalse(String deviceId);
}
