package com.xajiusuo.busi.villageMessage.service;

import com.xajiusuo.busi.villageMessage.entity.HouseOwner;
import com.xajiusuo.jpa.config.BaseService;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:26
 * @Description 业主与房屋信息处理接口
 */
public interface HouseOwnerService extends BaseService<HouseOwner,String>{

    
    /**
     *@Description: 保存或者更新关联信息
     *@param: [ownerid,houseid]
     *@return: Boolean
     *@Author: gaoyong
     *@date: 19-6-15 下午5:47
    */
    Boolean saveUpdataHouseOwner(String ownerid, String houseids);
    
    /**
     *@Description: 删除此业主的关联信息表
     *@param: [ownerid]
     *@return: Boolean
     *@Author: gaoyong
     *@date: 19-6-15 下午5:49
    */
    Boolean delHouseOwner(String ownerid);
}
