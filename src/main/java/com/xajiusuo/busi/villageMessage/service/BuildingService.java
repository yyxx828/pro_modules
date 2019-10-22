package com.xajiusuo.busi.villageMessage.service;

import com.xajiusuo.busi.villageMessage.entity.Building;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:26
 * @Description 楼栋信息处理接口
 */
public interface BuildingService  extends BaseService<Building,String>{
    /**
     *@Description: 小区信息分页查询展示
     *@param: [pageable,village]
     *@return: Page<Village>
     *@Author: gaoyong
     *@date: 19-6-10 下午2:29
     */
    Page<Building> getsqlpage(Pageable pageable, Building building);
    /**
     *@Description:看字段值是否有重复的
     *@param: [sql]
     *@return: Integer
     *@Author: gaoyong
     *@date: 19-6-11 上午11:15
     */
    Long chekBs(String villageid,String bs,String bsValue) throws Exception;
    
    /**
     *@Description:小区楼栋下拉
     *@param: villageid
     *@return:list
     *@Author: gaoyong
     *@date: 19-6-25 下午3:13
    */
    List getSelectBulid(String villageid) throws Exception;

}
