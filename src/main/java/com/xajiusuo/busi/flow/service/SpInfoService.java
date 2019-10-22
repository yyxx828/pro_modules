package com.xajiusuo.busi.flow.service;

import com.xajiusuo.busi.flow.entity.FlowIns;
import com.xajiusuo.busi.flow.entity.SpInfo;
import com.xajiusuo.jpa.config.BaseService;

import java.util.List;

/**
 * Created by fanhua on 18-1-23.
 */
public interface SpInfoService extends BaseService<SpInfo, Integer> {
    /**
     * @author fanhua
     * 根据insId获取审批历史
     * @param flowIns
     * @return
     */
    List<SpInfo> getByInsId(FlowIns flowIns);
}
