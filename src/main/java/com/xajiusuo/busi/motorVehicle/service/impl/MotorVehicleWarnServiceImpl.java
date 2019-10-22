package com.xajiusuo.busi.motorVehicle.service.impl;

import com.xajiusuo.busi.motorVehicle.dao.MotorVehicleLogDao;
import com.xajiusuo.busi.motorVehicle.dao.MotorVehicleWarnDao;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleLog;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleWarn;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleWarnVo;
import com.xajiusuo.busi.motorVehicle.service.MotorVehicleWarnService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.param.e.Configs;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.CfI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hadoop on 19-7-1.
 */
@Service
public class MotorVehicleWarnServiceImpl extends BaseServiceImpl<MotorVehicleWarn,String> implements MotorVehicleWarnService {

    @Autowired
    private MotorVehicleWarnDao motorVehicleWarnDao;
    @Autowired
    private MotorVehicleLogDao motorVehicleLogDao;
    @Override
    public BaseDao<MotorVehicleWarn, String> getBaseDao() {
        return this.motorVehicleWarnDao;
    }


    @Override
    public Map<String, Object> getOftenStopParams() {

        int nums = Configs.find(CfI.C_EARLYWARN_CAR_LONGTIMESTOP_DATARANGE).getInt();  //天数
        Map<String,Object> map = new HashMap<>();
        map.put("dataRange",nums);
        map.put("nums",nums);
        return map;
    }

    @Override
    public Map<String, Object> getOftenOutInParams() {
        int dataRange = Configs.find(CfI.C_EARLYWARN_CAR_OFTENOUTIN_DATARANGE).getInt();  //数据范围
        int nums = Configs.find(CfI.C_EARLYWARN_CAR_OFTENOUTIN_NUMS).getInt();  //次数
        Map<String,Object> map = new HashMap<>();
        map.put("dataRange",dataRange);
        map.put("nums",nums);
        return map;
    }

    @Override
    public Map<String, Object> getNightOutParams() {
        int dataRange = Configs.find(CfI.C_EARLYWARN_CAR_NIGHTOUTDUR_DATARANGE).getInt();  //数据范围
        String timeRange = Configs.find(CfI.C_EARLYWARN_CAR_NIGHTOUTDUR_TIMERANGE).getValue();  //时间范围
        int nums = Configs.find(CfI.C_EARLYWARN_CAR_NIGHTOUTDUR_NUMS).getInt();  //次数
        Map<String,Object> map = new HashMap<>();
        map.put("dataRange",dataRange);
        map.put("timeRange",timeRange);
        map.put("nums",nums);
        return map;
    }

    @Override
    public void updateOftenStopParams(int dataRange) {
        Configs.update(CfI.C_EARLYWARN_CAR_LONGTIMESTOP_DATARANGE,dataRange);  //天数
    }

    @Override
    public void updateOftenOutInParams(int dataRange, int nums) {
        Configs.update(CfI.C_EARLYWARN_CAR_OFTENOUTIN_DATARANGE,dataRange);  //数据范围
        Configs.update(CfI.C_EARLYWARN_CAR_OFTENOUTIN_NUMS,nums);  //次数
    }

    @Override
    public void updateNightOutParams(int dataRange, String timeRange, int nums) {
        Configs.update(CfI.C_EARLYWARN_CAR_NIGHTOUTDUR_DATARANGE,dataRange);  //数据范围
        Configs.update(CfI.C_EARLYWARN_CAR_NIGHTOUTDUR_TIMERANGE,timeRange);  //时间范围
        Configs.update(CfI.C_EARLYWARN_CAR_NIGHTOUTDUR_NUMS,nums);  //次数
    }

    @Override
    public Page<MotorVehicleWarn> queryPagePersonWarn(Pageable pageable, MotorVehicleWarnVo motorVehicleWarnVo) {

        String sql = "select * from "+tableName()+" where 1=1 ~ and analytype = ?~~ and plateno like ?~~ and nums >= ?~~ and datarangestart <= ?~~ and datarangeend <= ?~~ and timerangestart >= ?~~ and timerangeend <= ?~";
        List<Object> param = Arrays.asList(motorVehicleWarnVo.getAnalyType(),
                SqlUtils.sqlLike(motorVehicleWarnVo.getPlateno()),motorVehicleWarnVo.getNums(),
                motorVehicleWarnVo.getDataRangeStart(),motorVehicleWarnVo.getDataRangeEnd(),
                motorVehicleWarnVo.getTimeRangeStart(),motorVehicleWarnVo.getTimeRangeEend());
        return motorVehicleWarnDao.findPageWithSqlLikenoBlack(pageable,sql,param.toArray());
    }

    @Override
    public List<MotorVehicleLog> queryMotorVehicleByIds(String[] ids) {
        String sql="select * from "+tableName()+" where id in ("+SqlUtils.sqlIn(ids)+")";
        return motorVehicleLogDao.executeNativeQuerySql(sql,null);
    }


}
