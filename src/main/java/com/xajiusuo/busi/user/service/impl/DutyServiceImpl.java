package com.xajiusuo.busi.user.service.impl;

import com.xajiusuo.busi.user.dao.DutyDao;
import com.xajiusuo.busi.user.dao.UserinfoDao;
import com.xajiusuo.busi.user.entity.Duty;
import com.xajiusuo.busi.user.entity.Userinfo;
import com.xajiusuo.busi.user.service.DutyService;
import com.xajiusuo.jpa.config.BaseDao;
import com.xajiusuo.jpa.config.BaseServiceImpl;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.BeanUtils;
import com.xajiusuo.utils.CfI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * @author zlm 2018/1/18
 *         职位逻辑接口实现类
 */
@Service
public class DutyServiceImpl extends BaseServiceImpl<Duty, Integer> implements DutyService {
    @Autowired
    DutyDao dutyRepository;

    @Autowired
    UserinfoDao userInfoRepository;

    @Override
    public BaseDao<Duty, Integer> getBaseDao() {
        return dutyRepository;
    }

    @Override
    public List<Duty> findGeOptTime(Date optTime) {
        return this.dutyRepository.findByLastModifyTimeGreaterThanEqual(optTime);
    }

    @Override
    public Page<Duty> findAllDutyByDel(Boolean del, Pageable pageable, Duty duty) {
        String sql = BeanUtils.beanToSql(duty);
        return dutyRepository.executeQuerySqlByPage(pageable, sql);
    }

    @Override
    public Page<Duty> findAllDuty(Pageable pageable, Duty duty) {
        String sql = BeanUtils.beanToSql(duty, "delFlag");
        return dutyRepository.executeQuerySqlByPage(pageable, sql);
    }

    @Override
    public Result logicDelete(Duty duty) {
        try {
            if (null != duty) {
                List<Userinfo> userinfoList;
                userinfoList = userInfoRepository.findByDuty_IdAndDelFlagFalse(duty.getId());
                if (CommonUtils.isEmpty(userinfoList)) {
                    dutyRepository.delete(duty);
                    return Result.DELETE_SUCCESS.setData(duty);
                } else {
                    return Result.find(CfI.R_DUTY_EXISTUSER_FAIL);
                }
            } else {
                return Result.find(CfI.R_DUTY_NOTEXIST_FAIL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.DELETE_FAIL;
        }
    }

    @Override
    public Duty findById(Integer id) {
        return dutyRepository.findByIdAndDelFlag(id, false);
    }

    @Override
    public Page<Duty> queryDuty(Duty duty, Pageable pageable) {
        String sql = BeanUtils.beanToSql(duty);
        return dutyRepository.executeQuerySqlByPage(pageable, sql);
    }

    @Override
    public Boolean isValidDutyName(String dutyname, Integer id) {
        List duty;
        String sql = MessageFormat.format("select * from {0} e where e.dutyname = ?", SqlUtils.tableName(Duty.class));
        if (null == id) {
            duty = executeNativeQuerySql(sql, dutyname);
        } else {
            sql = MessageFormat.format("{0} and id <> ?", sql);
            duty = executeNativeQuerySql(sql, dutyname, id);
        }
        return CommonUtils.isEmpty(duty);
    }

    @Override
    public List<Duty> findDutysByDel(Boolean del) {
        return dutyRepository.findBy("delFlag", del);
    }
}
