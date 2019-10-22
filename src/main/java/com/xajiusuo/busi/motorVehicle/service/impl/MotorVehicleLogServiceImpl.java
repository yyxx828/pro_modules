package com.xajiusuo.busi.motorVehicle.service.impl;

import com.xajiusuo.busi.motorVehicle.dao.MotorVehicleLogDao;
import com.xajiusuo.busi.motorVehicle.dao.MotorVehicleWarnDao;
import com.xajiusuo.busi.motorVehicle.entity.*;
import com.xajiusuo.busi.motorVehicle.service.MotorVehicleInOutService;
import com.xajiusuo.busi.motorVehicle.service.MotorVehicleLogService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.param.e.Configs;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.DateUtils;
import com.xajiusuo.jpa.util.P;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.CfI;
import com.xajiusuo.utils.DateTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by shirenjing on 2019/6/6.
 */
@Service
public class MotorVehicleLogServiceImpl extends BaseServiceImpl<MotorVehicleLog, String> implements MotorVehicleLogService {

    @Autowired
    private MotorVehicleLogDao motorVehicleLogDao;
    @Autowired
    private MotorVehicleInOutService motorVehicleInOutService;
    @Autowired
    private MotorVehicleWarnDao motorVehicleWarnDao;
    @Override
    public BaseDao<MotorVehicleLog, String> getBaseDao() {
        return motorVehicleLogDao;
    }

    @Override
    public Page<MotorVehicleLog> queryPageLog(Pageable pageable, MotorVehicleLogVo motorVehicleLogVo) {
        motorVehicleLogVo = motorVehicleLogVo == null ? new MotorVehicleLogVo() : motorVehicleLogVo;
        StringBuilder sql = new StringBuilder("select * from " + tableName() + " where 1=1~ and plateno like ?~~ and passtime>= ?~~ and passtime <= ?~");
        List<Object> param = Arrays.asList(SqlUtils.sqlLike(motorVehicleLogVo.getPlateNo()),DateUtils.parse(motorVehicleLogVo.getStartpPassTime(),"yyyy-MM-dd HH:mm:ss"),
                DateUtils.parse(motorVehicleLogVo.getEndPassTime(),"yyyy-MM-dd HH:mm:ss"));
        return findPageWithSqlLikenoBlack(pageable,sql.toString(),param.toArray());
    }

    @Override
    public Map<String, Object> countAllVehicle(MotorVehicleLogVo motorVehicleLogVo) {
        StringBuilder sql = new StringBuilder("select count(tab.plateno) cczs,sum(platenoCount) crzcs from (select plateno,count(plateno) platenoCount  from T_MOTORVEHICLELOG_39 where 1=1");
        appendSql(sql,motorVehicleLogVo);
        sql.append(" group by plateno) tab");
        List<Object[]> list =  motorVehicleLogDao.listBySQL(sql.toString(),null);

        Map<String, Object> map = new HashMap<>();
        if(list!=null && list.size()>0){
            Object[] objects = list.get(0);
            map.put("vehicleNum",objects[0]);//车辆总数
            map.put("goComeNum",objects[1]);//总出入次数
        }
        return map;
    }

    @Override
    public List<MotorVehicleCountVo> countGoComeDay(MotorVehicleLogVo motorVehicleLogVo) {
        String startDate = "";
        String endDate = "";
        if(motorVehicleLogVo!=null){
            if(CommonUtils.isNotEmpty(motorVehicleLogVo.getStartpPassTime()) &&
                    CommonUtils.isNotEmpty(motorVehicleLogVo.getEndPassTime())){
                startDate = DateTools.formatDatetime(motorVehicleLogVo.getStartpPassTime(),"yyyy-MM-dd");
                endDate = DateTools.formatDatetime(motorVehicleLogVo.getEndPassTime(),"yyyy-MM-dd");
            }else{
                endDate = DateTools.formatDatetime(new Date(),"yyyy-MM-dd");
                startDate = DateUtils.dateAdds2s(endDate, -90);
            }
        }
        StringBuilder sb = new StringBuilder("select * from ( " +
                "select t.daylist days,nvl(t1.passtype,'0') passtype,nvl(t1.nums,0) nums from " +
                "(select to_char(to_date('"+startDate+"','yyyy-MM-dd')+rownum-1,'yyyy-MM-dd') as daylist,rownum from dual connect by rownum <= to_number(to_date('"+endDate+"','yyyy-MM-dd')-to_date('"+startDate+"','yyyy-MM-dd'))+1 ) t " +
                "left join (select to_char(passtime,'yyyy-MM-dd') days,passtype,count(1) nums from t_motorvehiclelog_39 where passtype = '0' ");
        appendSql(sb,motorVehicleLogVo);
        sb.append(" group by to_char(passtime,'yyyy-MM-dd'),passtype ) t1 on t.daylist = t1.days ");
        sb.append(" union ");
        sb.append(" select t.daylist days,nvl(t1.passtype,'1') passtype,nvl(t1.nums,0) nums from ");
        sb.append("(select to_char(to_date('"+startDate+"','yyyy-MM-dd')+rownum-1,'yyyy-MM-dd') as daylist,rownum from dual connect by rownum <= to_number(to_date('"+endDate+"','yyyy-MM-dd')-to_date('"+startDate+"','yyyy-MM-dd'))+1 ) t ");
        sb.append("left join (select to_char(passtime,'yyyy-MM-dd') days,passtype,count(1) nums from t_motorvehiclelog_39 where passtype = '1' ");
        appendSql(sb,motorVehicleLogVo);
        sb.append("group by to_char(passtime,'yyyy-MM-dd'),passtype ) t1 on t.daylist = t1.days ) tt order by tt.days");
        try {
            return motorVehicleLogDao.querySqlBean(sb.toString(),MotorVehicleCountVo.class,null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<MotorVehicleCountVo> countGoCome7Day(String startDate, String endDate) {
        StringBuilder sb = new StringBuilder("select * from ( " +
                "select t.daylist days,nvl(t1.passtype,'0') passtype,nvl(t1.nums,0) nums from " +
                "(select to_char(to_date('"+startDate+"','yyyy-MM-dd')+rownum-1,'yyyy-MM-dd') as daylist,rownum from dual connect by rownum <= to_number(to_date('"+endDate+"','yyyy-MM-dd')-to_date('"+startDate+"','yyyy-MM-dd'))+1 ) t " +
                "left join (select to_char(passtime,'yyyy-MM-dd') days,passtype,count(1) nums from t_motorvehiclelog_39 where passtype = '0' ");
        sb.append(" group by to_char(passtime,'yyyy-MM-dd'),passtype ) t1 on t.daylist = t1.days ");
        sb.append(" union ");
        sb.append(" select t.daylist days,nvl(t1.passtype,'1') passtype,nvl(t1.nums,0) nums from ");
        sb.append("(select to_char(to_date('"+startDate+"','yyyy-MM-dd')+rownum-1,'yyyy-MM-dd') as daylist,rownum from dual connect by rownum <= to_number(to_date('"+endDate+"','yyyy-MM-dd')-to_date('"+startDate+"','yyyy-MM-dd'))+1 ) t ");
        sb.append("left join (select to_char(passtime,'yyyy-MM-dd') days,passtype,count(1) nums from t_motorvehiclelog_39 where passtype = '1' ");
        sb.append("group by to_char(passtime,'yyyy-MM-dd'),passtype ) t1 on t.daylist = t1.days ) tt order by tt.days");
        try {
            return motorVehicleLogDao.querySqlBean(sb.toString(),MotorVehicleCountVo.class,null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<MotorVehicleCountVo> countGoComeWeek(MotorVehicleLogVo motorVehicleLogVo) {
        Date startDate = null;
        Date endDate = null;
        if(motorVehicleLogVo!=null){
            if(CommonUtils.isNotEmpty(motorVehicleLogVo.getStartpPassTime()) &&
                    CommonUtils.isNotEmpty(motorVehicleLogVo.getEndPassTime())){
                startDate = DateUtils.parse(motorVehicleLogVo.getStartpPassTime(),"yyyy-MM-dd");
                endDate = DateUtils.parse(motorVehicleLogVo.getEndPassTime(),"yyyy-MM-dd");
            }else{
                endDate = new Date();
                startDate = DateUtils.dateAddd2d(endDate, -90);
            }
        }

        StringBuilder sb = new StringBuilder("select to_char(passtime,'yyyy-iw') days,passtype,count(1) nums from T_MOTORVEHICLELOG_39 where 1=1");
        if(motorVehicleLogVo!=null){
            if(CommonUtils.isNotEmpty(motorVehicleLogVo.getStartpPassTime()) && CommonUtils.isNotEmpty(motorVehicleLogVo.getEndPassTime())){
                sb.append(" and passtime >= to_date('"+motorVehicleLogVo.getStartpPassTime()+"','yyyy-MM-dd hh24:mi:ss') " +
                        "and passtime <= to_date('"+motorVehicleLogVo.getEndPassTime()+"','yyyy-MM-dd hh24:mi:ss') ");
            }
        }
        sb.append(" group by to_char(passtime,'yyyy-iw'),passtype order by days");
        try {
            List<MotorVehicleCountVo> voList = motorVehicleLogDao.querySqlBean(sb.toString(),MotorVehicleCountVo.class,null);
            List<MotorVehicleCountVo> list = new ArrayList<>();
            List<String> weeks = DateTools.getWeeks(startDate,endDate);
            for (String w:weeks) {
                MotorVehicleCountVo vo0 = null;
                MotorVehicleCountVo vo1 = null;
                for(MotorVehicleCountVo vo:voList){
                    if(vo.getDays().equals(w) && "0".equals(vo.getPassType())){
                        vo0 = vo;
                    }
                    if(vo.getDays().equals(w) && "1".equals(vo.getPassType())){
                        vo1 = vo;
                    }
                }

                vo0 = vo0!=null ? vo0 : new MotorVehicleCountVo(w,"0","0");
                vo1 = vo1!=null ? vo1 : new MotorVehicleCountVo(w,"1","0");
                list.add(vo0);
                list.add(vo1);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<MotorVehicleCountVo> countGoComeMonth(MotorVehicleLogVo motorVehicleLogVo) {
        String startMonth = "";
        String endMonth = "";
        if(motorVehicleLogVo!=null){
            if(CommonUtils.isNotEmpty(motorVehicleLogVo.getStartpPassTime()) &&
                    CommonUtils.isNotEmpty(motorVehicleLogVo.getEndPassTime())){
                startMonth = DateTools.formatDatetime(motorVehicleLogVo.getStartpPassTime(),"yyyy-MM");
                endMonth = DateTools.formatDatetime(motorVehicleLogVo.getEndPassTime(),"yyyy-MM");
            }else{
                endMonth = DateTools.formatDatetime(new Date(),"yyyy-MM");
                startMonth = DateTools.formatDatetime(DateUtils.dateAddd2d(new Date(), -90),"yyyy-MM");
            }
        }
        StringBuilder sb = new StringBuilder("select * from ( " +
                "select t.monthlist days,nvl(t1.passtype,'0') passtype, nvl(t1.nums,0) nums from " +
                "(select to_char(add_months(to_date('"+startMonth+"','yyyy-MM'),rownum-1),'yyyy-MM') as monthlist from dual connect by rownum <= months_between(to_date('"+endMonth+"','yyyy-MM'),to_date('"+startMonth+"','yyyy-MM'))+1 ) t " +
                "left join (select to_char(passtime,'yyyy-MM') days,passtype,count(1) nums from T_MOTORVEHICLELOG_39 where passtype='0' ");
        appendSql(sb,motorVehicleLogVo);
        sb.append(" group by to_char(passtime,'yyyy-MM'),passtype) t1 on t.monthlist = t1.days ");
        sb.append(" union ");
        sb.append(" select t.monthlist days,nvl(t1.passtype,'1') passtype, nvl(t1.nums,0) nums from ");
        sb.append("(select to_char(add_months(to_date('"+startMonth+"','yyyy-MM'),rownum-1),'yyyy-MM') as monthlist from dual connect by rownum <= months_between(to_date('"+endMonth+"','yyyy-MM'),to_date('"+startMonth+"','yyyy-MM'))+1 ) t ");
        sb.append(" left join ");
        sb.append(" (select to_char(passtime,'yyyy-MM') days,passtype,count(1) nums from T_MOTORVEHICLELOG_39 where passtype='1' ");
        appendSql(sb,motorVehicleLogVo);
        sb.append(" group by to_char(passtime,'yyyy-MM'),passtype) t1 on t.monthlist = t1.days) tt order by tt.days");
//        return motorVehicleLogDao.listBySQL(sb.toString(),null);
        try {
            return motorVehicleLogDao.querySqlBean(sb.toString(),MotorVehicleCountVo.class,null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Map<String,Object>> countGoComeHour(int pill,String... times) {
        StringBuilder sb = new StringBuilder("select trunc(to_number(to_char(passtime,'hh24'))/"+pill+"),count(1) from t_motorvehiclelog_39 where 1=1 ");
        if(times!=null){
            if(times.length==1){
                sb.append(" and passtime = to_date('"+times[0]+"','yyyy-MM-dd hh24:mi:ss') ");
            }
            if(times.length==2){
                sb.append(" and passtime >= to_date('"+times[0]+"','yyyy-MM-dd hh24:mi:ss') and passtime <= to_date('"+times[1]+"','yyyy-MM-dd hh24:mi:ss')");
            }
        }
        sb.append(" group by trunc(to_number(to_char(passtime,'hh24'))/"+pill+")");
        List<Object[]> list = motorVehicleLogDao.listBySQL(sb.toString(),null);
        Map<Integer,Integer> map = new HashMap<>();
        for (Object[] obj:list) {
            map.put(Integer.parseInt(obj[0].toString()),Integer.parseInt(obj[1].toString()));
        }
        int count = 0;
        if(24%pill==0){
            count = 24/pill;
        }else{
            count = 24/pill+1;
        }
        List<Map<String,Object>> result = new ArrayList<>();
        Map<String,Object> m = null;
        for (int i = 0; i < count; i++) {
            m = new HashMap<>();
            m.put("name",getHourDur(pill,i));
            m.put("value",map.get(i)==null?0:map.get(i));
            result.add(m);
        }
        return result;
    }

    @Override
    public List<MotorVehicleLog> queryLogByHours(String[] dates, Integer[] hours) {
        StringBuilder sb = new StringBuilder("select * from t_motorvehiclelog_39 where 1=1 ");
        sb.append(" and passtime >= to_date('"+dates[0]+"','yyyy-MM-dd hh24:mi:ss')");
        sb.append(" and passtime <= to_date('"+dates[1]+"','yyyy-MM-dd hh24:mi:ss')");
        if(hours[0]>hours[1]){
            sb.append(" and (to_number(to_char(passtime,'hh24')) >= "+hours[0]+" or to_number(to_char(passtime,'hh24')) <= "+hours[1]+") ");
        }else{
            sb.append(" and to_number(to_char(passtime,'hh24')) >= "+hours[0]+" and to_number(to_char(passtime,'hh24')) <= "+hours[1]);
        }
        return motorVehicleLogDao.executeNativeQuerySql(sb.toString(), null);
    }

    @Override
    public Page<MotorVehicleLog> queryLogPageByHours(Pageable pageables, String[] dates, Integer[] hours) {
        List<Object> param = new ArrayList<>();
        StringBuilder sb = new StringBuilder("select * from t_motorvehiclelog_39 where 1=1 ");
        if(dates!=null && dates.length==2){
            sb.append(" and passtime >= ?");
            sb.append(" and passtime <= ?");
            param.add(DateUtils.parse(dates[0],"yyyy-MM-dd HH:mm:ss"));
            param.add(DateUtils.parse(dates[1],"yyyy-MM-dd HH:mm:ss"));
        }
        if(hours[0]>hours[1]){
            sb.append(" and (to_number(to_char(passtime,'hh24')) >= ? or to_number(to_char(passtime,'hh24')) <= ?) ");
            param.add(hours[0]);
            param.add(hours[1]);
        }else{
            sb.append(" and to_number(to_char(passtime,'hh24')) >= ? and to_number(to_char(passtime,'hh24')) <= ?");
            param.add(hours[0]);
            param.add(hours[1]);
        }
        return motorVehicleLogDao.executeQuerySqlByPage(pageables,sb.toString(),param.toArray());
    }

    @Override
    public Page<MotorVehicleLog> queryPageControltask(Pageable pageable, String taskId, String[] passtimes, String plateNo, String passtype) {
        String sql = "select * from t_motorvehiclelog_39 where 1=1 and plateno in (select distinct identify from t_controlidentify_16 where taskid = '"+taskId+"')";
        if(CommonUtils.isNotEmpty(passtimes) && passtimes.length==2){
            sql += " and passtime>=to_date('"+passtimes[0]+"','yyyy-MM-dd HH24:mi:ss') and passtime<=to_date('"+passtimes[1]+"','yyyy-MM-dd HH24:mi:ss') ";
        }
        if(CommonUtils.isNotEmpty(plateNo)) {
            sql += " and plateNo like '%"+plateNo+"%'";
        }
        if(CommonUtils.isNotEmpty(passtype)){
            sql += " and passtype = '"+passtype+"' ";
        }
        return motorVehicleLogDao.executeQuerySqlByPage(pageable,sql,null);
    }

    @Override
    public MotorVehicleLog queryLastControltask(String taskId, String[] passtimes) {
        String sql = "select * from t_motorvehiclelog_39 where 1=1 and plateno in (select distinct identify from t_controlidentify_16 where taskid = '"+taskId+"')";
        if(CommonUtils.isNotEmpty(passtimes) && passtimes.length==2){
            sql += " and passtime>=to_date('"+passtimes[0]+"','yyyy-MM-dd HH24:mi:ss') and passtime<=to_date('"+passtimes[1]+"','yyyy-MM-dd HH24:mi:ss') ";
        }
        sql += " order by passtime desc";
        List<MotorVehicleLog> logs = motorVehicleLogDao.executeNativeQuerySql(sql,null);
        if(logs!=null && logs.size()>0){
            return logs.get(0);
        }
        return null;
    }


    /**
     * @Author shirenjing
     * @Description 拼接查询条件
     * @Date 13:38 2019/6/13
     * @Param [stringBuilder, motorVehicleLogVo]
     * @return
     **/
    private void appendSql(StringBuilder stringBuilder,MotorVehicleLogVo motorVehicleLogVo){
        if(motorVehicleLogVo!=null){
            if(CommonUtils.isNotEmpty(motorVehicleLogVo.getPlateNo())){
                stringBuilder.append(" and plateno like '"+SqlUtils.sqlLike(motorVehicleLogVo.getPlateNo())+"'");
            }
            if(CommonUtils.isNotEmpty(motorVehicleLogVo.getStartpPassTime())){
                stringBuilder.append(" and passtime>=to_date('"+motorVehicleLogVo.getStartpPassTime()+"','yyyy-MM-dd HH24:mi:ss')");
            }
            if(CommonUtils.isNotEmpty(motorVehicleLogVo.getEndPassTime())){
                stringBuilder.append(" and passtime<=to_date('"+motorVehicleLogVo.getEndPassTime()+"','yyyy-MM-dd HH24:mi:ss')");
            }
        }
    }
    
    /**
     * @Author shirenjing
     * @Description 计算每一个时段对应的时间范围
     * @Date 14:27 2019/6/14
     * @Param [pill：时间颗粒都, dur：第几时段]
     * @return
     **/
    private String getHourDur(int pill,int dur){
        int count = 0;
        if(24%pill==0){
            count = 24/pill;
        }else{
            count = 24/pill+1;
        }
        int start = dur*pill;
        int end = (dur+1)*pill-1;
        if(dur==count-1) end = 23;
        String s = start>=10?start+"":"0"+start;
        String e = end>=10?end+"":"0"+end;
        if(pill==1){
            return s+"";
        }
        return s+"-"+e;
    }






    /**
     * @Author liujiankang
     * @Description 定时数据整合
     * @Date 13:38 2019/6/14
     * @return
     **/

    //    @Scheduled(cron = "0 30 * * * *")
    @Override
    public void text() {

        String sql="select t.id,t.plateno,t.intime,t.outtime,t.status,t.inid,t.outid,t.indata,t.outdata from (select tab.*,rownum num from (select distinct * from ( (select t.*,t.indata orderdate from t_motorvehicleinout_39 t where t.indata is not null) union (select t.*,t.outdata orderdate from t_motorvehicleinout_39 t where t.outdata is not null)) tt order by tt.orderdate desc ) tab where tab.plateno='---') t where t.num=1";
        //根据记录时间获取增量数据
        List<MotorVehicleLog> list=this.motorVehicleLogDao.executeNativeQuerySql("select * from t_motorvehiclelog_39 lo where lo.id not in (select io.inid from t_motorvehicleinout_39 io where io.inid is not null) and id not in (select io.outid from t_motorvehicleinout_39 io where io.outid is not null)  order by plateno desc ,passtime asc", null);
        List<MotorVehicleInOut> historyList=null;
        MotorVehicleInOut in=null;
        for(MotorVehicleLog log:list){
            historyList=this.motorVehicleInOutService.executeNativeQuerySql(sql.replace("---",log.getPlateNo()));
            if(null!=historyList&&historyList.size()>0){
                //判断新数据是不是比历史数据中最后一条的时间还早
                if(!isBefor(historyList.get(0).getIndata(),historyList.get(0).getOutdata(),log.getPassTime())){
                    MotorVehicleInOut mo=historyList.get(0);
                    saveNewInOut(mo,log);
                }
            }else{
                if(log.getPassType().equals("0")){
                    saveNewInOutByIn(log);
                }else{
                   saveNewInOutByOut(log);
                }
            }
        }
        //更新状态为0的数据停放时间
        List<MotorVehicleInOut> stop=this.motorVehicleInOutService.findBy("status",0);
        for(MotorVehicleInOut m:stop){
            m.setStoptime(getNumforInOut(m.getIndata(),new Date()));
            this.motorVehicleInOutService.save(m);
        }
        System.out.println("车辆进出数据整合完成！开始预警！");
    }

    public boolean isBefor(Date hisIn,Date hisOut,Date newtime){
        boolean is=false;
        if(null!=hisIn){
            is=newtime.before(hisIn);
        }
        if(!is&&null!=hisOut){
            is=newtime.before(hisOut);
        }
        return is;
    }

    public int getNumforTime(Date time){
        return time.getHours()*60+time.getMinutes();
    }

    public int getNumforInOut(Date inTime,Date outTime){
        return (int)((outTime.getTime()-inTime.getTime())/3600000);
    }

    public void saveNewInOut(MotorVehicleInOut mo,MotorVehicleLog log){
          if(log.getPassTime().equals("0")){
              if(mo.getStatus()==0){
                  mo.setStatus(2);
                  this.motorVehicleInOutService.save(mo);
              }
             saveNewInOutByIn(log);
          }else{
              if(mo.getStatus()==0){
                  mo.setOutdata(log.getPassTime());
                  mo.setOutid(log.getId());
                  mo.setOuttime(getNumforTime(log.getPassTime()));
                  mo.setStatus(1);
                  this.motorVehicleInOutService.save(mo);
              }else{
                 saveNewInOutByOut(log);
              }
          }
    }

    public void saveNewInOutByIn(MotorVehicleLog log){
        MotorVehicleInOut inOut=new MotorVehicleInOut();
        inOut.setStatus(0);
        inOut.setInid(log.getId());
        inOut.setIndata(log.getPassTime());
        inOut.setIntime(getNumforTime(log.getPassTime()));
        this.motorVehicleInOutService.save(inOut);
    }

    public void saveNewInOutByOut(MotorVehicleLog log){
        MotorVehicleInOut inOut=new MotorVehicleInOut();
        inOut.setStatus(3);
        inOut.setOutid(log.getId());
        inOut.setOutdata(log.getPassTime());
        inOut.setOuttime(getNumforTime(log.getPassTime()));
        this.motorVehicleInOutService.save(inOut);
    }

    public void MotorVehicleWarn(){
        List<MotorVehicleWarn> list=new ArrayList<MotorVehicleWarn>();
        //长时间停放
        int nums_stop = Configs.find(CfI.C_EARLYWARN_CAR_LONGTIMESTOP_DATARANGE).getInt();  //天数
        String sql_stop="select plateno,status times, id ids from T_MOTORVEHICLEINOUT_39 where STATUS = 0 and STOPTIME>="+nums_stop*24;
        List<InOutNewVo> inOutList_stop= null;
        try {
            inOutList_stop = this.querySqlBean(sql_stop,InOutNewVo.class,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(null!=inOutList_stop&&inOutList_stop.size()>0){
            saveWarn(inOutList_stop,0);
        }
        //频繁出入
        int dataRange_inout = Configs.find(CfI.C_EARLYWARN_CAR_OFTENOUTIN_DATARANGE).getInt();  //数据范围:小时
        int nums_inout = Configs.find(CfI.C_EARLYWARN_CAR_OFTENOUTIN_NUMS).getInt();  //次数
        String sql_inout="select * from (select plateno,count(*) times,listagg(id,',') within group (order by id) ids  from  ( select * from " + tableName() + "  where indata >= ? and  indata <= ?) group by plateno ) where times >= ?";
        List<Object> param_inout = new ArrayList<Object>();
        List<Date> timeSpane_inout=getDataTime(dataRange_inout);
        param_inout.add(timeSpane_inout.get(0));
        param_inout.add(timeSpane_inout.get(1));
        param_inout.add(nums_inout);
        List<InOutNewVo> inOutNewVoList=null;
        try {
             inOutNewVoList=this.querySqlBean(sql_inout,InOutNewVo.class,param_inout.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(null!=inOutNewVoList&&inOutNewVoList.size()>0){
            saveWarn(inOutNewVoList,1);
        }
        //昼伏夜出
        int dataRange = Configs.find(CfI.C_EARLYWARN_CAR_NIGHTOUTDUR_DATARANGE).getInt();  //数据范围
        String timeRange = Configs.find(CfI.C_EARLYWARN_CAR_NIGHTOUTDUR_TIMERANGE).getValue();  //时间范围
        int nums = Configs.find(CfI.C_EARLYWARN_CAR_NIGHTOUTDUR_NUMS).getInt();  //次数
        String sql=" select * from (select plateno,count(*) times,listagg(id,',') within group (order by id) ids from (select * from (select * from "+tableName()+"  where outdata >=? and  outdata <=?) where outtime > ? or outtime < ?) group by plateno) where times >= ?";
        List<Object> param = new ArrayList<Object>();
        List<Date> timeSpane=getDataTime(dataRange*24);
        param.add(timeSpane.get(0));
        param.add(timeSpane.get(1));
        param.add(Integer.parseInt(timeRange.split(",")[0])*60);
        param.add(Integer.parseInt(timeRange.split(",")[1])*60);
        param.add(nums);
        List<InOutNewVo> newVoList=null;
        try {
            newVoList=this.querySqlBean(sql,InOutNewVo.class,param.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(null!=newVoList&&newVoList.size()>0){
            saveWarn(newVoList,2);
        }

    }


    public void saveWarn(List<InOutNewVo> inOutList,int type){
        MotorVehicleWarn warn=null;
        for(InOutNewVo vo:inOutList){
            warn=new MotorVehicleWarn();
            warn.setIds(vo.getIds());
            warn.setAnalyType(type+"");
            warn.setPlateno(vo.getPlateno());
            warn.setNums(vo.getTimes());
            warn.setCreateTime(new Date());
            this.motorVehicleWarnDao.save(warn);
        }
    }

    public List<Date> getDataTime(int num){
        List<Date> list=new ArrayList<Date>();
        Long time=new Date().getTime();
        list.add(new Date(time-num*60*60*1000));
        list.add(new Date());
        return list;
    }
}
