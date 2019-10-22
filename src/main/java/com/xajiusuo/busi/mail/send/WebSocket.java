package com.xajiusuo.busi.mail.send;

import com.alibaba.fastjson.JSON;
import com.xajiusuo.busi.mail.entity.Talk;
import com.xajiusuo.busi.mail.entity.Touch;
import com.xajiusuo.busi.mail.send.conf.TipsConf;
import com.xajiusuo.busi.mail.service.CustomService;
import com.xajiusuo.busi.mail.service.TalkService;
import com.xajiusuo.busi.mail.service.TouchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;

/**
 * @Author: 杨勇
 * @Description: 和浏览器联通后
 * @Date:Created 19-3-28
 */
@Slf4j
@ServerEndpoint("/webSocket")
@Component
public class WebSocket{

    private static CustomService customService;

    private static TouchService touchService;

    private static TalkService talkService;

    //需要通过其它方法进行初始注入
    public static void init(CustomService customService, TouchService touchService, TalkService talkService){
        if(WebSocket.customService == null)
        WebSocket.customService = customService;
        if(WebSocket.touchService == null)
        WebSocket.touchService = touchService;
        if(WebSocket.talkService == null)
        WebSocket.talkService = talkService;
        WebMessageObserver.init(WebSocket.customService);
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        MsgSendObservable.sendLogin(WebMessageObserver.removeObservable(session),TipsConf.TIPS_OFFLINE);//下线通知
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        if(StringUtils.isNotBlank(message)){
            if(message.startsWith("1,") || message.startsWith("2,")){//上线通知
                WebMessageObserver wo = WebMessageObserver.addObserver(session,message);
                if(wo != null){
                    MsgSendObservable.sendLogin(wo,TipsConf.TIPS_ONLINE);//上线通知

                    session.getBasicRemote().sendText(JSON.toJSON(Touch.getMsgTouch(null)).toString());//用户登陆未读消息订阅查看

                    touchService.sendTouchByUser(wo.getUserId());//用户登陆积压消息提醒弹出

                    Talk entity = talkService.prevListTask(new Date(),wo.getUserId(),null);//聊天记录
                    Talk.setUserId(wo.getUserId());
                    if(entity.getList() != null && entity.getList().size() > 0){
                        for(Talk t:entity.getList()){

                            session.getBasicRemote().sendText(JSON.toJSON(t).toString());//用户登陆未读消息订阅查看
                            try {Thread.sleep(100);} catch (Exception e) {e.printStackTrace();}
                        }
                    }
                }
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) throws IOException {
        onClose(session);
    }


}