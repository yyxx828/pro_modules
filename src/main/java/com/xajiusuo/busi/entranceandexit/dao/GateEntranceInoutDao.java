package com.xajiusuo.busi.entranceandexit.dao;

import com.xajiusuo.busi.entranceandexit.entity.GateEntranceGuardLog;
import com.xajiusuo.busi.entranceandexit.entity.GateEntranceInOut;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by shirenjing on 2019/6/21.
 */
public interface GateEntranceInoutDao extends BaseDao<GateEntranceInOut, String>, JpaSpecificationExecutor<GateEntranceInOut> {
}
