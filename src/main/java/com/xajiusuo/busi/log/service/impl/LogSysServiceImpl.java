package com.xajiusuo.busi.log.service.impl;

import com.alibaba.fastjson.JSON;
import com.xajiusuo.busi.log.dao.LogSysDao;
import com.xajiusuo.busi.log.entity.LogSys;
import com.xajiusuo.busi.log.service.LogSysService;
import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.jpa.config.*;
import com.xajiusuo.jpa.config.e.BeanInit;
import com.xajiusuo.jpa.param.e.HasLog;
import com.xajiusuo.jpa.param.e.NoLog;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.param.entity.UserContainer;
import com.xajiusuo.jpa.util.P;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.UserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author zlm 2018/1/18
 *  角色逻辑接口实现类
 */
@Slf4j
@Service
public class LogSysServiceImpl extends BaseServiceImpl<LogSys, String> implements LogSysService {

    @Autowired
    LogSysDao logSysRepository;

    @Override
    public BaseDao<LogSys, String> getBaseDao() {
        return logSysRepository;
    }



    @Override
    public Page<LogSys> query(TableEntity entity, Pageable page, String start, String end) {
        List<Object> param = new ArrayList<Object>();
        param.add(P.U.dateParseYMD(start));
        param.add(P.U.dateParseYMD(end));
        if(param.get(1) != null){
            Date t = (Date) param.get(1);
            t.setTime(t.getTime() + 24 * 60 * 60 * 1000 - 1000);
        }
        StringBuilder sb = new StringBuilder();
        StringBuffer hql = new StringBuffer(MessageFormat.format("select e.* from {0} e where 1 = 1~ and e.logDate >= ?~~ and e.logDate <= ?~",SqlUtils.tableName(LogSys.class)));
//        SqlUtils.toHql0(param,entity,null,sb,null,null);
        hql.append(sb.toString());
        sb.delete(0,sb.length());
        Object[] os = SqlUtils.convertHSql(hql.toString(),sb,param.toArray());
        return logSysRepository.executeQuerySqlByPage(page,sb.toString(), os);
    }

    @Override
    public LogSys saveLog(LogSys entity, HttpServletRequest request) {
        //日志用户信息补全
        if(entity.getIp() == null){
            entity.setIp(UserUtil.getIpAddress(request));
        }
        logSysRepository.save(entity);
        return entity;
    }

    @Override
    public void batchSaveOrUpdate(List<LogSys> logs) {
        logSysRepository.batchSaveOrUpdate(logs);
    }


    @Override
    public void log(boolean err, HttpServletRequest request, HttpServletResponse response, ProceedingJoinPoint joinPoint, ResponseBean resBean) {
        //判断是否记录日志
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        if(!err){//非异常情况进行日志条件
            NoLog nl = joinPoint.getTarget().getClass().getAnnotation(NoLog.class);
            if(nl == null){
                nl = method.getAnnotation(NoLog.class);
            }
            if(nl == null){
                if(method.getAnnotation(HasLog.class) == null){
                    return;
                }
            }
        }

        Map<String, Object> map = UserContainer.info();//获取前端请求参数
        for(String key:map.keySet()){
            if(map.get(key) instanceof String && map.get(key).toString().length() > 200){
                map.put(key,((String) map.get(key)).substring(0,198) + "...");
            }
        }

        //定位请求的访问类型
        String type = null;//类型,如果有page则为查询

        Class<?> bClass = null;//参数包含对象的类型
        Object[] params = joinPoint.getArgs();
        LogSys logs = new LogSys();
        if(err){
            type = "异常";
            logs.setErrInfo(resBean.getData() + "");
            logs.setRowNums(0);
            logs.setStatus(type);
            logs.setMsg(type);
        }else{
            for(Object o:params){
                if(o instanceof BeanInit){
                    bClass = o.getClass();
                }else if(o instanceof Pageable){
                    type = "查询";
                    try{
                        if(Integer.parseInt(map.get("page").toString()) > 1){//分页非第一页排除
                            return;
                        }
                    }catch (Exception e){}
                    break;
                }
            }

            if(resBean != null){
                logs.setStatus(resBean.getStatusCode() == 200 ? "成功" : "失败");
                logs.setMsg(resBean.getMessage());
                Object o = resBean.getData();
                if(o instanceof PageImpl){
                    logs.setRowNums((int) ((PageImpl) o).getTotalElements());
                }else if( o instanceof BaseEntity){
                    logs.setRowNums(1);
                    logs.setEntityId(((BaseEntity) o).getId());
                }else if( o instanceof BaseIntEntity){
                    logs.setRowNums(1);
                    logs.setEntityId(((BaseIntEntity) o).getId() + "");
                }else if(o instanceof Collection){
                    logs.setRowNums(((Collection) o).size());
                }else if(o instanceof Map){
                    logs.setRowNums(((Map) o).size());
                }else{
                    logs.setRowNums(0);
                }
            }else{
                logs.setStatus("成功");
                logs.setMsg("无");
                logs.setRowNums(0);
            }
        }

        if(bClass != null){//对对象段解析
            Field[] dfs = bClass.getDeclaredFields();
            for(Field f:dfs){
                if(f.getName().equals("id")){
                    continue;
                }
                ApiModelProperty amp = f.getAnnotation(ApiModelProperty.class);
                if(amp != null && StringUtils.isNotBlank( amp.value()) && map.get(f.getName()) != null){
                    map.put(amp.value(),map.remove(f.getName()));
                }
            }
        }
        for(String f:new ArrayList<>(map.keySet())){
            if(map.get(f) instanceof Map && ((Map) map.get(f)).size() > 1){
                map.remove(f);
            }
        }
        logs.setEntityId((String) map.get("id"));
        logs.setUrl(request.getRequestURI());
        logs.setMethods(request.getMethod());
        logs.setParams(JSON.toJSON(map).toString().replace("\"",""));
        if(logs.getParams().length() >= 2000){
            logs.setParams(logs.getParams().substring(0,1996) + "...");
        }
        logs.setIp(UserUtil.getIpAddress(request));
        try{
            ApiOperation ao = method.getAnnotation(ApiOperation.class);
            logs.setOperDesc(ao.value());
        }catch (Exception e){}
        if(type == null){
            String mname = method.getName().toLowerCase() + "|" + request.getRequestURI().toLowerCase();
            if(mname.contains("save") || mname.contains("update") || mname.contains("add") || mname.contains("edit")){
                if(map.get("id") != null && ((map.get("id") instanceof String && StringUtils.isNotBlank( map.get("id").toString())) || (map.get("id") instanceof Number && ((Number) map.get("id")).intValue() != 0))){
                    type = "修改";
                }else{
                    type = "新增";
                }
            }else if(mname.contains("view") || mname.contains("get") || mname.contains("list") || mname.contains("page") || mname.contains("query")){
                type = "查询";
            }else if(mname.contains("delete") || logs.getMethods().toLowerCase().contains("delete")){
                type = "删除";
            } else {
                type = "操作";
            }
        }
        logs.setTypes(type);

        joinPoint.getSignature().getClass();
        try{
            logs.setItemTag(joinPoint.getTarget().getClass().getSimpleName().replace("Controller",""));
            logs.setItemName(joinPoint.getTarget().getClass().getAnnotation(Api.class).description());
        }catch (Exception e){}
        logs.setDelFlag(false);
        if(UserUtil.getCurrentUser(request) != null){
            logs.setCreateUID(UserContainer.getId());
            logs.setCreateName(UserContainer.getFullName());
            UserInfoVo user = UserUtil.getCurrentUser(request);
            logs.setDepId(user.getDepartmentId());
            logs.setDepName(user.getDepartmentName());
        }
        logSysRepository.save(logs);
    }

    /***
     * 访问信息输出
     * @param request
     * @param response
     */
    public void info(HttpServletRequest request, HttpServletResponse response){
        try{
            log.info(request.getRemoteAddr() + "\t" + request.getRequestURL() + "\t" + JSON.toJSON(UserContainer.info()).toString());
        }catch (Exception e){}
        UserContainer.remove();
        try{
            Result.responseBean().clear();
        }catch (Exception e){}
    }
}
