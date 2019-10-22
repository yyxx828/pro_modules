package com.xajiusuo.busi.villageMessage.service;

import com.xajiusuo.busi.villageMessage.entity.Unit;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:26
 * @Description 单元信息处理接口
 */
public interface UnitService extends BaseService<Unit,String>{
    /**
     *@Description: 单元信息分页查询展示
     *@param: [pageable,unit]
     *@return: Page<Unit>
     *@Author: gaoyong
     *@date: 19-6-10 下午2:29
     */
    Page<Unit> getsqlpage(Pageable pageable, Unit unit);
    /**
     *@Description:看字段值是否有重复的
     *@param: [sql]
     *@return: Integer
     *@Author: gaoyong
     *@date: 19-6-11 上午11:15
     */
    Long chekBs(String villageid,String buildingid,String bs,String bsValue) throws Exception;
    
    /**
     *@Description:获取单元下拉
     *@param:
     *@return: 
     *@Author: gaoyong
     *@date: 19-6-25 下午4:01
    */
    List getSelectUnit(String villageid, String buildingid) throws Exception;
}
