package com.xajiusuo.busi.motorVehicle.dao;

import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleInfo;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by shirenjing on 2019/6/14.
 */
public interface MotorVehicleInfoDao extends BaseDao<MotorVehicleInfo,String>,JpaSpecificationExecutor<MotorVehicleInfo> {
}
