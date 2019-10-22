package com.xajiusuo.busi.diction.dao;

import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.busi.diction.entity.Diction;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by 杨勇 on 18-8-20.
 */
public interface DictionDao extends BaseDao<Diction,Integer>,JpaSpecificationExecutor<Diction> {

}
