package com.xajiusuo.busi.mail.service.impl;

import com.xajiusuo.busi.mail.dao.MsgDao;
import com.xajiusuo.busi.mail.dao.TouchDao;
import com.xajiusuo.busi.mail.entity.Custom;
import com.xajiusuo.busi.mail.entity.Msg;
import com.xajiusuo.busi.mail.entity.Touch;
import com.xajiusuo.busi.mail.send.MsgSendObservable;
import com.xajiusuo.busi.mail.send.conf.TipsConf;
import com.xajiusuo.busi.mail.service.CustomService;
import com.xajiusuo.busi.mail.service.MsgService;
import com.xajiusuo.busi.user.dao.UserinfoDao;
import com.xajiusuo.busi.user.entity.Userinfo;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MsgServiceImpl extends BaseServiceImpl<Msg, Integer> implements MsgService {

    @Autowired
    private MsgDao entityRepository;

    @Autowired
    private CustomService customService;

    @Autowired
    private TouchDao touchRepository;

    @Autowired
    private UserinfoDao userinfoRepository;

    @Override
    public BaseDao<Msg, Integer> getBaseDao() {
        return entityRepository;
    }


    @Override
    @Transactional
    public void sendMessage(TipsConf tipsConf, String content, Object id, Object caseId,String param, boolean must, Integer sendUserId, Integer... receiveUserIds) {
        Integer[] uids = receiveUserIds;

        if(uids == null || uids.length == 0){//指定用户接收
            uids = userinfoRepository.findAll().stream().map(Userinfo::getId).collect(Collectors.toList()).toArray(new Integer[]{});
        }

        //发送去重设置
        Set<Integer> set = new HashSet<Integer>();

        for(Integer uid:uids){

            if(set.contains(uid)){
                continue;
            }
            set.add(uid);

            Touch t = packMessage(tipsConf,content,id, caseId, must,sendUserId,uid, param);//装包一个未发送消息

            Custom c = customService.getUserConf(uid,tipsConf);
            if(c.getSubscribe()){//订阅保存
                try{
                    Msg msg = entityRepository.save(t.toMsg());
                    t.setMsgId(msg.getId());//对于订阅保存消息信息放置提醒消息,通过点击消息对消息进行已读设置
                    MsgSendObservable.sendMessage(Touch.getMsgTouch(uid));//对于有新未读消息进行数量通知
                }catch (Exception e){
                    log.error("订阅消息保存错误,错误信息:" + e.getMessage());
                }
            }
            MsgSendObservable.sendMessage(t);//消息实时推送
            if((must || c.getRemind()) && null != uid && !uid.equals(sendUserId) && !t.isSend()){//订阅提醒
                try{
                    touchRepository.save(t);
                }catch (Exception e){
                    log.error("消息提醒不线用户信息保存出错,错误信息:" + e.getMessage());
                }
            }
        }
        set.clear();
    }

    @Override
    @Transactional
    public void sendMessage(TipsConf tipsConf, String content, Object id, Object caseId, boolean must, Integer sendUserId, Integer... receiveUserIds) {
        sendMessage(tipsConf,content,id, caseId, null,false,sendUserId,receiveUserIds);
    }

    @Override
    @Transactional
    public void sendMessage(TipsConf tipsConf, String content, Object id, Object caseId, Integer sendUserId, Integer... receiveUserId) {
        sendMessage(tipsConf,content,id, caseId, false,sendUserId,receiveUserId);
    }

    @Override
    @Transactional
    public void sendMessage(Touch t) {
        Set<Integer> set = new HashSet<Integer>();
        if(t.getReceiveUserId() != null){//单个发送
            set.add(t.getReceiveUserId());
        }
        if(t.getReceiveUserIds() != null){
            for(Integer id:t.getReceiveUserIds()){
                set.add(id);
            }
        }
        sendMessage(t.getTipsConf(),t.getContent(),t.getEntityId(),t.getCaseId(),t.getMust(),t.getSendUserId(), set.toArray(new Integer[]{}));
        set.clear();
    }

    @Override
    @Transactional
    public int markReadMsg(Integer[] ids, Integer uid, boolean read) {
        int res = 0;
        if(ids != null){
            for(Integer id:ids){
                Msg msg = entityRepository.getOne(id);
                if(msg.getReceiveUserId().equals(uid)){
                    if(msg.getRead() != read){
                        msg.setRead(read);
                        entityRepository.save(msg);
                        res++;
                    }
                }
            }
        }else{
            String sql = MessageFormat.format("select * from {0} e where receiveUserId = ? and (read is null or read = ?)",SqlUtils.tableName(Msg.class)) ;
            List<Msg> list = executeNativeQuerySql(sql, uid,!read);
            list.forEach(msg -> {
                msg.setRead(read);
            });
            entityRepository.saveAll(list);
            res = list.size();
        }
        if(res > 0){
            MsgSendObservable.sendMessage(Touch.getMsgTouch(uid));//标记已读 对于有新未读消息进行数量通知
        }
        return res;
    }

    @Override
    @Transactional
    public int deleteMsg(Integer[] ids, Integer uid) {
        int res = 0,read = 0;
        if(ids != null){
            for(Integer id:ids){
                Msg msg = entityRepository.getOne(id);
                if(msg.getReceiveUserId().equals(uid)){
                    if(msg.getRead() == null || !msg.getRead()){
                        read++;
                    }
                    entityRepository.destroy(msg);
                    res++;
                }
            }
        }
        if(read > 0){
            MsgSendObservable.sendMessage(Touch.getMsgTouch(uid));//未读消息删除 对于有新未读消息进行数量通知
        }
        return res;
    }

    @Override
    @Transactional
    public void readMsg(TipsConf conf, Integer userId, String entityId) {
        String sql = MessageFormat.format("select * from {0} e where e.tips = ? and receiveUserId = ? and entityId = ? and (read = ? or read is null)", SqlUtils.tableName(Msg.class));
        List<Msg> list = executeNativeQuerySql(sql, conf.toString(),userId,entityId,false);
        if(list != null && list.size() > 0){
            for(Msg m:list){
                m.setRead(true);
                save(m);
            }
            MsgSendObservable.sendMessage(Touch.getMsgTouch(userId));//业务实体查看时候消息已读提醒
        }
    }

    /***
     * 装包消息发送
     * @param tipsConf
     * @param content
     * @param id
     * @param caseId
     * @param must
     * @param sendUserId
     * @param receiveUserId
     * @param param
     */
    private Touch packMessage(TipsConf tipsConf, String content, Object id, Object caseId, boolean must, Integer sendUserId, Integer receiveUserId, String param) {
        Touch touch = new Touch(must);
        touch.setSendUserId(sendUserId);
        touch.setContent(content);
        touch.setEntityId(id == null ? null : id.toString());
        touch.setCaseId(caseId == null ? null : caseId.toString());
        touch.setTipsConf(tipsConf);
        touch.setReceiveUserId(receiveUserId);
        touch.setParam(param);
        touch.valid();
        return touch;
    }

}
