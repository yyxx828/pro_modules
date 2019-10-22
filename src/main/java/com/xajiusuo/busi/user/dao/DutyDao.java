package com.xajiusuo.busi.user.dao;

import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.busi.user.entity.Duty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

/**
 * @author zlm at 2018/1/18
 * zhiwu数据层
 */
public interface DutyDao extends BaseDao<Duty,Integer>,JpaSpecificationExecutor<Duty> {

    Page<Duty> findByDelFlag(Boolean del, Pageable pageable);
    Duty findByIdAndDelFlag(Integer id, boolean del);
    List<Duty> findByDutyname(String dutyname);
    List<Duty> findByDutynameAndIdNot(String dutyname, Integer id);
    List<Duty> findByLastModifyTimeGreaterThanEqual(Date optTime);
}
