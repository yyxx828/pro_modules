package com.xajiusuo.busi.query.service.impl;

import com.xajiusuo.busi.motorVehicle.dao.MotorVehicleInfoDao;
import com.xajiusuo.busi.motorVehicle.dao.MotorVehicleLogDao;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleInfo;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleLog;
import com.xajiusuo.busi.query.dao.QueryDao;
import com.xajiusuo.busi.query.entity.*;
import com.xajiusuo.busi.query.service.QueryService;
import com.xajiusuo.busi.villageMessage.dao.*;
import com.xajiusuo.busi.villageMessage.entity.*;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.utils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import java.util.*;

@Service
@Transactional
public class QueryServiceImpl extends BaseServiceImpl<Owner, String> implements QueryService {
    @Autowired
    private OwnerDao ownerDao;
    @Autowired
    private QueryDao queryDao;
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private BuildingDao buildingDao;
    @Autowired
    private HouseDao houseDao;
    @Autowired
    private MotorVehicleInfoDao motorVehicleInfoDao;
    @Autowired
    private MotorVehicleLogDao motorVehicleLogDao;
    @Autowired
    private HouseLogDao houseLogDao;
    @Autowired
    private EntityManager entityManager;

    private final String tableName = "T_QUERY_VIEW_37";

    @Override
    public BaseDao<Owner, String> getBaseDao() {
        return queryDao;
    }



    @Override
    public Page<Owner> queryOwnerList(Owner owner, Pageable pageable) {
        if (pageable.getPageNumber() >= 1) {
            pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort());
        }
        owner.setDelFlag(false);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withNullHandler(ExampleMatcher.NullHandler.IGNORE);
        return ownerDao.findAll(Example.of(owner, matcher), pageable);
    }

    @Override
    public Page<MotorVehicleInfo> queryMotorvehicleList(MotorVehicleInfo motorVehicleInfo, Pageable pageable) {
        if (pageable.getPageNumber() >= 1) {
            pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort());
        }
        motorVehicleInfo.setDelFlag(false);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withNullHandler(ExampleMatcher.NullHandler.IGNORE);

        return motorVehicleInfoDao.findAll(Example.of(motorVehicleInfo, matcher), pageable);
    }

    @Override
    public Page<House> queryHouseList(House house, Pageable pageable) {
        if (pageable.getPageNumber() >= 1) {
            pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort());
        }
        house.setDelFlag(false);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withNullHandler(ExampleMatcher.NullHandler.IGNORE);

        return houseDao.findAll(Example.of(house, matcher), pageable);
    }


    //********************************************************* 查询人
    @Override
    public Owner getOwner(String id) {
        return ownerDao.getOne(id);
    }

    @Override
    public Page<Map<String, Object>> getBuildingEntranceCardLog(String idCard, Pageable pageable) {
        if (pageable.getPageNumber() >= 1) {
            pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort());
        }
        List<String> entrancecardnos = queryDao.findBeglByPersonidnumber(idCard);
        if (entrancecardnos == null || entrancecardnos.size() == 0) return null;
        return queryDao.findBeglByEntrancecardnos(entrancecardnos, pageable);
    }

    @Override
    public Page<Map<String, Object>> getGateEntranceGuardLog(String idCard, Pageable pageable) {
        if (pageable.getPageNumber() >= 1) {
            pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort());
        }
        List<String> entrancecardnos = queryDao.findGeglByPersonidnumber(idCard);

        if (entrancecardnos == null || entrancecardnos.size() == 0) return null;

        return queryDao.findGeglByEntrancecardnos(entrancecardnos, pageable);
    }

    @Override
    public List<HouseLog> getHouseLogByIdCard(String idCard) {
        Owner owner = new Owner();
        owner.setIdnumber(idCard);
        return houseLogDao.findBy("idnumber", owner);
    }

    //*********************************************************查询车
    @Override
    public MotorVehicleInfo getCar(String id) {
        return motorVehicleInfoDao.getOne(id);
    }

    @Override
    public Page<MotorVehicleLog> getMotorVehicleLog(String plateno, Pageable pageable) {
        return motorVehicleLogDao.findBy("plateNo", plateno, pageable);
    }

    //******************************************************查询房
    @Override
    public House getHouse(String id) {
        return houseDao.getOne(id);
    }

    @Override
    public Page<HouseLog> getHouseLogByHouseId(House houseno, Pageable pageable) throws Exception {
        return houseLogDao.findBy("houseno", houseno, pageable);
    }


    //*********************************************************关联关系查询
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DiagramSeries getAssociationQuery(TypeEnum flag, String id){
        /**
         * 判断是否存在表
         */
        if (((Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM ALL_TABLES WHERE TABLE_NAME='" + tableName + "'").getSingleResult()).longValue() == 0) {
            entityManager.createNativeQuery("CREATE MATERIALIZED VIEW LOG ON T_HOUSE_19  TABLESPACE VILLAGE WITH ROWID(HOUSENO,UNITID,BUILDINGID)").executeUpdate();
            entityManager.createNativeQuery("CREATE MATERIALIZED VIEW LOG ON T_HOUSEOWNER_19   TABLESPACE VILLAGE WITH ROWID(HOUSEID,OWNERID)").executeUpdate();
            entityManager.createNativeQuery("CREATE MATERIALIZED VIEW LOG ON T_OWNER_19  TABLESPACE VILLAGE WITH ROWID(IDNUMBER)").executeUpdate();
            entityManager.createNativeQuery("CREATE MATERIALIZED VIEW LOG ON T_MOTORVEHICLE_16  TABLESPACE VILLAGE WITH ROWID(OWNERIDNUMBER)").executeUpdate();
            entityManager.createNativeQuery("CREATE MATERIALIZED VIEW LOG ON P_DICTION_11  TABLESPACE VILLAGE WITH ROWID(KEYS)").executeUpdate();
            entityManager.createNativeQuery("CREATE MATERIALIZED VIEW LOG ON T_UNIT_19  TABLESPACE VILLAGE WITH ROWID(UNITNO)").executeUpdate();
            entityManager.createNativeQuery("CREATE MATERIALIZED VIEW LOG ON T_BUILDING_19  TABLESPACE VILLAGE WITH ROWID(BUILDINGNO)").executeUpdate();
            String sql = "CREATE MATERIALIZED VIEW " + tableName + " BUILD IMMEDIATE REFRESH FAST ON COMMIT ENABLE QUERY REWRITE AS(" +
                    "SELECT " +
                    "T_UNIT_19.ROWID              AS UNIT_ROWID," +
                    "T_UNIT_19.UNIT_NAME          AS UNITNAME," +
                    "T_BUILDING_19.ROWID          AS BUILDING_ROWID," +
                    "T_BUILDING_19.BUILDINGNAME," +
                    "T_HOUSE_19.ROWID             AS HOUSE_ROWID," +
                    "T_HOUSE_19.ID                AS HOUSE_ID," +
                    "T_HOUSE_19.DELFLAG           AS HOUSE_DELFLAG," +
                    "T_HOUSE_19.HOUSENO," +
                    "T_HOUSE_19.ROOM_NAME," +
                    "T_HOUSE_19.FLOOR," +
                    "T_OWNER_19.ROWID             AS OWNER_ROWID," +
                    "T_OWNER_19.DELFLAG           AS OWNER_DELFLAG," +
                    "T_OWNER_19.ID                AS OWNER_ID," +
                    "T_OWNER_19.IDNUMBER," +
                    "T_OWNER_19.NAME," +
                    "T_OWNER_19.BIRTHDAY," +
                    "T_HOUSEOWNER_19.ROWID        AS HOUSEOWNER_ROWID," +
                    "T_MOTORVEHICLE_16.ROWID      AS MOTORVEHICLE_ROWID," +
                    "T_MOTORVEHICLE_16.ID         AS MOTORVEHICLE_ID," +
                    "T_MOTORVEHICLE_16.DELFLAG    AS MOTORVEHICLE_DELFLAG," +
                    "T_MOTORVEHICLE_16.PLATENO," +
                    "T_MOTORVEHICLE_16.VEHICLEMODEL," +
                    "T_MOTORVEHICLE_16.VEHICLECOLOR," +
                    "SEX_DICTION.ROWID            AS SEX_ROWID," +
                    "SEX_DICTION.VAL              AS SEX," +
                    "NATION_DICTION.ROWID         AS NATION_ROWID," +
                    "NATION_DICTION.VAL           AS NATION," +
                    "IDTYPE_DICTION.ROWID         AS IDTYPE_ROWID," +
                    "IDTYPE_DICTION.VAL           AS IDTYPE," +
                    "VEHICLECLASS_DICTION.ROWID   AS VEHICLECLASS_ROWID," +
                    "VEHICLECLASS_DICTION.VAL     AS VEHICLECLASS," +
                    "VEHICLEBRAND_DICTION.ROWID   AS VEHICLEBRAND_ROWID," +
                    "VEHICLEBRAND_DICTION.VAL     AS VEHICLEBRAND" +
                    " FROM " +
                    "T_UNIT_19," +
                    "T_BUILDING_19," +
                    "T_HOUSE_19," +
                    "T_OWNER_19," +
                    "T_HOUSEOWNER_19," +
                    "T_MOTORVEHICLE_16," +
                    "P_DICTION_11   SEX_DICTION," +
                    "P_DICTION_11   NATION_DICTION," +
                    "P_DICTION_11   IDTYPE_DICTION," +
                    "P_DICTION_11   VEHICLECLASS_DICTION," +
                    "P_DICTION_11   VEHICLEBRAND_DICTION" +
                    " WHERE " +
                    " T_HOUSEOWNER_19.HOUSEID (+) = T_HOUSE_19.HOUSENO" +
                    " AND T_OWNER_19.IDNUMBER = T_HOUSEOWNER_19.OWNERID" +
                    " AND T_OWNER_19.IDNUMBER = T_MOTORVEHICLE_16.OWNERIDNUMBER (+)" +
                    " AND T_HOUSE_19.UNITID = T_UNIT_19.UNITNO" +
                    " AND T_BUILDING_19.BUILDINGNO = T_HOUSE_19.BUILDINGID" +
                    " AND T_OWNER_19.SEX = SEX_DICTION.KEYS" +
                    " AND T_OWNER_19.NATION = NATION_DICTION.KEYS" +
                    " AND T_OWNER_19.IDTYPE = IDTYPE_DICTION.KEYS" +
                    " AND T_MOTORVEHICLE_16.VEHICLECLASS = VEHICLECLASS_DICTION.KEYS" +
                    " AND T_MOTORVEHICLE_16.VEHICLEBRAND = VEHICLEBRAND_DICTION.KEYS" +
                    ")";
            entityManager.createNativeQuery(sql).executeUpdate();
            entityManager.createNativeQuery("CREATE INDEX " + tableName + "_INDEX1 ON " + tableName + "(HOUSE_ID)").executeUpdate();
            entityManager.createNativeQuery("CREATE INDEX " + tableName + "_INDEX2 ON " + tableName + "(OWNER_ID)").executeUpdate();
            entityManager.createNativeQuery("CREATE INDEX " + tableName + "_INDEX3 ON " + tableName + "(MOTORVEHICLE_ID)").executeUpdate();
        }
        String sql =
                "SELECT ROOM_NAME,FLOOR,SEX,NATION,IDTYPE,HOUSENO,MOTORVEHICLE_DELFLAG,OWNER_DELFLAG,HOUSE_DELFLAG,HOUSE_ID,OWNER_ID,MOTORVEHICLE_ID,UNITNAME,BUILDINGNAME,IDNUMBER,NAME,BIRTHDAY,PLATENO,VEHICLEMODEL,VEHICLEBRAND,VEHICLECOLOR,VEHICLECLASS FROM " + tableName + " A WHERE  EXISTS(SELECT HOUSE_ID FROM " + tableName + " B WHERE 1=1 AND OWNER_ID='" + id + "' AND A.OWNER_ID=B.OWNER_ID)" +
                        " UNION SELECT ROOM_NAME,FLOOR,SEX,NATION,IDTYPE,HOUSENO,MOTORVEHICLE_DELFLAG,OWNER_DELFLAG,HOUSE_DELFLAG,HOUSE_ID,OWNER_ID,MOTORVEHICLE_ID,UNITNAME,BUILDINGNAME,IDNUMBER,NAME,BIRTHDAY,PLATENO,VEHICLEMODEL,VEHICLEBRAND,VEHICLECOLOR,VEHICLECLASS  FROM " + tableName + " A WHERE  EXISTS (SELECT OWNER_ROWID FROM " + tableName + " B WHERE 1=1 AND OWNER_ID='" + id + "' AND A.OWNER_ID=B.OWNER_ID)" +
                        " UNION SELECT ROOM_NAME,FLOOR,SEX,NATION,IDTYPE,HOUSENO,MOTORVEHICLE_DELFLAG,OWNER_DELFLAG,HOUSE_DELFLAG,HOUSE_ID,OWNER_ID,MOTORVEHICLE_ID,UNITNAME,BUILDINGNAME,IDNUMBER,NAME,BIRTHDAY,PLATENO,VEHICLEMODEL,VEHICLEBRAND,VEHICLECOLOR,VEHICLECLASS  FROM " + tableName + " A WHERE  EXISTS (SELECT MOTORVEHICLE_ID FROM " + tableName + " B WHERE 1=1 AND OWNER_ID='" + id + "' AND A.OWNER_ID=B.OWNER_ID)";
        List<AssociationQueryVO> voList = null;
       // queryDao.queryRangeAll();
        switch (flag) {
            case OWNER:
                voList = queryDao.querySqlBean(sql, AssociationQueryVO.class, null);
                break;
            case MOTORVEHICLE:
                voList = queryDao.querySqlBean(sql.replace("WHERE 1=1 AND OWNER_ID=", "WHERE 1=1 AND MOTORVEHICLE_ID="), AssociationQueryVO.class, null);
                break;
            case HOUSE:
                voList = queryDao.querySqlBean(sql.replace("WHERE 1=1 AND OWNER_ID=", "WHERE 1=1 AND HOUSE_ID="), AssociationQueryVO.class, null);
        }
        return getDiagramSeries(voList, flag, id);

    }
    private DiagramSeries getDiagramSeries(List<AssociationQueryVO> associationQueryVoList, TypeEnum flag, String id) {
        DiagramSeries diagramSeries = new DiagramSeries();
        Set<DiagramSeries.Data> dataSet = new DiagramSeries().getDatas();
        Set<DiagramSeries.Link> linkSet = new DiagramSeries().getLinks();
        if (associationQueryVoList.size() == 0) {
            if (Objects.equals(flag, TypeEnum.OWNER)) {
                Owner owner = getOwner(id);
                Map<String, Object> map_data = new LinkedHashMap<>();
                map_data.put(InfoEnum.NAME.getValue(), StringUtils.isNotBlank(owner.getName()) ? owner.getName() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.SEX.getValue(), owner.getSexDiction() != null && StringUtils.isNotBlank(owner.getSexDiction().getVal()) ? owner.getSexDiction().getVal() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.ID_TYPE.getValue(), owner.getIdtypeDiction() != null && StringUtils.isNotBlank(owner.getIdtypeDiction().getVal()) ? owner.getIdtypeDiction().getVal() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.ID_NUMBER.getValue(), StringUtils.isNotBlank(owner.getIdnumber()) ? owner.getIdnumber() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.BIRTHDAY.getValue(), StringUtils.isNotBlank(owner.getBirthday()) ? owner.getBirthday() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.NATION.getValue(), owner.getNationDiction() != null && StringUtils.isNotBlank(owner.getNationDiction().getVal()) ? owner.getNationDiction().getVal() : InfoEnum.NO_DATA.name());
                DiagramSeries.Data data = diagramSeries.new Data();
                data.setName(StringUtils.isNotBlank(owner.getName()) ? owner.getName() : "未知");
                data.setId(TypeEnum.OWNER.name() + "_" + owner.getId());
                DiagramSeries.Data.Label label = data.new Label();
                data.setSymbolSize(60);
                data.setKeyword(owner.getIdnumber());
                label.setOffset(new int[]{0, 38});
                if (Objects.equals(owner.getSexDiction().getVal(), "男")) {
                    data.setType(TypeEnum.MAN.name());
                    label.setColor(ColorEnum.MAN.getColor());
                } else {
                    data.setType(TypeEnum.WOMAN.name());
                    label.setColor(ColorEnum.WOMAN.getColor());
                }
                data.setLabel(label);
                data.setInfo(map_data);
                dataSet.add(data);
            }
            if (Objects.equals(flag, TypeEnum.HOUSE)) {
                House house = getHouse(id);
                Map<String, Object> map_data = new LinkedHashMap<String, Object>();
                String unitName = unitDao.getOne("unitno", house.getUnitid()).getUnit_name();
                String buildingName = buildingDao.getOne("buildingno", house.getBuildingid()).getBuildingname();
                map_data.put(InfoEnum.BUILDING_NAME.name(), StringUtils.isNotBlank(buildingName) ? buildingName : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.UNIT_NAME.name(), StringUtils.isNotBlank(unitName) ? unitName : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.FLOOR.getValue(), StringUtils.isNotBlank(house.getFloor()) ? house.getFloor() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.HOUSE_NO.getValue(), StringUtils.isNotBlank(house.getHouseno()) ? house.getHouseno() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.ROOM_NAME.getValue(), StringUtils.isNotBlank(house.getRoom_name()) ? house.getRoom_name() : InfoEnum.NO_DATA.name());
                DiagramSeries.Data data = diagramSeries.new Data();
                data.setId(TypeEnum.HOUSE.name() + "_" + house.getId());
                data.setType(TypeEnum.HOUSE.name());
                data.setName(StringUtils.isNotBlank(house.getRoom_name()) ? house.getRoom_name() : "未知");
                DiagramSeries.Data.Label label = data.new Label();
                data.setSymbolSize(60);
                data.setKeyword(house.getHouseno());
                label.setOffset(new int[]{0, 38});
                label.setColor(ColorEnum.HOUSE.getColor());
                data.setLabel(label);
                data.setInfo(map_data);
                dataSet.add(data);
            }
            if (Objects.equals(flag, TypeEnum.MOTORVEHICLE)) {
                MotorVehicleInfo motorVehicleInfo = getCar(id);
                Map<String, Object> map_data = new LinkedHashMap<String, Object>();
                map_data.put(InfoEnum.PLATE_NO.getValue(), StringUtils.isNotBlank(motorVehicleInfo.getPlateNo()) ? motorVehicleInfo.getPlateNo() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.VEHICLE_CLASS.getValue(), motorVehicleInfo.getVehicleClassDiction() != null && StringUtils.isNotBlank(motorVehicleInfo.getVehicleClassDiction().getVal()) ? motorVehicleInfo.getVehicleClassDiction().getVal() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.VEHICLE_BRAND.getValue(), motorVehicleInfo.getVehicleBrandDiction() != null && StringUtils.isNotBlank(motorVehicleInfo.getVehicleBrandDiction().getVal()) ? motorVehicleInfo.getVehicleBrandDiction().getVal() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.VEHICLE_COLOR.getValue(), StringUtils.isNotBlank(motorVehicleInfo.getVehicleColor()) ? motorVehicleInfo.getVehicleColor() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.VEHICLE_MODEL.getValue(), StringUtils.isNotBlank(motorVehicleInfo.getVehicleModel()) ? motorVehicleInfo.getVehicleModel() : InfoEnum.NO_DATA.name());
                DiagramSeries.Data data = diagramSeries.new Data();
                data.setId(TypeEnum.MOTORVEHICLE.name() + "_" + motorVehicleInfo.getId());
                data.setName(StringUtils.isNotBlank(motorVehicleInfo.getPlateNo()) ? motorVehicleInfo.getPlateNo() : "未知");
                data.setType(TypeEnum.MOTORVEHICLE.name());
                DiagramSeries.Data.Label label = data.new Label();
                data.setSymbolSize(60);
                data.setKeyword(motorVehicleInfo.getPlateNo());
                label.setOffset(new int[]{0, 38});
                label.setColor(ColorEnum.MOTOR_VEHICLE.getColor());
                data.setLabel(label);
                data.setInfo(map_data);
                dataSet.add(data);
            }
        }
        for (AssociationQueryVO vo : associationQueryVoList) {
            if (((StringUtils.isBlank(vo.getHouse_Id()) ? 0 : 1) + (StringUtils.isBlank(vo.getOwner_Id()) ? 0 : 1) + (StringUtils.isBlank(vo.getMotorVehicle_Id()) ? 0 : 1)) <= 1) {
                continue;
            }
            if (StringUtils.isNotBlank(vo.getOwner_Id()) && vo.getMotorVehicle_delFlag().intValue() == 0) {
                Map<String, Object> map_data = new LinkedHashMap<String, Object>();
                map_data.put(InfoEnum.NAME.getValue(), StringUtils.isNotBlank(vo.getName()) ? vo.getName() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.SEX.getValue(), StringUtils.isNotBlank(vo.getSex()) ? vo.getSex() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.ID_TYPE.getValue(), StringUtils.isNotBlank(vo.getIdType()) ? vo.getIdType() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.ID_NUMBER.getValue(), StringUtils.isNotBlank(vo.getIDNumber()) ? vo.getIDNumber() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.BIRTHDAY.getValue(), StringUtils.isNotBlank(vo.getBirthday()) ? vo.getBirthday() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.NATION.getValue(), StringUtils.isNotBlank(vo.getNation()) ? vo.getNation() : InfoEnum.NO_DATA.name());
                DiagramSeries.Data data = diagramSeries.new Data();
                data.setName(StringUtils.isNotBlank(vo.getName()) ? vo.getName() : "未知");
                data.setId(TypeEnum.OWNER.name() + "_" + vo.getOwner_Id());
                data.setKeyword(vo.getIDNumber());
                DiagramSeries.Data.Label label = data.new Label();
                if (Objects.equals(flag, TypeEnum.OWNER) && Objects.equals(id, vo.getOwner_Id())) {
                    data.setSymbolSize(60);
                    label.setOffset(new int[]{0, 38});
                }
                data.setLabel(label);
                if (Objects.equals(vo.getSex(), "男")) {
                    data.setType(TypeEnum.MAN.name());
                    label.setColor(ColorEnum.MAN.getColor());
                } else {
                    data.setType(TypeEnum.WOMAN.name());
                    label.setColor(ColorEnum.WOMAN.getColor());
                }
                data.setInfo(map_data);
                dataSet.add(data);
            }
            if (StringUtils.isNotBlank(vo.getHouse_Id()) && vo.getHouse_delFlag().intValue() == 0) {
                Map<String, Object> map_data = new LinkedHashMap<String, Object>();
                map_data.put(InfoEnum.BUILDING_NAME.getValue(), StringUtils.isNotBlank(vo.getBuildingName()) ? vo.getBuildingName() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.UNIT_NAME.getValue(), StringUtils.isNotBlank(vo.getUnitName()) ? vo.getUnitName() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.FLOOR.getValue(), StringUtils.isNotBlank(vo.getFloor()) ? vo.getFloor() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.HOUSE_NO.getValue(), StringUtils.isNotBlank(vo.getHouseNo()) ? vo.getHouseNo() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.ROOM_NAME.getValue(), StringUtils.isNotBlank(vo.getRoom_name()) ? vo.getRoom_name() : InfoEnum.NO_DATA.name());
                DiagramSeries.Data data = diagramSeries.new Data();
                data.setId(TypeEnum.HOUSE.name() + "_" + vo.getHouse_Id());
                data.setName(StringUtils.isNotBlank(vo.getRoom_name()) ? vo.getRoom_name() : "未知");
                data.setType(TypeEnum.HOUSE.name());
                data.setKeyword(vo.getHouseNo());
                DiagramSeries.Data.Label label = data.new Label();
                if (Objects.equals(flag, TypeEnum.HOUSE) && Objects.equals(id, vo.getHouse_Id())) {
                    data.setSymbolSize(60);
                    label.setOffset(new int[]{0, 38});
                }
                label.setColor(ColorEnum.HOUSE.getColor());
                data.setLabel(label);
                data.setInfo(map_data);
                dataSet.add(data);
            }
            if (StringUtils.isNotBlank(vo.getMotorVehicle_Id()) && vo.getMotorVehicle_delFlag().intValue() == 0) {
                Map<String, Object> map_data = new LinkedHashMap<String, Object>();
                map_data.put(InfoEnum.PLATE_NO.getValue(), StringUtils.isNotBlank(vo.getPlateno()) ? vo.getPlateno() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.VEHICLE_CLASS.getValue(), StringUtils.isNotBlank(vo.getVehicleClass()) ? vo.getVehicleClass() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.VEHICLE_BRAND.getValue(), StringUtils.isNotBlank(vo.getVehicleBrand()) ? vo.getVehicleBrand() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.VEHICLE_COLOR.getValue(), StringUtils.isNotBlank(vo.getVehicleColor()) ? vo.getVehicleColor() : InfoEnum.NO_DATA.name());
                map_data.put(InfoEnum.VEHICLE_MODEL.getValue(), StringUtils.isNotBlank(vo.getVehicleModel()) ? vo.getVehicleModel() : InfoEnum.NO_DATA.name());
                DiagramSeries.Data data = diagramSeries.new Data();
                data.setId(TypeEnum.MOTORVEHICLE.name() + "_" + vo.getMotorVehicle_Id());
                data.setType(TypeEnum.MOTORVEHICLE.name());
                data.setKeyword(vo.getPlateno());
                data.setName(StringUtils.isNotBlank(vo.getPlateno()) ? vo.getPlateno() : "未知");
                DiagramSeries.Data.Label label = data.new Label();
                if (Objects.equals(flag, TypeEnum.MOTORVEHICLE) && Objects.equals(id, vo.getMotorVehicle_Id())) {
                    data.setSymbolSize(60);
                    label.setOffset(new int[]{0, 38});
                }
                label.setColor(ColorEnum.MOTOR_VEHICLE.getColor());
                data.setLabel(label);
                data.setInfo(map_data);
                dataSet.add(data);
            }
            if (StringUtils.isNotBlank(vo.getOwner_Id()) && vo.getOwner_delFlag().intValue() == 0 && StringUtils.isNotBlank(vo.getHouse_Id()) && vo.getHouse_delFlag().intValue() == 0) {
                DiagramSeries.Link link = diagramSeries.new Link();
                link.setSource(TypeEnum.OWNER.name() + "_" + vo.getOwner_Id());
                link.setTarget(TypeEnum.HOUSE.name() + "_" + vo.getHouse_Id());
                linkSet.add(link);
            }
            if (StringUtils.isNotBlank(vo.getOwner_Id()) && vo.getOwner_delFlag().intValue() == 0 && StringUtils.isNotBlank(vo.getMotorVehicle_Id()) && vo.getMotorVehicle_delFlag().intValue() == 0) {
                DiagramSeries.Link link = diagramSeries.new Link();
                link.setSource(TypeEnum.OWNER.name() + "_" + vo.getOwner_Id());
                link.setTarget(TypeEnum.MOTORVEHICLE.name() + "_" + vo.getMotorVehicle_Id());
                linkSet.add(link);
            }
            if (StringUtils.isNotBlank(vo.getHouse_Id()) && vo.getHouse_delFlag().intValue() == 0 && StringUtils.isNotBlank(vo.getMotorVehicle_Id()) && vo.getMotorVehicle_delFlag().intValue() == 0) {
                DiagramSeries.Link link = diagramSeries.new Link();
                link.setSource(TypeEnum.HOUSE.name() + "_" + vo.getHouse_Id());
                link.setTarget(TypeEnum.MOTORVEHICLE.name() + "_" + vo.getMotorVehicle_Id());
                linkSet.add(link);
            }
        }
        diagramSeries.setDatas(dataSet);
        diagramSeries.setLinks(linkSet);
        return diagramSeries;
    }


}
