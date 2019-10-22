package com.xajiusuo.busi.log.service;

import com.alibaba.fastjson.JSON;
import com.xajiusuo.busi.log.entity.LogSys;
import com.xajiusuo.jpa.config.BaseService;
import com.xajiusuo.jpa.config.TableEntity;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.param.entity.UserContainer;
import com.xajiusuo.jpa.util.ResponseBean;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author 杨勇
 * @Date 2018/3/12
 * @Description 权限管理接口
 */
public interface LogSysService extends BaseService<LogSys, String> {

    /***
     * 查询执法日志
     * @param entity
     * @param page
     * @return
     */
    Page<LogSys> query(TableEntity entity, Pageable page, String start, String end);

    /***
     * 日志保存
     * @param entity
     * @param request
     */
    @Deprecated
    LogSys saveLog(LogSys entity, HttpServletRequest request);

    void batchSaveOrUpdate(List<LogSys> logs);

    /***
     * 杨勇 2019-09-05
     * 正常日志记录
     */
    default void sLog(HttpServletRequest request, HttpServletResponse response, ProceedingJoinPoint joinPoint, ResponseBean resBean){
        try {
            log(false,request,response,joinPoint, resBean);
        } catch (Exception e) {
        }
        info(request,response);
    }

    /***
     * 杨勇 2019-09-05
     * 异常日志记录
     */
    default void eLog(HttpServletRequest request, HttpServletResponse response, ProceedingJoinPoint joinPoint, ResponseBean resBean){
        try {
            log(true,request,response,joinPoint, resBean);
        } catch (Exception e) {
        }
        info(request,response);
    }

    void log(boolean err, HttpServletRequest request, HttpServletResponse response, ProceedingJoinPoint joinPoint, ResponseBean resBean);

    /***
     * 访问信息输出
     * @param request
     * @param response
     */
    void info(HttpServletRequest request, HttpServletResponse response);
}
