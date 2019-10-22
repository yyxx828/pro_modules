package com.xajiusuo.busi.buildingaccess.service.impl;

import com.xajiusuo.busi.buildingaccess.dao.BuildingEntranceGuardLogDao;
import com.xajiusuo.busi.buildingaccess.entity.BuildingEntranceGuardLog;
import com.xajiusuo.busi.buildingaccess.entity.ResultVo;
import com.xajiusuo.busi.buildingaccess.service.BuildingEntranceGuardLogService;
import com.xajiusuo.busi.villageMessage.dao.BuildingDao;
import com.xajiusuo.busi.villageMessage.dao.UnitDao;
import com.xajiusuo.busi.villageMessage.entity.Building;
import com.xajiusuo.busi.villageMessage.entity.Unit;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.DateUtils;
import com.xajiusuo.utils.DateTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by wangdou on 2019/6/6
 */
@Service
public class BuildingEntranceGuardLogServiceImpl extends BaseServiceImpl<BuildingEntranceGuardLog, String> implements BuildingEntranceGuardLogService {

    @Autowired
    private BuildingEntranceGuardLogDao buildingEntranceGuardLogDao;
    @Autowired
    private BuildingDao buildingDao;
    @Autowired
    private UnitDao unitDao;

    @Override
    public BaseDao<BuildingEntranceGuardLog, String> getBaseDao() {
        return buildingEntranceGuardLogDao;
    }

    @Override
    public Page<BuildingEntranceGuardLog> query(BuildingEntranceGuardLog entity, Pageable page) {
        StringBuilder hql = new StringBuilder("select a.*,c.name name from " + tableName() + " a left join T_BUILDINGENTRANCECARD_33 b on a.ENTRANCECARDNO = b.ENTRANCECARDNO left join T_OWNER_19 c on b.PERSONIDNUMBER = c.IDNUMBER where 1=1 ");
        if (!StringUtils.isEmpty(entity.getTimes()) && entity.getTimes().contains(",")) {
            String[] times = entity.getTimes().split(",");
            hql.append("and (TO_CHAR(a.PASSTIME,'yyyy-mm-dd hh24:mi:ss') BETWEEN '" + times[0] + "' AND '" + times[1] + "' )");
        }
        if (!StringUtils.isEmpty(entity.getEntrancecardno())) {
            hql.append(" and a.ENTRANCECARDNO = '" + entity.getEntrancecardno() + "'");
        }
        if (!StringUtils.isEmpty(entity.getName())) {
            hql.append(" and c.name = '" + entity.getName() + "'");
        }
        return buildingEntranceGuardLogDao.queryPageSqlBean(page, "select * from (" + hql.toString() + ")",BuildingEntranceGuardLog.class,null);
    }

    @Override
    public Map<String, List<ResultVo>> tjBuildingEntranceGuard(String times, String flag) {
        String sql = "";
        Map<String, List<ResultVo>> map = new LinkedHashMap<>();
        List<ResultVo> resultVos = new ArrayList<>();
        if ("day".equals(flag)) {
            initData(map, resultVos, flag, times);
            sql = "SELECT TO_CHAR(passtime,'yyyy-MM-dd') days, BUILDINGNO, COUNT(1) nums  FROM  (SELECT a.*, b.BUILDINGNO FROM T_BUILDINGENTRANCEGUARDLOG_33 a LEFT JOIN T_BUILDINGENTRANCEDEVICE_20 b ON a.APEID = b.DEVICEID )" +
                    "    WHERE 1=1 ";
            if (!StringUtils.isEmpty(times) && times.contains(",")) {
                String[] time = times.split(",");
                sql += " and (to_char(PASSTIME,'yyyy-mm-dd hh24:mi:ss') between '" + time[0] + "' and '" + time[1] + "' )";
            }
            sql += "GROUP BY TO_CHAR(passtime,'yyyy-MM-dd'),  BUILDINGNO";

        } else if ("week".equals(flag)) {
            initData(map, resultVos, flag, times);
            sql = "SELECT TO_CHAR(passtime,'yyyy-iw') days, BUILDINGNO, COUNT(1) nums  FROM  (SELECT a.*, b.BUILDINGNO FROM T_BUILDINGENTRANCEGUARDLOG_33 a LEFT JOIN T_BUILDINGENTRANCEDEVICE_20 b ON a.APEID = b.DEVICEID )\n" +
                    "    WHERE 1=1 ";
            if (!StringUtils.isEmpty(times) && times.contains(",")) {
                String[] time = times.split(",");
                sql += " and (to_char(PASSTIME,'yyyy-mm-dd hh24:mi:ss') between '" + time[0] + "' and '" + time[1] + "' )";
            }
            sql += "GROUP BY TO_CHAR(passtime,'yyyy-iw'),  BUILDINGNO";
            ;
        } else {
            initData(map, resultVos, flag, times);
            sql = "SELECT TO_CHAR(passtime,'yyyy-MM') days, BUILDINGNO, COUNT(1) nums  FROM  (SELECT a.*, b.BUILDINGNO FROM T_BUILDINGENTRANCEGUARDLOG_33 a LEFT JOIN T_BUILDINGENTRANCEDEVICE_20 b ON a.APEID = b.DEVICEID )\n" +
                    "    WHERE 1=1 ";
            if (!StringUtils.isEmpty(times) && times.contains(",")) {
                String[] time = times.split(",");
                sql += " and (to_char(PASSTIME,'yyyy-mm-dd hh24:mi:ss') between '" + time[0] + "' and '" + time[1] + "' )";
            }
            sql += "GROUP BY TO_CHAR(passtime,'yyyy-MM'),  BUILDINGNO";
        }
        List list = buildingEntranceGuardLogDao.listBySQL(sql, null);
        if (!CollectionUtils.isEmpty(list)) {
            for (Object o : list) {
                Object[] oo = (Object[]) o;
                List<ResultVo> bulidingResultVoList = map.get(oo[0].toString());
                List<ResultVo> newList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(bulidingResultVoList)) {
                    for (ResultVo vo : bulidingResultVoList) {
                        ResultVo vo1 = new ResultVo();
                        vo1.setName(vo.getName());
                        vo1.setNumber(vo.getNumber());
                        if (oo[1].toString().equals(vo.getNumber())) {
                            vo1.setCount(Integer.parseInt(oo[2].toString()));
                        } else {
                            vo1.setCount(vo.getCount());
                        }
                        newList.add(vo1);
                    }
                    map.put(oo[0].toString(), newList);
                }
            }
        } else {
            map.clear();
        }
        return map;
    }

    private void initData(Map<String, List<ResultVo>> map, List<ResultVo> resultVos, String flag, String times) {
        String sql = "";
        if ("day".equals(flag)) {
            String startDate = "";
            String endDate = "";
            if (!org.apache.commons.lang3.StringUtils.isEmpty(times)) {
                String[] timess = times.split(",");
                startDate = DateTools.formatDatetime(timess[0], "yyyy-MM-dd");
                endDate = DateTools.formatDatetime(timess[1], "yyyy-MM-dd");
            } else {
                endDate = DateTools.formatDatetime(new Date(), "yyyy-MM-dd");
                startDate = DateUtils.dateAdds2s(endDate, -90);
            }
            sql = "select to_char(to_date('" + startDate + "','yyyy-MM-dd')+rownum-1,'yyyy-MM-dd') as daylist,rownum from dual connect by rownum <= to_number(to_date('" + endDate + "','yyyy-MM-dd')-to_date('" + startDate + "','yyyy-MM-dd'))+1 ";
        } else if ("week".equals(flag)) {
            sql = "";
        } else {
            String startMonth = "";
            String endMonth = "";
            if (!org.apache.commons.lang3.StringUtils.isEmpty(times)) {
                String[] timess = times.split(",");
                startMonth = DateTools.formatDatetime(timess[0], "yyyy-MM");
                endMonth = DateTools.formatDatetime(timess[1], "yyyy-MM");
            } else {
                endMonth = DateTools.formatDatetime(new Date(), "yyyy-MM");
                startMonth = DateTools.formatDatetime(DateUtils.dateAddd2d(new Date(), -90), "yyyy-MM");
            }
            sql = "select to_char(add_months(to_date('" + startMonth + "','yyyy-MM'),rownum-1),'yyyy-MM') as monthlist,rownum from dual connect by rownum <= months_between(to_date('" + endMonth + "','yyyy-MM'),to_date('" + startMonth + "','yyyy-MM'))+1 ";
        }
        List dateList;
        if (!StringUtils.isEmpty(sql)) {
            dateList = buildingEntranceGuardLogDao.listBySQL(sql, null);
        } else {
            Date startDate = null;
            Date endDate = null;
            if (!org.apache.commons.lang3.StringUtils.isEmpty(times)) {
                String[] timess = times.split(",");
                startDate = DateUtils.parse(timess[0], "yyyy-MM-dd");
                endDate = DateUtils.parse(timess[1], "yyyy-MM-dd");

            } else {
                endDate = new Date();
                startDate = DateUtils.dateAddd2d(endDate, -90);
            }
            dateList = DateTools.getWeeks(startDate, endDate);
        }

        //获得所有楼栋
        List<Building> buildingList = buildingDao.findAll();
        for (Building building : buildingList) {
            resultVos.add(new ResultVo(building.getBuildingno(), building.getBuildingname(), 0));
        }
        //初始化数据
        for (Object o : dateList) {
            if (!"week".equals(flag)) {
                Object[] oo = (Object[]) o;
                map.put(oo[0].toString(), resultVos);
            } else {
                map.put(o.toString(), resultVos);
            }
        }
    }

    @Override
    public Page<BuildingEntranceGuardLog> queryPageLog(Pageable pageable, String idCard, String[] passtimes) {
        return queryPageLog(pageable, idCard, passtimes, null, null);
    }

    @Override
    public Page<Map> queryPageLogByOftenInOut(Pageable pageable, String[] passtimes, String entrancecardno) {
        return queryLogPageMap(pageable, passtimes, entrancecardno, null);
    }

    @Override
    public Page<Map> queryPageLogByNightOut(Pageable pageable, String[] passtimes, String entrancecardno, Integer[] hours) {
        return queryLogPageMap(pageable, passtimes, entrancecardno, hours);
    }

    public Page<Map> queryLogPageMap(Pageable pageable, String[] passtimes, String entrancecardno, Integer[] hours) {
        StringBuilder sb = new StringBuilder("select t.entrancecardno,to_char(t.passtime,'yyyy-MM-dd HH24:mi:ss') passtime,decode(t.passtype,'0','出','1','进') passtype," +
                "t3.place,t2.villagename from t_buildingentranceguardlog_33 t \n" +
                " left join t_buildingentrancecard_33 t1 on t.entrancecardno=t1.entrancecardno" +
                " left join t_village_19 t2 on t.villageid=t2.villageid " +
                " left join t_device_20 t3 on t.apeid=t3.apeid where 1=1 ");
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
        return buildingEntranceGuardLogDao.queryPageSqlBean(pageable, sb.toString(), null);
    }

    @Override
    public Page<BuildingEntranceGuardLog> queryPageControltask(Pageable pageable, String taskId, String[] passtimes, String idcard, String passtype) {
        StringBuilder sb = new StringBuilder("select t.* from t_buildingentranceguardlog_33 t \n" +
                "left join t_buildingentrancecard_33 t1 on t.entrancecardno=t1.entrancecardno where 1=1 ");
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
        return buildingEntranceGuardLogDao.executeQuerySqlByPage(pageable, sb.toString(), null);
    }

    @Override
    public BuildingEntranceGuardLog queryLastControltask(String taskId, String[] passtimes) {
        StringBuilder sb = new StringBuilder("select t.* from t_buildingentranceguardlog_33 t \n" +
                "left join t_buildingentrancecard_33 t1 on t.entrancecardno=t1.entrancecardno where 1=1 ");
        if (CommonUtils.isNotEmpty(taskId)) {
            sb.append(" and t1.personidnumber in (select distinct identify from t_controlidentify_16 where taskid = '" + taskId + "')");
        }
        if (CommonUtils.isNotEmpty(passtimes) && passtimes.length == 2) {
            sb.append(" and t.passtime>=to_date('" + passtimes[0] + "','yyyy-MM-dd HH24:mi:ss') " +
                    "and t.passtime<=to_date('" + passtimes[1] + "','yyyy-MM-dd HH24:mi:ss')");
        }
        sb.append(" order by t.passtime desc");
        List<BuildingEntranceGuardLog> list = buildingEntranceGuardLogDao.executeNativeQuerySql(sb.toString(), null);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Map<String, BuildingEntranceGuardLog> getLastByPerson(String villageid) {
        String sql = "select * from t_buildingentranceguardlog_33 where id in (\n" +
                "  select dataid from (\n" +
                "  select row_number() over(partition by tab.entrancecardno order by tab.orderdate desc) rn, tab.* from (\n" +
                "    select distinct * from ( \n" +
                "      (select t.*,t.indata orderdate,t.inid dataid from T_GATEENTRANCEINOUT_16 t where flag = 1 and t.indata is not null) \n" +
                "      union\n" +
                "      (select t.*,t.outdata orderdate,t.outid dataid from T_GATEENTRANCEINOUT_16 t where flag = 1 and t.outdata is not null)\n" +
                "       ) ) tab ) where rn = 1 and entrancecardno in ( select distinct entrancecardno from t_buildingentranceguardlog_33 where id not in (select inid from T_GATEENTRANCEINOUT_16 where flag = 1 and inid is not null) and id not in (select outid from T_GATEENTRANCEINOUT_16 where flag = 1 and outid is not null) and villageid = '"+villageid+"'))";
        List<BuildingEntranceGuardLog> list = buildingEntranceGuardLogDao.executeNativeQuerySql(sql,null);
        Map<String,BuildingEntranceGuardLog> map = new HashMap<>();
        for (BuildingEntranceGuardLog log:list) {
            map.put(log.getEntrancecardno(),log);
        }
        return map;
    }

    @Override
    public List<BuildingEntranceGuardLog> queryIncreamentByPerson(String villageid) {
        String sql = "select * from t_buildingentranceguardlog_33 where id not in (select inid from T_GATEENTRANCEINOUT_16 where flag = 1 and inid is not null) and id not in (select outid from T_GATEENTRANCEINOUT_16 where flag = 1 and outid is not null) and villageid = '"+villageid+"' order by villageid asc,entrancecardno asc,passtime asc";
        return buildingEntranceGuardLogDao.executeNativeQuerySql(sql,null);
    }

    /**
     * @return
     * @Author shirenjing
     * @Description
     * @Date 11:14 2019/6/22
     * @Param [pageable, idCard:证件编号, passtimes：数据范围, entrancecardno：门禁卡号, hours：时间段 （0-4）]
     **/
    Page<BuildingEntranceGuardLog> queryPageLog(Pageable pageable, String idCard, String[] passtimes, String entrancecardno, Integer[] hours) {
        StringBuilder sb = new StringBuilder("select t.id,t.villageid,t.apeid,t.entrancecardno,to_char(t.passtime ,'yyyy-MM-dd HH24:mi:ss') passtime," +
                "decode(t.passtype,'0','出','1','进') passtype,t2.name personname,t.personidnumber,t.ownername,t.ownertype,t.visitorpicture,t.homelocation,t.houseid,t.user_name,t.owner_name,t.phone,t.address,t.flag,t.createdate from t_buildingentranceguardlog_33 t \n" +
                "left join t_buildingentrancecard_33 t1 on t.entrancecardno=t1.entrancecardno " +
                " left join t_owner_19 t2 on t1.personidnumber = t2.idnumber where 1=1 ");
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
        return buildingEntranceGuardLogDao.executeQuerySqlByPage(pageable, sb.toString(), null);
    }

    @Override
    public List<ResultVo> tjBuildingEntranceGuardBydy(String buildingno, String time, String flag) {
        String param = "";
        if ("day".equals(flag)) {
            param = " and TO_CHAR(passtime,'yyyy-MM-dd') = '" + time + "'";
        } else if ("week".equals(flag)) {
            param = " and TO_CHAR(passtime,'yyyy-iw') = '" + time + "'";
        } else {
            param = " and TO_CHAR(passtime,'yyyy-MM') = '" + time + "'";
        }
        String sql = "select b.UNITNO,count(1) nums from (select * from T_BUILDINGENTRANCEGUARDLOG_33 where APEID in(select DEVICEID from T_BUILDINGENTRANCEDEVICE_20 where BUILDINGNO = '" + buildingno + "')" + param + ") a left join T_BUILDINGENTRANCEDEVICE_20  b on a.APEID = b.DEVICEID group by b.UNITNO";
        List<Unit> unitList = unitDao.findAll();
        List list = buildingEntranceGuardLogDao.listBySQL(sql);
        List<ResultVo> resultList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (Object o : list) {
                Object[] oo = (Object[]) o;
                for (Unit unit : unitList) {
                    if (unit.getUnitno().equals(oo[0])) {
                        resultList.add(new ResultVo(oo[0].toString(), unit.getUnit_name(), Integer.parseInt(oo[1].toString())));
                    }
                }
            }
        }
        return resultList;
    }

    @Override
    public List<Map<String, Object>> buildingEntranceGuardTimeDur(int pill, String time, String flag, String unitno) {
        String param = "";
        if ("day".equals(flag)) {
            param = " and TO_CHAR(passtime,'yyyy-MM-dd') = '" + time + "'";
        } else if ("week".equals(flag)) {
            param = " and TO_CHAR(passtime,'yyyy-iw') = '" + time + "'";
        } else {
            param = " and TO_CHAR(passtime,'yyyy-MM') = '" + time + "'";
        }
        StringBuilder sb = new StringBuilder("select trunc(to_number(to_char(passtime,'hh24'))/" + pill + "),count(1) from " +
                "(SELECT * FROM T_BUILDINGENTRANCEGUARDLOG_33 WHERE APEID IN (SELECT a.DEVICEID FROM T_BUILDINGENTRANCEDEVICE_20 a left join T_UNIT_19 b on a.UNITNO = b.UNITNO WHERE a.UNITNO = '" + unitno + "' ) " + param + " ) where 1=1 ");
        sb.append(" group by trunc(to_number(to_char(passtime,'hh24'))/" + pill + ")");
        List<Object[]> list = buildingEntranceGuardLogDao.listBySQL(sb.toString(), null);
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
    public Page<BuildingEntranceGuardLog> queryBuildingEntranceGuardByTimeDur(Pageable pageables, String time, Integer[] hours, String flag, String unitno) {
        List<Object> param = new ArrayList<>();
        String params = "";
        if ("day".equals(flag)) {
            params = " and TO_CHAR(a.passtime,'yyyy-MM-dd') = '" + time + "'";
        } else if ("week".equals(flag)) {
            params = " and TO_CHAR(a.passtime,'yyyy-iw') = '" + time + "'";
        } else {
            params = " and TO_CHAR(a.passtime,'yyyy-MM') = '" + time + "'";
        }
        StringBuilder sb = new StringBuilder("SELECT a.*,c.name name FROM T_BUILDINGENTRANCEGUARDLOG_33 a left join T_BUILDINGENTRANCECARD_33 b on a.ENTRANCECARDNO = b.ENTRANCECARDNO left join T_OWNER_19 c on b.PERSONIDNUMBER = c.IDNUMBER" +
                " WHERE a.APEID IN (SELECT a.DEVICEID FROM T_BUILDINGENTRANCEDEVICE_20 a left join T_UNIT_19 b on a.UNITNO = b.UNITNO WHERE a.UNITNO = '" + unitno + "' ) " + params + "");
        if (hours[0] > hours[1]) {
            sb.append(" and (to_number(to_char(a.passtime,'hh24')) >= ? or to_number(to_char(a.passtime,'hh24')) <= ?) ");
            param.add(hours[0]);
            param.add(hours[1]);
        } else {
            sb.append(" and to_number(to_char(a.passtime,'hh24')) >= ? and to_number(to_char(a.passtime,'hh24')) <= ? ");
            param.add(hours[0]);
            param.add(hours[1]);
        }
        return buildingEntranceGuardLogDao.executeQuerySqlByPage(pageables, sb.toString(), param.toArray());
    }

    @Override
    public Map getDefaultNumber() {
        Map map = new HashMap<>();
        List buildingList = buildingEntranceGuardLogDao.listBySQL("select min(BUILDINGNO) from T_BUILDING_19 where (delFlag = 0 or delFlag is null)");
        List unitList = buildingEntranceGuardLogDao.listBySQL("select min(UNITNO) from T_UNIT_19 where (delFlag = 0 or delFlag is null)");
        map.put("buildingno", buildingList.get(0).toString());
        map.put("unitno", unitList.get(0).toString());
        return map;
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
