package com.xajiusuo.busi.buildingaccess.dao;

import com.xajiusuo.busi.buildingaccess.entity.BuildingEntranceGuardLog;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by wangdou on 2019/6/6
 */
public interface BuildingEntranceGuardLogDao extends BaseDao<BuildingEntranceGuardLog, String>, JpaSpecificationExecutor<BuildingEntranceGuardLog> {
}
