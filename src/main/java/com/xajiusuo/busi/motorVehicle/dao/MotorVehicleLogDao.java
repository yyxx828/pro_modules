package com.xajiusuo.busi.motorVehicle.dao;

import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleLog;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by shirenjing on 2019/6/6.
 */
public interface MotorVehicleLogDao extends BaseDao<MotorVehicleLog,String>,JpaSpecificationExecutor<MotorVehicleLog> {
}
