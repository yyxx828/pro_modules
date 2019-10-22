package com.xajiusuo.busi.flow.dao;

import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.busi.flow.entity.FlowAllot;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 杨勇  on 2018/5/25.
 */
public interface FlowAllotDao extends BaseDao<FlowAllot,Integer>,JpaSpecificationExecutor<FlowAllot> {
}
