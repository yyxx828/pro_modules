package com.xajiusuo.busi.log.controller;

import com.xajiusuo.busi.log.entity.LogSys;
import com.xajiusuo.busi.log.service.LogSysService;
import com.xajiusuo.busi.user.service.UserinfoService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.down.FileContentType;
import com.xajiusuo.jpa.down.OutputStreamDown;
import com.xajiusuo.jpa.param.e.NoLog;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.P;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.ExcelUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/***
 * 杨勇 2018-12-03
 * 公共用户权限
 */
@Slf4j
@Api(description  = "日志系统")
@RestController
@RequestMapping(value = "/api/village/log")
@NoLog
public class LogsController extends BaseController {


    @Autowired
    private LogSysService logSysRepository;

    @Autowired
    private UserinfoService userinfoService;

    /**
     * @Author 杨勇
     * @Description 创建操作
     * @Date 2018/3/12 14:50
     * @return 操作
     */
    @ApiOperation(value = "查看执法日志", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "日志ID", paramType = "path", dataType = "string"),

    })
    @RequestMapping(value = "/viewLog/{id}", method = RequestMethod.GET)
    public ResponseBean viewLog(@PathVariable String id) {
        Object entity = logSysRepository.getOne(id);
        return Result.QUERY_SUCCESS.toBean(entity);
    }

    /***
     * @Author 杨勇
     * @Description 分页查询执法日志
     * @Date 2018/3/15 17:36
     * @param page 分页页码
     * @return 用户列表
     */
    @ApiOperation(value = "分页查询执法日志", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "start", value = "开始时间", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "end",  value = "结束时间", paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/logList", method = RequestMethod.GET)
    public ResponseBean logList(Pageable page, LogSys entity,String start, String end) {

        Page<LogSys> pager0 = logSysRepository.query(entity,page,start,end);
        return Result.QUERY_SUCCESS.toBean(pager0);
    }

    /***
     * @throws Exception 文件异常
     * @Author zlm
     * @Description 导出未删除并且启用的所有用户信息
     * @Date 2018/2/2 13:53
     */
    @ApiOperation(value = "日志导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "开始时间,格式yyyy-MM-dd", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "end",  value = "结束时间,格式yyyy-MM-dd", paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/logOutExport", method = RequestMethod.GET)
    @ResponseBody
    public void logOutExport(String start, String end) throws Exception {
        Integer type = 0;
        String temp = "select e.* from {0} e where e.logDate >= ? and e.logDate <= ? order by e.lastModifyTime DESC";
        String sql;
        List list = null;
        List<String> head = null;
        List<String> field = null;
        String sheet = null;

        Date endTime = P.U.dateParseYMD(end);
        endTime = endTime == null ? new Date() : endTime;
        Date startTime = P.U.dateParseYMD(start);
        startTime = startTime == null ? new Date(endTime.getTime() - 365 * 24 * 60 * 60 * 1000) : startTime;

        end = end == null ? "无" : end;
        start = start == null ? "无" :start;

        switch (type){
            case 0:
                sql = MessageFormat.format(temp,SqlUtils.tableName(LogSys.class));
                list = logSysRepository.executeNativeQuerySql(sql, startTime,endTime);
                head = Arrays.asList("序号","用户名称","操作时间","访问地址","部门名称","查询条件","模块名称","案件名称","操作描述","日志描述","系统消息");
                field = Arrays.asList("userName","logDate","ip","depName","ranges","itemName","caseName","operDesc","logDesc","sysDesc");
                sheet = "系统日志";
                break;
        }

        Workbook book = ExcelUtils.listToExcel(list, head,field,sheet,50000, true);
        String filename = MessageFormat.format("{0}(日期{1}-{2})[{3}条].xlsx",sheet,start,end,list.size() > 50000 ? 50000 : list.size());
        list.clear();

        fileDownOrShow(filename, FileContentType.xls, OutputStreamDown.to(out -> book.write(out)));

    }

}
