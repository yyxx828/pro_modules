package com.xajiusuo.busi.electronicFenceLog.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xajiusuo.busi.electronicFenceLog.entity.ElectronicFenceLog;
import com.xajiusuo.jpa.config.BaseService;

/**
 * @author GaoYong
 * @Date 19-6-12 上午11:52
 * @Description周界报警信息业务实现接口
 */
public interface ElectronicFenceLogService extends BaseService<ElectronicFenceLog,String> {
    
    /**
     *@Description: 获取特定时间段内不同设备ID(deviceid)的报警次数
     *@param: sql
     *@return: JSONObject
     *@Author: gaoyong
     *@date: 19-6-13 下午2:48
    */
    JSONArray countElectronicfencelog(String sql);
    
    /**
     *@Description: 指定时间颗粒统计
     *@param: [sql,timeBs,timePill]
     *@return: JSONObject
     *@Author: gaoyong
     *@date: 19-6-13 下午4:37
    */
    JSONObject countRegionTime(String sql,String timeBs,String timePill);

    /**
     *@Description:获取消息提醒的内容
     *@param:
     *@Author: gaoyong
     *@date: 19-6-24 下午7:09
    */
    void getMessgage();
}
