package com.xajiusuo.busi.entranceandexit.service.impl;

import com.xajiusuo.busi.entranceandexit.dao.PersonWarnDao;
import com.xajiusuo.busi.entranceandexit.entity.PersonWarn;
import com.xajiusuo.busi.entranceandexit.entity.PersonWarnVo;
import com.xajiusuo.busi.entranceandexit.service.PersonWarnService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.param.e.Configs;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.DateUtils;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.CfI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by shirenjing on 2019/6/26.
 */
@Service
public class PersonWarnServiceImpl extends BaseServiceImpl<PersonWarn,String> implements PersonWarnService {

    @Autowired
    private PersonWarnDao personWarnDao;

    @Override
    public BaseDao<PersonWarn, String> getBaseDao() {
        return personWarnDao;
    }


    @Override
    public void batchSaveOrUpate(List<PersonWarn> list) {
        personWarnDao.batchSaveOrUpdate(list);
    }

    @Override
    public Map<String, Object> getOftenOutInParams() {
        int dataRange = Configs.find(CfI.C_EARLYWARN_PERSON_OFTENOUTIN_DATARANGE).getInt();  //数据范围
        int nums = Configs.find(CfI.C_EARLYWARN_PERSON_OFTENOUTIN_NUMS).getInt();  //次数
        Map<String,Object> map = new HashMap<>();
        map.put("dataRange",dataRange);
        map.put("nums",nums);
        return map;
    }

    @Override
    public Map<String, Object> getNightOutParams() {
        int dataRange = Configs.find(CfI.C_EARLYWARN_PERSON_NIGHTOUTDUR_DATARANGE).getInt();  //数据范围
        String timeRange = Configs.find(CfI.C_EARLYWARN_PERSON_NIGHTOUTDUR_TIMERANGE).getValue();  //时间范围
        int nums = Configs.find(CfI.C_EARLYWARN_PERSON_NIGHTOUTDUR_NUMS).getInt();  //次数
        Map<String,Object> map = new HashMap<>();
        map.put("dataRange",dataRange);
        map.put("timeRange",timeRange);
        map.put("nums",nums);
        return map;
    }

    @Override
    public void updateOftenOutInParams(int dataRange,int nums) {
        Configs.update(CfI.C_EARLYWARN_PERSON_OFTENOUTIN_DATARANGE,dataRange);  //数据范围
        Configs.update(CfI.C_EARLYWARN_PERSON_OFTENOUTIN_NUMS,nums);  //次数
    }

    @Override
    public void updateNightOutParams(int dataRange, String timeRange, int nums) {
        Configs.update(CfI.C_EARLYWARN_PERSON_NIGHTOUTDUR_DATARANGE,dataRange);  //数据范围
        Configs.update(CfI.C_EARLYWARN_PERSON_NIGHTOUTDUR_TIMERANGE,timeRange);  //时间范围
        Configs.update(CfI.C_EARLYWARN_PERSON_NIGHTOUTDUR_NUMS,nums);  //次数
    }

    @Override
    public Page<PersonWarn> queryPagePersonWarn(Pageable pageable, PersonWarnVo personWarnVo) {
        String sql = "select * from t_personwarning_16 where 1=1 ~ and flag = ?~~ and analytype = ?~~ and entrancecardno like ?~~ and pidcard like ?~" +
                "~ and pname like ?~~ and nums >= ?~~ and datarangestart <= ?~~ and datarangeend <= ?~~ and timerangestart >= ?~" +
                "~ and timerangeend <= ?~";
        List<Object> param = Arrays.asList(personWarnVo.getFlag(),personWarnVo.getAnalyType(),SqlUtils.sqlLike(personWarnVo.getEntrancecardno()),SqlUtils.sqlLike(personWarnVo.getpIdCard()),
                SqlUtils.sqlLike(personWarnVo.getpName()),personWarnVo.getNums(),personWarnVo.getDataRangeStart(),personWarnVo.getDataRangeEnd(),
                personWarnVo.getTimeRangeStart(),personWarnVo.getTimeRangeEend());
        return personWarnDao.findPageWithSqlLikenoBlack(pageable,sql,param.toArray());
    }

    @Override
    public List<Map> queryGateLogByIds(String[] ids) {
        String sql = "select t.entrancecardno,to_char(t.passtime,'yyyy-MM-dd HH24:mi:ss') passtime,decode(t.passtype,'0','出','1','进') passtype," +
                "t.place,t2.villagename from t_gateentranceguardlog_33 t " +
                "left join t_gateentrancecard_33 t1 on t.entrancecardno=t1.entrancecardno " +
                "left join t_village_19 t2 on t.villageid=t2.villageid where 1=1  and t.id in ("+subIds(ids)+")";
        personWarnDao.queryRangeAll();
        try {
            return personWarnDao.querySqlMap(sql,null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Map> queryBuildLogByIds(String[] ids) {
        String sql = "select t.entrancecardno,to_char(t.passtime,'yyyy-MM-dd HH24:mi:ss') passtime,decode(t.passtype,'0','出','1','进') passtype," +
                "t3.place,t2.villagename from t_buildingentranceguardlog_33 t \n" +
                " left join t_buildingentrancecard_33 t1 on t.entrancecardno=t1.entrancecardno" +
                " left join t_village_19 t2 on t.villageid=t2.villageid " +
                " left join t_device_20 t3 on t.apeid=t3.apeid where 1=1 and t.id in ("+subIds(ids)+")";
        personWarnDao.queryRangeAll();
        try {
            return personWarnDao.querySqlMap(sql,null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, PersonWarn> queryListPersonWarn(String analyType, Integer flag, Date[] dataRange, Integer[] timeRange) {
        List<Object> params = new ArrayList<>();
        String sql = "select * from t_personwarning_16 where analytype =? and flag = ? and datarangestart >= ? and datarangeend <= ? ";
        params.add(analyType);
        params.add(flag);
        params.add(dataRange[0]);
        params.add(dataRange[1]);
        if(CommonUtils.isNotEmpty(timeRange) && timeRange.length==2){
            sql += " and timerangestart >= ? and timerangeend <= ?";
            params.add(timeRange[0]);
            params.add(timeRange[1]);
        }
        Map<String,PersonWarn> map = new HashMap<>();
        List<PersonWarn> list = personWarnDao.findListWithSQL(sql,params.toArray());
        for (PersonWarn warn:list) {
            map.put(warn.getEntrancecardno(),warn);
        }
        return map;
    }

    public String subIds(String[] ids){
        String idss = "";
        for (String id:ids) {
            idss += ",'"+id+"'";
        }
        if(idss.length()>0) idss = idss.substring(1);
        return idss;
    }

    public static void main(String[] args) {
        String[] ids = new String[]{"0D3E1C02302C4494B937589C03338795","0FEA770B72C34ACBA1678FF2619F703B"};
        String idss = "";
        for (String id:ids) {
            idss += ",'"+id+"'";
        }
        if(idss.length()>0) idss = idss.substring(1);
        System.out.println(idss);
    }
}
