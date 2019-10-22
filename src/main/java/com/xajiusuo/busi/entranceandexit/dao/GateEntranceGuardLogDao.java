package com.xajiusuo.busi.entranceandexit.dao;

import com.xajiusuo.busi.entranceandexit.entity.GateEntranceGuardLog;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by wangdou on 2019/6/6
 */
public interface GateEntranceGuardLogDao extends BaseDao<GateEntranceGuardLog, String>, JpaSpecificationExecutor<GateEntranceGuardLog> {
}
