package com.xajiusuo.busi.villageMessage.service;

import com.xajiusuo.busi.villageMessage.entity.HouseLog;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:26
 * @Description 房屋租赁接口
 */
public interface HouseLogService extends BaseService<HouseLog,String>{
    /**
     *@Description: 房屋租赁息分页查询展示
     *@param: [pageable,unit]
     *@return: Page<Unit>
     *@Author: gaoyong
     *@date: 19-6-10 下午2:29
     */
    Page<HouseLog> getsqlpage(Pageable pageable, String buildingid, String unitid, String houseid, String idnumber) throws Exception;
    /**
     *@Description:看字段值是否有重复的
     *@param: [houseno,endtime]
     *@return: Integer
     *@Author: gaoyong
     *@date: 19-6-11 上午11:15
     */
    Long chekBs(String idNumber,String houseno,Date starttime,Date endtime) throws Exception;
    
    /**
     *@Description: 获取上传表格所需要的字典
     *@param: pid
     *@return: 
     *@Author: gaoyong
     *@date: 19-6-20 上午11:59
    */
     List getDictionForUpload(Integer pid);


    /**
     *@Description:
     *@param: 返回成功多少条，那些房号失败 身份证已重复
     *@return:
     *@Author: gaoyong
     *@date: 19-6-25 下午7:23
     */
    List saveUpdate(InputStream in) throws Exception;
    
    /**
     *@Description:
     *@param: 返回所有的在使用房屋
     *@return: 
     *@Author: gaoyong
     *@date: 19-7-8 下午3:42
    */
    Integer getCountHouseNo() throws Exception;
}
