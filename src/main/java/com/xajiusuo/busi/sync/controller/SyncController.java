package com.xajiusuo.busi.sync.controller;

import com.xajiusuo.busi.sync.entity.InDataLog;
import com.xajiusuo.busi.sync.entity.SyncConf;
import com.xajiusuo.busi.sync.service.InDataLogService;
import com.xajiusuo.busi.sync.service.MultiFileService;
import com.xajiusuo.busi.sync.service.SyncConfService;
import com.xajiusuo.busi.sync.vo.SyncConfFactory;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by 杨勇 on 19-7-2.
 */
@Slf4j
@Api(description = "数据同步管理")
@RestController
@RequestMapping(value = "api/village/sync")
public class SyncController extends BaseController {

    @Autowired
    private SyncConfService syncConfService;

    @Autowired
    private InDataLogService inDataLogService;



    /***
     * @Author 杨勇
     * @Description 分页查询同步配置表
     * @Date 19-7-2
     * @return
     */
    @ApiOperation(value = "分页查询同步配置表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
    })
    @RequestMapping(value = "/pageConf", method = RequestMethod.GET)
    public synchronized ResponseBean pageConf(Pageable pageable) {

        String hql = "from " + syncConfService.className();
        Page<SyncConf> page = syncConfService.findPageWithHQLnoBlack(pageable,hql,new Object[]{});
        return Result.QUERY_SUCCESS.toBean(page);
    }

    /***
     * @Author 杨勇
     * @Description 分页查询同步记录
     * @Date 19-7-3
     * @return
     */
    @ApiOperation(value = "分页查询同步记录", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
    })
    @RequestMapping(value = "/pageSyncLog/{confId}", method = RequestMethod.GET)
    public ResponseBean pageSyncLog(Pageable pageable,@PathVariable(name = "confId") Integer confId) {
        if(confId == 0) confId = null;
        String hql = MessageFormat.format("from {0} e where 1=1~ and syncConf.id = ?~",inDataLogService.className());
        Page<InDataLog> page = inDataLogService.findPageWithHQLnoBlack(pageable,hql,new Object[]{confId});
        return Result.QUERY_SUCCESS.toBean(page);
    }

    /***
     * @Author 杨勇
     * @Description 查询配置信息
     * @Date 19-7-2
     * @return
     */
    @ApiOperation(value = "查询配置信息", httpMethod = "GET")
    @RequestMapping(value = "/viewConf/{id}", method = RequestMethod.GET)
    public ResponseBean viewConf(@PathVariable(name = "id") Integer id) {
        return Result.QUERY_SUCCESS.toBean(syncConfService.getOne(id));
    }

    /***
     * @Author 杨勇
     * @Description 查询同步记录
     * @Date 19-7-2
     * @return
     */
    @ApiOperation(value = "查询同步记录", httpMethod = "GET")
    @RequestMapping(value = "/viewSyncLog/{id}", method = RequestMethod.GET)
    public ResponseBean viewSyncLog(@PathVariable(name = "id") String id) {
        return Result.QUERY_SUCCESS.toBean(inDataLogService.getOne(id));
    }

    /***
     * @Author 杨勇
     * @Description 保存修改配置信息
     * @Date 19-7-2
     * @return
     */
    @ApiOperation(value = "保存修改配置信息", httpMethod = "POST")
    @RequestMapping(value = "/saveConf", method = RequestMethod.POST)
    public ResponseBean saveConf(@RequestBody SyncConf entity) {

        List res = entity.check();
        if(res.size() > 0){
            return Result.SAVE_FAIL.toBean(res);
        }
        SyncConfFactory.remove(entity);
        return syncConfService.saveUpdate(entity).toBean();
    }

    /***
     * @Author 杨勇
     * @Description 启用同步配置
     * @Date 19-7-2
     * @return
     */
    @ApiOperation(value = "启用同步配置", httpMethod = "GET")
    @RequestMapping(value = "/runon/{id}", method = RequestMethod.GET)
    public ResponseBean runon(@PathVariable(name = "id") Integer id) {
        SyncConf entity = syncConfService.getOne(id);
        entity.setEnabled(true);
        syncConfService.update(entity);
        return Result.OPERATE_SUCCESS.toBean(entity);
    }

    /***
     * @Author 杨勇
     * @Description 停用同步配置
     * @Date 19-7-2
     * @return
     */
    @ApiOperation(value = "停用同步配置", httpMethod = "GET")
    @RequestMapping(value = "/runoff/{id}", method = RequestMethod.GET)
    public ResponseBean runoff(@PathVariable(name = "id") Integer id) {
        SyncConf entity = syncConfService.getOne(id);
        entity.setEnabled(false);
        syncConfService.update(entity);
        return Result.OPERATE_SUCCESS.toBean(entity);
    }


    /***
     * @Author 杨勇
     * @Description 彻底删除配置信息和记录
     * @Date 19-7-2
     * @return
     */
    @ApiOperation(value = "彻底删除配置信息和记录", httpMethod = "DELETE")
    @RequestMapping(value = "/destory/{id}", method = RequestMethod.DELETE)
    public ResponseBean destory(@PathVariable(name = "id") Integer id) {
        syncConfService.destoryConfAndLog(id);
        return Result.DELETE_SUCCESS.toBean(id);
    }


}