package com.xajiusuo.busi.mail.dao;

import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.busi.mail.entity.Talk;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TalkDao extends BaseDao<Talk,String>, JpaSpecificationExecutor<Talk> {
}
