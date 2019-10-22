package com.xajiusuo.busi.entranceandexit.dao;

import com.xajiusuo.busi.entranceandexit.entity.PersonWarn;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by shirenjing on 2019/6/26.
 */
public interface PersonWarnDao extends BaseDao<PersonWarn, String>, JpaSpecificationExecutor<PersonWarn> {
}
