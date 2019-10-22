package com.xajiusuo.busi.electronicFenceLog.service.iml;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xajiusuo.busi.electronicFenceLog.dao.ElectronicFenceLogDao;
import com.xajiusuo.busi.electronicFenceLog.entity.ElectronicFenceLog;
import com.xajiusuo.busi.electronicFenceLog.service.ElectronicFenceLogService;
import com.xajiusuo.busi.mail.send.conf.TipsConf;
import com.xajiusuo.busi.mail.service.MsgService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author GaoYong
 * @Date 19-6-12 上午11:54
 * @Description 周界报警信息逻辑处理接口
 */
@Service
public class ElectronicFenceLogServiceIml extends BaseServiceImpl<ElectronicFenceLog,String> implements ElectronicFenceLogService{

    private final Logger log = LoggerFactory.getLogger(ElectronicFenceLogServiceIml.class);

    @Autowired
    ElectronicFenceLogDao electronicFenceLogDao;

    @Autowired
    MsgService msgService;

    @Override
    public BaseDao<ElectronicFenceLog, String> getBaseDao() {
        return electronicFenceLogDao;
    }

    @Override
    public JSONArray countElectronicfencelog(String sql){
        JSONArray aa = new JSONArray();
        try {
            List<Object[]> list = this.electronicFenceLogDao.listBySQL(sql,null);
            for(Object [] o: list){
                Object [] a = o;
                JSONObject data = new JSONObject();
//                data.put("deviceid",a[0].toString());
                data.put("name",a[1].toString()+","+a[0].toString());
                data.put("value",a[2].toString());
                aa.add(data);
            }
            log.info("周界报警信息查询统计");
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return aa;
    };
    @Override
    public JSONObject countRegionTime(String sql,String timeBs,String timePill){
        JSONObject aa = new JSONObject();
        String timePillSql ="";
        try{
            switch (timeBs){
                case "y":
                    timePillSql = " to_char(alert_date,'MM')";
                    break;
                case "r":
                    timePillSql = " to_char(alert_date,'dd')";
                    break;
                case "z":
                    timePillSql = " to_number(trunc((to_number(to_char(alert_date,'ddd'))-1+to_number(to_char(to_date(to_char(alert_date,'yyyy')||'-01-01','yyyy-MM-dd')-1,'D'))-1)/7))+1";
                    break;
                case "x":
                    timePillSql = "to_number(trunc(to_number(to_char(alert_date,'HH24')-1)/"+Integer.parseInt(timePill)+"))*"+Integer.parseInt(timePill)+"+1||':00-'||(to_number(trunc(to_number(to_char(alert_date,'HH24')-1)/"+Integer.parseInt(timePill)+"))+1)*"+Integer.parseInt(timePill)+"||':59'";
                    break;
                default:

            }
           String  sqlResult = "select "+timePillSql+",count(*) from ("+sql+") where 1=1  group by "+timePillSql+"  order by "+timePillSql+" asc";
            List<Object []> list = this.electronicFenceLogDao.listBySQL(sqlResult,null);
            log.info("周界报警信息自定义时间段,区域,时间颗粒度查询");
            if(list.size()>0){
                for(Object[] o:list){
                    if(timeBs.equals("x")){
                        String  timeTotime = o[0].toString();
                        String str1 = timeTotime.split("-")[0];
                        String str2 = timeTotime.split("-")[1];
                        String [] _str2 = str2.split(":");
                        if(Integer.parseInt(_str2[0])>=24){
                            str2 = "23:59";
                            timeTotime =str1+"-"+str2;
                        }
                        aa.put(timeTotime,o[1].toString());
                    }else{
                        aa.put(o[0].toString(),o[1].toString());
                    };
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return aa;
    };



//    /***
//         * 消息发送
//         * 杨勇 19-4-2
//         * @param tipsConf 消息分类
//         * @param content 消息内容
//         * @param id 业务ID
//         * @param caseId
//         * @param must 强制提醒,登陆后接受消息
//         * @param sendUserId 发送人ID,不对该用户进行发送
//         * @param receiveUserId 接收人,如果空,则为全部人员
//         */
//       void sendMessage(TipsConf tipsConf.TIPS_TASK, String content, Object id, null, boolean must, Integer sendUserId, Integer... receiveUserId);


    @Override
    public  void getMessgage(){
        JSONObject result = new JSONObject();
        try {
            msgService.sendMessage(TipsConf.TIPS_ELECTRONICFENCELOG,"周界报警300条","id",null,true,null,null);
        }catch (Exception e){
            e.printStackTrace();
        }
    };
}
