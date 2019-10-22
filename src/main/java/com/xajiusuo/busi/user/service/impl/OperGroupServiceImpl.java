package com.xajiusuo.busi.user.service.impl;

import com.xajiusuo.busi.user.dao.OperGroupDao;
import com.xajiusuo.busi.user.entity.OperGroup;
import com.xajiusuo.busi.user.service.OperGroupService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.util.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @author zlm 2018/1/18
 *  角色逻辑接口实现类
 */
@Service
public class OperGroupServiceImpl extends BaseServiceImpl<OperGroup, Integer> implements OperGroupService {

    @Autowired
    OperGroupDao operGroupRepository;

    @Override
    public BaseDao<OperGroup, Integer> getBaseDao() {
        return operGroupRepository;
    }


    @Override
    public Page<OperGroup> query(OperGroup entity, Pageable page) {

        StringBuffer hql = new StringBuffer(MessageFormat.format("select e.* from {0} e where 1 = 1", SqlUtils.tableName(OperGroup.class)));

        if( StringUtils.isNotBlank(entity.getGname())){
            hql.append(" and e.gname like '" + SqlUtils.sqlLike(entity.getGname()) + "'");
        }

        if( StringUtils.isNotBlank(entity.getGdesc())){
            hql.append(" and e.gdesc like '" + SqlUtils.sqlLike(entity.getGdesc()) + "'");
        }

        return operGroupRepository.executeQuerySqlByPage(page,hql.toString());
    }
}
