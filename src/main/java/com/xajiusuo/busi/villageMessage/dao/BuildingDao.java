package com.xajiusuo.busi.villageMessage.dao;

import com.xajiusuo.busi.villageMessage.entity.Building;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:22
 * @Description 楼栋基本信息
 */
public interface BuildingDao extends BaseDao<Building,String>,JpaSpecificationExecutor<Building> {
}
