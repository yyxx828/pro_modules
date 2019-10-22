package com.xajiusuo.busi.villageMessage.service.impl;

import com.xajiusuo.busi.villageMessage.dao.UnitDao;
import com.xajiusuo.busi.villageMessage.entity.Unit;
import com.xajiusuo.busi.villageMessage.service.UnitService;
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
 * @Description 单元信息逻辑处理接口
 */
@Slf4j
@Service
public class UnitServiceImpl extends BaseServiceImpl<Unit,String> implements UnitService {

    @Autowired
    UnitDao unitDao;
    @Override
    public BaseDao<Unit, String> getBaseDao() {
        return unitDao;
    }

    @Override
    public Page<Unit> getsqlpage(Pageable pageable, Unit unit){
        Page<Unit> page = null;
        String sqlbase = "select a.ID,a.UNITNO,a.STANDARDADDR,a.STANDARDADDRCODE,a.HOUSENUM,a.BUILDINGID,a.VILLAGEID,a.UNIT_NAME,a.CREATETIME,a.LASTMODIFYTIME,a.CREATEUID,a.LASTMODIFYUID,a.CREATENAME,a.DELFLAG,b.BUILDINGNAME,c.VILLAGENAME from  T_UNIT_19 a,T_BUILDING_19 b,T_VILLAGE_19 c  where a.buildingid = b.buildingno and a.villageid = c.villageid";
        try{
            String sql = "select * from ("+sqlbase+") where 1=1 and delflag = 0 ";
            if( !"".equals(unit.getVillageid()) && null != unit.getVillageid()){
                sql += " and villageid = '"+unit.getVillageid()+"'";
            }
            if(!"".equals(unit.getBuildingid()) && null != unit.getBuildingid()){
                sql += " and buildingid = '"+unit.getBuildingid()+"'";
            }
            if(!"".equals(unit.getUnitno()) && null != unit.getUnitno()){
                sql += " and unitno like '%"+unit.getUnitno()+"%' ";
            }
            if(!"".equals(unit.getUnit_name()) && null!=unit.getUnit_name()){
                sql += " and unit_name like '%"+unit.getUnit_name()+"%' ";
            }
            page = this.unitDao.executeQuerySqlByPage(pageable,sql, null,null);
            log.info("小区信息分页查询展示");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return page;
    };
    @Override
    public Long chekBs(String villageid,String buildingid,String bs,String bsValue) throws Exception{
        String sql = " select count(*) from T_UNIT_19 where 1=1 and delflag = 0  and "+bs+"= '"+bsValue+"'";
        return this.unitDao.count(sql,null);
    };
    @Override
    public List getSelectUnit(String villageid, String buildingid) throws Exception{
        String sql = "select * from T_UNIT_19 where 1=1 and delflag = ? and villageid = ? and buildingid = ?";
        return this.getBaseDao().executeNativeQuerySql(sql, "0",villageid,buildingid);
    };
}
