package com.xajiusuo.busi.mail.dao;

import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.busi.mail.entity.Custom;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomDao extends BaseDao<Custom,Integer>, JpaSpecificationExecutor<Custom> {
}
