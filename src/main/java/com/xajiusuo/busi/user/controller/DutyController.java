package com.xajiusuo.busi.user.controller;

import com.xajiusuo.busi.user.entity.Depart;
import com.xajiusuo.busi.user.entity.Duty;
import com.xajiusuo.busi.user.service.DutyService;
import com.xajiusuo.busi.user.service.UserinfoService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.param.entity.UserContainer;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.utils.CfI;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 职位控制器
 *
 * @author zlm
 */
@Slf4j
@Api(description = "职位管理")
@RestController
@RequestMapping(value = "/api/village/duty")
public class DutyController extends BaseController {

    @Autowired
    private DutyService dutyService;

    @Autowired
    private UserinfoService userinfoService;

    @ApiOperation(value = "分页获得所有的职位", notes = "获得所有职位的各种详细信息包括所有字段，默认查询未删除的职位", produces = "application/json")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "dutyname", value = "职位名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "dlevel", value = "职位级别", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "del", value = "是否删除", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(id,asc)", example = "dutyname,asc", paramType = "query", dataType = "string")
    })
    @RequestMapping(value = "/allDuty", method = RequestMethod.GET)
    public ResponseBean allDuty(Pageable pageable, Duty duty) {
        String del = (String) UserContainer.info().get("delFlag");
        if ("true".equals(del)) {
            dutyService.queryRangeDelete();
        }
        Page<Duty> page = dutyService.findAllDuty(pageable, duty);
        return Result.QUERY_SUCCESS.toBean(page);
    }

    /**
     * @Author zlm
     * @Date 2018/2/26 16:10
     */
    @ApiOperation(value = "查询所有职位[DUTY-1]", notes = "查询所有未删除且已启用的职位", httpMethod = "GET")
    @RequestMapping(value = "/dutyAll", method = RequestMethod.GET)
    public ResponseBean dutyAll() {
        try {
            List<Duty> dutyList = dutyService.findDutysByDel(false);
            return Result.QUERY_SUCCESS.toBean(dutyList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }

    @ApiOperation(value = "新增或修改职位", notes = "职位的各种详细信息，新增时不要传id", httpMethod = "POST")
    @PostMapping(value = "/save")
    @ResponseBody
    public ResponseBean save(@RequestBody Duty duty) {
        if (dutyService.findSame("dutyname", duty.getDutyname(), duty.getId(), null)) {
            return Result.find(CfI.R_DEPART_DNAMEEXIST_FAIL).toBean();
        }
        return dutyService.saveUpdate(duty).toBean();

    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "删除职位", notes = "逻辑删除，只有当职位不存在子职位和用户时才能删除", httpMethod = "GET")
    @ResponseBody
    public ResponseBean delete(@ApiParam(value = "职位id", required = true, example = "00") @PathVariable(value = "id") Integer id, HttpServletRequest request) {
        Duty duty = dutyService.getOne(id);
        if (duty != null) {
            duty.setLastModifyUID(userinfoService.getCurrentUser(request).getId());
        }
        return dutyService.logicDelete(duty).toBean();
    }

    @ApiOperation(value = "恢复已经删除职位")
    @RequestMapping(value = "/recover/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean recover(@PathVariable(value = "id") Integer id, HttpServletRequest request) {
        Duty duty = dutyService.getOne(id);
        if (duty == null) {
            return Result.find(CfI.R_DUTY_NOTEXIST_FAIL).toBean();
        }
        if (dutyService.findSame("dutyname", duty.getDutyname(), duty.getId(), null)) {
            return Result.find(CfI.R_DEPART_RECONVERDNAMEEXIST_FAIL).toBean();
        }
        dutyService.recover(duty);
        return Result.OPERATE_SUCCESS.toBean(duty);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体", notes = "根据职位id查询未删除的职位实体，可用于查看职位", response = Depart.class)
    public ResponseBean getById(@ApiParam(value = "职位id", required = true, defaultValue = "00", example = "00") @PathVariable(value = "id") Integer id) {
        Duty duty = dutyService.findById(id);
        if (null == duty) {
            return Result.find(CfI.R_DUTY_NAMEEXIST_FAIL).toBean(duty);
        } else {
            return Result.QUERY_SUCCESS.toBean();
        }
    }

    @ApiOperation(value = "查询未删除的所有职位", notes = "查询职位列表")
    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseBean queryList() {
        List<Duty> list = dutyService.findDutysByDel(false);
        return Result.QUERY_SUCCESS.toBean(list);
    }

    @ApiOperation(value = "职位的查询接口", notes = "支持字符串域的多条件模糊查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string")
    })
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseBean query(Duty duty, Pageable pageable) {
        if (pageable.getSort() == null) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), new Sort(Sort.Direction.DESC, "id"));
        }
        Page<Duty> list = dutyService.queryDuty(duty, pageable);
        return Result.QUERY_SUCCESS.toBean(list);
    }

    @ApiOperation(value = "职位名称唯一性校验", notes = "新增或修改职位名称时使用，注意校验时不包含已删除的职位，已删除的职位会解除对该职位名称的占用")
    @GetMapping(value = "/validate/dutyname")
    public ResponseBean validateDname(@RequestParam(required = false) String dutyname, @RequestParam(required = false) Integer id) {
        if (dutyService.isValidDutyName(dutyname, id)) {
            return Result.find(CfI.R_DUTY_VALIDATE_USEDUTYNAME_SUCCESS).toBean();
        } else {
            return Result.find(CfI.R_DUTY_NAMEEXIST_FAIL).toBean();
        }
    }


}
