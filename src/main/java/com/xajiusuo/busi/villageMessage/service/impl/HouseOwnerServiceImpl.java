package com.xajiusuo.busi.villageMessage.service.impl;

import com.xajiusuo.busi.villageMessage.dao.HouseOwnerDao;
import com.xajiusuo.busi.villageMessage.entity.HouseOwner;
import com.xajiusuo.busi.villageMessage.service.HouseOwnerService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:27
 * @Description 业主信息逻辑处理接口
 */
@Service
public class HouseOwnerServiceImpl extends BaseServiceImpl<HouseOwner,String> implements HouseOwnerService {
    private final Logger log = LoggerFactory.getLogger(HouseOwnerServiceImpl.class);

    @Autowired
    HouseOwnerDao houseOwnerDao;
    @Override
    public BaseDao<HouseOwner, String> getBaseDao() {
        return houseOwnerDao;
    }

    @Override
    public  Boolean saveUpdataHouseOwner(String ownerid,String houseids){
        List<HouseOwner> houseOwnerList = new ArrayList<>();
        Boolean result = true;
        try {
            String sqldel = "delete from T_HOUSEOWNER_19 where ownerid = '"+ownerid+"'";
            this.houseOwnerDao.executeUpdateSql(sqldel);
            if(houseids.contains(",")){
                for(String houseid:houseids.split(",")){
                    HouseOwner houseOwner = new HouseOwner();
                    houseOwner.setOwnerid(ownerid);
                    houseOwner.setHouseid(houseid);
                    houseOwnerList.add(houseOwner);
                }
            }else{
                HouseOwner houseOwner = new HouseOwner();
                houseOwner.setOwnerid(ownerid);
                houseOwner.setHouseid(houseids);
                houseOwnerList.add(houseOwner);
            }
            this.houseOwnerDao.saveAll(houseOwnerList);
            log.info("业主房屋关联信息成功");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        }

        return result;
    };

    @Override
    public Boolean delHouseOwner(String ownerid){
        Boolean result = true;
        try {
            String sqldel = "delete from T_HOUSEOWNER_19 where ownerid = '"+ownerid+"'";
            this.houseOwnerDao.executeUpdateSql(sqldel);
            log.info("业主房屋关联信息成功");
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        }
        return result;
    };
}
