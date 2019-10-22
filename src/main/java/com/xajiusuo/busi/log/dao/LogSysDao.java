package com.xajiusuo.busi.log.dao;

import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.busi.log.entity.LogSys;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author zlm at 2018/1/18
 * zhiwu数据层
 */
public interface LogSysDao extends BaseDao<LogSys,String>,JpaSpecificationExecutor<LogSys> {

}
