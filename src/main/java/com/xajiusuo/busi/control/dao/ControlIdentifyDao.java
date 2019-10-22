package com.xajiusuo.busi.control.dao;

import com.xajiusuo.busi.control.entity.ControlIdentify;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by shirenjing on 2019/6/22.
 */
public interface ControlIdentifyDao extends BaseDao<ControlIdentify,String>, JpaSpecificationExecutor<ControlIdentify> {
}
