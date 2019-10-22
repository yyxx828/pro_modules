package com.xajiusuo.busi.sync.service.impl;

import com.xajiusuo.busi.sync.dao.InDataLogDao;
import com.xajiusuo.busi.sync.entity.InDataLog;
import com.xajiusuo.busi.sync.service.InDataLogService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 杨勇 19-7-2
 *  导入记录
 */
@Service
public class InDataLogServiceImpl extends BaseServiceImpl<InDataLog, String> implements InDataLogService {

    @Autowired
    InDataLogDao entityRepository;


    @Override
    public BaseDao<InDataLog, String> getBaseDao() {
        return entityRepository;
    }

}
