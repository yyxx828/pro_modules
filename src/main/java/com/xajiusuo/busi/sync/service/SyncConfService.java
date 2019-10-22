package com.xajiusuo.busi.sync.service;

import com.xajiusuo.busi.sync.entity.SyncConf;
import com.xajiusuo.jpa.config.BaseService;

/**
 * @author 杨勇
 * @Date 2019/7/2
 * @Description 数据同步配置
 */
public interface SyncConfService extends BaseService<SyncConf, Integer> {

    void destoryConfAndLog(Integer id);
}
