package com.xajiusuo.busi.villageMessage.service;

import com.sun.star.uno.Exception;
import com.xajiusuo.busi.villageMessage.entity.Village;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author GaoYong
 * @Date 19-6-5 下午2:18
 * @Description 小区基本信息逻辑逻辑处理接口
 */
public interface VillageService extends BaseService<Village,String> {
    /**
     *@Description: 小区信息分页查询展示
     *@param: [pageable,village]
     *@return: Page<Village>
     *@Author: gaoyong
     *@date: 19-6-10 下午2:29
    */
    Page<Village> getsqlpage(Pageable pageable, Village village) throws Exception;
    
    /**
     *@Description:看字段值是否有重复的
     *@param: [sql]
     *@return: Integer
     *@Author: gaoyong
     *@date: 19-6-11 上午11:15
    */
    Long chekBs(String bs,String bsValue) throws Exception;
}
