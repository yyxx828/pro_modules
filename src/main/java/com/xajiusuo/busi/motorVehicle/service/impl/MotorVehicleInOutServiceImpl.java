package com.xajiusuo.busi.motorVehicle.service.impl;

import com.xajiusuo.busi.motorVehicle.dao.MotorVehicleInOutDao;
import com.xajiusuo.busi.motorVehicle.entity.*;
import com.xajiusuo.busi.motorVehicle.service.MotorVehicleInOutService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.param.e.Configs;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.DateUtils;
import com.xajiusuo.jpa.util.P;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.CfI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by liujiankang on 19-6-10.
 */
@Service
public class MotorVehicleInOutServiceImpl extends BaseServiceImpl<MotorVehicleInOut, String> implements MotorVehicleInOutService {

    @Autowired
    private MotorVehicleInOutDao motorVehicleInOutDao;

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public BaseDao<MotorVehicleInOut, String> getBaseDao() {
        return this.motorVehicleInOutDao;
    }

    @Override
    public List<MotorVehicleCountVo> queryLongtimestop(MotorVehicleLogVo motorVehicleLogVo) {
        StringBuilder sb = new StringBuilder("select * from (select plateno,trunc(sum(stoptime)/24) nums from T_MOTORVEHICLEINOUT_39 where STATUS in ('0','1') and STOPTIME>=30*24");
        if(motorVehicleLogVo!=null){
            if(CommonUtils.isNotEmpty(motorVehicleLogVo.getStartpPassTime())){
                sb.append(" and indata >=to_date('"+motorVehicleLogVo.getStartpPassTime()+"','yyyy-MM-dd HH24:mi:ss') and indata <=to_date('"+motorVehicleLogVo.getStartpPassTime()+"','yyyy-MM-dd HH24:mi:ss')");
            }
            if(CommonUtils.isNotEmpty(motorVehicleLogVo.getEndPassTime())){
                sb.append(" and outdata >=to_date('"+motorVehicleLogVo.getEndPassTime()+"','yyyy-MM-dd HH24:mi:ss') and outdata <=to_date('"+motorVehicleLogVo.getEndPassTime()+"','yyyy-MM-dd HH24:mi:ss')");
            }
        }
        sb.append(" group by plateno order by nums desc ) t where rownum<11");
        try {
            return motorVehicleInOutDao.querySqlBean(sb.toString(),MotorVehicleCountVo.class,null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<MotorVehicleCountVo> queyrNightOut(MotorVehicleLogVo motorVehicleLogVo) {
        int start = Configs.find(CfI.C_NIGHTOUTDUR_START).getInt();
        int end = Configs.find(CfI.C_NIGHTOUTDUR_END).getInt();
        StringBuilder sb = new StringBuilder("select * from (\n" +
                "select plateno,count(1) nums from T_MOTORVEHICLEINOUT_39 where outtime >= "+start*60+" and outtime <="+end*60);
        if(motorVehicleLogVo!=null){
            if(CommonUtils.isNotEmpty(motorVehicleLogVo.getStartpPassTime())){
                sb.append(" and indata >=to_date('"+motorVehicleLogVo.getStartpPassTime()+"','yyyy-MM-dd HH24:mi:ss') and indata <=to_date('"+motorVehicleLogVo.getStartpPassTime()+"','yyyy-MM-dd HH24:mi:ss')");
            }
            if(CommonUtils.isNotEmpty(motorVehicleLogVo.getEndPassTime())){
                sb.append(" and outdata >=to_date('"+motorVehicleLogVo.getEndPassTime()+"','yyyy-MM-dd HH24:mi:ss') and outdata <=to_date('"+motorVehicleLogVo.getEndPassTime()+"','yyyy-MM-dd HH24:mi:ss')");
            }
        }
        sb.append(" group by plateno order by count(1) desc ) t  where rownum<11");

        try {
            return motorVehicleInOutDao.querySqlBean(sb.toString(),MotorVehicleCountVo.class,null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Page<MotorVehicleInOut> queryPageByLongtime(Pageable pageable, String plateNo, String dateTime, int dataTime) {
        String startDate="";
        String endDate="";
        if(null==dateTime||dateTime==""||dateTime.equals("null")){
            startDate="1970-01-01 01:01:01";
            endDate=format.format(new Date());
        }else{
            startDate=dateTime.split(",")[0];
            endDate=dateTime.split(",")[1];
        }
        String sql="select * from (select  t.*  from (select * from "+tableName()+" where (indata >=? and  outdata <=?) or (indata >=? and indata <= ? and status = 0)) t where t.stoptime >?) where 1=1 ";
        List<Object> param = new ArrayList<Object>();
        param.add(DateUtils.parse(startDate, P.S.fmtYmd11));
        param.add(DateUtils.parse(endDate, P.S.fmtYmd11));
        param.add(DateUtils.parse(startDate, P.S.fmtYmd11));
        param.add(DateUtils.parse(endDate, P.S.fmtYmd11));
        param.add(dataTime*24);
        if(null!=plateNo&&plateNo!=""){
            sql += " and plateno like ?";
            param.add(SqlUtils.sqlLike( plateNo));
        }

        return motorVehicleInOutDao.executeQuerySqlByPage(pageable,sql,param.toArray());
    }

    @Override
    public Page<InOutNewVo> queryPageByMoringAndNight(Pageable pageable, String dateTime, String plateNo, String startTime, String endTime, int times) {
        String startDate="";
        String endDate="";
        if(null==dateTime||dateTime==""||dateTime.equals("null")){
            startDate="1970-01-01 01:01:01";
            endDate=format.format(new Date());
        }else{
            startDate=dateTime.split(",")[0];
            endDate=dateTime.split(",")[1];
        }

        String sql=" select * from (select plateno,count(*) times,listagg(id,',') within group (order by id) ids from (select * from (select * from "+tableName()+"  where outdata >=? and  outdata <=?) where outtime > ? or outtime < ?) group by plateno) where times >= ?";
        List<Object> param = new ArrayList<Object>();
        param.add(DateUtils.parse(startDate, P.S.fmtYmd11));
        param.add(DateUtils.parse(endDate, P.S.fmtYmd11));
        param.add(getNumByTime(startTime));
        param.add(getNumByTime(endTime));
        param.add(times);
        if(null!=plateNo&&plateNo!=""){
            sql += " and plateno like ?";
            param.add(SqlUtils.sqlLike( plateNo));
        }

        try {
            return motorVehicleInOutDao.queryPageSqlBean(pageable,sql,InOutNewVo.class,param.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public Page<InOutNewVo> queryPageBytimes(Pageable pageable, String plateNo, String dateTime, String startTime, String endTime, int times) {

        String startDate="";
        String endDate="";
        if(null==dateTime||dateTime==""||dateTime.equals("null")){
            startDate="1970-01-01 01:01:01";
            endDate=format.format(new Date());
        }else{
            startDate=dateTime.split(",")[0];
            endDate=dateTime.split(",")[1];
        }

        String sql="select * from (select plateno,count(*) times,listagg(id,',') within group (order by id) ids  from ( select * from ( select * from " + tableName() + "  where indata >= ? and  indata <= ?) where intime >= ? or intime <= ?) group by plateno ) where times >= ?";
//        String sql=" select * from (select plateno,count(*) times from (select * from t_motorvehicleinout_39  where indata >=to_date('"+startDate+"','yyyy-MM-dd HH24:mi:ss') and  indata <=to_date('"+endDate+"','yyyy-MM-dd HH24:mi:ss') and intime > "+getNumByTime(startTime)+" and intime< "+getNumByTime(endTime)+" ) group by plateno) where times >= "+times;
        List<Object> param = new ArrayList<Object>();
        param.add(DateUtils.parse(startDate, P.S.fmtYmd11));
        param.add(DateUtils.parse(endDate, P.S.fmtYmd11));
        param.add(getNumByTime(startTime));
        param.add(getNumByTime(endTime));
        param.add(times);
        if(StringUtils.isNoneBlank(plateNo)){
            sql += " and plateno like ?";
            param.add(SqlUtils.sqlLike( plateNo));
        }
        try {
            return motorVehicleInOutDao.queryPageSqlBean(pageable,sql,InOutNewVo.class,param.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Page<MotorVehicleInOut> queryPageByIds(Pageable pageable, String ids) {
        String sql="select * from "+tableName()+" where id in ("+SqlUtils.sqlIn(ids.split(","))+")";
        return motorVehicleInOutDao.executeQuerySqlByPage(pageable,sql);
    }

    public int getNumByTime(String time){
        String[] t=time.split(":");
        return Integer.parseInt(t[0])*60+Integer.parseInt(t[1]);
    }
}
