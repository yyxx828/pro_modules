package com.xajiusuo.busi.buildingaccess.service.impl;

import com.xajiusuo.busi.buildingaccess.dao.BuildingEntranceCardDao;
import com.xajiusuo.busi.buildingaccess.entity.BuildingEntranceCard;
import com.xajiusuo.busi.buildingaccess.service.BuildingEntranceCardService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Iterator;

/**
 * Created by wangdou on 2019/6/6
 */
@Service
public class BuildingEntranceCardServiceImpl extends BaseServiceImpl<BuildingEntranceCard, String> implements BuildingEntranceCardService {

    @Autowired
    private BuildingEntranceCardDao buildingEntranceCardDao;

    @Override
    public BaseDao<BuildingEntranceCard, String> getBaseDao() {
        return buildingEntranceCardDao;
    }

    @Override
    public Page<BuildingEntranceCard> query(BuildingEntranceCard buildingEntranceCard, Pageable page) {
        StringBuilder hql = new StringBuilder("select * from " + tableName() + " where 1=1 ");
        return buildingEntranceCardDao.executeQuerySqlByPage(page, hql.toString());
    }
}
