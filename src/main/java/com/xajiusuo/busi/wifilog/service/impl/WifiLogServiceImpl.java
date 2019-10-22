package com.xajiusuo.busi.wifilog.service.impl;

import com.xajiusuo.busi.wifilog.dao.WifiLogDao;
import com.xajiusuo.busi.wifilog.entity.WifiCountVo;
import com.xajiusuo.busi.wifilog.entity.WifiLog;
import com.xajiusuo.busi.wifilog.entity.WifiQueryVo;
import com.xajiusuo.busi.wifilog.service.WifiLogService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.SqlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by liujiankang on 19-6-10.
 */
@Service
public class WifiLogServiceImpl extends BaseServiceImpl<WifiLog, String> implements WifiLogService {

    @Autowired
    private WifiLogDao wifiLogDao;
    @Override
    public BaseDao<WifiLog, String> getBaseDao() {
        return this.wifiLogDao;
    }

    @Override
    public Page<WifiLog> queryPageLog(Pageable pageable, String sbbm,String ipAddress,String placecode,String orgcode,String dateTime) {
        String sql="select * from T_WIFILOG_39 where 1=1 ";
        if(null!=dateTime&&dateTime!=""){
            sql+=" and passtime >= to_date('"+dateTime.split(",")[0]+"','yyyy-MM-dd hh24:mi:ss') and passtime <= to_date('"+dateTime.split(",")[0]+"','yyyy-MM-dd hh24:mi:ss')";
        }
        if(null!=sbbm&&sbbm!=""){
            sql+=" and apeid like '%"+sbbm+"%'";
        }
        if(null!=placecode&&placecode!=""){
            sql += " and placecode  like '%"+placecode+"%'";
        }
        if(null!=orgcode&&orgcode!=""){
            sql += " and orgcode  like '%"+orgcode+"%'";
        }
        return this.wifiLogDao.executeQuerySqlByPage(pageable,sql);
    }

    @Override
    public List<WifiCountVo> countByMac(String startTime, String endTime) {
        String sql=" select w.apeid name,w.value from  (  \n" +
                "    select t.*,rownum ss from(\n" +
                "       select apeid,count(*) value from t_wifilog_39 where passtime >= to_date('"+startTime+"','yyyy-MM-dd hh24:mi:ss') and passtime <= to_date('"+endTime+"','yyyy-MM-dd hh24:mi:ss') group by apeid order by value desc\n" +
                "    ) t\n" +
                "  ) w where ss <= 10";
        try {
            return this.wifiLogDao.querySqlBean(sql, WifiCountVo.class,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<WifiCountVo> countByApe(String startTime, String endTime) {
        String sql="select w.mac1 name,w.value from  (  \n" +
                "    select t.*,rownum ss from(\n" +
                "       select mac1,count(*) value from t_wifilog_39 where passtime >= to_date('"+startTime+"','yyyy-MM-dd hh24:mi:ss') and passtime <= to_date('"+endTime+"','yyyy-MM-dd hh24:mi:ss')  group by mac1 order by value desc\n" +
                "    ) t\n" +
                "  ) w where ss <= 10";
        try {
            return this.wifiLogDao.querySqlBean(sql, WifiCountVo.class,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Page<WifiLog> queryPageControltask(Pageable pageable, String taskId, WifiQueryVo wifiQueryVo) {
        StringBuilder sb = new StringBuilder(appendControltaskSql(taskId,wifiQueryVo));
        return wifiLogDao.executeQuerySqlByPage(pageable,sb.toString(),new String[]{taskId});
    }

    @Override
    public WifiLog queryLastControltask(String taskId, WifiQueryVo wifiQueryVo) {
        StringBuilder sb = new StringBuilder(appendControltaskSql(taskId,wifiQueryVo));
        sb.append(" order by t.passtime desc");
        List<WifiLog> wifiLogs = wifiLogDao.executeNativeQuerySql(sb.toString(),null);
        if(wifiLogs!=null && wifiLogs.size()>0){
            return wifiLogs.get(0);
        }
        return null;
    }

    public String appendControltaskSql(String taskId, WifiQueryVo wifiQueryVo){
        StringBuilder sb = new StringBuilder(" select * from t_wifilog_39 t where exists (select distinct identify from t_controlidentify_16 t1 where t1.identify = t.mac1 and t1.taskid = ?) ");
        if(wifiQueryVo!=null){
            String[] passtimes = wifiQueryVo.getPasstimes();
            if(CommonUtils.isNotEmpty(passtimes) && passtimes.length==2){
                sb.append(" and t.passtime >= to_date('"+passtimes[0]+"','yyyy-MM-dd hh24:mi:ss') and t.passtime <= to_date('"+passtimes[1]+"','yyyy-MM-dd hh24:mi:ss')");
            }
            if(CommonUtils.isNotEmpty(wifiQueryVo.getMac())){
                sb.append(" and t.mac1 like '%"+wifiQueryVo.getMac()+"%'");
            }
            if(CommonUtils.isNotEmpty(wifiQueryVo.getIPAddr())){
                sb.append(" and t.ipaddr = '"+wifiQueryVo.getIPAddr()+"'");
            }
            if(CommonUtils.isNotEmpty(wifiQueryVo.getApeID())){
                sb.append(" and t.apeid like '%"+wifiQueryVo.getApeID()+"%'");
            }
            if(CommonUtils.isNotEmpty(wifiQueryVo.getName())){
                sb.append(" and t.name like '%wifiQueryVo.getName()%'");
            }
            if(CommonUtils.isNotEmpty(wifiQueryVo.getPlaceCode())){
                sb.append(" and t.placecode = '"+wifiQueryVo.getPlaceCode()+"'");
            }
            if(CommonUtils.isNotEmpty(wifiQueryVo.getOrgCode())){
                sb.append(" and t.orgcode = '"+wifiQueryVo.getOrgCode()+"'");
            }
        }
        return sb.toString();
    }
}
