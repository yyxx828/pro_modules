package com.xajiusuo.busi.control.service.impl;

import com.xajiusuo.busi.control.dao.ControlIdentifyDao;
import com.xajiusuo.busi.control.entity.ControlIdentify;
import com.xajiusuo.busi.control.entity.ControlIdentifyVo;
import com.xajiusuo.busi.control.entity.ControlTask;
import com.xajiusuo.busi.control.service.ControlIdentifyService;
import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.util.SqlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shirenjing on 2019/6/22.
 */
@Service
public class ControlIdentifyServiceImpl extends BaseServiceImpl<ControlIdentify,String> implements ControlIdentifyService {

    @Autowired
    private ControlIdentifyDao controlIdentifyDao;
    @Override
    public BaseDao<ControlIdentify, String> getBaseDao() {
        return controlIdentifyDao;
    }

    @Override
    public void batchSaveIdentify(List<ControlIdentify> identifyList) {
        controlIdentifyDao.batchSaveOrUpdate(identifyList);
    }

    @Override
    public void batchSaveIdentify(ControlTask task, List<ControlIdentifyVo> identifies) {
        List<ControlIdentify> identifyList = new ArrayList<>();
        ControlIdentify identify = null;
        for (ControlIdentifyVo iden: identifies) {
            Diction diction = new Diction();
            diction.setKeys(iden.getIdType());
            identify = new ControlIdentify(task,iden.getIdentify(),diction);
            identifyList.add(identify);
        }
        if(identifyList.size()>0) controlIdentifyDao.batchSaveOrUpdate(identifyList);
    }

    @Override
    public void deleteByTaskId(String taskId) {
        String sql = "select * from t_controlidentify_16 where taskid = ?";
        controlIdentifyDao.deleteInBatch(controlIdentifyDao.executeNativeQuerySql(sql,taskId));
    }

    @Override
    public void deleteByTask(ControlTask task) {
        controlIdentifyDao.deleteInBatch(controlIdentifyDao.findBy("task",task));
    }

    @Override
    public Page<ControlIdentify> queryPageIdentify(Pageable pageable, String taskid) {
        String sql = "select * from t_controlidentify_16 where taskid = ?";
        return controlIdentifyDao.executeQuerySqlByPage(pageable,sql,taskid);
    }
}
