package com.xajiusuo.busi.control.service.impl;

import com.xajiusuo.busi.buildingaccess.entity.BuildingEntranceGuardLog;
import com.xajiusuo.busi.buildingaccess.service.BuildingEntranceGuardLogService;
import com.xajiusuo.busi.control.dao.ControlTaskDao;
import com.xajiusuo.busi.control.entity.ControlTask;
import com.xajiusuo.busi.control.entity.ControlTaskQueryVo;
import com.xajiusuo.busi.control.service.ControlTaskService;
import com.xajiusuo.busi.entranceandexit.entity.GateEntranceGuardLog;
import com.xajiusuo.busi.entranceandexit.service.GateEntranceGuardLogService;
import com.xajiusuo.busi.mail.send.conf.TipsConf;
import com.xajiusuo.busi.mail.service.MsgService;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleLog;
import com.xajiusuo.busi.motorVehicle.service.MotorVehicleLogService;
import com.xajiusuo.busi.wifilog.entity.WifiLog;
import com.xajiusuo.busi.wifilog.entity.WifiQueryVo;
import com.xajiusuo.busi.wifilog.service.WifiLogService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.param.e.Configs;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.DateUtils;
import com.xajiusuo.utils.CfI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by shirenjing on 2019/6/22.
 */
@Service
public class ControlTaskServiceImpl extends BaseServiceImpl<ControlTask,String> implements ControlTaskService {

    @Autowired
    private ControlTaskDao controlTaskDao;
    @Override
    public BaseDao<ControlTask, String> getBaseDao() {
        return controlTaskDao;
    }

    @Autowired
    private MsgService msgService; //消息提醒
    @Autowired
    private GateEntranceGuardLogService gateEntranceGuardLogService; //出入口门禁
    @Autowired
    private BuildingEntranceGuardLogService buildingEntranceGuardLogService; //楼宇门禁
    @Autowired
    private MotorVehicleLogService motorVehicleLogService; //停车场采集
    @Autowired
    private WifiLogService wifiLogService; // wifi探针信息采集

    @Override
    public Page<ControlTask> queryTaskPage(Pageable pageable, ControlTaskQueryVo controlTaskVo) {
        String sql = "select * from T_CONTROLTASK_16 t where t.delFlag = 0";
        if(controlTaskVo!=null){
            if(CommonUtils.isNotEmpty(controlTaskVo.getIdentify())){
                sql += " and id in(select distinct taskid from t_controlidentify_16 where identify like '%"+controlTaskVo.getIdentify()+"%' )";
            }
            if(CommonUtils.isNotEmpty(controlTaskVo.getTaskName())){
                sql += " and taskname like '%"+controlTaskVo.getTaskName()+"%'";
            }
            if(CommonUtils.isNotEmpty(controlTaskVo.getTaskType())){
                sql += " and controltype = "+controlTaskVo.getTaskType();
            }
            if(CommonUtils.isNotEmpty(controlTaskVo.getTaskstatus())){
                sql += " and controlstatus = "+controlTaskVo.getTaskstatus();
            }
            if(CommonUtils.isNotEmpty(controlTaskVo.getTaskTimes()) && controlTaskVo.getTaskTimes().length==2){
                sql += " and ((controlstarttime >= to_date('2019-06-22 08:16:02','yyyy-MM-dd HH24:mi:ss') and controlstarttime <= to_date('2019-06-22 08:16:02','yyyy-MM-dd HH24:mi:ss'))" +
                        " or (controlendtime >= to_date('2019-06-22 08:16:02','yyyy-MM-dd HH24:mi:ss') and controlendtime <= to_date('2019-06-22 08:16:02','yyyy-MM-dd HH24:mi:ss'))）";
            }
        }
        return controlTaskDao.executeQuerySqlByPage(pageable,sql,null);
    }

    @Override
    public ControlTask saveOrUpateTask(ControlTask controlTask) {
        return controlTaskDao.saveOrUpdate(controlTask);
    }

    @Override
    public void batchSave(List<ControlTask> tasks) {
        controlTaskDao.batchSaveOrUpdate(tasks);
    }

    @Override
    public List<ControlTask> queryTaskList(String current,String flag) {
        String sql = "select * from T_CONTROLTASK_16 where controlstatus = '0' and controlstarttime>=to_date('"+current+"','yyyy-MM-dd HH24:mi:ss') and controlendtime<=to_date('"+current+"','yyyy-MM-dd HH24:mi:ss')";
        if("nocontrol".equals(flag)){
            sql = "select * from T_CONTROLTASK_16 where controlstatus != '2' and controlendtime>=to_date('"+current+"','yyyy-MM-dd HH24:mi:ss')";
        }
        return controlTaskDao.executeNativeQuerySql(sql,null);
    }

    @Override
//    @Scheduled(cron = "3 * * * * ?")
    public void controlTaskSendMessge() {
        List<ControlTask> controlTasks = queryTaskList(DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"),"control"); //获取在控任务
        for(ControlTask task:controlTasks){
            if("1".equals(task.getControlType())){//1：人员布控
                gateMessge(task);  //出入口门禁
                buildMessge(task);  //楼宇门禁
            }else if("2".equals(task.getControlType())){//2：车辆布控
                carMessge(task);  //停车场采集信息
            }else {// 3:wifi布控
                wifiMessge(task);  //wifi探针采集信息
            }
        }
    }


    private void buildMessge(ControlTask task){
        BuildingEntranceGuardLog lastBuildLog = buildingEntranceGuardLogService.queryLastControltask(task.getId(),new String[]{DateUtils.format(task.getControlStartTime(),"yyyy-MM-dd HH:mm:ss"),
                DateUtils.format(task.getControlEndTime(),"yyyy-MM-dd HH:mm:ss")});
        String buildLogId = Configs.find(CfI.C_CATCH_BULID_MSGID).getValue();
        if(CommonUtils.isNotEmpty(buildLogId)){
            if(!lastBuildLog.getId().equals(buildLogId)){
                msgService.sendMessage(TipsConf.TIPS_BUILDCONTROL,task.getTaskName()+"任务有中标楼宇门禁数据",task.getId(),null,null,null);
                Configs.update(CfI.C_CATCH_BULID_MSGID,lastBuildLog.getId());
            }
        }
    }

    private void gateMessge(ControlTask task){
        GateEntranceGuardLog lastGateLog = gateEntranceGuardLogService.queryLastControltask(task.getId(),new String[]{DateUtils.format(task.getControlStartTime(),"yyyy-MM-dd HH:mm:ss"),
                DateUtils.format(task.getControlEndTime(),"yyyy-MM-dd HH:mm:ss")});
        String gateLogId = Configs.find(CfI.C_CATCH_GATE_MSGID).getValue();
        if(CommonUtils.isNotEmpty(gateLogId)){
            if(!lastGateLog.getId().equals(gateLogId)){
                msgService.sendMessage(TipsConf.TIPS_GATECONTROL,task.getTaskName()+"任务有中标出入口门禁数据",task.getId(),null,null,null);
                Configs.update(CfI.C_CATCH_GATE_MSGID,lastGateLog.getId());
            }
        }
    }

    private void carMessge(ControlTask task){
        MotorVehicleLog lastMotorVehicleLog = motorVehicleLogService.queryLastControltask(task.getId(),new String[]{DateUtils.format(task.getControlStartTime(),"yyyy-MM-dd HH:mm:ss"),
                DateUtils.format(task.getControlEndTime(),"yyyy-MM-dd HH:mm:ss")});
        String motorVehicleLogId = Configs.find(CfI.C_CATCH_CAR_MSGID).getValue();
        if(CommonUtils.isNotEmpty(motorVehicleLogId)){
            if(!lastMotorVehicleLog.getId().equals(motorVehicleLogId)){
                msgService.sendMessage(TipsConf.TIPS_CARCONTROL,task.getTaskName()+"任务有中标车辆采集数据",task.getId(),null,null,null);
                Configs.update(CfI.C_CATCH_CAR_MSGID,lastMotorVehicleLog.getId());
            }
        }
    }

    private void wifiMessge(ControlTask task){
        WifiQueryVo wifiQueryVo = new WifiQueryVo();
        wifiQueryVo.setPasstimes(new String[]{DateUtils.format(task.getControlStartTime(),"yyyy-MM-dd HH:mm:ss"),
                DateUtils.format(task.getControlEndTime(),"yyyy-MM-dd HH:mm:ss")});
        WifiLog lastWifiLog = wifiLogService.queryLastControltask(task.getId(),wifiQueryVo);
        String wifiLogId = Configs.find(CfI.C_CATCH_WIFI_MSGID).getValue();
        if(CommonUtils.isNotEmpty(wifiLogId)){
            if(!lastWifiLog.getId().equals(wifiLogId)){
                msgService.sendMessage(TipsConf.TIPS_WIFICONTROL,task.getTaskName()+"任务有中标wifi探针数据",task.getId(),null,null,null);
                Configs.update(CfI.C_CATCH_WIFI_MSGID,lastWifiLog.getId());
            }
        }
    }

}
