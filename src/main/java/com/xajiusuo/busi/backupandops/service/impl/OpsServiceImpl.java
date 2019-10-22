package com.xajiusuo.busi.backupandops.service.impl;

import com.xajiusuo.busi.backupandops.backopsutils.BackopsUtil;
import com.xajiusuo.busi.backupandops.service.OpsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by wangdou on 2019/6/18
 */
@Service
public class OpsServiceImpl implements OpsService {
    @Value("${device.path}")
    private String devicepath;

    @Override
    public Map<String, Object> getDevStatus(String ips) {
        if (!BackopsUtil.operatingSys()) {
            return BackopsUtil.getDevStatus("D:\\backops\\device\\", ips);
        } else {
            return BackopsUtil.getDevStatus(devicepath, ips);
        }
    }
}
