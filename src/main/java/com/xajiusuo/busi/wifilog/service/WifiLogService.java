package com.xajiusuo.busi.wifilog.service;

import com.xajiusuo.busi.wifilog.entity.WifiCountVo;
import com.xajiusuo.busi.wifilog.entity.WifiLog;
import com.xajiusuo.busi.wifilog.entity.WifiQueryVo;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by liujiankang on 19-6-10.
 */
public interface WifiLogService extends BaseService<WifiLog,String> {

    /**
     * @Author liujiankang
     * @Description wifi探针查询
     * @Date 19:53 2019/6/13
     * @Param []
     * @return
     **/
    Page<WifiLog> queryPageLog(Pageable pageable, String sbbm,String ipAddress,String placecode,String orgcode,String dateTime);


    /**
     * @Author liujiankang
     * @Description  wifi探针统计Top 10 按MAC次数
     * @Date 19:53 2019/6/13
     * @Param [startTime,endTime]
     * @return
     **/
     List<WifiCountVo> countByMac(String startTime, String endTime);


    /**
     * @Author liujiankang
     * @Description wifi探针统计Top 10 设备采集条数
     * @Date 19:53 2019/6/13
     * @Param [startTime,endTime]
     * @return
     **/
    List<WifiCountVo> countByApe(String startTime,String endTime);

    /**
     * @Author shirenjing
     * @Description 布控数据查询
     * @Date 15:23 2019/6/24
     * @Param [pageable, taskId, wifiQueryVo]
     * @return
     **/
    Page<WifiLog> queryPageControltask(Pageable pageable, String taskId, WifiQueryVo wifiQueryVo);

    /**
     * @Author shirenjing
     * @Description 布控数据查询--最新一条中标数据
     * @Date 15:23 2019/6/24
     * @Param [pageable, taskId, wifiQueryVo]
     * @return
     **/
    WifiLog queryLastControltask(String taskId, WifiQueryVo wifiQueryVo);
}
