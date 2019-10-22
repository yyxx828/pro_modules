package com.xajiusuo.busi.motorVehicle.service;

import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleLog;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleWarn;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleWarnVo;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Created by hadoop on 19-7-1.
 */
public interface MotorVehicleWarnService extends BaseService<MotorVehicleWarn,String> {


    /**
     * @Author liujiankang
     * @Description 获取长时间停放参数
     * @Date 13:30 2019/6/27
     * @Param []
     * @return
     **/
    Map<String,Object> getOftenStopParams();
    /**
     * @Author liujiankang
     * @Description 获取频繁出入参数
     * @Date 13:30 2019/6/27
     * @Param []
     * @return
     **/
    Map<String,Object> getOftenOutInParams();
    /**
     * @Author liujiankang
     * @Description 获取昼伏夜出参数
     * @Date 13:30 2019/6/27
     * @Param []
     * @return
     **/
    Map<String,Object> getNightOutParams();

    /**
     * @Author liujiankang
     * @Description 修改长时间停放参数
     * @Date 14:10 2019/6/27
     * @Param []
     * @return
     **/
    void updateOftenStopParams(int dataRange);
    /**
     * @Author liujiankang
     * @Description 修改频繁出入参数
     * @Date 14:10 2019/6/27
     * @Param []
     * @return
     **/
    void updateOftenOutInParams(int dataRange,int nums);

    /**
     * @Author liujiankang
     * @Description 修改昼伏夜出参数
     * @Date 14:27 2019/6/27
     * @Param [dataRange, timeRange, nums]
     * @return
     **/
    void updateNightOutParams(int dataRange, String timeRange, int nums);


    /**
     * @Author liujiankang
     * @Description 预警信息分页查询
     * @Date 16:34 2019/6/29
     * @Param [pageable, personWarnVo]
     * @return
     **/
    Page<MotorVehicleWarn> queryPagePersonWarn(Pageable pageable, MotorVehicleWarnVo motorVehicleWarnVo);

    /**
     * @Author liujiankang
     * @Description 根据id查询详单 --车辆进出记录
     * @Date 17:19 2019/6/29
     * @Param [ids]
     * @return
     **/
    List<MotorVehicleLog> queryMotorVehicleByIds(String[] ids);
}
