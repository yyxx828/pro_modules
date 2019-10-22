package com.xajiusuo.busi.motorVehicle.service;

import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleInfo;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleInfoVo;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleOwnerVo;
import com.xajiusuo.busi.motorVehicle.entity.MotorVehicleVo;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by shirenjing on 2019/6/15.
 */
public interface MotorVehicleInfoService extends BaseService<MotorVehicleInfo,String> {

    /**
     * @Author shirenjing
     * @Description 机动车基本信息查询列表
     * @Date 10:30 2019/6/15
     * @Param [pageable, motorVehicleInfoVo]
     * @return
     **/
    Page<MotorVehicleVo> queryPageInfo(Pageable  pageable, MotorVehicleInfoVo motorVehicleInfoVo);

    /**
     * @Author shirenjing
     * @Description 根据id查询并翻译
     * @Date 15:28 2019/6/15
     * @Param [id]
     * @return
     **/
    MotorVehicleInfo queryTranslateById(String id);

    /**
     * @Author shirenjing
     * @Description 根据车牌查询并翻译
     * @Date 15:28 2019/6/15
     * @Param [id]
     * @return
     **/
    MotorVehicleInfo queryTranslateByPlateNo(String plateNo);

    /**
     * @Author shirenjing
     * @Description 批量删除机动车基本信息
     * @Date 16:33 2019/6/15
     * @Param [ids]
     * @return
     **/
    void batchDeleteByid(String ids);

    /**
     * @Author shirenjing
     * @Description 根据指定字段查询未被删除的数据
     * @Date 17:22 2019/6/15
     * @Param [fieldName, fieldValue]
     * @return
     **/
    List<MotorVehicleInfo> queryByField(String fieldName,String fieldValue);

    /**
     * @Author shirenjing
     * @Description 机动车主信息查询
     * @Date 14:14 2019/6/20
     * @Param [fieldName, fieldValue]
     * @return
     **/
    List<MotorVehicleOwnerVo> queryMotorVehicleOwner(String fieldName, String fieldValue);

    /**
     * @Author shirenjing
     * @Description 统计机动车数量
     * @Date 10:55 2019/7/1
     * @Param []
     * @return
     **/
    Map<String,Integer> getMotorVehicleCount(String villageId);
    
    /**
     * @Author shirenjing
     * @Description 获取字典信息--excel下拉
     * @Date 13:59 2019/7/1
     * @Param [keys]
     * @return
     **/
    String[] getDictionForUpload(String  keys);


    /**
     * @Author shirenjing
     * @Description 导入车辆excel
     * @Date 16:08 2019/7/1
     * @Param [in]
     * @return
     **/
    List uploadCarExcel(InputStream in) throws Exception;

    /**
     * @Author shirenjing
     * @Description 获取所有的车辆编号
     * @Date 16:16 2019/7/1
     * @Param []
     * @return
     **/
    List<Object[]> getAllMotorVehicleIDs();

    /**
     * @Author shirenjing
     * @Description 获取所有的车牌号
     * @Date 16:16 2019/7/1
     * @Param []
     * @return
     **/
    List<Object[]> getAllPlateNo();

    /**
     * @Author shirenjing
     * @Description 根据车牌号模糊查询
     * @Date 16:57 2019/7/5
     * @Param [plageNo]
     * @return
     **/
    List<MotorVehicleInfo> queryInfoByPlateNo(String plageNo);
}
