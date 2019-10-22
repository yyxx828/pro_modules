package com.xajiusuo.busi.buildingaccess.service;

import com.xajiusuo.busi.buildingaccess.entity.BuildingEntranceGuardLog;
import com.xajiusuo.busi.buildingaccess.entity.ResultVo;
import com.xajiusuo.busi.entranceandexit.entity.GateEntranceGuardLog;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Created by wangdou on 2019/6/6
 */
public interface BuildingEntranceGuardLogService extends BaseService<BuildingEntranceGuardLog, String> {

    Page<BuildingEntranceGuardLog> query(BuildingEntranceGuardLog buildingEntranceGuardLog, Pageable page);

    Map<String, List<ResultVo>> tjBuildingEntranceGuard(String times, String flag);

    /**
     * @return
     * @Author shirenjing
     * @Description
     * @Date 11:24 2019/6/21
     * @Param [pageable, idCard, passtimes]
     **/
    Page<BuildingEntranceGuardLog> queryPageLog(Pageable pageable, String idCard, String[] passtimes);

    /**
     * @return
     * @Author shirenjing
     * @Description 频繁出入详单查询
     * @Date 11:24 2019/6/22
     * @Param [pageable, passtimes：数据范围, entrancecardno:门禁卡号]
     **/
    Page<Map> queryPageLogByOftenInOut(Pageable pageable, String[] passtimes, String entrancecardno);

    /**
     * @return
     * @Author shirenjing
     * @Description
     * @Date 11:25 2019/6/22
     * @Param [pageable, passtimes：数据范围, entrancecardno:门禁卡号, hours:夜出时段（0-4）]
     **/
    Page<Map> queryPageLogByNightOut(Pageable pageable, String[] passtimes, String entrancecardno, Integer[] hours);


    /**
     * @return
     * @Author shirenjing
     * @Description
     * @Date 18:18 2019/6/22
     * @Param [pageable, taskId:任务id, passtimes：数据范围, passtype:0出1进]
     **/
    Page<BuildingEntranceGuardLog> queryPageControltask(Pageable pageable, String taskId, String[] passtimes, String idcard, String passtype);

    /**
     * @return
     * @Author shirenjing
     * @Description 布控数据查询--最新一条中标数据
     * @Date 19:55 2019/6/24
     * @Param [taskId, passtimes]
     **/
    BuildingEntranceGuardLog queryLastControltask(String taskId, String[] passtimes);

    /**
     * @Author shirenjing
     * @Description 获取每个人最新一条数据(增量数据中所有人的数据) ---楼宇门禁
     * @Date 11:49 2019/7/4
     * @Param []
     * @return map key:idcard value:log
     **/
    Map<String,BuildingEntranceGuardLog> getLastByPerson(String villageid);

    /**
     * @Author shirenjing
     * @Description 获取每个人的增量数据---楼宇门禁
     * @Date 14:33 2019/7/4
     * @Param []
     * @return
     **/
    List<BuildingEntranceGuardLog> queryIncreamentByPerson(String villageid);


    List<ResultVo> tjBuildingEntranceGuardBydy(String buildingno, String time, String flag);


    List<Map<String, Object>> buildingEntranceGuardTimeDur(int pill, String time, String flag, String unitno);

    Page<BuildingEntranceGuardLog> queryBuildingEntranceGuardByTimeDur(Pageable pageables, String time, Integer[] hours,String flag, String unitno);

    Map getDefaultNumber();
}
