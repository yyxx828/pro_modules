package com.xajiusuo.busi.flow.dao;

import com.xajiusuo.busi.flow.entity.SpInfo;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by fanhua on 18-1-23.
 */
public interface SpInfoDao extends BaseDao<SpInfo,Integer>,JpaSpecificationExecutor<SpInfo> {
}
