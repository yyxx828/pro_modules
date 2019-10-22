package com.xajiusuo.busi.user.service;

import com.xajiusuo.jpa.config.BaseService;
import com.xajiusuo.busi.user.entity.OperGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author 杨勇
 * @Date 2018/3/12
 * @Description 权限管理接口
 */
public interface OperGroupService extends BaseService<OperGroup, Integer> {

    /***
     * 杨勇
     * 查询权限组
     * @param entity
     * @param page
     * @return
     */
    Page<OperGroup> query(OperGroup entity, Pageable page);
}
