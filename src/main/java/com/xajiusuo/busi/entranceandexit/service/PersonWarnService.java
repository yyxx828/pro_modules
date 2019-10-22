package com.xajiusuo.busi.entranceandexit.service;

import com.xajiusuo.busi.entranceandexit.entity.GateEntranceGuardLog;
import com.xajiusuo.busi.entranceandexit.entity.PersonWarn;
import com.xajiusuo.busi.entranceandexit.entity.PersonWarnVo;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by shirenjing on 2019/6/26.
 */
public interface PersonWarnService extends BaseService<PersonWarn,String> {

    /**
     * @Author shirenjing
     * @Description 
     * @Date 18:12 2019/6/26
     * @Param [list]
     * @return
     **/
    void batchSaveOrUpate(List<PersonWarn> list);

    /**
     * @Author shirenjing
     * @Description 获取频繁出入参数
     * @Date 13:30 2019/6/27
     * @Param []
     * @return
     **/
    Map<String,Object> getOftenOutInParams();
    /**
     * @Author shirenjing
     * @Description 获取昼伏夜出参数
     * @Date 13:30 2019/6/27
     * @Param []
     * @return
     **/
    Map<String,Object> getNightOutParams();

    /**
     * @Author shirenjing
     * @Description 修改频繁出入参数
     * @Date 14:10 2019/6/27
     * @Param []
     * @return
     **/
    void updateOftenOutInParams(int dataRange,int nums);

    /**
     * @Author shirenjing
     * @Description 修改昼伏夜出参数
     * @Date 14:27 2019/6/27
     * @Param [dataRange, timeRange, nums]
     * @return
     **/
    void updateNightOutParams(int dataRange, String timeRange, int nums);

    /**
     * @Author shirenjing
     * @Description 预警信息分页查询
     * @Date 16:34 2019/6/29
     * @Param [pageable, personWarnVo]
     * @return
     **/
    Page<PersonWarn> queryPagePersonWarn(Pageable pageable, PersonWarnVo personWarnVo);

    /**
     * @Author shirenjing
     * @Description 根据id查询详单 --出入口门禁
     * @Date 17:19 2019/6/29
     * @Param [ids]
     * @return
     **/
    List<Map> queryGateLogByIds(String[] ids);

    /**
     * @Author shirenjing
     * @Description 根据id查询详单 --楼宇门禁  根据条件查询预警信息
     * @Date 17:19 2019/6/29
     * @Param [ids]
     * @return
     **/
    List<Map> queryBuildLogByIds(String[] ids);

    /**
     * @Author shirenjing
     * @Description 根据条件查询预警信息
     * @Date 15:56 2019/7/2
     * @Param [analyType 1:昼伏夜出 2：频繁出入, flag0:出入口门禁 1：楼宇门禁, dataRange 数据范围, timeRange 时间范围]
     * @return
     **/
    Map<String,PersonWarn> queryListPersonWarn(String analyType, Integer flag, Date[] dataRange, Integer[] timeRange);
}
