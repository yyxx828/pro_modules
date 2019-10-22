package com.xajiusuo.busi.villageMessage.service;

import com.xajiusuo.busi.villageMessage.entity.ParkingLog;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:26
 * @Description 车位租赁接口
 */
public interface ParkingLogService extends BaseService<ParkingLog,String>{
    /**
     *@Description: 车位租赁息分页查询展示
     *@param: [pageable,unit]
     *@return: Page<Unit>
     *@Author: gaoyong
     *@date: 19-6-10 下午2:29
     */
    Page<ParkingLog> getsqlpage(Pageable pageable, ParkingLog ParkingLog);
    /**
     *@Description:看字段值是否有重复的
     *@param: [sql]
     *@return: Integer
     *@Author: gaoyong
     *@date: 19-6-11 上午11:15
     */
    Long chekBs(String parkingno, Date starttime, Date endtime) throws Exception;
    
    /**
     *@Description: 获取上传表格所需要的字典
     *@param: pid
     *@return: 
     *@Author: gaoyong
     *@date: 19-6-20 上午11:59
    */
     List getDictionForUpload(Integer pid);
}
