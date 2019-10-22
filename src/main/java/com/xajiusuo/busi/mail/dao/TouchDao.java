package com.xajiusuo.busi.mail.dao;

import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.busi.mail.entity.Touch;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TouchDao extends BaseDao<Touch,Integer>, JpaSpecificationExecutor<Touch> {
}
