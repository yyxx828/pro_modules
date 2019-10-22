package com.xajiusuo.busi.backupandops.controller;

import com.xajiusuo.busi.backupandops.entity.BackupVo;
import com.xajiusuo.busi.backupandops.entity.DelLogVo;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.busi.backupandops.backopsutils.BackopsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangdou on 2019/6/17
 */
@Slf4j
@Api(description = "系统备份管理")
@RestController
@RequestMapping(value = "/api/village/backup")
public class BackupController extends BaseController {

    @Value("${oracle.backuppath}")
    private String oraclebackuppath;

    /**
     * 备份记录查询
     *
     * @return
     */
    @ApiOperation(value = "备份记录查询", httpMethod = "GET")
    @RequestMapping(value = "/backupLog", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string"),
    })
    public ResponseBean backupLog(Pageable page) {
        try {
            List<BackupVo> ulist = new ArrayList<>();
            List<String> list;
            if (!BackopsUtil.operatingSys()) {
                list = BackopsUtil.readerTxt("D:\\backops\\oraclebackup\\dmp_history.log");
            } else {
                list = BackopsUtil.readerTxt(oraclebackuppath + "dmp_history.log");
            }
            for (int i = 1; i < list.size(); i++) {
                BackopsUtil.getResultList(ulist, list.get(i), list.get(0).split(":")[1], "back");
            }
            Page page1 = listToPage(ulist, page.getPageNumber(), page.getPageSize());
            return getResponseBean(Result.QUERY_SUCCESS.setData(page1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getResponseBean(Result.QUERY_FAIL);
    }

    /**
     * 删除记录查询
     *
     * @return
     */
    @ApiOperation(value = "删除记录查询", httpMethod = "GET")
    @RequestMapping(value = "/delLog", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string"),
    })
    public ResponseBean delLog(Pageable page) {
        try {
            List<DelLogVo> ulist = new ArrayList<>();
            List<String> list;
            if (!BackopsUtil.operatingSys()) {
                list = BackopsUtil.readerTxt("D:\\backops\\oraclebackup\\dmp_del.log");
            } else {
                list = BackopsUtil.readerTxt(oraclebackuppath + "dmp_del.log");
            }
            if (!CollectionUtils.isEmpty(list) && list.size() > 31) {
                for (int i = list.size() - 1; i > (list.size() - 31); i--) {
                    BackopsUtil.getResultList(ulist, list.get(i), "", "del");
                }
            } else {
                for (String s : list) {
                    BackopsUtil.getResultList(ulist, s, "", "del");
                }
            }
            Page page1 = listToPage(ulist, page.getPageNumber(), page.getPageSize());
            return getResponseBean(Result.QUERY_SUCCESS.setData(page1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getResponseBean(Result.QUERY_FAIL);
    }
}
