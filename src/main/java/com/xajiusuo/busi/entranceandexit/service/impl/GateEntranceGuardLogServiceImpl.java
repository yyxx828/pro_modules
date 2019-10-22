package com.xajiusuo.busi.entranceandexit.service.impl;

import com.xajiusuo.busi.entranceandexit.dao.GateEntranceGuardLogDao;
import com.xajiusuo.busi.entranceandexit.entity.GateEntranceGuardLog;
import com.xajiusuo.busi.entranceandexit.entity.GuardCardCountVo;
import com.xajiusuo.busi.entranceandexit.entity.MjkAdnSkCount;
import com.xajiusuo.busi.entranceandexit.service.GateEntranceGuardLogService;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleCountVo;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleLogVo;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.DateUtils;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.BeanUtils;
import com.xajiusuo.utils.DateTools;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by wangdou on 2019/6/6
 */
@Service
public class GateEntranceGuardLogServiceImpl extends BaseServiceImpl<GateEntranceGuardLog, String> implements GateEntranceGuardLogService {

    @Autowired
    private GateEntranceGuardLogDao gateEntranceGuardLogDao;

    @Override
    public BaseDao<GateEntranceGuardLog, String> getBaseDao() {
        return gateEntranceGuardLogDao;
    }

    @Override
    public List tjGateEntranceCard(String time, String flag) throws Exception {
        StringBuilder sql = new StringBuilder();
        if ("day".equals(flag)) {
            String startDate = "";
            String endDate = "";
            if (!StringUtils.isEmpty(time)) {
                String[] times = time.split(",");
                startDate = DateTools.formatDatetime(times[0], "yyyy-MM-dd");
                endDate = DateTools.formatDatetime(times[1], "yyyy-MM-dd");
            } else {
                endDate = DateTools.formatDatetime(new Date(), "yyyy-MM-dd");
                startDate = DateUtils.dateAdds2s(endDate, -90);
            }
            sql.append("select * from ( " +
                    "select t.daylist days,nvl(t1.passtype,'0') passtype,nvl(t1.nums,0) nums from " +
                    "(select to_char(to_date('" + startDate + "','yyyy-MM-dd')+rownum-1,'yyyy-MM-dd') as daylist,rownum from dual connect by rownum <= to_number(to_date('" + endDate + "','yyyy-MM-dd')-to_date('" + startDate + "','yyyy-MM-dd'))+1 ) t " +
                    "left join (select to_char(passtime,'yyyy-MM-dd') days,passtype,count(1) nums from T_GATEENTRANCEGUARDLOG_33 where passtype = '0' ");
            appendSql(sql, time);
            sql.append(" group by to_char(passtime,'yyyy-MM-dd'),passtype ) t1 on t.daylist = t1.days ");
            sql.append(" union ");
            sql.append(" select t.daylist days,nvl(t1.passtype,'1') passtype,nvl(t1.nums,0) nums from ");
            sql.append("(select to_char(to_date('" + startDate + "','yyyy-MM-dd')+rownum-1,'yyyy-MM-dd') as daylist,rownum from dual connect by rownum <= to_number(to_date('" + endDate + "','yyyy-MM-dd')-to_date('" + startDate + "','yyyy-MM-dd'))+1 ) t ");
            sql.append("left join (select to_char(passtime,'yyyy-MM-dd') days,passtype,count(1) nums from T_GATEENTRANCEGUARDLOG_33 where passtype = '1' ");
            appendSql(sql, time);
            sql.append("group by to_char(passtime,'yyyy-MM-dd'),passtype ) t1 on t.daylist = t1.days ) tt order by tt.days");

        } else if ("week".equals(flag)) {
            Date startDate = null;
            Date endDate = null;
            if (!StringUtils.isEmpty(time)) {
                String[] times = time.split(",");
                startDate = DateUtils.parse(times[0], "yyyy-MM-dd");
                endDate = DateUtils.parse(times[1], "yyyy-MM-dd");

            } else {
                endDate = new Date();
                startDate = DateUtils.dateAddd2d(endDate, -90);
            }
            sql.append("select to_char(passtime,'yyyy-iw') days,passtype,count(1) nums from T_GATEENTRANCEGUARDLOG_33 where 1=1");
            if (time != null) {
                String[] times = time.split(",");
                if (CommonUtils.isNotEmpty(times[0]) && CommonUtils.isNotEmpty(times[1])) {
                    sql.append(" and passtime >= to_date('" + times[0] + "','yyyy-MM-dd hh24:mi:ss') " +
                            "and passtime <= to_date('" + times[1] + "','yyyy-MM-dd hh24:mi:ss') ");
                }
            }
            sql.append(" group by to_char(passtime,'yyyy-iw'),passtype order by days");
            List<GuardCardCountVo> voList = gateEntranceGuardLogDao.querySqlBean(sql.toString(), GuardCardCountVo.class, null);
            List<GuardCardCountVo> list = new ArrayList<>();
            List<String> weeks = DateTools.getWeeks(startDate, endDate);
            for (String w : weeks) {
                GuardCardCountVo vo0 = null;
                GuardCardCountVo vo1 = null;
                for (GuardCardCountVo vo : voList) {
                    if (vo.getDays().equals(w) && "0".equals(vo.getPassType())) {
                        vo0 = vo;
                    }
                    if (vo.getDays().equals(w) && "1".equals(vo.getPassType())) {
                        vo1 = vo;
                    }
                }

                vo0 = vo0 != null ? vo0 : new GuardCardCountVo(w, "0", "0");
                vo1 = vo1 != null ? vo1 : new GuardCardCountVo(w, "1", "0");
                list.add(vo0);
                list.add(vo1);
            }
            return list;

        } else {

            String startMonth = "";
            String endMonth = "";
            if (!StringUtils.isEmpty(time)) {
                String[] times = time.split(",");
                startMonth = DateTools.formatDatetime(times[0], "yyyy-MM");
                endMonth = DateTools.formatDatetime(times[1], "yyyy-MM");
            } else {
                endMonth = DateTools.formatDatetime(new Date(), "yyyy-MM");
                startMonth = DateTools.formatDatetime(DateUtils.dateAddd2d(new Date(), -90), "yyyy-MM");
            }
            sql.append("select * from ( " +
                    "select t.monthlist days,nvl(t1.passtype,'0') passtype, nvl(t1.nums,0) nums from " +
                    "(select to_char(add_months(to_date('" + startMonth + "','yyyy-MM'),rownum-1),'yyyy-MM') as monthlist from dual connect by rownum <= months_between(to_date('" + endMonth + "','yyyy-MM'),to_date('" + startMonth + "','yyyy-MM'))+1 ) t " +
                    "left join (select to_char(passtime,'yyyy-MM') days,passtype,count(1) nums from T_GATEENTRANCEGUARDLOG_33 where passtype='0' ");
            appendSql(sql, time);
            sql.append(" group by to_char(passtime,'yyyy-MM'),passtype) t1 on t.monthlist = t1.days ");
            sql.append(" union ");
            sql.append(" select t.monthlist days,nvl(t1.passtype,'1') passtype, nvl(t1.nums,0) nums from ");
            sql.append("(select to_char(add_months(to_date('" + startMonth + "','yyyy-MM'),rownum-1),'yyyy-MM') as monthlist from dual connect by rownum <= months_between(to_date('" + endMonth + "','yyyy-MM'),to_date('" + startMonth + "','yyyy-MM'))+1 ) t ");
            sql.append(" left join ");
            sql.append(" (select to_char(passtime,'yyyy-MM') days,passtype,count(1) nums from T_GATEENTRANCEGUARDLOG_33 where passtype='1' ");
            appendSql(sql, time);
            sql.append(" group by to_char(passtime,'yyyy-MM'),passtype) t1 on t.monthlist = t1.days) tt order by tt.days");

        }
        return gateEntranceGuardLogDao.querySqlBean(sql.toString(), GuardCardCountVo.class, null);

    }

    private void appendSql(StringBuilder stringBuilder, String time) {
        if (!StringUtils.isEmpty(time)) {
            String[] times = time.split(",");
            if (CommonUtils.isNotEmpty(times[0])) {
                stringBuilder.append(" and passtime>=to_date('" + times[0] + "','yyyy-MM-dd HH24:mi:ss')");
            }
            if (CommonUtils.isNotEmpty(times[1])) {
                stringBuilder.append(" and passtime<=to_date('" + times[1] + "','yyyy-MM-dd HH24:mi:ss')");
            }
        }
    }

    @Override
    public Page<GateEntranceGuardLog> query(GateEntranceGuardLog gateEntranceGuardLog, Pageable page) {
        StringBuilder hql = new StringBuilder("select a.*,c.name name from T_GATEENTRANCEGUARDLOG_33 a left join T_GATEENTRANCECARD_33 b on a.ENTRANCECARDNO = b.ENTRANCECARDNO left join T_OWNER_19 c on b.PERSONIDNUMBER = c.IDNUMBER where 1=1 ");
        if (!StringUtils.isEmpty(gateEntranceGuardLog.getTimes()) && gateEntranceGuardLog.getTimes().contains(",")) {
            String[] times = gateEntranceGuardLog.getTimes().split(",");
            hql.append("and (TO_CHAR(a.PASSTIME,'yyyy-mm-dd hh24:mi:ss') BETWEEN '" + times[0] + "' AND '" + times[1] + "' )");
        }
        if (!StringUtils.isEmpty(gateEntranceGuardLog.getEntrancecardno())) {
            hql.append(" and a.ENTRANCECARDNO = '" + gateEntranceGuardLog.getEntrancecardno() + "'");
        }
        if (!StringUtils.isEmpty(gateEntranceGuardLog.getPlace())) {
            hql.append(" and a.PLACE = '" + gateEntranceGuardLog.getPlace() + "'");
        }
        return gateEntranceGuardLogDao.queryPageSqlBean(page, "select * from (" + hql.toString() + ")", GateEntranceGuardLog.class, null);
    }

    @Override
    public List<MjkAdnSkCount> tjMjkAdnSkCount() {
        try {
            return gateEntranceGuardLogDao.querySqlBean("select count(1) mkcount,nvl(sum(zs),0) skcount from(select ENTRANCECARDNO,count(1) zs  from T_GATEENTRANCEGUARDLOG_33 where 1=1  group by ENTRANCECARDNO)", MjkAdnSkCount.class, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> gateEntranceCardTimeDur(int pill, String[] times) {
        StringBuilder sb = new StringBuilder("select trunc(to_number(to_char(passtime,'hh24'))/" + pill + "),count(1) from T_GATEENTRANCEGUARDLOG_33 where 1=1 ");
        if (times != null) {
            if (times.length == 1) {
                sb.append(" and passtime = to_date('" + times[0] + "','yyyy-MM-dd hh24:mi:ss') ");
            }
            if (times.length == 2) {
                sb.append(" and passtime >= to_date('" + times[0] + "','yyyy-MM-dd hh24:mi:ss') and passtime <= to_date('" + times[1] + "','yyyy-MM-dd hh24:mi:ss')");
            }
        }
        sb.append(" group by trunc(to_number(to_char(passtime,'hh24'))/" + pill + ")");
        List<Object[]> list = gateEntranceGuardLogDao.listBySQL(sb.toString(), null);
        Map<Integer, Integer> map = new HashMap<>();
        for (Object[] obj : list) {
            map.put(Integer.parseInt(obj[0].toString()), Integer.parseInt(obj[1].toString()));
        }
        int count = 0;
        if (24 % pill == 0) {
            count = 24 / pill;
        } else {
            count = 24 / pill + 1;
        }
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> m = null;
        for (int i = 0; i < count; i++) {
            m = new HashMap<>();
            m.put("name", getHourDur(pill, i));
            m.put("value", map.get(i) == null ? 0 : map.get(i));
            result.add(m);
        }
        return result;
    }

    @Override
    public Page<GateEntranceGuardLog> queryGateEntranceCardByTimeDur(Pageable pageables, String[] dates, Integer[] hours) {
        List<Object> param = new ArrayList<>();
        StringBuilder sb = new StringBuilder("select * from T_GATEENTRANCEGUARDLOG_33 where 1=1 ");
        if (dates != null && dates.length == 2) {
            sb.append(" and passtime >= ?");
            sb.append(" and passtime <= ?");
            param.add(DateUtils.parse(dates[0], "yyyy-MM-dd HH:mm:ss"));
            param.add(DateUtils.parse(dates[1], "yyyy-MM-dd HH:mm:ss"));
        }
        if (hours[0] > hours[1]) {
            sb.append(" and (to_number(to_char(passtime,'hh24')) >= ? or to_number(to_char(passtime,'hh24')) <= ?) ");
            param.add(hours[0]);
            param.add(hours[1]);
        } else {
            sb.append(" and to_number(to_char(passtime,'hh24')) >= ? and to_number(to_char(passtime,'hh24')) <= ?");
            param.add(hours[0]);
            param.add(hours[1]);
        }
        return gateEntranceGuardLogDao.executeQuerySqlByPage(pageables, sb.toString(), param.toArray());
    }

    @Override
    public Page<GateEntranceGuardLog> queryPageLog(Pageable pageable, String idCard, String[] passtimes) {
        return queryPageLog(pageable, idCard, passtimes, null, null);
    }

    @Override
    public Page<Map> queryPageLogByOftenInOut(Pageable pageable, String[] passtimes, String entrancecardno) {
        return queryLogPageMap(pageable, passtimes, entrancecardno, null);
    }

    @Override
    public Page<Map> queryPageLogByNightOut(Pageable pageable, String[] passtimes, String entrancecardno, Integer[] hours) {
        return queryLogPageMap(pageable, passtimes, entrancecardno, null);
    }

    public Page<Map> queryLogPageMap(Pageable pageable, String[] passtimes, String entrancecardno, Integer[] hours) {
        StringBuilder sb = new StringBuilder("select t.entrancecardno,to_char(t.passtime,'yyyy-MM-dd HH24:mi:ss') passtime,decode(t.passtype,'0','出','1','进') passtype," +
                "t.place,t2.villagename from t_gateentranceguardlog_33 t " +
                "left join t_gateentrancecard_33 t1 on t.entrancecardno=t1.entrancecardno " +
                "left join t_village_19 t2 on t.villageid=t2.villageid where 1=1 ");
        if (CommonUtils.isNotEmpty(passtimes) && passtimes.length == 2) {
            sb.append(" and t.passtime>=to_date('" + passtimes[0] + "','yyyy-MM-dd HH24:mi:ss') " +
                    "and t.passtime<=to_date('" + passtimes[1] + "','yyyy-MM-dd HH24:mi:ss')");
        }
        if (CommonUtils.isNotEmpty(entrancecardno)) {
            sb.append(" and t.entrancecardno like '%" + entrancecardno + "%'");
        }
        if (CommonUtils.isNotEmpty(hours) && hours.length == 2) {
            if (hours[0] > hours[1]) {
                sb.append(" and (to_number(to_char(t.passtime,'hh24')) >= " + hours[0] + " or to_number(to_char(t.passtime,'hh24')) <= " + hours[1] + ") ");
            } else {
                sb.append(" and to_number(to_char(t.passtime,'hh24')) >= " + hours[0] + " and to_number(to_char(t.passtime,'hh24')) <= " + hours[1] + " ");
            }
        }
        return gateEntranceGuardLogDao.queryPageSqlBean(pageable, sb.toString(), null);
    }

    @Override
    public Page<GateEntranceGuardLog> queryPageControltask(Pageable pageable, String taskId, String[] passtimes, String idcard, String passtype) {
        StringBuilder sb = new StringBuilder("select t.* from t_gateentranceguardlog_33 t " +
                "left join t_gateentrancecard_33 t1 on t.entrancecardno=t1.entrancecardno where 1=1 ");
        if (CommonUtils.isNotEmpty(taskId)) {
            sb.append(" and t1.personidnumber in (select distinct identify from t_controlidentify_16 where taskid = '" + taskId + "')");
        }
        if (CommonUtils.isNotEmpty(passtimes) && passtimes.length == 2) {
            sb.append(" and t.passtime>=to_date('" + passtimes[0] + "','yyyy-MM-dd HH24:mi:ss') " +
                    "and t.passtime<=to_date('" + passtimes[1] + "','yyyy-MM-dd HH24:mi:ss')");
        }
        if (CommonUtils.isNotEmpty(passtype)) {
            sb.append(" and t.passtype = '" + passtype + "'");
        }
        if (CommonUtils.isNotEmpty(idcard)) {
            sb.append(" and t1.personidnumber like '%" + idcard + "%'");
        }

//        sb.append(SqlUtils.addSqlOrderby(pageable,"t"));
        return gateEntranceGuardLogDao.executeQuerySqlByPage(pageable, sb.toString(), null);
    }

    @Override
    public GateEntranceGuardLog queryLastControltask(String taskId, String[] passtimes) {
        StringBuilder sb = new StringBuilder("select t.* from t_gateentranceguardlog_33 t " +
                "left join t_gateentrancecard_33 t1 on t.entrancecardno=t1.entrancecardno where 1=1 ");
        if (CommonUtils.isNotEmpty(taskId)) {
            sb.append(" and t1.personidnumber in (select distinct identify from t_controlidentify_16 where taskid = '" + taskId + "')");
        }
        if (CommonUtils.isNotEmpty(passtimes) && passtimes.length == 2) {
            sb.append(" and t.passtime>=to_date('" + passtimes[0] + "','yyyy-MM-dd HH24:mi:ss') " +
                    "and t.passtime<=to_date('" + passtimes[1] + "','yyyy-MM-dd HH24:mi:ss')");
        }
        sb.append(" order by t.passtime desc");
        List<GateEntranceGuardLog> list = gateEntranceGuardLogDao.executeNativeQuerySql(sb.toString(), null);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Map<String, GateEntranceGuardLog> getLastByPerson(String villageid) {
        String sql = "select * from t_gateentranceguardlog_33 where id in (\n" +
                "  select dataid from (\n" +
                "  select row_number() over(partition by tab.entrancecardno order by tab.orderdate desc) rn, tab.* from (\n" +
                "    select distinct * from ( \n" +
                "      (select t.*,t.indata orderdate,t.inid dataid from T_GATEENTRANCEINOUT_16 t where flag = 0 and t.indata is not null) \n" +
                "      union\n" +
                "      (select t.*,t.outdata orderdate,t.outid dataid from T_GATEENTRANCEINOUT_16 t where flag = 0 and t.outdata is not null)\n" +
                "       ) ) tab ) where rn = 1 and entrancecardno in ( select distinct entrancecardno from T_GATEENTRANCEGUARDLOG_33 where id not in (select inid from T_GATEENTRANCEINOUT_16 where flag = 0 and inid is not null) and id not in (select outid from T_GATEENTRANCEINOUT_16 where flag = 0 and outid is not null) and villageid = '"+villageid+"'))";
        List<GateEntranceGuardLog> list = gateEntranceGuardLogDao.executeNativeQuerySql(sql,null);
        Map<String,GateEntranceGuardLog> map = new HashMap<>();
        for (GateEntranceGuardLog log:list) {
            map.put(log.getEntrancecardno(),log);
        }
        return map;
    }

    @Override
    public List<GateEntranceGuardLog> queryIncreamentByPerson(String villageid) {
        String sql = "select * from T_GATEENTRANCEGUARDLOG_33 where id not in (select inid from T_GATEENTRANCEINOUT_16 where flag = 0 and inid is not null) and id not in (select outid from T_GATEENTRANCEINOUT_16 where flag = 0 and outid is not null) and villageid = '"+villageid+"' order by villageid asc,entrancecardno asc,passtime asc";
        return gateEntranceGuardLogDao.executeNativeQuerySql(sql,null);
    }

    @Override
    public List<Object[]> getLastIdsByPerson(String[] idcards) {
        String sql = "select id from (\n" +
                "  select row_number() over(partition by tab.entrancecardno order by tab.orderdate desc) rn, tab.* from (\n" +
                "    select distinct * from ( \n" +
                "      (select t.*,t.indata orderdate,t.inid dataid from T_GATEENTRANCEINOUT_16 t where flag = 0 and t.indata is not null and entrancecardno in ("+SqlUtils.sqlIn(idcards)+")) \n" +
                "      union\n" +
                "      (select t.*,t.outdata orderdate,t.outid dataid from T_GATEENTRANCEINOUT_16 t where flag = 0 and t.outdata is not null and entrancecardno in ("+SqlUtils.sqlIn(idcards)+"))\n" +
                "       ) ) tab ) where rn = 1";
        return gateEntranceGuardLogDao.listBySQL(sql);
    }

    @Override
    public Map<String, List> nearly7daysByGate() {
        Map<String, List> map = new HashMap<>();
        String endDate = DateTools.formatDatetime(new Date(), "yyyy-MM-dd");
        String startDate = DateUtils.dateAdds2s(endDate, -6);
        StringBuilder sql = new StringBuilder("select * from ( " +
                "select t.daylist days,nvl(t1.passtype,'0') passtype,nvl(t1.nums,0) nums from " +
                "(select to_char(to_date('" + startDate + "','yyyy-MM-dd')+rownum-1,'yyyy-MM-dd') as daylist,rownum from dual connect by rownum <= to_number(to_date('" + endDate + "','yyyy-MM-dd')-to_date('" + startDate + "','yyyy-MM-dd'))+1 ) t " +
                "left join (select to_char(passtime,'yyyy-MM-dd') days,passtype,count(1) nums from T_GATEENTRANCEGUARDLOG_33 where passtype = '0'   group by to_char(passtime,'yyyy-MM-dd'),passtype) t1 on t.daylist = t1.days) ");
        sql.append("union select * from ( " +
                "select t.daylist days,nvl(t1.passtype,'1') passtype,nvl(t1.nums,0) nums from " +
                "(select to_char(to_date('" + startDate + "','yyyy-MM-dd')+rownum-1,'yyyy-MM-dd') as daylist,rownum from dual connect by rownum <= to_number(to_date('" + endDate + "','yyyy-MM-dd')-to_date('" + startDate + "','yyyy-MM-dd'))+1 ) t " +
                "left join (select to_char(passtime,'yyyy-MM-dd') days,passtype,count(1) nums from T_GATEENTRANCEGUARDLOG_33 where passtype = '1'   group by to_char(passtime,'yyyy-MM-dd'),passtype) t1 on t.daylist = t1.days) ");
        try {
            List<GuardCardCountVo> result = gateEntranceGuardLogDao.querySqlBean(sql.toString(), GuardCardCountVo.class, null);
            if (result != null && result.size() > 0) {
                List xList = new ArrayList<>();
                List inNums = new ArrayList<>();
                List outNums = new ArrayList<>();
                for (GuardCardCountVo vo : result) {
                    if ("0".equals(vo.getPassType())) {
                        xList.add(vo.getDays());
                        outNums.add(vo.getNums());
                    } else {
                        inNums.add(vo.getNums());
                    }

                }
                map.put("date", xList);
                map.put("inNums", inNums);
                map.put("outNums", outNums);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, List> todayByGate() {
        Map<String, List> map = new HashMap<>();
        String today = DateTools.formatDatetime(new Date(), "yyyy-MM-dd");
        List<GuardCardCountVo> result = gateEntranceGuardLogDao.querySqlBean("select to_char(passtime,'yyyy-MM-dd') days,passtype,count(1) nums from T_GATEENTRANCEGUARDLOG_33 where passtype = '0' and to_char(passtime,'yyyy-MM-dd')='" + today + "'  group by to_char(passtime,'yyyy-MM-dd'),passtype " + "union select to_char(passtime,'yyyy-MM-dd') days,passtype,count(1) nums from T_GATEENTRANCEGUARDLOG_33 where passtype = '1' and to_char(passtime,'yyyy-MM-dd')='" + today + "'  group by to_char(passtime,'yyyy-MM-dd'),passtype", GuardCardCountVo.class, null);
        if (!CollectionUtils.isEmpty(result)) {
            List inNums = new ArrayList<>();
            List outNums = new ArrayList<>();
            result.forEach(t -> {
                if ("0".equals(t.getPassType())) {
                    outNums.add(t.getNums());
                } else {
                    inNums.add(t.getNums());
                }
            });
            map.put("inNums", inNums);
            map.put("outNums", outNums);
        }else {
            map.put("inNums", new ArrayList<>(Collections.singletonList("0")));
            map.put("outNums", new ArrayList<>(Collections.singletonList("0")));
        }
        return map;
    }

    /**
     * @return
     * @Author shirenjing
     * @Description
     * @Date 11:14 2019/6/22
     * @Param [pageable, idCard:证件编号, passtimes：数据范围, entrancecardno：门禁卡号, hours：时间段 （0-4）]
     **/
    Page<GateEntranceGuardLog> queryPageLog(Pageable pageable, String idCard, String[] passtimes, String entrancecardno, Integer[] hours) {
        StringBuilder sb = new StringBuilder("select t.id,t.villageid,t.apeid,t.entrancecardno,t.passtime,decode(t.passtype,'0','出','1','进') passtype," +
                "t.place,t.createdate,t.user_name,t.owner_name,t.phone,t.address from t_gateentranceguardlog_33 t " +
                "left join t_gateentrancecard_33 t1 on t.entrancecardno=t1.entrancecardno where 1=1 ");
        if (CommonUtils.isNotEmpty(idCard)) {
            sb.append(" and t1.personidnumber='" + idCard + "'");
        }
        if (CommonUtils.isNotEmpty(passtimes) && passtimes.length == 2) {
            sb.append(" and t.passtime>=to_date('" + passtimes[0] + "','yyyy-MM-dd HH24:mi:ss') " +
                    "and t.passtime<=to_date('" + passtimes[1] + "','yyyy-MM-dd HH24:mi:ss')");
        }
        if (CommonUtils.isNotEmpty(entrancecardno)) {
            sb.append(" and t.entrancecardno like '%" + entrancecardno + "%'");
        }
        if (CommonUtils.isNotEmpty(hours) && hours.length == 2) {
            if (hours[0] > hours[1]) {
                sb.append(" and (to_number(to_char(t.passtime,'hh24')) >= " + hours[0] + " or to_number(to_char(t.passtime,'hh24')) <= " + hours[1] + ") ");
            } else {
                sb.append(" and to_number(to_char(t.passtime,'hh24')) >= " + hours[0] + " and to_number(to_char(t.passtime,'hh24')) <= " + hours[1] + " ");
            }
        }
//        sb.append(SqlUtils.addSqlOrderby(pageable,"t"));
        return gateEntranceGuardLogDao.executeQuerySqlByPage(pageable, sb.toString(), null);
    }

    private String getHourDur(int pill, int dur) {
        int count = 0;
        if (24 % pill == 0) {
            count = 24 / pill;
        } else {
            count = 24 / pill + 1;
        }
        int start = dur * pill;
        int end = (dur + 1) * pill - 1;
        if (dur == count - 1) end = 23;
        String s = start >= 10 ? start + "" : "0" + start;
        String e = end >= 10 ? end + "" : "0" + end;
        if (pill == 1) {
            return s + "";
        }
        return s + "-" + e;
    }

}
