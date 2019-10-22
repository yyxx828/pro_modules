package com.xajiusuo.busi.motorVehicle.dao;

import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleWarn;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by hadoop on 19-7-1.
 */
public interface MotorVehicleWarnDao extends BaseDao<MotorVehicleWarn,String>,JpaSpecificationExecutor<MotorVehicleWarn> {
}
