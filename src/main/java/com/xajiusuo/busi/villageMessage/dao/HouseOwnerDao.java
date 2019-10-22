package com.xajiusuo.busi.villageMessage.dao;

import com.xajiusuo.busi.villageMessage.entity.HouseOwner;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:22
 * @Description 业主与房屋关联信息表
 */
public interface HouseOwnerDao extends BaseDao<HouseOwner,String>,JpaSpecificationExecutor<HouseOwner> {
}
