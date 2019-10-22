package com.xajiusuo.busi.device.dao;

import com.xajiusuo.busi.device.entity.VideoDevice;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

/**
 * @author lizhidong
 * @date 2019-6-10 11:17
 */
public interface VideoDeviceDao extends BaseDao<VideoDevice, String>, JpaSpecificationExecutor<VideoDevice> {

    List<VideoDevice> findByDeviceIdAndDelFlagFalse(String deviceId);

    List<VideoDevice> findByDeviceIdInAndDelFlagFalse(Collection<String> deviceIds);

    List<VideoDevice> findByDelFlagFalse();
}
