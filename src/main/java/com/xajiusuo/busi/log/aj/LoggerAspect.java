package com.xajiusuo.busi.log.aj;

import com.xajiusuo.busi.log.service.LogSysService;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.jpa.util.RsaKeyUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hadoop on 19-9-4.
 */
@Aspect
@Component
public class LoggerAspect {

    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;

    @Autowired
    protected LogSysService logSysService;

    @Pointcut("@annotation(io.swagger.annotations.ApiOperation)")
    public void cutMethod(){
    }

    @Before("cutMethod()")
    public void begin(JoinPoint joinPoint){
        if(!request.getRequestURI().contains("api/param")){
            if(!RsaKeyUtil.authenticate()){
                throw new RuntimeException(Result.VALIDY_WARN.getMsg());
            }
        }
    }

    @AfterReturning("cutMethod()")
    public void afterReturning(JoinPoint joinPoint){
    }

    @After("cutMethod()")
    public void after(JoinPoint joinPoint){
    }

    /***
     * 方法异常日志调用
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "cutMethod()",throwing = "e")
    public void afterThrowing(JoinPoint joinPoint,Throwable e){
        if(RsaKeyUtil.authenticate() && joinPoint instanceof ProceedingJoinPoint){
            logSysService.eLog(request,response, (ProceedingJoinPoint) joinPoint, Result.VALIDY_WARN.toBean(e.getMessage()));
        }
    }

    /***
     * 方法正常日志调用
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("cutMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        Object result = joinPoint.proceed();
        {

        }
        logSysService.sLog(request,response, joinPoint, result instanceof ResponseBean ? (ResponseBean) result : null);
        return result;
    }

}
