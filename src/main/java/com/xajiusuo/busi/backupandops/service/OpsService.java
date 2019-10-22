package com.xajiusuo.busi.backupandops.service;

import java.util.Map;

/**
 * Created by wangdou on 2019/6/18
 */
public interface OpsService {
    Map<String, Object> getDevStatus(String ips);
}
