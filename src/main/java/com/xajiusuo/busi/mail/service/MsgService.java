package com.xajiusuo.busi.mail.service;

import com.xajiusuo.jpa.config.BaseService;
import com.xajiusuo.busi.mail.entity.Msg;
import com.xajiusuo.busi.mail.entity.Touch;
import com.xajiusuo.busi.mail.send.conf.TipsConf;

public interface MsgService extends BaseService<Msg, Integer> {


    /***
     * 消息发送
     * 杨勇 19-4-2
     * @param tipsConf 消息分类
     * @param content 消息内容
     * @param id 业务ID
     * @param caseId 案件ID
     * @param param 其它参数
     * @param must 强制提醒,登陆后接受消息
     * @param sendUserId 发送人ID,不对该用户进行发送
     * @param receiveUserId 接收人,如果空,则为全部人员
     */
    void sendMessage(TipsConf tipsConf, String content, Object id, Object caseId,String param, boolean must, Integer sendUserId, Integer... receiveUserId);

    /***
     * 消息发送
     * 杨勇 19-4-2
     * @param tipsConf 消息分类
     * @param content 消息内容
     * @param id 业务ID
     * @param caseId
     * @param must 强制提醒,登陆后接受消息
     * @param sendUserId 发送人ID,不对该用户进行发送
     * @param receiveUserId 接收人,如果空,则为全部人员
     */
    void sendMessage(TipsConf tipsConf, String content, Object id, Object caseId, boolean must, Integer sendUserId, Integer... receiveUserId);


     /***
     * 消息发送
     * 杨勇 19-4-2
     * @param tipsConf 消息分类
     * @param content 消息内容
     * @param id 业务ID
     * @param caseId
     * @param sendUserId 发送人ID,不对该用户进行发送
     * @param receiveUserId 接收人,如果空,则为全部人员
     */
    void sendMessage(TipsConf tipsConf, String content, Object id, Object caseId, Integer sendUserId, Integer... receiveUserId);

    /***
     * 消息发送
     * 杨勇 19-4-2
     * @param entity 发送提醒
     */
    void sendMessage(Touch entity);

    /***
     * 对消息进行已读标记
     * 杨勇 19-4-2
     * @param ids 消息
     * @param uid 用户ID
     * @param read
     */
    int markReadMsg(Integer[] ids, Integer uid, boolean read);

    /***
     * 消息删除
     * 杨勇 19-4-2
     * @param ids
     * @param uid
     */
    int deleteMsg(Integer[] ids, Integer uid);

    /***
     * 通过实体访问进行消息已读
     * 杨勇
     * @param conf
     * @param userId
     * @param entityId
     * @return
     */
    void readMsg(TipsConf conf, Integer userId, String entityId);

}
