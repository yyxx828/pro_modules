package com.xajiusuo.busi.motorVehicle.service.impl;

import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.busi.diction.service.DictionService;
import com.xajiusuo.busi.motorVehicle.dao.MotorVehicleInfoDao;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleInfo;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleInfoVo;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleOwnerVo;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleVo;
import com.xajiusuo.busi.motorVehicle.service.MotorVehicleInfoService;
import com.xajiusuo.busi.villageMessage.entity.Village;
import com.xajiusuo.busi.villageMessage.service.OwnerService;
import com.xajiusuo.busi.villageMessage.service.VillageService;
import com.xajiusuo.busi.villageMessage.untilmodel.ModelExcel;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.SqlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

/**
 * Created by shirenjing on 2019/6/15.
 */
@Service
public class MotorVehicleInfoServiceImpl extends BaseServiceImpl<MotorVehicleInfo,String> implements MotorVehicleInfoService {

    @Autowired
    private MotorVehicleInfoDao motorVehicleInfoDao;
    @Override
    public BaseDao<MotorVehicleInfo, String> getBaseDao() {
        return motorVehicleInfoDao;
    }
    @Autowired
    private DictionService dictionService; //字典
    @Autowired
    private VillageService villageService; //小区
    @Autowired
    private OwnerService ownerService; //业主信息


    @Override
    public Page<MotorVehicleVo> queryPageInfo(Pageable pageable, MotorVehicleInfoVo motorVehicleInfoVo) {
        StringBuilder sb = new StringBuilder("select m.id ,m.motorvehicleid,t1.val plateclass,m.plateno,t2.val vehicleclass," +
                "t3.val vehiclebrand,m.vehiclemodel,m.vehiclecolor,m.storageurl1,m.storageurl2,m.storageurl3,t4.val vehiclestatus," +
                "m.ownerid,m.ownername,m.owneridnumber,v.villagename villageid,m.createtime,m.lastmodifytime,m.createuid,m.lastmodifyuid,m.createname,m.delflag from t_motorvehicle_16 m " +
                " left join t_village_19 v on m.villageid=v.villageid   --小区编号\n" +
                " left join p_diction_11 t1 on m.plateclass=t1.keys  --号牌种类\n" +
                " left join p_diction_11 t2 on m.vehicleclass=t2.keys  --车辆类型\n" +
                " left join p_diction_11 t3 on m.vehiclebrand=t3.keys  --车辆品牌\n" +
                " left join p_diction_11 t4 on m.vehiclestatus=t4.keys  --车辆状态\n" +
                "where m.delflag = 0 ");
        if(motorVehicleInfoVo!=null){
            if(CommonUtils.isNotEmpty(motorVehicleInfoVo.getMotorVehicleID())){
                sb.append(" and m.motorvehicleid like '%"+motorVehicleInfoVo.getMotorVehicleID()+"%'"); //车辆编号
            }
            if(CommonUtils.isNotEmpty(motorVehicleInfoVo.getPlateClass())){
                sb.append(" and m.plateclass = '"+motorVehicleInfoVo.getPlateClass()+"'"); //号牌种类
            }
            if(CommonUtils.isNotEmpty(motorVehicleInfoVo.getPlateNo())){
                sb.append(" and m.plateno like '%"+motorVehicleInfoVo.getPlateNo()+"%'"); //车牌号
            }
            if(CommonUtils.isNotEmpty(motorVehicleInfoVo.getVehicleClass())){
                sb.append(" and m.vehicleClass = '"+motorVehicleInfoVo.getVehicleClass()+"'"); //车辆类型
            }
            if(CommonUtils.isNotEmpty(motorVehicleInfoVo.getVehicleBrand())){
                sb.append(" and m.vehiclebrand = '"+motorVehicleInfoVo.getVehicleBrand()+"'"); //车辆品牌
            }
            if(CommonUtils.isNotEmpty(motorVehicleInfoVo.getVehicleModel())){
                sb.append(" and m.vehiclemodel like '%"+motorVehicleInfoVo.getVehicleModel()+"%'"); //车辆型号
            }
            if(CommonUtils.isNotEmpty(motorVehicleInfoVo.getVehicleColor())){
                sb.append(" and m.vehiclecolor = '"+motorVehicleInfoVo.getVehicleColor()+"'"); //车身颜色
            }
            if(CommonUtils.isNotEmpty(motorVehicleInfoVo.getVehicleStatus())){
                sb.append(" and m.vehiclestatus = '"+motorVehicleInfoVo.getVehicleStatus()+"'"); //车辆状态
            }
            if(CommonUtils.isNotEmpty(motorVehicleInfoVo.getOwnerName())){
                sb.append(" and m.ownerName like '%"+motorVehicleInfoVo.getOwnerName()+"%'"); //车主姓名
            }
            if(CommonUtils.isNotEmpty(motorVehicleInfoVo.getOwnerIDNumber())){
                sb.append(" and m.owneridnumber like '%"+motorVehicleInfoVo.getOwnerIDNumber()+"%'"); //车主证件号
            }
            if(CommonUtils.isNotEmpty(motorVehicleInfoVo.getVillageName())){
                sb.append(" and v.villagename like '%"+motorVehicleInfoVo.getVillageName()+"%'"); //小区名称
            }
            if(CommonUtils.isNotEmpty(motorVehicleInfoVo.getVillageID())){
                sb.append(" and m.villageid like '%"+motorVehicleInfoVo.getVillageID()+"%'");  //小区编号
            }
        }
        if(pageable.getSort()!=null){
            sb.append(" order by");
            Iterator<Sort.Order> it = pageable.getSort().iterator();
            while(it.hasNext()){
                Sort.Order order = it.next();
                sb.append(" m."+order.getProperty()+" "+order.getDirection());
                if(it.hasNext()) sb.append(",");
            }
        }
        try {
            return queryPageSqlBean(pageable,sb.toString(),MotorVehicleVo.class,null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public MotorVehicleInfo queryTranslateById(String id) {
        MotorVehicleInfo info = motorVehicleInfoDao.getOne(id);
//        //号牌种类
//        info.setPlateClass(dictionService.getValueByKey(info.getPlateClass()));
//        //车辆类型
//        info.setVehicleClass(dictionService.getValueByKey(info.getVehicleClass()));
//        //车辆品牌
//        info.setVehicleBrand(dictionService.getValueByKey(info.getVehicleBrand()));
        //小区
        List<Village> villageList = villageService.findBy("villageid",info.getVillageID());
        Village village =null;
        if(villageList!=null && villageList.size()>0){
            village = villageList.get(0);
        }
//        //车辆状态
//        String cczt = info.getVehicleStatus();
//        if("0".equals(cczt)){
//            info.setVehicleStatus("否");
//        }else{
//            info.setVehicleStatus("是");
//        }
        if(village!=null){
            info.setVillageID(village.getVillagename());
        }
        return info;
    }

    @Override
    public MotorVehicleInfo queryTranslateByPlateNo(String plateNo) {
        MotorVehicleInfo info = null;
        List<MotorVehicleInfo> motorVehicleInfoList = motorVehicleInfoDao.findBy("plateNo",plateNo);
        if(motorVehicleInfoList!=null && motorVehicleInfoList.size()>0){
            info = motorVehicleInfoList.get(0);
        }
//        //号牌种类
//        info.setPlateClass(dictionService.getValueByKey(info.getPlateClass()));
//        //车辆类型
//        info.setVehicleClass(dictionService.getValueByKey(info.getVehicleClass()));
//        //车辆品牌
//        info.setVehicleBrand(dictionService.getValueByKey(info.getVehicleBrand()));
        //小区
        Village village =null;
        if(info!=null){
            List<Village> villageList = villageService.findBy("villageid",info.getVillageID());
            if(villageList!=null && villageList.size()>0){
                village = villageList.get(0);
            }
        }
//        //车辆状态
//        String cczt = info.getVehicleStatus();
//        if("0".equals(cczt)){
//            info.setVehicleStatus("否");
//        }else{
//            info.setVehicleStatus("是");
//        }
        if(village!=null){
            info.setVillageID(village.getVillagename());
        }
        return info;
    }

    @Override
    public void batchDeleteByid(String ids) {
        String[] idArr = ids.trim().split(",");
        String idss = "";
        for(String id:idArr){
            idss+=",'"+id+"'";
        }
        if(idss.length()>0) idss = idss.substring(1);
        String sql = "update t_motorvehicle_16 set delflag=1 where id in("+idss+")";
        motorVehicleInfoDao.executeUpdateSql(sql);
    }

    @Override
    public List<MotorVehicleInfo> queryByField(String fieldName, String fieldValue) {
        String sql = "select * from t_motorvehicle_16 where delflag = 0 and "+fieldName+" = '"+fieldValue+"'";
        return motorVehicleInfoDao.executeNativeQuerySql(sql, null);
    }

    @Override
    public List<MotorVehicleOwnerVo> queryMotorVehicleOwner(String fieldName, String fieldValue) {
        motorVehicleInfoDao.queryRangeAll();
        String sql = "select t.id,t.name ownerName,t.idnumber ownerIdcard,t.villageId,t1.villagename from t_owner_19 t left join t_village_19 t1 on t.villageid = t1.villageid where t.delFlag = 0 and t."+fieldName+" like '%"+fieldValue+"%'";
        try {
            return motorVehicleInfoDao.querySqlBean(sql,MotorVehicleOwnerVo.class,null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, Integer> getMotorVehicleCount(String villageId) {
        Map<String,Integer> map = new HashMap<>();
        Integer count = motorVehicleInfoDao.queryCountSql("select count(*) from T_MOTORVEHICLE_16 where VILLAGEID = '"+villageId+"'",null);
        map.put("motorVehicleCount",count);
        return map;
    }

    @Override
    public String[] getDictionForUpload(String keys) {
        String[] result = null;
        List<Diction> listDiction = dictionService.listDictions(keys);
        if(listDiction!=null && listDiction.size()>0){
            result = new String[listDiction.size()];
            for(int i=0;i<listDiction.size();i++){
                Diction diction = listDiction.get(i);
                result[i] = (diction.getVal()+"-"+diction.getKeys());
            }
        }
        return result;
    }

    @Override
    public List uploadCarExcel(InputStream in) throws Exception {
        ModelExcel modelExcel = new ModelExcel();
        Map<Integer, Map<Integer, Object>> content= modelExcel.readExcelContent(in);
        List<Object[]> motorVehicleIDList = getAllMotorVehicleIDs();  //车辆编号
        List<Object[]> plateNoList = getAllPlateNo();  //车牌号
        Map<String,String> onwerMap = ownerService.getOwnerInfO();  //获取业主信息
        List<String> errors = new ArrayList<>();
        List<MotorVehicleInfo> infoList = new ArrayList<>();
        for (Map.Entry<Integer, Map<Integer, Object>> entry: content.entrySet()) {
            boolean flag = false;
            MotorVehicleInfo info = new MotorVehicleInfo();
            int lineNum = entry.getKey();
            Map<Integer, Object> map = entry.getValue();
            //车辆编号不能为空
            String carId = map.get(0)!=null ? map.get(0).toString() : "";
            if("".equals(carId)) continue;
            //车牌号不能为空
            String carNo = map.get(2)!=null ? map.get(2).toString() : "";
            if("".equals(carNo)) continue;
            //车主姓名不能为空
//            String carOwnerName = map.get(11)!=null ? map.get(11).toString() : "";
//            if("".equals(carOwnerName)) continue;
            //车主证件号不能为空
            String carOwnerIdcard = map.get(12)!=null ? map.get(12).toString() : "";
            if("".equals(carOwnerIdcard)) continue;
            //小区编号不能为空
            String valliageId = map.get(13)!=null ? map.get(13).toString() : "";
            if("".equals(valliageId)) continue;

            if(!motorVehicleIDList.contains(carId)){
                info.setMotorVehicleID(carId);  //车辆编号
            }else{
                flag = true;
            }
            String plateClass = map.get(1)!=null ? map.get(1).toString() : "";  //号牌种类
            if(!"".equals(plateClass)){
                Diction plateClassDiction = new Diction();
                plateClassDiction.setKeys(plateClass.split("-")[1]);
                info.setPlateClassDiction(plateClassDiction);
            }
            if(!plateNoList.contains(carNo)){
                info.setPlateNo(carNo);  //车牌号
            }else{
                flag = true;
            }
            String carType = map.get(3)!=null ? map.get(3).toString() : ""; // 车辆类型
            if(!"".equals(carType)){
                Diction vehicleClassDiction = new Diction();
                vehicleClassDiction.setKeys(plateClass.split("-")[1]);
                info.setVehicleClassDiction(vehicleClassDiction);
            }
            String carBrand = map.get(4)!=null ? map.get(4).toString() : "";  //车辆品牌
            if(!"".equals(carBrand)){
                Diction vehicleBrandDiction = new Diction();
                vehicleBrandDiction.setKeys(plateClass.split("-")[1]);
                info.setVehicleBrandDiction(vehicleBrandDiction);
            }
            String carModel = map.get(5)!=null ? map.get(5).toString() : "";  //车辆型号
            info.setVehicleModel(carModel);
            String carColor = map.get(6)!=null ? map.get(6).toString() : "";  //车身颜色
            info.setVehicleColor(carColor);
            String carStatus = map.get(10)!=null ? map.get(10).toString() : "";  //车辆状态
            if(!"".equals(carStatus)){
                Diction vehicleStatusDiction = new Diction();
                vehicleStatusDiction.setKeys(plateClass.split("-")[1]);
                info.setVehicleStatusDiction(vehicleStatusDiction);
            }
            String carOwnerName = onwerMap.get(carOwnerIdcard); //业主姓名
            if(!"".equals(carOwnerName)){
                info.setOwnerName(carOwnerName);
            }
            if(!"".equals(carOwnerIdcard)){
                info.setOwnerIDNumber(carOwnerIdcard);
            }
            if(!"".equals(valliageId)){
                info.setVillageID(valliageId);
            }
            infoList.add(info);
            if(flag){
                errors.add("行号:"+(lineNum+1)+"    "+carId +": "+carNo);
            }
        }

        if(infoList!=null && infoList.size()>0){
            motorVehicleInfoDao.batchSaveOrUpdate(infoList);
        }
        return errors;
    }

    @Override
    public List<Object[]> getAllMotorVehicleIDs() {
        return motorVehicleInfoDao.listBySQL("select motorvehicleid from t_motorvehicle_16",null);
    }

    @Override
    public List<Object[]> getAllPlateNo() {
        return motorVehicleInfoDao.listBySQL("select plateno from t_motorvehicle_16",null);
    }

    @Override
    public List<MotorVehicleInfo> queryInfoByPlateNo(String plageNo) {
        return motorVehicleInfoDao.findListWithSQL("select * from T_MOTORVEHICLE_16 where plateNo like '%"+plageNo+"%'");
    }
}