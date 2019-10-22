package com.xajiusuo.busi.electroPatrol.service.impl;

import com.xajiusuo.busi.device.entity.Device;
import com.xajiusuo.busi.device.enums.ApeType;
import com.xajiusuo.busi.device.service.DeviceService;
import com.xajiusuo.busi.echartsUtil.LineBarCharts;
import com.xajiusuo.busi.echartsUtil.TimeFrame;
import com.xajiusuo.busi.electroPatrol.dao.ElectroPatrolLogDao;
import com.xajiusuo.busi.electroPatrol.entity.ElectroPatrolLog;
import com.xajiusuo.busi.electroPatrol.service.ElectroPatrolLogService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.util.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lizhidong
 * @date 2019-6-12 13:56
 */
@Service
public class ElectroPatrolLogServiceImpl extends BaseServiceImpl<ElectroPatrolLog, String> implements ElectroPatrolLogService {

    @Autowired
    private ElectroPatrolLogDao electroPatrolLogDao;
    @Autowired
    private DeviceService deviceService;

    @Override
    public BaseDao<ElectroPatrolLog, String> getBaseDao() {
        return electroPatrolLogDao;
    }

    /**
     * 获取当日指定设备的电子巡更记录
     *
     * @param apeId 设备编码
     * @return
     */
    @Override
    public List<ElectroPatrolLog> getTodayLogByApeId(String apeId) {
        if (StringUtils.isEmpty(apeId)) return null;
        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String sql = "select * from T_ELECTROPATROLLOG_20 where apeId=? and patrolTime >= to_date('" + time + " 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and patrolTime <= to_date('" + time + " 23:59:59', 'yyyy-mm-dd hh24:mi:ss') order by patrolTime asc";
        return this.electroPatrolLogDao.executeNativeQuerySql(sql, apeId);
    }

    /**
     * 获取指定小区向前第几天的巡更日志记录
     *
     * @param villageId 小区编号
     * @param days      向前天数
     * @return
     */
    @Override
    public List<ElectroPatrolLog> getBeforeDayLogByVillageId(String villageId, int days) {
        if (StringUtils.isEmpty(villageId)) return null;
        List<Device> devices = this.deviceService.getDevicesByApeType(villageId, ApeType.ELEC_PATROL.getValue(), null);
        Set<String> apeIds = devices.stream().map(Device::getApeId).collect(Collectors.toSet());
        String time;
        if (days == 0) {
            time = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        } else {
            time = new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(new Date(), -days));
        }
        String sql = "select * from T_ELECTROPATROLLOG_20 where apeId in (" + SqlUtils.sqlIn(apeIds) + ") and patrolTime >= to_date('" + time + " 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and patrolTime <= to_date('" + time + " 23:59:59', 'yyyy-mm-dd hh24:mi:ss') order by patrolTime asc";
        return this.electroPatrolLogDao.executeNativeQuerySql(sql, new Object[]{});
    }

    @Override
    public Page queryPatrolLog(HttpServletRequest request, Pageable pageable, ElectroPatrolLog electroPatrolLog) {
        StringBuilder sql = new StringBuilder("select * from T_ELECTROPATROLLOG_20 where 1=1");
        if (StringUtils.isNotEmpty(electroPatrolLog.getVillageId())) {
            sql.append(" and villageId = '").append(electroPatrolLog.getVillageId()).append("'");
        }
        if (StringUtils.isNotEmpty(electroPatrolLog.getApeId())) {
            sql.append(" and apeId = '").append(electroPatrolLog.getApeId()).append("'");
        }
        if (StringUtils.isNotEmpty(electroPatrolLog.getPatrolPersonName())) {
            sql.append(" and patrolPersonName like '%").append(electroPatrolLog.getPatrolPersonName()).append("%'");
        }
        if (StringUtils.isNotEmpty(electroPatrolLog.getPatrolPersonNo())) {
            sql.append(" and patrolPersonNo = '").append(electroPatrolLog.getPatrolPersonNo()).append("'");
        }
        if (StringUtils.isNotEmpty(electroPatrolLog.getPatrolPersonPhone())) {
            sql.append(" and patrolPersonPhone = '").append(electroPatrolLog.getPatrolPersonPhone()).append("'");
        }
        if (StringUtils.isNotEmpty(electroPatrolLog.getManager())) {
            sql.append(" and manager like '%").append(electroPatrolLog.getManager()).append("%'");
        }
        if (StringUtils.isNotEmpty(electroPatrolLog.getPatrolAddr())) {
            sql.append(" and patrolAddr like '%").append(electroPatrolLog.getPatrolAddr()).append("%'");
        }
        if (StringUtils.isNotEmpty(electroPatrolLog.getPatrolStood())) {
            sql.append(" and patrolStood like '%").append(electroPatrolLog.getPatrolStood()).append("%'");
        }
        if (StringUtils.isNotEmpty(electroPatrolLog.getDepartType())) {
            sql.append(" and departType = '").append(electroPatrolLog.getDepartType()).append("'");
        }
        if (StringUtils.isNotEmpty(electroPatrolLog.getPatrolTimeRange()) && electroPatrolLog.getPatrolTimeRange().split(",").length == 2) {
            String[] times = electroPatrolLog.getPatrolTimeRange().split(",");
            sql.append(" and patrolTime >= to_date('").append(times[0]).append(" 00:00:00','yyyy-mm-dd hh24:mi:ss') and patrolTime <= to_date('").append(times[1]).append(" 23:59:59','yyyy-MM-dd HH:mm:ss')");
        }
        return this.electroPatrolLogDao.executeQuerySqlByPage(pageable, sql.toString(), new Object[]{});
    }

    /**
     * 获取指定小区昨日电子巡更情况统计
     *
     * @param villageId 小区编号
     * @param days      向前天数
     * @return
     */
    @Override
    public Object getYesterdayPartrolStatByVillageId(String villageId, int days) {
        List<ElectroPatrolLog> patrolLogs = getBeforeDayLogByVillageId(villageId, days);
        return
                Optional.ofNullable(patrolLogs).map(n -> {
                    LineBarCharts lineBarCharts = new LineBarCharts();
                    patrolLogs.stream().collect(Collectors.groupingBy(ElectroPatrolLog::getPoint)).forEach((k, v) -> {
                        lineBarCharts.add(k, "巡更次数", v.stream().mapToInt(ElectroPatrolLog::getRoundSum).sum());
                        lineBarCharts.add(k, "应巡次数", v.get(0).getMustRoundSum());
                    });
                    return lineBarCharts.generate();
                }).orElse("");
    }

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
     * @param startTime 开始时间， yyyy-mm-dd
     * @param endTime   结束时间， yyyy-mm-dd
     * @return
     */
    @Override
    public Object getPartrolStat(String villageId, String timeFrame, String startTime, String endTime) {
        String groupSql = "";
        TimeFrame timeFrame1 = TimeFrame.valueBy(timeFrame);
        switch (timeFrame1) {
            case D:
                groupSql = "to_char(t.patroltime, 'yyyy-mm-dd')";
                break;
            case W:
                groupSql = "to_char(t.patroltime, 'yyyy-iw')";
                break;
            case M:
                groupSql = "to_char(t.patroltime, 'yyyy-mm')";
                break;
            default:
                break;
        }
        if (groupSql.length() == 0) return "";
        StringBuilder sql = new StringBuilder();
        sql.append("select ").append(groupSql).append(" time, t.point, sum(t.roundsum) roundsum from T_ELECTROPATROLLOG_20 t where t.villageid = '").append(villageId).append("'");
        if (StringUtils.isNotEmpty(startTime)) {
            sql.append(" and t.patroltime >= to_date('").append(startTime).append(" 00:00:00', 'yyyy-mm-dd hh24:mi:ss')");
        }
        if (StringUtils.isNotEmpty(endTime)) {
            sql.append(" and t.patroltime <= to_date('").append(endTime).append(" 23:59:59', 'yyyy-mm-dd hh24:mi:ss')");
        }
        sql.append(" group by ").append(groupSql).append(", t.point");
        List<Object[]> list = this.electroPatrolLogDao.listBySQL(sql.toString(), new Object[]{});
        //获取所有时间
        Set<Object> times = list.stream().collect(Collectors.groupingBy(o -> o[0], Collectors.counting())).keySet();
        //获取所有点位
        Set<Object> points = list.stream().collect(Collectors.groupingBy(o -> o[1], Collectors.counting())).keySet();
        //获取所有时间
        List<Object> allTimes;
        String eTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()), sTime = dayAddMonth(eTime, -3);
        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            allTimes = timeSplit(timeFrame1, startTime, endTime);
        } else if (StringUtils.isNotEmpty(startTime)) {
            allTimes = timeSplit(timeFrame1, startTime, eTime);
        } else if (StringUtils.isNotEmpty(endTime)) {
            allTimes = timeSplit(timeFrame1, sTime, endTime);
        } else {
            allTimes = timeSplit(timeFrame1, sTime, eTime);
        }
        LineBarCharts charts = new LineBarCharts();
        allTimes.removeAll(times);
        allTimes.stream().forEach(n -> points.stream().forEach(m -> charts.add(n + "", m + "", 0)));
        list.stream().collect(Collectors.groupingBy(o -> o[0])).forEach((k, v) -> {
            List<Object> currPoints = new ArrayList<Object>();
            v.stream().forEach(m -> {
                charts.add(k + "", m[1] + "", m[2]);
                currPoints.add(m[1]);
            });
            List<Object> empPoints = new ArrayList<>(points);
            empPoints.removeAll(currPoints);
            empPoints.stream().forEach(n -> charts.add(k + "", n + "", 0));
        });
        return charts.generate();
    }

    /**
     * 获取指定时间范围内的所有时间段
     *
     * @param timeFrame
     * @param sTime     yyyy-MM-dd
     * @param eTime     yyyy-MM-dd
     * @return
     */
    private List<Object> timeSplit(TimeFrame timeFrame, String sTime, String eTime) {
        List<Object> list = new ArrayList<>();
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date st = df.parse(sTime), et = df.parse(eTime);
            Calendar sc = Calendar.getInstance(), ec = Calendar.getInstance();
            switch (timeFrame) {
                case D:
                    sc.setTime(st);
                    ec.setTime(et);
                    list.add(df.format(sc.getTime()));
                    while (sc.before(ec)) {
                        sc.add(Calendar.DAY_OF_YEAR, 1);
                        list.add(df.format(sc.getTime()));
                    }
                    break;
                case W:
                    sc.setFirstDayOfWeek(Calendar.MONDAY);
                    sc.setTime(st);
                    ec.setTime(et);
                    Set<String> empWSet = new HashSet<>();
                    int _w = sc.get(Calendar.WEEK_OF_YEAR);
                    empWSet.add(sc.get(Calendar.YEAR) + "-" + (_w < 10 ? "0" + _w : _w));
                    while (sc.before(ec)) {
                        sc.add(Calendar.DAY_OF_YEAR, 1);
                        _w = sc.get(Calendar.WEEK_OF_YEAR);
                        empWSet.add(sc.get(Calendar.YEAR) + "-" + (_w < 10 ? "0" + _w : _w));
                    }
                    list.addAll(empWSet);
                    break;
                case M:
                    sc.setTime(st);
                    ec.setTime(et);
                    Set<String> empYSet = new HashSet<>();
                    int _m = sc.get(Calendar.MONTH) + 1;
                    empYSet.add(sc.get(Calendar.YEAR) + "-" + (_m < 10 ? "0" + _m : _m));
                    while (sc.before(ec)) {
                        sc.add(Calendar.DAY_OF_YEAR, 1);
                        _m = sc.get(Calendar.MONTH) + 1;
                        empYSet.add(sc.get(Calendar.YEAR) + "-" + (_m < 10 ? "0" + _m : _m));
                    }
                    list.addAll(empYSet);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 将一个yyyy-MM-dd时间字符串所在时间向前/后几个月
     *
     * @param timeStr
     * @param month
     * @return
     */
    private String dayAddMonth(String timeStr, int month) {
        String time = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date date = df.parse(timeStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, month);
            time = df.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 按照指定时间粒度处理一个yyyy-mm-dd的时间字符串
     *
     * @param timeFrame
     * @param timeStr
     * @return
     */
    private String processTime(TimeFrame timeFrame, String timeStr) {
        String time = "";
        switch (timeFrame) {
            case D:
                time = timeStr;
                break;
            case W:
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(timeStr);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setFirstDayOfWeek(Calendar.MONDAY);
                    calendar.setTime(date);
                    int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
                    time = timeStr.substring(0, timeStr.indexOf("-")) + "-" + weekOfYear;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case M:
                time = timeStr.substring(0, timeStr.lastIndexOf("-"));
                break;
            default:
                break;
        }
        return time;
    }
}
