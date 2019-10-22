package com.xajiusuo.busi.diction.service.impl;

import com.xajiusuo.busi.diction.dao.CitysDataDao;
import com.xajiusuo.busi.diction.entity.CitysData;
import com.xajiusuo.busi.diction.service.CitysDataService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author GaoYong
 * @Date 19-6-6 下午6:05
 * @Description
 */
@Slf4j
@Service
public class CitysDataServiceImpl extends BaseServiceImpl<CitysData,String> implements CitysDataService{

    @Autowired
    CitysDataDao CitysDataDao;

    @Override
    public BaseDao<CitysData, String> getBaseDao() {
        return CitysDataDao;
    }
}
