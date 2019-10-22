package com.xajiusuo.busi.user.dao;

import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.busi.user.entity.OperGroup;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author zlm at 2018/1/18
 * zhiwu数据层
 */
public interface OperGroupDao extends BaseDao<OperGroup,Integer>,JpaSpecificationExecutor<OperGroup> {

}
