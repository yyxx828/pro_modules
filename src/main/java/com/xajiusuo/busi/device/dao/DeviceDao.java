package com.xajiusuo.busi.device.dao;

import com.xajiusuo.busi.device.entity.Device;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

/**
 * @author lizhidong
 * @date 2019-6-6 16:21
 */
public interface DeviceDao extends BaseDao<Device, String>, JpaSpecificationExecutor<Device> {

    List<Device> findByVillageIdAndApeTypeAndDelFlagFalse(String villageId, Integer apeType);

    List<Device> findByVillageIdAndDelFlagFalse(String villageId);

    Long countByApeIdAndDelFlagFalse(String apeId);

    Long countByApeIdAndIdNotAndDelFlagFalse(String apeId, String id);

    List<Device> findByVillageIdAndApeTypeIn(String villageId, Collection<Integer> apeTypes);

    List<Device> findByApeIdInAndDelFlagFalse(Collection<String> apeIds);

}
