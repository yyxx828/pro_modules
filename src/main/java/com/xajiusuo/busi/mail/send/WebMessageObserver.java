package com.xajiusuo.busi.mail.send;

import com.alibaba.fastjson.JSON;
import com.xajiusuo.busi.mail.entity.Custom;
import com.xajiusuo.busi.mail.entity.Talk;
import com.xajiusuo.busi.mail.entity.Touch;
import com.xajiusuo.busi.mail.send.conf.TipsConf;
import com.xajiusuo.busi.mail.service.CustomService;
import com.xajiusuo.busi.user.entity.Userinfo;
import org.apache.tomcat.websocket.WsSession;

import javax.websocket.Session;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * web消息接受者,一个浏览器对应一个
 * Created by hadoop on 19-3-27.
 */
public class WebMessageObserver implements Observer {

    private static final Map<String,WebMessageObserver> observers = new HashMap<String,WebMessageObserver>();

    private static final Map<String,Map<String,Long>> sendTime = new HashMap<String,Map<String,Long>>();

    private Observable observable;//监听对象-发消息者

    private Session session;//用户连接会话,和浏览器对话

    private Integer userId;//用户ID

    private static CustomService customService;

    private WebMessageObserver(Session session, Userinfo userinfo){
        this.observable = MsgSendObservable.getInstance();
        this.session = session;
        this.userId = userinfo.getId();
        observable.addObserver(this);
        observers.put(getSeesionId(session),this);
    }

    //初始service
    public static void init(CustomService service){
        if(customService == null){
            customService = service;
        }
    }

    /***
     * 增加上线用户
     * @param session
     * @param message
     * @return
     */
    public static WebMessageObserver addObserver(Session session,String message){
        try{
            Integer uid = Integer.parseInt(message.split(",")[1]);
            customService.userLogin(uid);//用户登陆对用户进行重新加载
            Userinfo user = customService.getUserInfo(uid);//
            if(user.getId() != null){
                String sessionId = getSeesionId(session);
                WebMessageObserver webObserver = observers.get(sessionId);
                if(webObserver != null){
                    webObserver.session = session;
                    return webObserver;
                }
                return new WebMessageObserver(session,user);
            }
        }catch (Exception e){
        }
        return null;
    }

    /***
     * 接受消息对象下线后移除观察对象
     * 杨勇 19-3-28
     * @param session
     */
    public static WebMessageObserver removeObservable(Session session){
        WebMessageObserver o = observers.remove(getSeesionId(session));
        if(o != null){
            o.removeObservable();
        }
        return o;
    }

    //消息发送
    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof List){//批量
            for(Object a:(List)arg){
                update(o,a);
            }
        }else if(arg instanceof Touch){//消息
            Touch touch = (Touch) arg;
            if(userId == null || userId.equals(touch.getSendUserId())) {//为空或者发送人是自己则不进行发送
                touch.send();
                return;
            }

            if(touch.getReceiveUserId() == null || userId.equals(touch.getReceiveUserId())){//消息接收者被通知消息
                TipsConf t = touch.getTipsConf();
                if(t != null){
                    Custom c = customService.getUserConf(userId, t);
                    if(touch.getType() != 0 || touch.getMust() ||(c != null && (c.getRemind() == null || c.getRemind()))){
                        send(JSON.toJSON(touch).toString());
                    }
                }
                touch.send();//标记发送
            }
        }else if(arg instanceof Talk){//群聊
            Talk talk = (Talk) arg;
            talk.setMe(userId);
            if(talk.getCase() != -1){//和自己有关消息发送
                send(JSON.toJSON(talk).toString());
            }
        }
    }

    private void send(String msg){
        try {
            session.getBasicRemote().sendText(msg);
        } catch (Exception e) {
        }
    }

    public void removeObservable(){
        observable.deleteObserver(this);
    }

    /***
     * 计算当前在线总人数,按照人员ID计算
     * @return
     */
    public synchronized static int getOnlineNum(){
        Set<Integer> set = new HashSet<Integer>();
        for(WebMessageObserver w : observers.values()){
            set.add(w.userId);
        }
        int num = set.size();
        set.clear();
        return num == 0 ? 1 : num;
    }

    /***
     * 计算当前在线总人数,按照人员ID计算
     * @return
     * @param userId
     */
    public synchronized static List<Map<String, Object>> getOnlineUser(Integer userId){
        Set<Integer> set = new HashSet<Integer>();
        List<Userinfo> list = new ArrayList<Userinfo>();
        list.add(new Userinfo("","所有人"));
        list.get(0).setId(0);
        for(WebMessageObserver w : observers.values()){
            if(set.contains(w.getUserId()) || w.getUserId().equals(userId)){
                continue;
            }
            set.add(w.getUserId());
            list.add(customService.getUserInfo( w.userId));
        }
        set.clear();
        List<Map<String,Object>> maps = list.stream().map(u -> {
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("value",((Number)u.getId()).toString());
            String name = null;
            if(u.getId() != null && u.getId() != 0){
                name = MessageFormat.format("{0}[{1}]", u.getFullname(), u.getDepartName());
            }else{
                name = u.getFullname();
            }
            map.put("name", name);
            return map;}).collect(Collectors.toList());
        return maps;
    }

    public Integer getUserId() {
        return userId;
    }

    public Userinfo getUser() {
        return customService.getUserInfo(userId);
    }

    public static String getSeesionId(Session session){
        if(session instanceof WsSession){
            return ((WsSession) session).getHttpSessionId();
        }
        return session.getId() + session.toString();
    }

    public static Map<String,Long> getSendTime(Session session){
         Map<String,Long> map = sendTime.get( getSeesionId(session));
        if(map == null){
            map = new HashMap<String,Long>();
            sendTime.put(getSeesionId(session),map);
        }
        return map;
    }

}