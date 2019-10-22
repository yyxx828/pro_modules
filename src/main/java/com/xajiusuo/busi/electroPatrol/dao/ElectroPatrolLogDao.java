package com.xajiusuo.busi.electroPatrol.dao;

import com.xajiusuo.busi.electroPatrol.entity.ElectroPatrolLog;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

/**
 * @author lizhidong
 * @date 2019-6-12 10:44
 */
public interface ElectroPatrolLogDao extends BaseDao<ElectroPatrolLog, String>, JpaSpecificationExecutor {

}
