package com.xajiusuo.busi.user.dao;

import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.busi.user.entity.Operate;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author zlm at 2018/1/18
 * zhiwu数据层
 */
public interface OperateDao extends BaseDao<Operate,Integer>,JpaSpecificationExecutor<Operate> {

}
