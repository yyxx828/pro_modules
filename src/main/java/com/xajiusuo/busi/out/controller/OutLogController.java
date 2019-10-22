package com.xajiusuo.busi.out.controller;

import com.xajiusuo.busi.log.entity.LogSys;
import com.xajiusuo.busi.log.service.LogSysService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.NoLog;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.*;

/**
 * @Author zlm
 * @Date 2018/1/18
 */
@Slf4j
@Api(description = "对外接口")
@RestController
@RequestMapping(value="/api/out/log")
public class OutLogController extends BaseController{

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private LogSysService logSysRepository;

    /**
     * @Author 杨勇
     * @Description 创建操作
     * @Date 2018/3/12 14:50
     * @return 操作
     */
    @NoLog
    @ApiOperation(value = "新增系统日志", notes = "执法日志新增", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 500, message = "保存失败")
    })
    @PostMapping(value = "/saveLogSys")
    @ResponseBody
    public ResponseBean saveLogSys(@RequestBody LogSys entity) {
        entity = logSysRepository.saveLog(entity,request);
        return getResponseBean(Result.SAVE_SUCCESS.setData(entity));
    }

    public static void main(String[] args) {
        System.out.println(System.nanoTime());
        System.out.println(System.currentTimeMillis());
        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long tt1 = System.currentTimeMillis();
        long t1 = System.nanoTime();
        System.out.println(System.nanoTime() - t1);
        System.out.println(System.currentTimeMillis() - tt1);
        System.out.println(System.nanoTime());
        System.out.println(System.currentTimeMillis());
    }


}