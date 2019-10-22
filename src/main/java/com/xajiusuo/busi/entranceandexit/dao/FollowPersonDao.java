package com.xajiusuo.busi.entranceandexit.dao;

import com.xajiusuo.busi.entranceandexit.entity.FollowPerson;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by shirenjing on 2019/6/17.
 */
public interface FollowPersonDao extends BaseDao<FollowPerson,String>,JpaSpecificationExecutor<FollowPerson> {
}
