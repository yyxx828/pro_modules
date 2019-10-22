package com.xajiusuo.busi.villageMessage.dao;

import com.xajiusuo.busi.villageMessage.entity.Village;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author GaoYong
 * @Date 19-6-5 下午12:07
 * @Description 小区基本信息数据层
 */
public interface VillageDao extends BaseDao<Village, String>,JpaSpecificationExecutor<Village> {

}
