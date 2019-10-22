package com.xajiusuo.busi.mail.service.impl;

import com.xajiusuo.busi.mail.dao.TouchDao;
import com.xajiusuo.busi.mail.entity.Touch;
import com.xajiusuo.busi.mail.send.MsgSendObservable;
import com.xajiusuo.busi.mail.service.TouchService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
@Service
public class TouchServiceImpl extends BaseServiceImpl<Touch, Integer> implements TouchService {

    @Autowired
    private TouchDao entityRepository;

    @Override
    public BaseDao<Touch, Integer> getBaseDao() {
        return entityRepository;
    }

    @Override
    @Scheduled(cron = "0 0/10 * * * ?")
    public void deleteInValid() {
        String sql = MessageFormat.format("delete from {0} where valid < sysdate",SqlUtils.tableName(Touch.class));
        entityRepository.executeUpdateSql(sql);
    }

    @Override
    public List<Touch> getTouchByUser(Integer uid) {
        String sql = MessageFormat.format("select * from {0} e where e.receiveUserId = ?",SqlUtils.tableName(Touch.class));
        List<Touch> list = entityRepository.executeNativeQuerySql(sql, uid);
        return list;
    }

    @Override
    public void sendTouchByUser(Integer uid) {
        List<Touch> list = getTouchByUser(uid);
        try{
            Thread.sleep(1000);
            if(list.size() > 0){
                for(Touch t:list){
                    MsgSendObservable.sendMessage(t);//首次登陆消息堆发送
                    if(t.isSend()){//如果发送则进行删除该条消息
                        entityRepository.delete(t);
                        Thread.sleep(800);//针对单一用户发送,进行间隔时间,避免造成消息雍堵
                    }
                }
            }
        }catch (Exception e){
        }
    }

}