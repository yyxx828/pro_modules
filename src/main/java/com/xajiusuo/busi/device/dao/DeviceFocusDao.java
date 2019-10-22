package com.xajiusuo.busi.device.dao;

import com.xajiusuo.busi.device.entity.DeviceFocus;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author lizhidong
 * @date 2019-6-26 14:58
 */
public interface DeviceFocusDao extends BaseDao<DeviceFocus, String>, JpaSpecificationExecutor<DeviceFocus> {

    @Modifying
    @Transactional
    void deleteByDeviceId(String deviceId);

    List<DeviceFocus> findByVillageIdAndApeType(String villageId, Integer apeType);
}
