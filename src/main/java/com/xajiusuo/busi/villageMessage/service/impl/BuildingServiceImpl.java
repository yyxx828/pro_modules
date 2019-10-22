package com.xajiusuo.busi.villageMessage.service.impl;

import com.xajiusuo.busi.villageMessage.dao.BuildingDao;
import com.xajiusuo.busi.villageMessage.entity.Building;
import com.xajiusuo.busi.villageMessage.service.BuildingService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:27
 * @Description 楼栋信息逻辑处理接口
 */
@Slf4j
@Service
public class BuildingServiceImpl extends BaseServiceImpl<Building,String> implements BuildingService {

    @Autowired
    BuildingDao buildingDao;
    @Override
    public BaseDao<Building, String> getBaseDao() {
        return buildingDao;
    }

    @Override
    public Page<Building> getsqlpage(Pageable pageable, Building building){
        Page<Building> page = null;
        String sqlbase = " select * from ( select a.ID,a.BUILDINGNO,a.VILLAGEID,a.BUILDINGNAME,a.STANDARDADDR,a.STANDARDADDRCODE,a.LONGITUDE,a.LATITUDE,a.UNITNUM,a.FLOORNUM,a.FLOORHOUSENUM,a.OVERGROUNDFLOORNUM,a.UNDERGROUNDFLOORNUM,a.BUILDINGSTRUCTURE,a.BUILDINGNUM,a.BUILDING_TYPE,a.CREATETIME,a.LASTMODIFYTIME,a.CREATEUID,a.LASTMODIFYUID,a.CREATENAME,a.DELFLAG,b.villagename from  T_BUILDING_19 a,t_village_19 b where  a.villageid = b.villageid )";
        try{
            String sql = ""+sqlbase+"  where 1=1 and delflag = 0";
            if(!"".equals(building.getVillageid()) && null !=  building.getVillageid()){
                sql += "and villageid = '"+building.getVillageid()+"' ";
            }
            if(!"".equals(building.getBuildingno()) && null != building.getBuildingno()){
                sql += "and buildingno like '%"+building.getBuildingno()+"%' ";
            }
            if(!"".equals(building.getBuildingname()) && null!=building.getBuildingname()){
                sql += "and buildingname like '%"+building.getBuildingname()+"%' ";
            }
            page = this.buildingDao.executeQuerySqlByPage(pageable,sql, null);
            log.info("小区信息分页查询展示");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return page;
    };
    @Override
    public Long chekBs(String villageid,String bs,String bsValue) throws Exception{
            String sql = " select count(*) from T_BUILDING_19 where 1=1 and delflag = 0   and "+bs+"= '"+bsValue+"'";
        return this.buildingDao.count(sql,null);
    };

    @Override
    public List getSelectBulid(String villageid) throws Exception{
        String sql = "select * from T_BUILDING_19 where 1=1 and delflag = ? and villageid = ?";
        return this.buildingDao.executeNativeQuerySql(sql, "0",villageid);
    };
}
