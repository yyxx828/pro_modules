package com.xajiusuo.busi.mail.service.impl;

import com.xajiusuo.busi.mail.dao.CustomDao;
import com.xajiusuo.busi.mail.entity.Custom;
import com.xajiusuo.busi.mail.send.WebSocket;
import com.xajiusuo.busi.mail.send.conf.TipsConf;
import com.xajiusuo.busi.mail.service.CustomService;
import com.xajiusuo.busi.mail.service.TalkService;
import com.xajiusuo.busi.mail.service.TouchService;
import com.xajiusuo.busi.user.dao.UserinfoDao;
import com.xajiusuo.busi.user.entity.Userinfo;
import com.xajiusuo.busi.user.service.UserinfoService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CustomServiceImpl extends BaseServiceImpl<Custom, Integer> implements CustomService {

    @Autowired
    private CustomDao entityRepository;

    @Autowired
    private UserinfoService userinfoService;

    @Autowired
    private TouchService touchService;

    @Autowired
    private UserinfoDao userinfoDao;

    @Autowired
    private TalkService talkService;

    @Override
    public BaseDao<Custom, Integer> getBaseDao() {
        return entityRepository;
    }

    //所有用户消息发送管理,即根据订阅进行判断是否保存,发送,动态读取用户配置.如果未涉及则对用户不进行预读
    private static Map<Integer,Map<TipsConf,Custom>> messageFilterMap = Collections.synchronizedMap(new HashMap<Integer,Map<TipsConf,Custom>>());

    private static Map<Integer,Userinfo> userMap = Collections.synchronizedMap(new HashMap<Integer,Userinfo>());

    public Custom getUserConf(Integer userId, TipsConf t){
        Map<TipsConf, Custom> m = getUserConfMap(userId);
        return m.get(t);
    }

    @Override
    public synchronized Map<TipsConf, Custom> getUserConfMap(Integer userId) {
        Map<TipsConf, Custom> m = messageFilterMap.get(userId);
        if(m == null){
            m = new HashMap<TipsConf,Custom>();
            List<Custom> list = null;
            if (null != userId){
                list = findBy("userId",userId);
            }
            if(list != null){
                for(Custom c:list){
                    TipsConf tc = null;
                    try{
                        tc = TipsConf.valueOf(c.getTipsConf());
                    }catch (Exception e){}
                    if(tc != null){
                        c.setEnble(tc.isSubscribe());
                        m.put(tc,c);
                    }else{
                        entityRepository.delete(c);//对于未存在的进行删除
                    }
                }
            }
            for(TipsConf tc:TipsConf.values()){
                if(tc.equals(TipsConf.TIPS_NO)){
                    continue;
                }
                if(m.get(tc) == null){
                    Custom c = new Custom(userId,tc);
                    entityRepository.save(c);
                    m.put(tc,c);
                }
            }
            messageFilterMap.put(userId,m);
        }
        return m;
    }


    public Userinfo getUserInfo(Integer userId){
        Userinfo user = userMap.get(userId);
        if(user == null){
            user = userinfoService.getOne(userId);
            if(user.getId() != null)
            userMap.put(userId,user);
        }
        return user;
    }

    public void userLogin(Integer userId){
        userMap.remove(userId);
    }

    @PostConstruct
    public void initService(){
        WebSocket.init(this,touchService, talkService);//对webSocket增加相关service

        List<Integer> uids = userinfoDao.getList("id",(String)null,null,null,null,null);

        log.info("初始用户消息配置");
        for(Integer uid:uids) {
            Custom c = getUserConf(uid, null);
        }
        log.info("用户消息配置完成");
    }
}
