package com.xajiusuo.busi.diction.dao;

import com.xajiusuo.busi.diction.entity.DepartPolice;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author GaoYong
 * @Date 19-6-6 下午5:28
 * @Description 分局字典维护
 */
public interface DepartPoliceDao extends BaseDao<DepartPolice,String>,JpaSpecificationExecutor<DepartPolice> {
}
