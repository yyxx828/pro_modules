package com.xajiusuo.busi.sync.service.impl;

import com.xajiusuo.busi.sync.dao.InDataLogDao;
import com.xajiusuo.busi.sync.dao.SyncConfDao;
import com.xajiusuo.busi.sync.entity.SyncConf;
import com.xajiusuo.busi.sync.service.SyncConfService;
import com.xajiusuo.busi.sync.vo.SyncConfFactory;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 杨勇 19-7-2
 *  数据同步配置
 */
@Service
public class SyncConfServiceImpl extends BaseServiceImpl<SyncConf, Integer> implements SyncConfService {

    @Autowired
    private SyncConfDao entityRepository;

    @Autowired
    private InDataLogDao inDataLogDao;

    @Override
    public BaseDao<SyncConf, Integer> getBaseDao() {
        return entityRepository;
    }

    @Override
    @Transactional
    public void destoryConfAndLog(Integer id) {
        List<String> ids = inDataLogDao.getList("id","syncConf",new SyncConf(id),null,null,null);
        if(ids.size() > 0){
            inDataLogDao.destroy(ids.toArray(new String[ids.size()]));
        }
        entityRepository.destroy(id);
        SyncConfFactory.remove(new SyncConf(id));
    }
}
