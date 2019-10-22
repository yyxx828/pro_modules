package com.xajiusuo.busi.villageMessage.service;

import com.alibaba.fastjson.JSONObject;
import com.xajiusuo.busi.villageMessage.entity.Owner;
import com.xajiusuo.jpa.config.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author GaoYong
 * @Date 19-6-10 下午5:26
 * @Description 业主信息处理接口
 */
public interface OwnerService extends BaseService<Owner,String>{
    /**
     *@Description: 业主信息分页查询展示
     *@param: [pageable,unit]
     *@return: Page<Unit>
     *@Author: gaoyong
     *@date: 19-6-10 下午2:29
     */
    Page<Owner> getsqlpage(Pageable pageable, Owner owner,String startTime,String endTime);
    /**
     *@Description:看字段值是否有重复的
     *@param: [sql]
     *@return: Integer
     *@Author: gaoyong
     *@date: 19-6-11 上午11:15
     */
    Long chekBs(String villageid,String bs,String bsValue) throws Exception;
    
    /**
     *@Description: 获取上传表格所需要的字典
     *@param: pid
     *@return: 
     *@Author: gaoyong
     *@date: 19-6-20 上午11:59
    */
    List getDictionForUpload(String keys) throws Exception;
    
    /**
     *@Description:选择所有人员信息
     *@param:
     *@return: 
     *@Author: gaoyong
     *@date: 19-6-26 上午10:36
    */
    List  getSelectOwner(String villageid,String nameOrIdNumber) throws Exception;
    /**
     *@Description:根据房屋选择人员信息
     *@param: 
     *@return: 
     *@Author: gaoyong
     *@date: 19-6-26 上午10:39
    */
    List getSelectHouseOwner(String villageid,String houseno) throws Exception;


    /**
     *@Description:
     *@param: 返回成功多少条，那些房号失败 身份证已重复
     *@return:
     *@Author: gaoyong
     *@date: 19-6-25 下午7:23
     */
    List saveUpdate(InputStream in) throws Exception;

    /**
     * @Author shirenjing
     * @Description 获取业主信息证件编号、姓名
     * @Date 14:11 2019/7/2
     * @Param []
     * @return
     **/
    Map<String,String> getOwnerInfO();
    /**
     *@Description:获取人口比例统计接口
     *@param:
     *@return: 
     *@Author: gaoyong
     *@date: 19-7-8 下午4:05
    */
    JSONObject getPeoplePercent(String ownerLocation) throws Exception;
    /**
     *@Description:统计身份类型不同的人
     *@param:
     *@return: 
     *@Author: gaoyong
     *@date: 19-7-9 下午3:32
    */
    Integer getPeopleType(String idType) throws Exception;
    /**
     *@Description:统计境外人口的数量
     *@param: 
     *@return: 
     *@Author: gaoyong
     *@date: 19-7-9 下午3:33
    */
    Integer getOverseasPeopleCount() throws Exception;
}
