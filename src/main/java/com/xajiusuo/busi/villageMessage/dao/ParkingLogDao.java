package com.xajiusuo.busi.villageMessage.dao;

import com.xajiusuo.busi.villageMessage.entity.ParkingLog;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:22
 * @Description 车位租赁流水记录
 */
public interface ParkingLogDao extends BaseDao<ParkingLog,String>,JpaSpecificationExecutor<ParkingLog> {
}
