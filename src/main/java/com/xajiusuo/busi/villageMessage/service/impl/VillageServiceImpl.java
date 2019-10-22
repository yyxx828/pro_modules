package com.xajiusuo.busi.villageMessage.service.impl;

import com.sun.star.uno.Exception;
import com.xajiusuo.busi.villageMessage.dao.VillageDao;
import com.xajiusuo.busi.villageMessage.entity.Village;
import com.xajiusuo.busi.villageMessage.service.VillageService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author GaoYong
 * @Date 19-6-5 下午2:22
 * @Description 小区信息逻辑接口实现类
 */
@Slf4j
@Service
public class VillageServiceImpl extends BaseServiceImpl<Village,String> implements VillageService {

    @Autowired
    VillageDao villageDao;

    @Override
    public BaseDao<Village, String> getBaseDao() {
        return villageDao;
    }

    @Override
    public Page<Village> getsqlpage(Pageable pageable, Village village) throws Exception{
            String sql = "select * from  T_VILLAGE_19 where 1=1 and delflag = 0";
        if( !"".equals(village.getVillageid())&& null != village.getVillageid()){
            sql += "and villageid like '%"+village.getVillageid()+"%' ";
        }
        if(!"".equals(village.getVillagename()) && null!=village.getVillagename()){
            sql += "and villagename like '%"+village.getVillagename()+"%' ";
        }
        Page<Village> page = this.villageDao.executeQuerySqlByPage(pageable,sql, null);
            log.info("小区信息分页查询展示");
        return page;
    };
    @Override
    public Long chekBs(String bs,String bsValue) throws Exception{
        String sql = " select count(*) from T_VILLAGE_19 where 1=1 and delflag = 0 and "+bs+" = '"+bsValue+"' ";
        return this.villageDao.count(sql,null);
    };
}
