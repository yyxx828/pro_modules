package com.xajiusuo.busi.user.service;

import com.xajiusuo.jpa.config.BaseService;
import com.xajiusuo.busi.user.entity.Operate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @author 杨勇
 * @Date 2018/3/12
 * @Description 权限管理接口
 */
public interface OperateService extends BaseService<Operate, Integer> {


    /***
     * 杨勇
     * 查询操作
     * @param entity
     * @param page
     * @return
     */
    Page<Operate> query(Operate entity, Pageable page);

    /***
     * 获取操作的组名(typeName)
     * @return
     */
    List<String> getOperTypenameList();


    Map<String,List<Operate>> operaterByGroupList(String typeName);
}
