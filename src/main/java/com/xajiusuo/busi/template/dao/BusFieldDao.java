package com.xajiusuo.busi.template.dao;

import com.xajiusuo.busi.template.entity.BusField;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author zlm at 2018/1/18
 * zhiwu数据层
 */
public interface BusFieldDao extends BaseDao<BusField,Integer>,JpaSpecificationExecutor<BusField> {

}
