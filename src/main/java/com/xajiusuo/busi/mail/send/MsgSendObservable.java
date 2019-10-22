package com.xajiusuo.busi.mail.send;

import com.xajiusuo.busi.mail.entity.Touch;
import com.xajiusuo.busi.mail.send.conf.TipsConf;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Observable;

/**
 * 消息发送控制
 * 杨勇 19-3-28
 * Created by hadoop on 19-3-28.
 */
public class MsgSendObservable extends Observable {

    private static MsgSendObservable instance;
    private static final SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private MsgSendObservable(){
    }

    /***
     * 获取单例消息发送总控制器
     * 杨勇 19-3-28
     * @return
     */
    public static synchronized MsgSendObservable getInstance(){
        if(instance == null){
            instance = new MsgSendObservable();
        }
        return instance;
    }

    //消息发送
    public synchronized static void sendMessage(Object arg){
        getInstance().setChanged();
        getInstance().notifyObservers(arg);
    }

    //用户上下线通知
    public synchronized static void sendLogin(WebMessageObserver w, TipsConf tipsOnOff){
        if(w != null && (TipsConf.TIPS_ONLINE.equals(tipsOnOff) || TipsConf.TIPS_OFFLINE.equals(tipsOnOff))){//上下线消息

            Touch touch = new Touch();
            touch.setTipsConf(tipsOnOff);
            touch.setSendUserId(w.getUserId());//发送人为当前人,不接受自己上下线消息
            touch.setContent(MessageFormat.format("用户[{0}]{1},所属部门[{2}]",w.getUser().getFullname(),touch.getTipsConf().getName(),w.getUser().getDepartName()));
            getInstance().setChanged();
            getInstance().notifyObservers(touch);//通知上下线消息

            getInstance().setChanged();
            getInstance().notifyObservers(Touch.getOnlineTouch());//更改上线人数

            //增加聊天记录
        }
    }

    @Override
    protected synchronized void setChanged() {
        super.setChanged();
    }
}