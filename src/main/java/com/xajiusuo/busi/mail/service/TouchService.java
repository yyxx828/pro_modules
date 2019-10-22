package com.xajiusuo.busi.mail.service;

import com.xajiusuo.jpa.config.BaseService;
import com.xajiusuo.busi.mail.entity.Touch;

import java.util.List;

public interface TouchService extends BaseService<Touch, Integer> {

    /***
     * 删除过期无效提醒
     * 杨勇 19-4-2
     */
    void deleteInValid();

    /***
     * 读取某人消息
     * 杨勇 19-4-2
     * @param uid
     * @return
     */
    List<Touch> getTouchByUser(Integer uid);

    /***
     * 用户登陆推送集合消息
     * 杨勇 19-4-2
     * @param uid
     */
    void sendTouchByUser(Integer uid);
}
