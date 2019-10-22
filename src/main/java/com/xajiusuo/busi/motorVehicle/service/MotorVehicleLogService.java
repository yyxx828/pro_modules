package com.xajiusuo.busi.motorVehicle.service;

import com.xajiusuo.busi.entranceandexit.entity.GateEntranceGuardLog;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleCountVo;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleLog;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleLogVo;
import com.xajiusuo.busi.wifilog.entity.WifiLog;
import com.xajiusuo.busi.wifilog.entity.WifiQueryVo;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by shirenjing on 2019/6/6.
 */
public interface MotorVehicleLogService extends BaseService<MotorVehicleLog,String> {

    /**
     * @Author shirenjing
     * @Description 机动车采集信息列表
     * @Date 15:18 2019/6/6
     * @Param [pageable, motorVehicleLogVo]
     * @return
     **/
    Page<MotorVehicleLog> queryPageLog(Pageable pageable, MotorVehicleLogVo motorVehicleLogVo);

    /**
     * @Author shirenjing
     * @Description 统计车辆总数 和 出入总次数
     * @Date 17:13 2019/6/6
     * @Param []
     * @return
     **/
    Map<String,Object> countAllVehicle(MotorVehicleLogVo motorVehicleLogVo);

    /**
     * @Author shirenjing
     * @Description 机动车出入流量统计 按日统计
     * @Date 11:49 2019/6/13
     * @Param [motorVehicleLogVo]
     * @return
     **/
    List<MotorVehicleCountVo> countGoComeDay(MotorVehicleLogVo motorVehicleLogVo);

    /**
     * @Author shirenjing
     * @Description 统计近七天的车辆出入量
     * @Date 17:12 2019/7/4
     * @Param [startDate, endDate]
     * @return
     **/
    List<MotorVehicleCountVo> countGoCome7Day(String startDate, String endDate);

    /**
     * @Author shirenjing
     * @Description 机动车出入流量统计 按周统计
     * @Date 11:49 2019/6/13
     * @Param [motorVehicleLogVo]
     * @return
     **/
    List<MotorVehicleCountVo> countGoComeWeek(MotorVehicleLogVo motorVehicleLogVo);

    /**
     * @Author shirenjing
     * @Description 机动车出入流量统计 按月统计
     * @Date 13:38 2019/6/13
     * @Param [motorVehicleLogVo]
     * @return
     **/
    List<MotorVehicleCountVo> countGoComeMonth(MotorVehicleLogVo motorVehicleLogVo);

    /**
     * @Author shirenjing
     * @Description 机动车出入流量统计 按小时统计
     * @Date 19:53 2019/6/13
     * @Param [pill,times]
     * @return
     **/
    List<Map<String,Object>> countGoComeHour(int pill,String... times);

    /**
     * @Author shirenjing
     * @Description 根据时间段查询详单
     * @Date 15:34 2019/6/14
     * @Param [dates, hours]
     * @return
     **/
    List<MotorVehicleLog> queryLogByHours(String[] dates,Integer[] hours);

    /**
     * @Author shirenjing
     * @Description 根据时间段查询详单
     * @Date 15:34 2019/6/14
     * @Param [dates, hours]
     * @return
     **/
    Page<MotorVehicleLog> queryLogPageByHours(Pageable pageables, String[] dates,Integer[] hours);

    /**
     * @Author shirenjing
     * @Description
     * @Date 18:18 2019/6/22
     * @Param [pageable, taskId:任务id, passtimes：数据范围, passtype:0出1进]
     * @return
     **/
    Page<MotorVehicleLog> queryPageControltask(Pageable pageable, String taskId, String[] passtimes, String plateNo, String passtype);


    /**
     * @Author shirenjing
     * @Description 布控数据查询--最新一条中标数据
     * @Date 19:43 2019/6/24
     * @Param [taskId, passtimes]
     * @return
     **/
    MotorVehicleLog queryLastControltask(String taskId, String[] passtimes);

    /**
     *测试方法，功能结束后删除
     **/
    void text();
}
