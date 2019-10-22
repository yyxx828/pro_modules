package com.xajiusuo.busi.diction.service.impl;

import com.xajiusuo.busi.diction.dao.DepartPoliceDao;
import com.xajiusuo.busi.diction.entity.DepartPolice;
import com.xajiusuo.busi.diction.service.DepartPoliceService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author GaoYong
 * @Date 19-6-6 下午5:32
 * @Description
 */
@Slf4j
@Service
public class DepartPoliceServiceImpl extends BaseServiceImpl<DepartPolice,String> implements DepartPoliceService {
    
    @Autowired
    DepartPoliceDao departPoliceDao;

    @Override
    public BaseDao<DepartPolice, String> getBaseDao() {
        return departPoliceDao;
    }
}
