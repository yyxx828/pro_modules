package com.xajiusuo.busi.user.service;

import com.xajiusuo.jpa.config.BaseService;
import com.xajiusuo.busi.user.entity.Duty;
import com.xajiusuo.jpa.param.e.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

/**
 * @author zlm
 * @Date 2018/1/18
 * @Description 职位逻辑接口
 */
public interface DutyService extends BaseService<Duty, Integer> {

    /**
     * 查找晚于指定时间修改的职位
     * @param optTime 时间
     * @return 职位列表
     */
    List<Duty> findGeOptTime(Date optTime);

    /**
     * 职位分页查询接口
     * @param del 职位是否已经删除
     * @param pageable 分页对象
     * @return 职位分页
     */
    Page<Duty> findAllDutyByDel(Boolean del, Pageable pageable, Duty duty);
    /**
     * 职位分页查询接口
     * @param pageable 分页对象
     * @param duty 职位对象
     * @return 职位分页
     */
    Page<Duty> findAllDuty(Pageable pageable, Duty duty);

    /**
     * 职位的逻辑删除，某些条件下不能删除
     * @param duty 职位
     * @return 删除操作的提示信息
     */
    Result logicDelete(Duty duty);

    /**
     * 根据职位id获得职位
     * @param id id
     * @return 职位
     */
    Duty findById(Integer id);

    /**
     * 职位的高级查询
     * @param duty 职位
     * @param pageable 分页对象
     * @return 职位分页
     */
    Page<Duty> queryDuty(Duty duty, Pageable pageable);

    /**
     * 校验职位名称是否唯一
     * @param dutyname 职位名称
     * @param id 职位id
     * @return 唯一：true；不唯一：false
     */
    Boolean isValidDutyName(String dutyname, Integer id);

    /***
     * @Author zlm
     * @Description 根据删除状态查询职位列表
     * @Date 2018/2/26 15:57
     * @param del 删除状态
     * @return 职位list
     */
    List<Duty> findDutysByDel(Boolean del);
}
