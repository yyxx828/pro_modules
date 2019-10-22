package com.xajiusuo.busi.motorVehicle.service;

import com.xajiusuo.busi.motorVehicle.entity.*;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Created by hadoop on 19-6-10.
 */
public interface MotorVehicleInOutService extends BaseService<MotorVehicleInOut,String> {

    /**
     * @Author shirenjing
     * @Description 超长停放统计
     * @Date 16:05 2019/6/17
     * @Param [motorVehicleInfoVo]
     * @return
     **/
    List<MotorVehicleCountVo> queryLongtimestop(MotorVehicleLogVo motorVehicleLogVo);

    /**
     * @Author shirenjing
     * @Description 昼伏夜出统计
     * @Date 16:46 2019/6/17
     * @Param [motorVehicleLogVo]
     * @return
     **/
    List<MotorVehicleCountVo> queyrNightOut(MotorVehicleLogVo motorVehicleLogVo);


    /**
     * @Author liujiankang
     * @Description 超长停放查询
     * @Date 16:46 2019/6/17
     * @return
     **/
    Page<MotorVehicleInOut> queryPageByLongtime(Pageable pageable, String plateNo,String dateTime,int dataTime);

    /**
     * @Author liujiankang
     * @Description 昼伏夜出查询
     * @Date 16:46 2019/6/17
     * @return
     **/
    Page<InOutNewVo> queryPageByMoringAndNight(Pageable pageable, String plateNo, String dateTime, String startTime, String endTime, int times);


    /**
     * @Author liujiankang
     * @Description 频繁出入查询
     * @Date 16:46 2019/6/17
     * @return
     **/
    Page<InOutNewVo> queryPageBytimes(Pageable pageable, String plateNo,String dateTime,String startTime,String endTime,int times);


    /**
     * @Author liujiankang
     * @Description 根据IDS查询列表
     * @Date 10:46 2019/6/25
     * @return
     **/
    Page<MotorVehicleInOut> queryPageByIds(Pageable pageable,String plateNo);
}
