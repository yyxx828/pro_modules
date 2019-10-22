package com.xajiusuo.busi.entranceandexit.service.impl;

import com.xajiusuo.busi.buildingaccess.entity.BuildingEntranceGuardLog;
import com.xajiusuo.busi.buildingaccess.entity.ResultVo;
import com.xajiusuo.busi.buildingaccess.service.BuildingEntranceGuardLogService;
import com.xajiusuo.busi.entranceandexit.dao.GateEntranceInoutDao;
import com.xajiusuo.busi.entranceandexit.entity.GateEntranceGuardLog;
import com.xajiusuo.busi.entranceandexit.entity.GateEntranceInOut;
import com.xajiusuo.busi.entranceandexit.entity.GuardCardCountVo;
import com.xajiusuo.busi.entranceandexit.entity.PersonWarn;
import com.xajiusuo.busi.entranceandexit.i.InOutBean;
import com.xajiusuo.busi.entranceandexit.service.GateEntranceGuardLogService;
import com.xajiusuo.busi.entranceandexit.service.GateEntranceInOutService;
import com.xajiusuo.busi.entranceandexit.service.PersonWarnService;
import com.xajiusuo.busi.entranceandexit.vo.PersonEarlyWarningVo;
import com.xajiusuo.busi.mail.send.conf.TipsConf;
import com.xajiusuo.busi.mail.service.MsgService;
import com.xajiusuo.busi.villageMessage.entity.Village;
import com.xajiusuo.busi.villageMessage.service.VillageService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.param.e.Configs;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.DateUtils;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.CfI;
import com.xajiusuo.utils.DateTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.*;

/**
 * Created by shirenjing on 2019/6/21.
 */
@Service
public class GateEntranceInOutServiceImple extends BaseServiceImpl<GateEntranceInOut, String> implements GateEntranceInOutService {

    @Autowired
    private GateEntranceInoutDao gateEntranceInoutDao;


    @Override
    public BaseDao<GateEntranceInOut, String> getBaseDao() {
        return gateEntranceInoutDao;
    }

    @Autowired
    private GateEntranceGuardLogService gateEntranceGuardLogService;//出入口门禁采集

    @Autowired
    private BuildingEntranceGuardLogService buildingEntranceGuardLogService;//楼宇门禁采集

    @Autowired
    private PersonWarnService personWarnService; //人员预警
    @Autowired
    private MsgService msgService; //消息提醒
    @Autowired
    private VillageService villageService;  //小区


    @Override
    public Page<Map> queryOftenInOut(Pageable pageable, String[] passTimes, Integer count, Integer flag) {
        return queryOftenAndNightInOut(pageable, passTimes, count, flag, null);
    }

    @Override
    public Page<Map> queryNightOut(Pageable pageable, String[] passTimes, Integer count, Integer flag, Integer[] hours) {
        return queryOftenAndNightInOut(pageable, passTimes, count, flag, hours);
    }

    @Override
    public List<PersonEarlyWarningVo> queryWarnNightOut(Date startTime, Date endTime, Integer count, Integer flag, Integer[] hours) {
        String sql = "select tab.*,t2.name pName,t1.personidnumber pIdCard from ( " +
                "select entrancecardno,count(1) nums,to_char(wm_concat(outid)) as ids from t_gateentranceinout_16 where flag = ? and status in ('1','3') and outdata >= ? and outdata <= ? ";
        if (hours[0] > hours[1]) {
            sql += " and (outtime >= ? or outtime <= ?) ";
        } else {
            sql += " and outtime >= ? and outtime <= ? ";
        }
        sql += " group by entrancecardno ) tab left join t_gateentrancecard_33 t1 on tab.entrancecardno=t1.entrancecardno " +
                " left join t_owner_19 t2 on t1.personidnumber = t2.idnumber where nums >=?";
        Object[] objArr = new Object[]{flag, startTime, endTime, hours[0]*60, hours[1]*60, count};
        try {
            return gateEntranceInoutDao.querySqlBean(sql, PersonEarlyWarningVo.class, objArr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<PersonEarlyWarningVo> queryWarnOftenInOut(Date startTime, Date endTime, Integer count, Integer flag) {
        String sql = "select tab.*,t2.name pName,t1.personidnumber pIdCard from ( " +
                "  select entrancecardno,count(1) nums,to_char(wm_concat(outid)) as ids from t_gateentranceinout_16 where flag = ?" +
                "  and indata >= ? and outdata <= ? group by entrancecardno " +
                "  ) tab left join t_gateentrancecard_33 t1 on tab.entrancecardno=t1.entrancecardno " +
                " left join t_owner_19 t2 on t1.personidnumber = t2.idnumber where nums >=?";
        Object[] objArr = new Object[]{flag, startTime, endTime, count};
        try {
            return gateEntranceInoutDao.querySqlBean(sql, PersonEarlyWarningVo.class, objArr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int deleteInOutsBylogIds(List<String> logIds) {
        String sql = "delete from T_GATEENTRANCEINOUT_16 where inid in ("+ SqlUtils.sqlIn(logIds)+") or outid in ("+ SqlUtils.sqlIn(logIds)+")";
        return gateEntranceInoutDao.executeUpdateSql(sql);
    }

    @Override
//    @Scheduled(cron = "0 */2 * * * ?")
    public void earlwarning() {
        /*-----------------昼伏夜出------------------*/
        nightOutEarlyWarning(0);//出入口门禁
        nightOutEarlyWarning(1);//楼宇门禁

        /*-----------------频繁出入------------------*/
        oftenOutInEarlyWarning(0);//出入口门禁
        oftenOutInEarlyWarning(1);//楼宇门禁
    }

    @Override
    public void gateLogIntegration() { //进出类型0出1进
        //获取当前小区
        Village village = villageService.getAll().get(0);

        //获取每个人历史数据中的最新一条出入数据(增量数据中所有人的数据)
        Map<String,GateEntranceGuardLog> historyMap = gateEntranceGuardLogService.getLastByPerson(village.getVillageid());
        //获取每个人的增量数据
        List<GateEntranceGuardLog> increments = gateEntranceGuardLogService.queryIncreamentByPerson(village.getVillageid());
        //过滤后
        List<GateEntranceGuardLog> incrementLogs = getIncreamentAndHistroyLogs(increments,historyMap);
        if(incrementLogs.size()>0){
            inOutData(false,incrementLogs);
        }

    }

    @Override
    public void buildLogIntegration() {
        //获取当前小区
        Village village = villageService.getAll().get(0);
        //获取每个人历史数据中的最新一条出入数据(增量数据中所有人的数据)
        Map<String, BuildingEntranceGuardLog> historyMap = buildingEntranceGuardLogService.getLastByPerson(village.getVillageid());
        //获取每个人的增量数据
        List<BuildingEntranceGuardLog> increments = buildingEntranceGuardLogService.queryIncreamentByPerson(village.getVillageid());
        //过滤后
        List<BuildingEntranceGuardLog> incrementLogs = getIncreamentAndHistroyLogs(increments,historyMap);
        if(incrementLogs.size()>0){
            inOutData(true,incrementLogs);
        }
    }

    private <E extends InOutBean> void inOutData(Boolean flag,List<E> incrementLogs){
        List<GateEntranceInOut> resultInOutList = new ArrayList<>();  //入库的进出信息
        E lastLog = null;  //上一条数据
        for(E log: incrementLogs){
            if(lastLog == null){
                lastLog = log;
            }else{
                if(lastLog.getEntrancecardno().equals(log.getEntrancecardno())){ //门禁卡号相同
                    if("1".equals(lastLog.getPasstype()) && "0".equals(log.getPasstype())){ //一进一出
                        resultInOutList.add(getGateEntranceInOut1(lastLog,log,flag));  //一进一出
                        lastLog = null;
                    }else{
                        resultInOutList.add(getGateEntranceInOut2(lastLog,flag));  //只进没有出 或 只出没有进
                        lastLog = log;
                    }
                }else{  //门禁卡号不同
                    if("0".equals(lastLog.getPasstype())){  //出
                        resultInOutList.add(getGateEntranceInOut2(lastLog,flag)); //只出没有进
                    }else{  //进
                        resultInOutList.add(getGateEntranceInOut0(lastLog,flag));  //进入未出
                    }
                    lastLog = log;
                }
            }
        }
        if(lastLog!=null){
            if("0".equals(lastLog.getPasstype())){  //出
                resultInOutList.add(getGateEntranceInOut2(lastLog,flag)); //只出没有进
            }else{  //进
                resultInOutList.add(getGateEntranceInOut0(lastLog,flag));  //进入未出
            }
        }
        if(resultInOutList.size()>0) gateEntranceInoutDao.batchSaveOrUpdate(resultInOutList);
    }



    /**
     * @Author shirenjing
     * @Description 一进一出
     * @Date 15:23 2019/7/4
     * @Param []
     * @return
     **/
    private <E extends InOutBean> GateEntranceInOut getGateEntranceInOut1(E inLog, E outLog, Boolean flag){
        GateEntranceInOut inOut = new GateEntranceInOut();
        inOut.setEntranceCardno(inLog.getEntrancecardno());
        inOut.setStatus(1);
        inOut.setIndata(inLog.getPasstime());
        inOut.setInid(inLog.getId());
        inOut.setIntime(getNumforTime(inLog.getPasstime()));
        inOut.setOutdata(outLog.getPasstime());
        inOut.setOutid(outLog.getId());
        inOut.setOuttime(getNumforTime(outLog.getPasstime()));
        inOut.setStoptime(getNumforInOut(inLog.getPasstime(),outLog.getPasstime()));
        inOut.setFlag(flag);
        return inOut;
    }


    /**
     * @Author shirenjing
     * @Description 非一进一出
     * @Date 15:29 2019/7/4
     * @Param [log]
     * @return
     **/
    private <E extends InOutBean> GateEntranceInOut getGateEntranceInOut2(E log,Boolean flag){
        GateEntranceInOut inOut = new GateEntranceInOut();
        inOut.setEntranceCardno(log.getEntrancecardno());
        if("0".equals(log.getPasstype())){ //出
            inOut.setStatus(3); //只出没有进
            inOut.setOutdata(log.getPasstime());
            inOut.setOutid(log.getId());
            inOut.setOuttime(getNumforTime(log.getPasstime()));
        }
        if("1".equals(log.getPasstype())){ //进
            inOut.setStatus(2);  //只进没有出
            inOut.setIndata(log.getPasstime());
            inOut.setInid(log.getId());
            inOut.setIntime(getNumforTime(log.getPasstime()));
        }
        inOut.setFlag(flag);
        return inOut;
    }

    /**
     * @Author shirenjing
     * @Description 进入未出
     * @Date 15:29 2019/7/4
     * @Param [log]
     * @return
     **/
    private <E extends InOutBean> GateEntranceInOut getGateEntranceInOut0(E log,Boolean flag){
        GateEntranceInOut inOut = new GateEntranceInOut();
        inOut.setEntranceCardno(log.getEntrancecardno());
        if("1".equals(log.getPasstype())){ //进
            inOut.setStatus(0);  //进入未出
            inOut.setIndata(log.getPasstime());
            inOut.setInid(log.getId());
            inOut.setIntime(getNumforTime(log.getPasstime()));
            inOut.setStoptime(getNumforInOut(log.getPasstime(),new Date()));
        }
        inOut.setFlag(flag);
        return inOut;
    }

    /**
     * @Author shirenjing
     * @Description 根据历史数据过滤新增增量数据
     * @Date 11:30 2019/7/5
     * @Param []
     * @return
     **/
    private <E extends InOutBean> List<E> getIncreamentAndHistroyLogs(List<E> incrementLogs, Map<String,E> historyLogMap){
        List<E> newList = new ArrayList<>();
        E historyLog = null;
        List<String> logIds = new ArrayList<>();
        if(incrementLogs!=null && incrementLogs.size()>0){
            for(E log:incrementLogs){
                if(historyLog == null){
                    if(historyLogMap.containsKey(log.getEntrancecardno())){
                        historyLog = historyLogMap.get(log.getEntrancecardno());
                        if("1".equals(historyLog.getPasstype())){ //进
                            newList.add(historyLog);
                            logIds.add(historyLog.getId());
                        }
                    }
                }else{
                    if(!historyLog.getEntrancecardno().equals(log.getEntrancecardno())){ //门禁卡号不同
                        if(historyLogMap.containsKey(log.getEntrancecardno())){
                            historyLog = historyLogMap.get(log.getEntrancecardno());
                            if("1".equals(historyLog.getPasstype())){ //进
                                newList.add(historyLog);
                                logIds.add(historyLog.getId());
                            }
                        }else{
                            historyLog = null;
                        }
                    }
                }

                if(historyLog!=null){//包含历史
                    if(historyLog.getPasstime().before(log.getPasstime())) newList.add(log);
                }else{//不包含历史
                    newList.add(log);
                }
            }
        }
        //删除历史数据中最后一条未进的数据
        if(logIds.size()>0){
            deleteInOutsBylogIds(logIds);
        }
        return newList;
    }


    /**
     * @Author shirenjing
     * @Description 昼伏夜出预警
     * @Date 11:54 2019/6/25
     * @Param []
     * @return
     **/
    private void nightOutEarlyWarning(Integer flag){

        int dataRange = Configs.find(CfI.C_EARLYWARN_PERSON_NIGHTOUTDUR_DATARANGE).getInt();
        String timeRange = Configs.find(CfI.C_EARLYWARN_PERSON_NIGHTOUTDUR_TIMERANGE).getValue();
        int nums = Configs.find(CfI.C_EARLYWARN_PERSON_NIGHTOUTDUR_NUMS).getInt();
        try {
            //条件
            String endTimeStr = DateUtils.format(new Date(), "yyyy-MM-dd");
            Date endTime = DateUtils.parse(endTimeStr + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
            String startTimeStr = DateUtils.dateAdds2s(endTimeStr, -dataRange);
            Date startTime = DateUtils.parse(startTimeStr + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
//            System.out.println("startTime:" + startTime + "......endTime:" + endTime);
            String[] hours = timeRange.split(",");
            Integer[] hs = new Integer[]{Integer.parseInt(hours[0]),Integer.parseInt(hours[1])};

            Map<String,PersonWarn> oldWarnMap = personWarnService.queryListPersonWarn("1",flag,new Date[]{startTime,endTime},hs);
            //获取中标数据
            List<PersonEarlyWarningVo> warningVoList = queryWarnNightOut(startTime,endTime,nums,flag,hs);
            if(warningVoList!=null && warningVoList.size()>0){  //有中标数据
                List<PersonWarn> personWarnList = new ArrayList<>();
                for (PersonEarlyWarningVo vo:warningVoList) {
                    PersonWarn personWarn = null;
                    if(oldWarnMap!=null && oldWarnMap.size()>0){
                        if(oldWarnMap.containsKey(vo.getEntrancecardno())){
                            PersonWarn oldWarn = oldWarnMap.get(vo.getEntrancecardno());
                            if(oldWarn.getNums() < vo.getNums()){
                                personWarn = oldWarn;
                                personWarn.setNums(vo.getNums());
                                personWarn.setIds(vo.getIds());
                            }
                        }else{
                            personWarn = new PersonWarn(vo.getEntrancecardno(),vo.getpIdCard(),vo.getpName(),vo.getNums(),
                                    vo.getIds(),startTime,endTime,hs[0],hs[1],"1",flag);
                        }
                    }else{
                        personWarn = new PersonWarn(vo.getEntrancecardno(),vo.getpIdCard(),vo.getpName(),vo.getNums(),
                                vo.getIds(),startTime,endTime,hs[0],hs[1],"1",flag);
                    }
                    if(personWarn!=null) personWarnList.add(personWarn);
                }
                if(personWarnList.size()>0){
                    String msg = "";
                    if(flag==0){ //出入口门禁
                        msg = "昼伏夜出预警有中标出入口门禁数据";
                    }
                    if(flag==1){ //楼宇门禁
                        msg = "昼伏夜出预警有中标楼宇门禁数据";
                    }
                    msgService.sendMessage(TipsConf.TIPS_PERSONWARN,msg,null,null,null,null);
                    personWarnService.batchSaveOrUpate(personWarnList); //保存预警数据
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Author shirenjing
     * @Description 频繁出入预警
     * @Date 11:54 2019/6/25
     * @Param []
     * @return
     **/
    public void oftenOutInEarlyWarning(Integer flag) {
        //条件
        int dataRange = Configs.find(CfI.C_EARLYWARN_PERSON_OFTENOUTIN_DATARANGE).getInt();
        int nums = Configs.find(CfI.C_EARLYWARN_PERSON_OFTENOUTIN_NUMS).getInt();
        try {

            String endTimeStr = DateUtils.format(new Date(),"yyyy-MM-dd");
            Date endTime = DateUtils.parse(endTimeStr+" 23:59:59","yyyy-MM-dd HH:mm:ss");
            String startTimeStr = DateUtils.dateAdds2s(endTimeStr,-dataRange);
            Date startTime = DateUtils.parse(startTimeStr+" 00:00:00","yyyy-MM-dd HH:mm:ss");
//            System.out.println("startTime:"+startTime+"......endTime:"+endTime);

            Map<String,PersonWarn> oldWarnMap = personWarnService.queryListPersonWarn("2",flag,new Date[]{startTime,endTime},null);
            //获取中标数据
            List<PersonEarlyWarningVo> warningVoList = queryWarnOftenInOut(startTime,endTime,nums,flag);
            if(warningVoList!=null && warningVoList.size()>0){
                List<PersonWarn> personWarnList = new ArrayList<>();
                for (PersonEarlyWarningVo vo:warningVoList) {
                    PersonWarn personWarn = null;
                    if(oldWarnMap!=null && oldWarnMap.size()>0){
                        if(oldWarnMap.containsKey(vo.getEntrancecardno())){
                            PersonWarn oldWarn = oldWarnMap.get(vo.getEntrancecardno());
                            if(oldWarn.getNums()< vo.getNums()){
                                personWarn = oldWarn;
                                personWarn.setNums(vo.getNums());
                                personWarn.setIds(vo.getIds());
                            }
                        }else{
                            personWarn = new PersonWarn(vo.getEntrancecardno(),vo.getpIdCard(),vo.getpName(),vo.getNums(),
                                    vo.getIds(),startTime,endTime,null,null,"2",flag);
                        }
                    }else{
                        personWarn = new PersonWarn(vo.getEntrancecardno(),vo.getpIdCard(),vo.getpName(),vo.getNums(),
                                vo.getIds(),startTime,endTime,null,null,"2",flag);
                    }
                    if(personWarnList!=null) personWarnList.add(personWarn);
                }
                if(personWarnList.size()>0){
                    String msg = "";
                    if(flag==0){ //出入口门禁
                        msg = "频繁出入预警有中标出入口门禁数据";
                    }
                    if(flag==1){ //楼宇门禁
                        msg = "频繁出入预警有中标楼宇门禁数据";
                    }
                    msgService.sendMessage(TipsConf.TIPS_PERSONWARN,msg,null,null,null,null);
                    personWarnService.batchSaveOrUpate(personWarnList); //保存预警数据
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * @Author shirenjing
     * @Description
     * @Date 12:36 2019/6/22
     * @Param [pageable, passTimes:数据范围, count次数, flag：0:出入口门禁 1：楼宇门禁]
     * @return
     **/
    public Page<Map> queryOftenAndNightInOut(Pageable pageable, String[] passTimes, Integer count, Integer flag, Integer[] hours) {
        StringBuilder sb = new StringBuilder("select tab.*,t2.name personname from ( ");
        sb.append("select t.entrancecardno,count(1) nums from t_gateentranceinout_16 t where t.outtime is not null and flag = 0 ");
        if (CommonUtils.isNotEmpty(passTimes) && passTimes.length == 2) {
            sb.append(" and (t.indata >= to_date('" + passTimes[0] + "','yyyy-MM-dd HH24:mi:ss') and t.indata<= to_date('" + passTimes[1] + "','yyyy-MM-dd HH24:mi:ss')) ");
            sb.append(" and (t.outdata >= to_date('" + passTimes[0] + "','yyyy-MM-dd HH24:mi:ss') and t.outdata<= to_date('" + passTimes[1] + "','yyyy-MM-dd HH24:mi:ss'))");
        }
        if (CommonUtils.isNotEmpty(hours) && hours.length == 2) {
            if (hours[0] > hours[1]) {
                sb.append(" and (t.outtime >= " + hours[0] * 60 + " or t.outtime <= " + hours[1] * 60 + ") ");
            } else {
                sb.append(" and t.outtime >= " + hours[0] * 60 + " and t.outtime <= " + hours[1] * 60 + " ");
            }
        }
        sb.append(" group by t.entrancecardno ) tab left join t_gateentrancecard_33 t1 on tab.entrancecardno=t1.entrancecardno " +
                " left join t_owner_19 t2 on t1.personidnumber = t2.idnumber where nums >=" + count + " order by nums desc");
        return gateEntranceInoutDao.queryPageSqlBean(pageable, sb.toString(), null);
    }


    public Page<Map<String, Object>> queryOftenAndNightInOut1(Pageable pageable, String[] passTimes, Integer count, Integer flag, Integer[] hours) {
        int pagenum = pageable.getPageNumber() + 1;
        int pagesize = pageable.getPageSize();
        StringBuilder countSb = new StringBuilder("select tr.*,rownum rn from (\n" +
                "select tab.*,t1.personname from ( \n" +
                "select t.entrancecardno,count(1) nums from t_gateentranceinout_16 t where t.outtime is not null and flag = " + flag);
        if (CommonUtils.isNotEmpty(passTimes) && passTimes.length == 2) {
            countSb.append(" and (t.indata >= to_date('" + passTimes[0] + "','yyyy-MM-dd HH24:mi:ss') and t.indata<= to_date('" + passTimes[1] + "','yyyy-MM-dd HH24:mi:ss')) ");
            countSb.append(" or (t.outdata >= to_date('" + passTimes[0] + "','yyyy-MM-dd HH24:mi:ss') and t.outdata<= to_date('" + passTimes[1] + "','yyyy-MM-dd HH24:mi:ss'))");
        }
        if (CommonUtils.isNotEmpty(hours) && hours.length == 2) {
            if (hours[0] > hours[1]) {
                countSb.append(" and (t.outtime >= " + hours[0] * 60 + " or t.outtime <= " + hours[1] * 60 + ") ");
            } else {
                countSb.append(" and t.outtime >= " + hours[0] * 60 + " and t.outtime <= " + hours[1] * 60 + " ");
            }
        }
        countSb.append("  group by t.entrancecardno ) tab left join t_gateentrancecard_33 t1 on tab.entrancecardno=t1.entrancecardno where nums >=" + count + " order by nums desc ) tr where 1=1");
        long total = gateEntranceInoutDao.count(countSb.toString(), null);
        StringBuilder sb = new StringBuilder("select tr1.* from ( ");
        sb.append(countSb);
        sb.append(" )tr1 where tr1.rn between " + ((pagenum - 1) * pagesize + 1) + " and " + pagenum * pagesize);
        List<Object[]> resultList = gateEntranceInoutDao.listBySQL(sb.toString(), null);
        return new PageImpl(resultList, pageable, total);
    }



    @Override
    public void text1() {


        //根据记录时间获取增量数据
        List<GateEntranceGuardLog> list = this.gateEntranceGuardLogService.executeNativeQuerySql("select * from T_GATEENTRANCEGUARDLOG_33 lo where lo.id not in (select io.inid from T_GATEENTRANCEINOUT_16 io where io.inid is not null) and lo.id not in (select io.outid from T_GATEENTRANCEINOUT_16 io where io.outid is not null)  order by entranceCardno desc ,passtime asc", null);

        List<GateEntranceInOut> insertList = new ArrayList<GateEntranceInOut>();
        List<GateEntranceInOut> historyList = null;
        String plateNo = "";
        GateEntranceInOut in = null;
        for (GateEntranceGuardLog mo : list) {

            if (plateNo.isEmpty()) {
                plateNo = mo.getEntrancecardno();
                //获取该车牌的历史数据
                historyList = this.gateEntranceInoutDao.executeNativeQuerySql("select tab.id,tab.entranceCardno,tab.intime,tab.outtime,tab.status,tab.inid,tab.outid,to_char(tab.indata,'yyyy-MM-dd HH24:mi:ss') indata,to_char(tab.outdata,'yyyy-MM-dd HH24:mi:ss') outdata,tab.flag from (\n" +
                        "                select distinct * from (\n" +
                        "                (select t.*,t.indata orderdate from T_GATEENTRANCEINOUT_16 t where t.indata is not null)\n" +
                        "                union\n" +
                        "                (select t.*,t.outdata orderdate from T_GATEENTRANCEINOUT_16 t where t.outdata is not null)\n" +
                        "                ) tt order by tt.orderdate desc\n" +
                        "                ) tab where tab.entranceCardno like '%" + plateNo + "%'");
                in = new GateEntranceInOut();
                in.setEntranceCardno(plateNo);

            }
            if (!plateNo.equals(mo.getEntrancecardno())) {

                if (null != in.getInid() || null != in.getOutid()) {
                    if (null != in.getInid()) {
                        in.setStatus(0);

                    } else {
                        in.setStatus(3);
                    }
                    insertList.add(in);
                    in = new GateEntranceInOut();
                }
                plateNo = mo.getEntrancecardno();
                historyList = this.gateEntranceInoutDao.executeNativeQuerySql("select * from (select tab.id,tab.entranceCardno,tab.intime,tab.outtime,tab.status,tab.inid,tab.outid,tab.indata,tab.outdata,tab.flag from (" +
                        "                select distinct * from (" +
                        "                (select t.*,t.indata orderdate from T_GATEENTRANCEINOUT_16 t where t.indata is not null)" +
                        "                union" +
                        "                (select t.*,t.outdata orderdate from T_GATEENTRANCEINOUT_16 t where t.outdata is not null)" +
                        "                ) tt order by tt.orderdate desc" +
                        "                ) tab where tab.entranceCardno like '%" + plateNo + "%')", null);
                in.setEntranceCardno(plateNo);
            }

            boolean isTrue = false;

            if (null != historyList && historyList.size() > 0) {
                //判断新数据是不是比历史数据中最后一条的时间还早
                if (isBefor(historyList.get(0).getIndata(), historyList.get(0).getOutdata(), mo.getPasstime())) {
                    GateEntranceInOut onleyOne = historyList.get(0);
                    //如果历史数据只有1条
                    if (historyList.size() == 1) {
                        //如果历史数据中没有进来的数据
                        if (null == onleyOne.getIndata()) {
                            //如果新数据是出去的数据（数据中passtype 0代表出，1代表进）
                            if (mo.getPasstype().equals("0")) {
                                insertNewValue(mo, in, 3, 0);
                            } else {
                                insertNewValue(mo, in, 1, 1);
                            }
                            in = new GateEntranceInOut();
                            in.setEntranceCardno(plateNo);
                            continue;
                        } else if (null != onleyOne.getInid() && null != onleyOne.getOutid() && isBetween(onleyOne.getIndata(), onleyOne.getOutdata(), mo.getPasstime())) {
                            //如果新数据时间在一条完整数据的进出之间，那么替换原来的进或者出，并且把历史数据中被替换的新加一条数据
                            if (mo.getPasstype().equals("0")) {
                                updateNewValue(onleyOne, in, 2, 0);
                                insertNewValue(mo, in, -1, 0);
                            } else {
                                updateNewValue(onleyOne, in, 3, 1);
                                insertNewValue(mo, in, -1, 1);
                            }
                            in = new GateEntranceInOut();
                            in.setEntranceCardno(plateNo);
                            continue;
                        } else {
                            if (mo.getPasstype().equals("0")) {
                                insertNewValue(mo, in, 3, 0);
                            } else {
                                insertNewValue(mo, in, 2, 1);
                            }
                        }


                        in = new GateEntranceInOut();
                        in.setEntranceCardno(plateNo);
                        continue;
                    } else {
                        for (int i = 0; i < historyList.size(); i++) {
                            GateEntranceInOut his = historyList.get(i);
                            if (i < historyList.size() - 1) {
                                if (null == his.getIndata()) {
                                    if (i + 1 < historyList.size()) {
                                        if (null == historyList.get(i + 1).getIndata() && isBetween(historyList.get(i + 1).getOutdata(), his.getOutdata(), mo.getPasstime())) {
                                            isTrue = true;
                                            if (mo.getPasstype().equals("0")) {
                                                insertNewValue(mo, in, 3, 0);
                                            } else {
                                                insertNewValue(mo, his, 1, 1);
                                            }
                                            in = new GateEntranceInOut();
                                            in.setEntranceCardno(plateNo);
                                            break;
                                        }
                                    }
                                } else if (null == his.getOutdata()) {
                                    if (null == historyList.get(i + 1).getIndata() && isBetween(historyList.get(i + 1).getOutdata(), his.getIndata(), mo.getPasstime())) {
                                        isTrue = true;
                                        if (mo.getPasstype().equals("0")) {
                                            insertNewValue(mo, in, 3, 0);

                                        } else {
                                            insertNewValue(mo, his, 2, 1);
                                        }
                                        in = new GateEntranceInOut();
                                        in.setEntranceCardno(plateNo);
                                        break;
                                    } else if (null == historyList.get(i + 1).getOutdata() && isBetween(historyList.get(i + 1).getIndata(), his.getIndata(), mo.getPasstime())) {
                                        isTrue = true;
                                        if (mo.getPasstype().equals("0")) {
                                            GateEntranceInOut nextOne = historyList.get(i + 1);
                                            insertNewValue(mo, nextOne, 1, 0);

                                        } else {
                                            insertNewValue(mo, historyList.get(0), 1, 1);
                                        }
                                        in = new GateEntranceInOut();
                                        in.setEntranceCardno(plateNo);
                                        break;
                                    }
                                } else {
                                    if (isBetween(his.getIndata(), his.getOutdata(), mo.getPasstime())) {
                                        isTrue = true;
                                        if (mo.getPasstype().equals("0")) {
                                            updateNewValue(his, in, 2, 0);
                                            insertNewValue(mo, his, -1, 0);
                                        } else {
                                            updateNewValue(his, in, 3, 1);
                                            insertNewValue(mo, his, -1, 1);
                                        }
                                        in = new GateEntranceInOut();
                                        in.setEntranceCardno(plateNo);
                                        break;
                                    }
                                }
                            } else {
                                if (null == his.getIndata()) {
                                    if (mo.getPasstype().equals("0")) {
                                        insertNewValue(mo, in, 3, 0);
                                    } else {
                                        insertNewValue(mo, his, 1, 1);
                                    }
                                    in = new GateEntranceInOut();
                                    in.setEntranceCardno(plateNo);
                                    break;
                                } else {
                                    if (mo.getPasstype().equals("0")) {
                                        insertNewValue(mo, in, 3, 0);
                                    } else {
                                        insertNewValue(mo, in, 2, 1);
                                    }
                                }
                                in = new GateEntranceInOut();
                                in.setEntranceCardno(plateNo);
                                break;
                            }

                        }
                    }

                } else {
                    GateEntranceInOut first = historyList.get(0);
                    if (first.getStatus() == 0 && mo.getPasstype().equals("0")) {
                        insertNewValue(mo, first, 1, 0);
                        isTrue = true;
                        in = new GateEntranceInOut();
                        in.setEntranceCardno(plateNo);
                    }
                }
            }
            if (isTrue) {
                historyList = this.gateEntranceInoutDao.executeNativeQuerySql("select tab.id,tab.entranceCardno,tab.intime,tab.outtime,tab.status,tab.inid,tab.outid,to_char(tab.indata,'yyyy-MM-dd HH24:mi:ss') indata,to_char(tab.outdata,'yyyy-MM-dd HH24:mi:ss') outdata,tab.flag from (\n" +
                        "                select distinct * from (\n" +
                        "                (select t.*,t.indata orderdate from T_GATEENTRANCEINOUT_16 t where t.indata is not null)\n" +
                        "                union\n" +
                        "                (select t.*,t.outdata orderdate from T_GATEENTRANCEINOUT_16 t where t.outdata is not null)\n" +
                        "                ) tt order by tt.orderdate desc\n" +
                        "                ) tab where tab.entranceCardno like '%" + plateNo + "%'", null);
                continue;
            }

            if (mo.getPasstype().equals("1")) {
                if (null != in.getInid()) {
                    in.setStatus(2);
                    insertList.add(in);
                    in = new GateEntranceInOut();
                    in.setIndata(mo.getPasstime());
                    in.setIntime(getNumforTime(mo.getPasstime()));
                    in.setInid(mo.getId());
                    in.setEntranceCardno(plateNo);
                } else if (null != in.getOutid()) {
                    in.setStatus(3);
                    insertList.add(in);
                    in = new GateEntranceInOut();
                    in.setIndata(mo.getPasstime());
                    in.setIntime(getNumforTime(mo.getPasstime()));
                    in.setInid(mo.getId());
                    in.setEntranceCardno(plateNo);
                } else {
                    in.setIndata(mo.getPasstime());
                    in.setIntime(getNumforTime(mo.getPasstime()));
                    in.setInid(mo.getId());
                }

            } else {
                if (null != in.getOutid()) {
                    in.setStatus(3);
                    insertList.add(in);
                    in = new GateEntranceInOut();
                    in.setOutdata(mo.getPasstime());
                    in.setOuttime(getNumforTime(mo.getPasstime()));
                    in.setOutid(mo.getId());
                    in.setEntranceCardno(plateNo);
                } else {
                    in.setOutdata(mo.getPasstime());
                    in.setOuttime(getNumforTime(mo.getPasstime()));
                    in.setOutid(mo.getId());
                }

            }

            if (null != in.getInid() && null != in.getOutid()) {
                in.setStatus(1);
                in.setStoptime(getNumforInOut(in.getIndata(), in.getOutdata()));
                insertList.add(in);
                plateNo = "";
            }

        }

        if (null == in.getInid() || null == in.getOutid()) {
            if (null != in.getInid()) {
                in.setStatus(0);
                insertList.add(in);
            } else {
                in.setStatus(3);
                insertList.add(in);
            }
        }
        //如果有新增数据入库
        if (null != insertList && insertList.size() > 1) {
            for (GateEntranceInOut m : insertList) {
                m.setFlag(false);
                this.gateEntranceInoutDao.save(m);
            }
        }

        //更新状态为0的数据停放时间
        List<GateEntranceInOut> stop = this.gateEntranceInoutDao.findBy("status", 0);
        for (GateEntranceInOut m : stop) {
            m.setStoptime(getNumforInOut(m.getIndata(), new Date()));
            m.setFlag(false);
            this.gateEntranceInoutDao.save(m);
        }
        System.out.println("批量比对定时任务发起成功！");

    }

    @Override
    public void text2() {
        //根据记录时间获取增量数据
        List<BuildingEntranceGuardLog> list = this.buildingEntranceGuardLogService.executeNativeQuerySql("select * from T_BUILDINGENTRANCEGUARDLOG_33 lo where lo.id not in (select io.inid from T_GATEENTRANCEINOUT_16 io where io.inid is not null) and id not in (select io.outid from T_GATEENTRANCEINOUT_16 io where io.outid is not null)  order by entranceCardno desc ,passtime asc", null);

        List<GateEntranceInOut> insertList = new ArrayList<GateEntranceInOut>();
        List<GateEntranceInOut> historyList = null;
        String plateNo = "";
        GateEntranceInOut in = null;
        for (BuildingEntranceGuardLog mo : list) {

            if (plateNo.isEmpty()) {
                plateNo = mo.getEntrancecardno();
                //获取该车牌的历史数据
                historyList = this.gateEntranceInoutDao.executeNativeQuerySql("select tab.id,tab.entranceCardno,tab.intime,tab.outtime,tab.status,tab.inid,tab.outid,to_char(tab.indata,'yyyy-MM-dd HH24:mi:ss') indata,to_char(tab.outdata,'yyyy-MM-dd HH24:mi:ss') outdata,tab.flag from (\n" +
                        "                select distinct * from (\n" +
                        "                (select t.*,t.indata orderdate from T_GATEENTRANCEINOUT_16 t where t.indata is not null)\n" +
                        "                union\n" +
                        "                (select t.*,t.outdata orderdate from T_GATEENTRANCEINOUT_16 t where t.outdata is not null)\n" +
                        "                ) tt order by tt.orderdate desc\n" +
                        "                ) tab where tab.entranceCardno like '%" + plateNo + "%'", null);
                in = new GateEntranceInOut();
                in.setEntranceCardno(plateNo);

            }
            if (!plateNo.equals(mo.getEntrancecardno())) {

                if (null != in.getInid() || null != in.getOutid()) {
                    if (null != in.getInid()) {
                        in.setStatus(0);

                    } else {
                        in.setStatus(3);
                    }
                    insertList.add(in);
                    in = new GateEntranceInOut();
                }
                plateNo = mo.getEntrancecardno();
                historyList = this.gateEntranceInoutDao.executeNativeQuerySql("select tab.id,tab.entranceCardno,tab.intime,tab.outtime,tab.status,tab.inid,tab.outid,to_char(tab.indata,'yyyy-MM-dd HH24:mi:ss') indata,to_char(tab.outdata,'yyyy-MM-dd HH24:mi:ss') outdata,tab.flag from (\n" +
                        "                select distinct * from (\n" +
                        "                (select t.*,t.indata orderdate from T_GATEENTRANCEINOUT_16 t where t.indata is not null)\n" +
                        "                union\n" +
                        "                (select t.*,t.outdata orderdate from T_GATEENTRANCEINOUT_16 t where t.outdata is not null)\n" +
                        "                ) tt order by tt.orderdate desc\n" +
                        "                ) tab where tab.entranceCardno like '%" + plateNo + "%'", null);
                in.setEntranceCardno(plateNo);
            }

            boolean isTrue = false;

            if (null != historyList && historyList.size() > 0) {
                //判断新数据是不是比历史数据中最后一条的时间还早
                if (isBefor(historyList.get(0).getIndata(), historyList.get(0).getOutdata(), mo.getPasstime())) {
                    GateEntranceInOut onleyOne = historyList.get(0);
                    //如果历史数据只有1条
                    if (historyList.size() == 1) {
                        //如果历史数据中没有进来的数据
                        if (null == onleyOne.getIndata()) {
                            //如果新数据是出去的数据（数据中passtype 0代表出，1代表进）
                            if (mo.getPasstype().equals("0")) {
                                insertNewValue(mo, in, 3, 0);
                            } else {
                                insertNewValue(mo, in, 1, 1);
                            }
                            in = new GateEntranceInOut();
                            in.setEntranceCardno(plateNo);
                            continue;
                        } else if (null != onleyOne.getInid() && null != onleyOne.getOutid() && isBetween(onleyOne.getIndata(), onleyOne.getOutdata(), mo.getPasstime())) {
                            //如果新数据时间在一条完整数据的进出之间，那么替换原来的进或者出，并且把历史数据中被替换的新加一条数据
                            if (mo.getPasstype().equals("0")) {
                                updateNewValue(onleyOne, in, 2, 0);
                                insertNewValue(mo, in, -1, 0);
                            } else {
                                updateNewValue(onleyOne, in, 3, 1);
                                insertNewValue(mo, in, -1, 1);
                            }
                            in = new GateEntranceInOut();
                            in.setEntranceCardno(plateNo);
                            continue;
                        } else {
                            if (mo.getPasstype().equals("0")) {
                                insertNewValue(mo, in, 3, 0);
                            } else {
                                insertNewValue(mo, in, 2, 1);
                            }
                        }


                        in = new GateEntranceInOut();
                        in.setEntranceCardno(plateNo);
                        continue;
                    } else {
                        for (int i = 0; i < historyList.size(); i++) {
                            GateEntranceInOut his = historyList.get(i);
                            if (i < historyList.size() - 1) {
                                if (null == his.getIndata()) {
                                    if (i + 1 < historyList.size()) {
                                        if (null == historyList.get(i + 1).getIndata() && isBetween(historyList.get(i + 1).getOutdata(), his.getOutdata(), mo.getPasstime())) {
                                            isTrue = true;
                                            if (mo.getPasstype().equals("0")) {
                                                insertNewValue(mo, in, 3, 0);
                                            } else {
                                                insertNewValue(mo, his, 1, 1);
                                            }
                                            in = new GateEntranceInOut();
                                            in.setEntranceCardno(plateNo);
                                            break;
                                        }
                                    }
                                } else if (null == his.getOutdata()) {
                                    if (null == historyList.get(i + 1).getIndata() && isBetween(historyList.get(i + 1).getOutdata(), his.getIndata(), mo.getPasstime())) {
                                        isTrue = true;
                                        if (mo.getPasstype().equals("0")) {
                                            insertNewValue(mo, in, 3, 0);

                                        } else {
                                            insertNewValue(mo, his, 2, 1);
                                        }
                                        in = new GateEntranceInOut();
                                        in.setEntranceCardno(plateNo);
                                        break;
                                    } else if (null == historyList.get(i + 1).getOutdata() && isBetween(historyList.get(i + 1).getIndata(), his.getIndata(), mo.getPasstime())) {
                                        isTrue = true;
                                        if (mo.getPasstype().equals("0")) {
                                            GateEntranceInOut nextOne = historyList.get(i + 1);
                                            insertNewValue(mo, nextOne, 1, 0);

                                        } else {
                                            insertNewValue(mo, historyList.get(0), 1, 1);
                                        }
                                        in = new GateEntranceInOut();
                                        in.setEntranceCardno(plateNo);
                                        break;
                                    }
                                } else {
                                    if (isBetween(his.getIndata(), his.getOutdata(), mo.getPasstime())) {
                                        isTrue = true;
                                        if (mo.getPasstype().equals("0")) {
                                            updateNewValue(his, in, 2, 0);
                                            insertNewValue(mo, his, -1, 0);
                                        } else {
                                            updateNewValue(his, in, 3, 1);
                                            insertNewValue(mo, his, -1, 1);
                                        }
                                        in = new GateEntranceInOut();
                                        in.setEntranceCardno(plateNo);
                                        break;
                                    }
                                }
                            } else {
                                if (null == his.getIndata()) {
                                    if (mo.getPasstype().equals("0")) {
                                        insertNewValue(mo, in, 3, 0);
                                    } else {
                                        insertNewValue(mo, his, 1, 1);
                                    }
                                    in = new GateEntranceInOut();
                                    in.setEntranceCardno(plateNo);
                                    break;
                                } else {
                                    if (mo.getPasstype().equals("0")) {
                                        insertNewValue(mo, in, 3, 0);
                                    } else {
                                        insertNewValue(mo, in, 2, 1);
                                    }
                                }
                                in = new GateEntranceInOut();
                                in.setEntranceCardno(plateNo);
                                break;
                            }

                        }
                    }

                } else {
                    GateEntranceInOut first = historyList.get(0);
                    if (first.getStatus() == 0 && mo.getPasstype().equals("0")) {
                        insertNewValue(mo, first, 1, 0);
                        isTrue = true;
                        in = new GateEntranceInOut();
                        in.setEntranceCardno(plateNo);
                    }
                }
            }
            if (isTrue) {
                historyList = this.gateEntranceInoutDao.executeNativeQuerySql("select tab.id,tab.entranceCardno,tab.intime,tab.outtime,tab.status,tab.inid,tab.outid,to_char(tab.indata,'yyyy-MM-dd HH24:mi:ss') indata,to_char(tab.outdata,'yyyy-MM-dd HH24:mi:ss') outdata,tab.flag from (\n" +
                        "                select distinct * from (\n" +
                        "                (select t.*,t.indata orderdate from T_GATEENTRANCEINOUT_16 t where t.indata is not null)\n" +
                        "                union\n" +
                        "                (select t.*,t.outdata orderdate from T_GATEENTRANCEINOUT_16 t where t.outdata is not null)\n" +
                        "                ) tt order by tt.orderdate desc\n" +
                        "                ) tab where tab.entranceCardno like '%" + plateNo + "%'", null);
                continue;
            }

            if (mo.getPasstype().equals("1")) {
                if (null != in.getInid()) {
                    in.setStatus(2);
                    insertList.add(in);
                    in = new GateEntranceInOut();
                    in.setIndata(mo.getPasstime());
                    in.setIntime(getNumforTime(mo.getPasstime()));
                    in.setInid(mo.getId());
                    in.setEntranceCardno(plateNo);
                } else if (null != in.getOutid()) {
                    in.setStatus(3);
                    insertList.add(in);
                    in = new GateEntranceInOut();
                    in.setIndata(mo.getPasstime());
                    in.setIntime(getNumforTime(mo.getPasstime()));
                    in.setInid(mo.getId());
                    in.setEntranceCardno(plateNo);
                } else {
                    in.setIndata(mo.getPasstime());
                    in.setIntime(getNumforTime(mo.getPasstime()));
                    in.setInid(mo.getId());
                }

            } else {
                if (null != in.getOutid()) {
                    in.setStatus(3);
                    insertList.add(in);
                    in = new GateEntranceInOut();
                    in.setOutdata(mo.getPasstime());
                    in.setOuttime(getNumforTime(mo.getPasstime()));
                    in.setOutid(mo.getId());
                    in.setEntranceCardno(plateNo);
                } else {
                    in.setOutdata(mo.getPasstime());
                    in.setOuttime(getNumforTime(mo.getPasstime()));
                    in.setOutid(mo.getId());
                }

            }

            if (null != in.getInid() && null != in.getOutid()) {
                in.setStatus(1);
                in.setStoptime(getNumforInOut(in.getIndata(), in.getOutdata()));
                insertList.add(in);
                plateNo = "";
            }

        }

        if (null == in.getInid() || null == in.getOutid()) {
            if (null != in.getInid()) {
                in.setStatus(0);
                insertList.add(in);
            } else {
                in.setStatus(3);
                insertList.add(in);
            }
        }
        //如果有新增数据入库
        if (null != insertList && insertList.size() > 1) {
            for (GateEntranceInOut m : insertList) {
                m.setFlag(false);
                this.gateEntranceInoutDao.save(m);
            }
        }

        //更新状态为0的数据停放时间
        List<GateEntranceInOut> stop = this.gateEntranceInoutDao.findBy("status", 0);
        for (GateEntranceInOut m : stop) {
            m.setStoptime(getNumforInOut(m.getIndata(), new Date()));
            m.setFlag(true);
            this.gateEntranceInoutDao.save(m);
        }
        System.out.println("批量比对定时任务发起成功！");
    }

    @Override
    public List countNightoutForBEG(String time) {
        int start = Configs.find(CfI.C_ACCESSCARD_NIGHTOUTDUR_START).getInt();
        int end = Configs.find(CfI.C_ACCESSCARD_NIGHTOUTDUR_END).getInt();
        StringBuilder sb = new StringBuilder("select t.*,c.name from (\n" +
                "select ENTRANCECARDNO ,count(1)  from " + tableName() + " where FLAG = 1 and outtime >= " + start * 60 + " and outtime <=" + end * 60);
        if (!StringUtils.isEmpty(time)) {
            String[] times = time.split(",");
            if (times.length == 1) {
                sb.append(" and indata >=to_date('" + times[0] + "','yyyy-MM-dd HH24:mi:ss') and indata <=to_date('" + times[0] + "','yyyy-MM-dd HH24:mi:ss')");
            }
            if (times.length == 2) {
                sb.append(" and outdata >=to_date('" + times[1] + "','yyyy-MM-dd HH24:mi:ss') and outdata <=to_date('" + times[1] + "','yyyy-MM-dd HH24:mi:ss')");
            }
        }
        sb.append(" group by ENTRANCECARDNO order by count(1) desc ) t " +
                "left join T_GATEENTRANCECARD_33 b on t.ENTRANCECARDNO = b.ENTRANCECARDNO left join T_OWNER_19 c on b.PERSONIDNUMBER = c.IDNUMBER  where rownum<11");
        return gateEntranceInoutDao.listBySQL(sb.toString());
    }


    public void insertNewValue(BuildingEntranceGuardLog old, GateEntranceInOut newMo, int status, int type) {
        if (type == 0) {
            newMo.setOutid(old.getId());
            newMo.setOutdata(old.getPasstime());
            newMo.setOuttime(getNumforTime(old.getPasstime()));
        } else {
            newMo.setInid(old.getId());
            newMo.setIndata(old.getPasstime());
            newMo.setIntime(getNumforTime(old.getPasstime()));
        }
        newMo.setStatus(status);
        if (status == 1) {
            newMo.setStoptime(getNumforInOut(newMo.getIndata(), newMo.getOutdata()));
        }
        newMo.setFlag(true);
        this.gateEntranceInoutDao.save(newMo);
    }


    public boolean isBefor(Date hisIn, Date hisOut, Date newtime) {
        boolean is = false;
        if (null != hisIn) {
            is = newtime.before(hisIn);
        }
        if (!is && null != hisOut) {
            is = newtime.before(hisOut);
        }
        return is;
    }

    public boolean isBetween(Date hisIn, Date hisOut, Date newtime) {
        boolean is = false;
        if (null != hisIn && null != hisOut) {
            is = newtime.after(hisIn) && newtime.before(hisOut);
        } else if (null != hisOut) {
            is = newtime.before(hisOut);
        } else if (null != hisIn) {
            is = newtime.after(hisIn);
        }
        return is;
    }

    public int getNumforTime(Date time) {
        return time.getHours() * 60 + time.getMinutes();
    }

    public int getNumforInOut(Date inTime, Date outTime) {
        return (int) ((outTime.getTime() - inTime.getTime()) / 3600000);
    }

    public void insertNewValue(GateEntranceGuardLog old, GateEntranceInOut newMo, int status, int type) {
        if (type == 0) {
            newMo.setOutid(old.getId());
            newMo.setOutdata(old.getPasstime());
            newMo.setOuttime(getNumforTime(old.getPasstime()));
        } else {
            newMo.setInid(old.getId());
            newMo.setIndata(old.getPasstime());
            newMo.setIntime(getNumforTime(old.getPasstime()));
        }
        newMo.setStatus(status);
        if (status == 1) {
            newMo.setStoptime(getNumforInOut(newMo.getIndata(), newMo.getOutdata()));
        }
        newMo.setFlag(false);
        this.gateEntranceInoutDao.save(newMo);
    }

    public void updateNewValue(GateEntranceInOut old, GateEntranceInOut newMo, int status, int type) {
        if (type == 0) {
            newMo.setOutid(old.getOutid());
            newMo.setOutdata(old.getOutdata());
            newMo.setOuttime(old.getOuttime());
        } else {
            newMo.setInid(old.getInid());
            newMo.setIndata(old.getIndata());
            newMo.setIntime(old.getIntime());
        }
        if (status != -1) {
            newMo.setStatus(status);
        }
        if (status == 1) {
            newMo.setStoptime(getNumforInOut(newMo.getIndata(), newMo.getOutdata()));
        }
        newMo.setFlag(false);
        this.gateEntranceInoutDao.save(newMo);
    }
}
