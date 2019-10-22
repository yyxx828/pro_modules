package com.xajiusuo.busi.villageMessage.dao;

import com.xajiusuo.busi.villageMessage.entity.Unit;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:22
 * @Description 单元基本信息
 */
public interface UnitDao extends BaseDao<Unit,String>,JpaSpecificationExecutor<Unit> {
}
