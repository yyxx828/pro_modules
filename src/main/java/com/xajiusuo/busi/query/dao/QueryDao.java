package com.xajiusuo.busi.query.dao;

import com.xajiusuo.busi.buildingaccess.entity.BuildingEntranceCard;
import com.xajiusuo.busi.buildingaccess.entity.BuildingEntranceGuardLog;
import com.xajiusuo.busi.entranceandexit.entity.GateEntranceCard;
import com.xajiusuo.busi.entranceandexit.entity.GateEntranceGuardLog;
import com.xajiusuo.busi.villageMessage.entity.Owner;
import com.xajiusuo.jpa.config.BaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/6/20.
 */
public interface QueryDao extends BaseDao<Owner, String>, JpaSpecificationExecutor<Owner> {

    @Query("select entrancecardno from BuildingEntranceCard b where b.personidnumber=?1")
    List<String> findBeglByPersonidnumber(String personidnumber);
    @Query("select entrancecardno from GateEntranceCard b where b.personidnumber=?1")
    List<String> findGeglByPersonidnumber(String personidnumber);

    @Query(value = "SELECT * FROM T_BUILDINGENTRANCEGUARDLOG_33 b where b.entrancecardno in (?1) order by ?#{#pageable}", nativeQuery = true,
            countQuery = "select count (*) from  T_BUILDINGENTRANCEGUARDLOG_33 b where b.entrancecardno in (?1)")
    Page<Map<String, Object>> findBeglByEntrancecardnos(List<String> entrancecardnos, Pageable pageable);

    @Query(value = "SELECT * FROM T_GATEENTRANCEGUARDLOG_33 g where g.entrancecardno in (?1)",
            countQuery = "select count (*) from  T_GATEENTRANCEGUARDLOG_33 g where g.entrancecardno in (?1) order by ?#{#pageable}"
            , nativeQuery = true)
    Page<Map<String, Object>> findGeglByEntrancecardnos(List<String> entrancecardnos, Pageable pageable);




}
