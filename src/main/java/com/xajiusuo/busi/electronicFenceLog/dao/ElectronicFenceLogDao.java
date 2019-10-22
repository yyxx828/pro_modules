package com.xajiusuo.busi.electronicFenceLog.dao;

import com.xajiusuo.busi.electronicFenceLog.entity.ElectronicFenceLog;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author GaoYong
 * @Date 19-6-12 上午11:49
 * @Description 周界报警信息
 */
public interface ElectronicFenceLogDao extends BaseDao<ElectronicFenceLog,String>,JpaSpecificationExecutor<ElectronicFenceLog> {
}
