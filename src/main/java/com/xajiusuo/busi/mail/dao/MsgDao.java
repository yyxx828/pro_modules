package com.xajiusuo.busi.mail.dao;

import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.busi.mail.entity.Msg;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MsgDao extends BaseDao<Msg,Integer>, JpaSpecificationExecutor<Msg> {
}
