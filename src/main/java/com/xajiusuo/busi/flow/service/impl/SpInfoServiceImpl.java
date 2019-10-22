package com.xajiusuo.busi.flow.service.impl;

import com.xajiusuo.busi.flow.dao.SpInfoDao;
import com.xajiusuo.busi.flow.entity.FlowIns;
import com.xajiusuo.busi.flow.entity.SpInfo;
import com.xajiusuo.busi.flow.service.SpInfoService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.util.SqlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanhua on 18-1-23.
 */
@Service
public class SpInfoServiceImpl extends BaseServiceImpl<SpInfo, Integer> implements SpInfoService {

    @Autowired
    private SpInfoDao spInfoDao;

    @Override
    public BaseDao<SpInfo, Integer> getBaseDao() {
        return spInfoDao;
    }

    @Override
    public List<SpInfo> getByInsId(FlowIns flowIns) {
        List<Object> list = new ArrayList(0);
        list.add(flowIns);
        String sb = MessageFormat.format("select * from {0} f where 1 = 1 and f.insId = ? order by createTime", SqlUtils.tableName(SpInfo.class));
        return spInfoDao.executeNativeQuerySql(sb.toString(), list.toArray());
    }
}
