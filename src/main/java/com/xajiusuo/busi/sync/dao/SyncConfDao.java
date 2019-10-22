package com.xajiusuo.busi.sync.dao;

import com.xajiusuo.busi.user.entity.Depart;
import com.xajiusuo.busi.sync.entity.SyncConf;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author 杨勇
 * @Date 2019/7/2
 * @Description 数据同步配置
 */
public interface SyncConfDao extends BaseDao<SyncConf,Integer>,JpaSpecificationExecutor<SyncConf> {

}
