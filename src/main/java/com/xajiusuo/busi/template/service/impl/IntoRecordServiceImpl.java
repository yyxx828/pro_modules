package com.xajiusuo.busi.template.service.impl;

import com.xajiusuo.busi.template.dao.IntoRecordDao;
import com.xajiusuo.busi.template.entity.IntoRecord;
import com.xajiusuo.busi.template.service.IntoRecordService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.config.PropertiesConfig;
import com.xajiusuo.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class IntoRecordServiceImpl extends BaseServiceImpl<IntoRecord, Integer> implements IntoRecordService {

    @Autowired
    private PropertiesConfig config;

    @Autowired
    private IntoRecordDao entityRepository;

    @Override
    public BaseDao<IntoRecord, Integer> getBaseDao() {
        return entityRepository;
    }

    @Override
    public Page<IntoRecord> findPageByEntity(Pageable pageable, IntoRecord entity) {
        String sql = BeanUtils.beanToSql(entity);
        sql += " order by " + pageable.getSort().toString().replaceAll(":", "");
        return entityRepository.executeQuerySqlByPage(pageable,sql);
    }

}
