package com.xajiusuo.busi.diction.service.impl;

import com.xajiusuo.busi.diction.dao.ProvincesDataDao;
import com.xajiusuo.busi.diction.entity.ProvincesData;
import com.xajiusuo.busi.diction.service.ProvincesDataService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author GaoYong
 * @Date 19-6-6 下午6:05
 * @Description
 */
@Slf4j
@Service
public class ProvincesDataServiceImpl extends BaseServiceImpl<ProvincesData,String> implements ProvincesDataService{

    @Autowired
    ProvincesDataDao provincesDataDao;

    @Override
    public BaseDao<ProvincesData, String> getBaseDao() {
        return provincesDataDao;
    }
}
