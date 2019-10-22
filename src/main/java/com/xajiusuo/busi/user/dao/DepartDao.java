package com.xajiusuo.busi.user.dao;

import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.busi.user.entity.Depart;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * @author zlm
 * @Date 2018/1/18
 * @Description 机构数据层
 */
public interface DepartDao extends BaseDao<Depart,Integer>,JpaSpecificationExecutor<Depart> {

    Page<Depart> findByDelFlag(Boolean del, Pageable pageable);

    List<Depart> findByDelFlag(Boolean del);

    Depart findByParent_idNull();

    List<Depart> findByDname(String dname);

    List<Depart> findByDlevelAndDelFlag(Integer dlevel, Boolean del);

    Depart findByIdAndDelFlag(Integer id, Boolean del);

    Depart findByIdAndDelFlagFalse(Integer id);

    List<Depart> findByParent_idAndDelFlag(Integer parentid, Boolean del);

    List<Depart> findByParent_idAndDnameAndIdNot(Integer parentid, String dname,Integer id);

    List<Depart> findByParent_idAndDname(Integer parentid, String dname);

    Depart findByParent_id(Integer departId);

    /**
     * @Author zlm
     * @Description 查询时间段以后操作的所有数据
     * @Date 2018/2/7 8:59
     */
    List<Depart> findByLastModifyTimeGreaterThanEqual(Date optTime);

    List<Depart> findByDnameAndIdNot(String dname, Integer id);
}
