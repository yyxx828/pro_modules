package com.xajiusuo.busi.mail.service;

import com.xajiusuo.jpa.config.BaseService;
import com.xajiusuo.busi.mail.entity.Talk;

import java.util.Date;
import java.util.List;

public interface TalkService extends BaseService<Talk, String> {

    /***
     * 杨勇 // TODO: 19-4-11
     * 群聊
     * @return
     */
    Talk speak(Talk entity);

    /***
     * 上一页聊天记录
     * 杨勇
     * @param date 上一页最大日期(含)
     * @param userId
     * @param key
     * @return
     */
    Talk prevListTask(Date date, Integer userId, String key);

    /***
     * 下一页聊天记录
     * 杨勇
     * @param date 下一页最大日期(含)
     * @param userId
     * @param key
     * @return
     */
    Talk nextListTask(Date date, Integer userId, String key);

    /***
     * 查询聊天历史日期
     * @param userId
     * @return
     */
    List<String> listTalkDate(Integer userId);

    /***
     * 清理历史记录保留30天记录
     */
    void clearHistory();
}
