package com.xajiusuo.busi.query.service;


import com.xajiusuo.busi.buildingaccess.entity.BuildingEntranceGuardLog;
import com.xajiusuo.busi.entranceandexit.entity.GateEntranceGuardLog;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleInfo;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleLog;
import com.xajiusuo.busi.query.entity.DiagramSeries;
import com.xajiusuo.busi.query.entity.TypeEnum;
import com.xajiusuo.busi.villageMessage.entity.House;
import com.xajiusuo.busi.villageMessage.entity.HouseLog;
import com.xajiusuo.busi.villageMessage.entity.Owner;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface QueryService extends BaseService<Owner, String> {




    Page<HouseLog> getHouseLogByHouseId(House houseno, Pageable pageable) throws Exception;

    DiagramSeries getAssociationQuery(TypeEnum flag, String id) throws Exception;

    Page<MotorVehicleLog> getMotorVehicleLog(String plateno, Pageable pageable);

    House getHouse(String id);

    List<HouseLog> getHouseLogByIdCard(String idCard);

    MotorVehicleInfo getCar(String id);

    Page<Owner> queryOwnerList(Owner owner, Pageable pageable);

    Page<MotorVehicleInfo> queryMotorvehicleList(MotorVehicleInfo motorVehicleInfo, Pageable pageable);

    Page<House> queryHouseList(House house, Pageable pageable);

    Owner getOwner(String id);

    Page<Map<String, Object>> getBuildingEntranceCardLog(String idCard, Pageable pageable);

    Page<Map<String, Object>> getGateEntranceGuardLog(String idCard, Pageable pageable);
}
