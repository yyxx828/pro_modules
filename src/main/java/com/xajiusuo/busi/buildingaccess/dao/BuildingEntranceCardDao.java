package com.xajiusuo.busi.buildingaccess.dao;

import com.xajiusuo.busi.buildingaccess.entity.BuildingEntranceCard;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by wangdou on 2019/6/6
 */
public interface BuildingEntranceCardDao extends BaseDao<BuildingEntranceCard, String>, JpaSpecificationExecutor<BuildingEntranceCard> {
}
