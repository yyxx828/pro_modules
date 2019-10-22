package com.xajiusuo.busi.villageMessage.dao;

import com.xajiusuo.busi.villageMessage.entity.HouseLog;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:22
 * @Description 房屋租赁流水记录
 */
public interface HouseLogDao extends BaseDao<HouseLog,String>,JpaSpecificationExecutor<HouseLog> {
}
