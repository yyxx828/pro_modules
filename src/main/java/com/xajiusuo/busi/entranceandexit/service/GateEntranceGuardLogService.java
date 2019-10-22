package com.xajiusuo.busi.entranceandexit.service;

import com.xajiusuo.busi.entranceandexit.entity.GateEntranceGuardLog;
import com.xajiusuo.busi.entranceandexit.entity.GuardCardCountVo;
import com.xajiusuo.busi.entranceandexit.entity.MjkAdnSkCount;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Created by wangdou on 2019/6/6
 */
public interface GateEntranceGuardLogService extends BaseService<GateEntranceGuardLog, String> {
    List<GuardCardCountVo> tjGateEntranceCard(String time, String flag) throws Exception;

    Page<GateEntranceGuardLog> query(GateEntranceGuardLog gateEntranceGuardLog, Pageable page);

    List<MjkAdnSkCount> tjMjkAdnSkCount();

    List<Map<String,Object>> gateEntranceCardTimeDur(int pill, String[] times);

    Page<GateEntranceGuardLog> queryGateEntranceCardByTimeDur(Pageable pageables, String[] dates,Integer[] hours);

    /**
     * @Author shirenjing
     * @Description 关注人员中出入口信息
     * @Date 10:39 2019/6/21
     * @Param []
     * @return
     **/
    Page<GateEntranceGuardLog> queryPageLog(Pageable pageable, String idCard, String[] passtimes);

    /**
     * @Author shirenjing
     * @Description 频繁出入详单查询
     * @Date 11:24 2019/6/22
     * @Param [pageable, passtimes：数据范围, entrancecardno:门禁卡号]
     * @return
     **/
    Page<Map> queryPageLogByOftenInOut(Pageable pageable,String[] passtimes,String entrancecardno);

    /**
     * @Author shirenjing
     * @Description 
     * @Date 11:25 2019/6/22
     * @Param [pageable, passtimes：数据范围, entrancecardno:门禁卡号, hours:夜出时段（0-4）]
     * @return
     **/
    Page<Map> queryPageLogByNightOut(Pageable pageable,String[] passtimes,String entrancecardno,Integer[] hours);

    /**
     * @Author shirenjing
     * @Description 
     * @Date 18:18 2019/6/22
     * @Param [pageable, taskId:任务id, passtimes：数据范围, passtype:0出1进]
     * @return
     **/
    Page<GateEntranceGuardLog> queryPageControltask(Pageable pageable,String taskId,String[] passtimes,String idcard,String passtype);


    /**
     * @Author shirenjing
     * @Description 布控数据查询--最新一条中标数据
     * @Date 19:55 2019/6/24
     * @Param [taskId, passtimes]
     * @return
     **/
    GateEntranceGuardLog queryLastControltask(String taskId, String[] passtimes);

    /**
     * @Author shirenjing
     * @Description 获取每个人最新一条数据(增量数据中所有人的数据) ---出入口门禁
     * @Date 11:49 2019/7/4
     * @Param []
     * @return map key:idcard value:log
     **/
    Map<String,GateEntranceGuardLog> getLastByPerson(String villageid);

    /**
     * @Author shirenjing
     * @Description 获取每个人的增量数据
     * @Date 14:33 2019/7/4
     * @Param []
     * @return
     **/
    List<GateEntranceGuardLog> queryIncreamentByPerson(String villageid);

    /**
     * @Author shirenjing
     * @Description 
     * @Date 16:20 2019/7/4
     * @Param [idcards]
     * @return
     **/
    List<Object[]> getLastIdsByPerson(String[] idcards);


    Map<String,List> nearly7daysByGate();

    Map<String,List> todayByGate();
}
