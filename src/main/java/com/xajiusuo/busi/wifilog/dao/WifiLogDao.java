package com.xajiusuo.busi.wifilog.dao;

import com.xajiusuo.busi.wifilog.entity.WifiLog;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by liujiankang on 19-6-10.
 */
public interface WifiLogDao extends BaseDao<WifiLog,String>,JpaSpecificationExecutor<WifiLog> {
}
