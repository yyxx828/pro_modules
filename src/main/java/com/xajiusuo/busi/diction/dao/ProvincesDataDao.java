package com.xajiusuo.busi.diction.dao;

import com.xajiusuo.busi.diction.entity.ProvincesData;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author GaoYong
 * @Date 19-6-6 下午5:56
 * @Description
 */
public interface ProvincesDataDao extends BaseDao<ProvincesData,String>,JpaSpecificationExecutor<ProvincesData> {
}
