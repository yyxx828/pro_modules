package com.xajiusuo.busi.mail.service;

import com.xajiusuo.jpa.config.BaseService;
import com.xajiusuo.busi.mail.entity.Custom;
import com.xajiusuo.busi.mail.send.conf.TipsConf;
import com.xajiusuo.busi.user.entity.Userinfo;

import java.util.Map;

public interface CustomService extends BaseService<Custom, Integer> {

    /***
     * 读取某用户对应消息类型配置
     * 杨勇 19-4-1
     * @param userId 要获取的用户ID
     * @param t 消息类型
     * @return 获取某用户的消息类型的订阅信息
     */
    Custom getUserConf(Integer userId, TipsConf t);

    /***
     * 读取某用户对应消息类型配置
     * 杨勇 19-4-3
     * @param userId 要获取的用户ID
     * @return 获取某用户的消息类型的订阅信息
     */
    Map<TipsConf, Custom> getUserConfMap(Integer userId);

    /***
     * 获取用户信息
     * @param userId
     * @return
     */
    Userinfo getUserInfo(Integer userId);

    /***
     * 用户登陆对用户信息进行重值
     * @param userId
     */
    void userLogin(Integer userId);

    /***
     * 初始方法
     */
    void initService();
}
