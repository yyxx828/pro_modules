package com.xajiusuo.busi.entranceandexit.service;

import com.xajiusuo.busi.buildingaccess.entity.ResultVo;
import com.xajiusuo.busi.entranceandexit.entity.GateEntranceGuardLog;
import com.xajiusuo.busi.entranceandexit.entity.GateEntranceInOut;
import com.xajiusuo.busi.entranceandexit.entity.GuardCardCountVo;
import com.xajiusuo.busi.entranceandexit.vo.PersonEarlyWarningVo;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by shirenjing on 2019/6/21.
 */
public interface GateEntranceInOutService extends BaseService<GateEntranceInOut,String> {

    /**
     * @Author shirenjing
     * @Description 查询频繁出入--出入口门禁/楼宇门禁
     * @Date 15:39 2019/6/21
     * @Param [pageable, passTimes：采集时间, count：出入频次 flag:0:出入口门禁 1：楼宇门禁]
     * @return
     **/
    Page<Map> queryOftenInOut(Pageable pageable, String[] passTimes, Integer count,Integer flag);


    /**
     * @Author shirenjing
     * @Description 查询昼伏夜出--出入口门禁/楼宇门禁
     * @Date 12:42 2019/6/22
     * @Param [pageable, passTimes：采集时间, count：出入频次 flag:0:出入口门禁 1：楼宇门禁, hours:夜出时段（0-4）]
     * @return
     **/
    Page<Map> queryNightOut(Pageable pageable, String[] passTimes, Integer count,Integer flag,Integer[] hours);

    /**
     * @Author shirenjing
     * @Description 预警--昼伏夜出
     * @Date 13:59 2019/6/25
     * @Param [passTimes：采集时间, count：出入频次 flag:0:出入口门禁 1：楼宇门禁, hours:夜出时段（0-4）]
     * @return
     **/
    List<PersonEarlyWarningVo> queryWarnNightOut(Date startTime, Date endTime, Integer count, Integer flag, Integer[] hours);

    /**
     * @Author shirenjing
     * @Description 预警--频繁出入
     * @Date 10:51 2019/6/27
     * @Param [startTime, endTime, count, flag, hours]
     * @return
     **/
    List<PersonEarlyWarningVo> queryWarnOftenInOut(Date startTime, Date endTime, Integer count, Integer flag);

    int deleteInOutsBylogIds(List<String> logIds);

    /**
     * @Author shirenjing
     * @Description 人员异常预警
     * @Date 11:53 2019/6/25
     * @Param []
     * @return
     **/
    void earlwarning();

    /**
     * @Author shirenjing
     * @Description 出入口门禁数据整合
     * @Date 11:47 2019/7/4
     * @Param []
     * @return
     **/
    void gateLogIntegration();

    /**
     * @Author shirenjing
     * @Description 楼宇门禁数据整合
     * @Date 14:22 2019/7/5
     * @Param []
     * @return
     **/
    void buildLogIntegration();

    /**
     * 出入口门禁数据整合
     **/
    void text1();

    /**
     * 楼宇门禁数据整合
     **/
    void text2();

     List countNightoutForBEG(String time);
}
