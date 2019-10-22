package com.xajiusuo.busi.motorVehicle.dao;

import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleInOut;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by liujiankang on 19-6-10.
 */
public interface MotorVehicleInOutDao extends BaseDao<MotorVehicleInOut,String>,JpaSpecificationExecutor<MotorVehicleInOut> {

}
