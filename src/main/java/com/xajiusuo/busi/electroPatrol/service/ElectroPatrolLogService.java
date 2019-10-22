package com.xajiusuo.busi.electroPatrol.service;

import com.xajiusuo.busi.electroPatrol.entity.ElectroPatrolLog;
import com.xajiusuo.busi.electroPatrol.vo.PatrolRate;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author lizhidong
 * @date 2019-6-12 10:46
 */
public interface ElectroPatrolLogService extends BaseService<ElectroPatrolLog, String> {

    /**
     * 获取当日指定设备的电子巡更记录
     *
     * @param apeId 设备编码
     * @return
     */
    List<ElectroPatrolLog> getTodayLogByApeId(String apeId);

    /**
     * 获取指定小区向前第几天的巡更日志记录
     *
     * @param villageId 小区编号
     * @param days      向前天数
     * @return
     */
    List<ElectroPatrolLog> getBeforeDayLogByVillageId(String villageId, int days);

    Page queryPatrolLog(HttpServletRequest request, Pageable pageable, ElectroPatrolLog electroPatrolLog);

    /**
     * 获取指定小区昨日电子巡更情况统计
     *
     * @param villageId 小区编号
     * @param days      向前天数
     * @return
     */
    Object getYesterdayPartrolStatByVillageId(String villageId, int days);

    /**
     * 电子巡更点位巡更次数统计
     * <p>
     * 1. 电子巡更日志默认存放三个月
     * 2. 如果开始时间和结束时间都为空,用三个月全量数据统计
     * 3. 有开始时间，统计开始时间到当前时间
     * 4. 有结束时间，统计全量数据最小时间到结束时间
     * 5. 返回结果按照timeFrame所指定的粒度返回
     * </p>
     *
     * @param villageId 小区编码
     * @param timeFrame enum TimeFrame，当前业务只用到日、周、月
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    Object getPartrolStat(String villageId, String timeFrame, String startTime, String endTime);
}
