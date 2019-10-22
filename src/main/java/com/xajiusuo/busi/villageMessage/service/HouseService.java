package com.xajiusuo.busi.villageMessage.service;

import com.xajiusuo.busi.villageMessage.entity.House;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:26
 * @Description 房屋信息处理接口
 */
public interface HouseService extends BaseService<House,String>{
    /**
     *@Description: 单元信息分页查询展示
     *@param: [pageable,unit]
     *@return: Page<Unit>
     *@Author: gaoyong
     *@date: 19-6-10 下午2:29
     */
    Page<House> getsqlpage(Pageable pageable, House house) throws Exception;
    /**
     *@Description:看字段值是否有重复的
     *@param: [sql]
     *@return: Integer
     *@Author: gaoyong
     *@date: 19-6-11 上午11:15
     */
    Long chekBs(String villageid,String buildingid,String unitid,String bs,String bsValue) throws Exception;

    /**
     *@Description:获取房屋下拉菜单
     *@param: 
     *@return: 
     *@Author: gaoyong
     *@date: 19-6-25 下午5:24
    */
    List getSelectHouse(String villageid,String buildingid,String unitid) throws Exception;
    
    /**
     *@Description: 获取上传表格所需要的字典
     *@param: pid
     *@return:
     *@Author: gaoyong
     *@date: 19-6-20 上午11:59
     */
    List getDictionForUpload(String keys) throws Exception;
    
    /**
     *@Description:
     *@param: 返回成功多少条，那些房号失败
     *@return: 
     *@Author: gaoyong
     *@date: 19-6-25 下午7:23
    */
    List saveUpdate(InputStream in) throws Exception;
}
