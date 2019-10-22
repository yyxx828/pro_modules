package com.xajiusuo.busi.user.controller;

import com.xajiusuo.busi.user.entity.Depart;
import com.xajiusuo.busi.user.entity.Duty;
import com.xajiusuo.busi.user.entity.OperGroup;
import com.xajiusuo.busi.user.entity.Userinfo;
import com.xajiusuo.busi.user.service.DepartService;
import com.xajiusuo.busi.user.service.DutyService;
import com.xajiusuo.busi.user.service.OperGroupService;
import com.xajiusuo.busi.user.service.UserinfoService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.down.FileContentType;
import com.xajiusuo.jpa.down.OutputStreamDown;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.param.entity.UserContainer;
import com.xajiusuo.jpa.util.CommonUtils;
import com.xajiusuo.jpa.util.MD5utils;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.CfI;
import com.xajiusuo.utils.ExcelUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialClob;
import java.io.InputStream;
import java.sql.Clob;
import java.text.MessageFormat;
import java.util.*;

/**
 * @Author zlm
 * @Date 2018/1/18
 * @Description 用户信息控制器
 */
@Slf4j
@Api(description = "用户管理")
@RestController
@RequestMapping(value = "/api/village/userinfo")
public class UserinfoController extends BaseController {

    @Autowired
    private UserinfoService userinfoService;
    @Autowired
    private DepartService departService;
    @Autowired
    private DutyService dutyService;
    @Autowired
    OperGroupService operGroupService;

    /***
     * @param page     分页页码
     * @param userinfo 用户
     * @return 用户列表
     * @Author zlm
     * @Description 分页查询所有用户
     * @Date 2018/2/27 17:20
     */
    @ApiOperation(value = "分页查询所有用户", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "username", value = "用户名", example = "zhangsan", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "fullname", value = "真实姓名", example = "张三", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "cardId", value = "身份证号码", example = "610124199110010011", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "policeNum", value = "警号", example = "041525", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "ulevel", value = "级别", example = "1", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sex", value = "性别(男：true  女：false)", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "depart.id", value = "机构id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "duty.id", value = "职位id", paramType = "query", dataType = "int"),
    })
    @RequestMapping(value = "/queryUserinfo", method = RequestMethod.GET)
    public ResponseBean queryUserinfo(Pageable page, Userinfo userinfo,HttpServletRequest request) {
        String del = (String) UserContainer.info().get("delFlag");
        if("true".equals(del)){
            userinfoService.queryRangeDelete();
        }
        Page<Userinfo> ulist = userinfoService.queryByAllDepart(userinfo, page, null);
        return Result.QUERY_SUCCESS.toBean(ulist);
    }


    /***
     * @param page     分页页码
     * @param userinfo 用户
     * @return 用户列表
     * @Author 杨勇
     * @Description 分页查询所有注册新用户列表
     * @Date 2019/5/9 16:20
     */
    @ApiOperation(value = "分页查询所有注册新用户列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "username", value = "用户名", example = "zhangsan", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "fullname", value = "真实姓名", example = "张三", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "cardId", value = "身份证号码", example = "610124199110010011", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "policeNum", value = "警号", example = "041525", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "ulevel", value = "级别", example = "1", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sex", value = "性别(男：true  女：false)", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "depart.id", value = "机构id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "duty.id", value = "职位id", paramType = "query", dataType = "int"),
    })
    @RequestMapping(value = "/registUserList", method = RequestMethod.GET)
    public ResponseBean registUserList(Pageable page, Userinfo userinfo) {
        Page<Userinfo> ulist = userinfoService.queryByAllDepart(userinfo, page, " and regflag is null");
        return Result.QUERY_SUCCESS.toBean(ulist);
    }

    /***
     * @Author 杨勇
     * @Description 用户审批
     * @Date 2018/2/27 17:20
     */
    @ApiOperation(value = "用户审批", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", required = true, value = "用户ID", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "type", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "departId", required = true, value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "dutyId", required = true, value = "用户名", example = "zhangsan", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "operGroupId", required = true, value = "真实姓名", example = "张三", paramType = "query", dataType = "string"),
    })
    @RequestMapping(value = "/approvalUser", method = RequestMethod.GET)
    public ResponseBean approvalUser(Integer userId, Integer type, Integer departId, Integer dutyId, Integer operGroupId) {
        Userinfo entity = userinfoService.getOne(userId);
        if (entity != null && entity.getId() != null) {
            if (entity.getRegflag() != null) {
                return Result.find(CfI.R_USER_NOTNEWUSER_FAIL).toBean();
            }
            if (type == 0) {
                entity.setRegflag(false);
            }

            if (type == 1) {
                entity.setRegflag(true);
                Depart depart = departService.getOne(departId);
                if (depart == null || depart.getId() == null) {
                    return Result.find(CfI.R_USER_EMPTYDEPT_FAIL).toBean();
                }
                Duty duty = dutyService.getOne(dutyId);
                if (duty == null || duty.getId() == null) {
                    return Result.find(CfI.R_USER_EMPTYDUTY_FAIL).toBean();
                }
                OperGroup operGroup = operGroupService.getOne(operGroupId);
                if (operGroup == null || operGroup.getId() == null) {
                    return Result.find(CfI.R_USER_EMPTYOPERGROUP_FAIL).toBean();
                }
                entity.setDepart(depart);
                entity.setDuty(duty);
                entity.setOperGroup(operGroup);
            }
            entity.setPassword(MD5utils.getMd5(entity.getPassword()));
            userinfoService.save(entity);
            return Result.QUERY_SUCCESS.toBean(entity);
        }
        return Result.find(CfI.R_USER_USERNOTEXIST_FAIL).toBean();
    }

    /***
     * @param userinfo 用户实体
     * @return 用户
     * @Author zlm
     * @Description 新增或者更新保存用户
     * @Date 2018/1/22 15:37
     */
    @ApiOperation(value = "新增/更新人员", httpMethod = "POST")
    @PostMapping(value = "/save")
    @ResponseBody
    public ResponseBean save(@RequestBody Userinfo userinfo) {
        try {
            if (userinfoService.isExist("username", userinfo.getUsername(), userinfo.getId())) {
                return Result.find(CfI.R_USER_USERNAMEEXIST_FAIL).toBean();

            }
            if (StringUtils.isNotBlank(userinfo.getPoliceNum()) && userinfoService.isExist("policeNum", userinfo.getPoliceNum(), userinfo.getId())) {
                return Result.find(CfI.R_USER_POLICENUMEXIST_FAIL).toBean();
            }
            if (userinfoService.isExist("cardId", userinfo.getCardId(), userinfo.getId())) {
                return Result.find(CfI.R_USER_CARDIDEXIST_FAIL).toBean();
            }
            if (userinfo.getDepartId() == null) {
                return Result.find(CfI.R_USER_EMPTYDEPT_FAIL).toBean();
            }
            if (userinfo.getDutyId() == null) {
                return Result.find(CfI.R_USER_EMPTYDUTY_FAIL).toBean();
            }
            if (userinfo.getOperGroupId() == null) {
                return Result.find(CfI.R_USER_EMPTYOPERGROUP_FAIL).toBean();
            }
            if (StringUtils.isBlank(userinfo.getFullname()) || userinfo.getFullname().replaceAll("[\u4e00-\u9ef5]", "**").length() > 24) {
                return Result.find(CfI.R_USER_USERNAME_FAIL).toBean();
            }
            if (StringUtils.isBlank(userinfo.getPassword())) {
                if (userinfo.getId() != null) {//修改时密码为空则设置为原密码
                    Userinfo u = userinfoService.getOne(userinfo.getId());
                    userinfo.setPassword(u.getPassword());
                } else {//写密码就加密修改
                    return Result.find(CfI.R_USER_PASSWORDEMPTY_FAIL).toBean();

                }
            } else {
                userinfo.setPassword(MD5utils.getMd5(userinfo.getPassword()));
            }
            if (userinfo.getId() == null) {
                userinfo.setCreateUID(userinfoService.getCurrentUser(request).getId());
                userinfo.setAble(true);
                userinfo.setDelFlag(false);
                userinfo.setRegflag(true);
            }
            if (userinfo.getAble() == null) {
                userinfo.setAble(true);
            }
            if (userinfo.getDelFlag() == null) {
                userinfo.setDelFlag(false);
            }
            userinfo.setLastModifyUID(userinfoService.getCurrentUser(request).getId());
            userinfo.setLastModifyTime(new Date());
            Depart d = departService.getOne(userinfo.getDepartId());
            Duty du = dutyService.getOne(userinfo.getDutyId());
            OperGroup og = operGroupService.getOne(userinfo.getOperGroupId());
            userinfo.setOperGroup(og);
            userinfo.setDepart(d);
            userinfo.setDuty(du);
            //签章信息
            if (!(userinfo.getGzcodeStr().isEmpty())) {
                if (StringUtils.isNotBlank(userinfo.getGzcodeStr()) && userinfo.getGzcodeStr().trim().length() > 0) {
                    Clob c1 = new SerialClob(userinfo.getGzcodeStr().toCharArray());
                    userinfo.setGzCode(c1);
                }
            }
            if (!(userinfo.getQmcodeStr().isEmpty())) {
                if (StringUtils.isNotBlank(userinfo.getQmcodeStr()) && userinfo.getQmcodeStr().trim().length() > 0) {
                    Clob c2 = new SerialClob(userinfo.getQmcodeStr().toCharArray());
                    userinfo.setQmCode(c2);
                }
            }
            Userinfo old = userinfoService.getOne(userinfo.getId());
            if (null != old) {
                userinfo.setRegflag(old.getRegflag());
            }
            userinfoService.saveOrUpdate(userinfo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.SAVE_FAIL.toBean();
        }
        return Result.SAVE_SUCCESS.toBean(userinfo);
    }

    /***
     * @param request
     * @param oldPwd     旧密码
     * @param newPwd     要修改的新密码
     * @param confirmPwd 确认密码
     * @return
     * @Author zlm
     * @Description
     * @Date 2018/4/2 11:54
     */
    @ApiOperation(value = "修改更新用户密码", httpMethod = "POST")
    @PostMapping(value = "/updatePwd")
    @ResponseBody
    public ResponseBean updatePwd(HttpServletRequest request, String oldPwd, String newPwd, String confirmPwd) {
        String msg = "";
        if (userinfoService.getCurrentUser(request).getId() == null) {
            return Result.NOPOWER_FAIL.toBean();
        }
        if (StringUtils.isBlank(oldPwd)) {
            return Result.find(CfI.R_USER_EMPTYOLD_FAIL).toBean();
        } else if (StringUtils.isBlank(newPwd) || StringUtils.isBlank(confirmPwd)) {
            return Result.find(CfI.R_USER_PASSWORDEMPTY_FAIL).toBean();
        } else if (!newPwd.equals(confirmPwd)) {
            return Result.find(CfI.R_USER_NOTEQ_FAIL).toBean();
        } else if (!(userinfoService.getCurrentUser(request).getPassword()).equals(MD5utils.getMd5(oldPwd))) {
            return Result.find(CfI.R_USER_PASSWORD_FAIL).toBean();
        } else if ((userinfoService.getCurrentUser(request).getPassword()).equals(MD5utils.getMd5(newPwd))) {
            return Result.find(CfI.R_USER_PWDSAME_FAIL).toBean();
        } else {
            Userinfo user = userinfoService.getCurrentUser(request);
            user.setPassword(MD5utils.getMd5(newPwd));
            user.setLastModifyTime(new Date());
            userinfoService.save(user);
            return Result.find(CfI.R_USER_PASSWORDCHANGE_SUCCESS).toBean();
        }
    }

    /***
     * @param id 用户id
     * @return 用户
     * @Author zlm
     * @Description 删除用户（逻辑删除）
     * @Date 2018/1/29 10:51
     */
    @ApiOperation(value = "删除用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "请输入用户id", paramType = "path", dataType = "int"),
    })
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseBean delete(@PathVariable int id) {
        Userinfo curUser = userinfoService.getCurrentUser(request);
        if (curUser == null || curUser.getId() == null) {
            return Result.find(CfI.R_USER_OPERATEADMIN_FAIL).toBean();
        }

        try {
            Userinfo u = userinfoService.getOne(id);
            if (u.getUsername().equals("admin")) {
                return Result.find(CfI.R_USER_OPERATEADMIN_FAIL).toBean();
            }
            userinfoService.delete(u);
            return Result.DELETE_SUCCESS.toBean();
        } catch (Exception e) {
            return Result.DELETE_FAIL.toBean();
        }
    }

    /***
     * @param id 用户id
     * @return 用户
     * @Author zlm
     * @Description 禁用/启用 用户
     * @Date 2018/2/1 16:52
     */
    @ApiOperation(value = "禁用/启用用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "请输入用户id", paramType = "path", dataType = "int"),
    })
    @RequestMapping(value = "/ableSwitch/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean ableSwitch(@PathVariable int id, HttpServletRequest request) {
        Userinfo u = userinfoService.getOne(id);
        try {
            if (u.getUsername().equals("admin") && u.getAble()) {
                return Result.find(CfI.R_USER_OPERATEADMIN_FAIL).toBean(u);
            }
            u.setAble(!u.getAble());
            u.setLastModifyUID(userinfoService.getCurrentUser(request).getId());
            u.setLastModifyTime(new Date());
            userinfoService.save(u);
            return Result.OPERATE_SUCCESS.toBean(u);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.OPERATE_FAIL.toBean(u);
        }
    }

    /***
     * @param id 用户id
     * @return 用户
     * @Author zlm
     * @Description 恢复已删除用户
     * @Date 2018/2/1 11:33
     */
    @ApiOperation(value = "恢复已删除用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "请输入用户id", paramType = "path", dataType = "int"),
    })
    @RequestMapping(value = "/recover/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean recover(@PathVariable int id, HttpServletRequest request) {
        try {
            Userinfo u = userinfoService.getOne(id);
            userinfoService.recover(u);
            return Result.OPERATE_SUCCESS.toBean(u);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.OPERATE_FAIL.toBean();
        }
    }

    /***
     * @param response 响应
     * @throws Exception 文件异常
     * @Author zlm
     * @Description 导出未删除并且启用的所有用户信息
     * @Date 2018/2/2 13:53
     */
    @ApiOperation(value = "导出用户列表")
    @RequestMapping(value = "/usersExport", method = RequestMethod.GET)
    @ResponseBody
    public void usersExport(HttpServletResponse response) throws Exception {
        List<Userinfo> ulist = userinfoService.findByDelAndAble(false, true);
        Workbook book = userinfoService.listToExcel(ulist);
        ulist.clear();
        fileDownOrShow("users.xlsx",FileContentType.xls, OutputStreamDown.to(out -> book.write(out)));
    }

    /***
     * @param file     上传的文件名
     * @param response 响应
     * @return 待定
     * @Author yhl
     * @Description 导入用户列表
     * @Date 2018/2/1 10:5
     */
    @ApiOperation(value = "批量导入用户")
    @PostMapping(value = "/usersImport")
    @ResponseBody
    public ResponseBean usersImport(@RequestParam(value = "file") MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
        try {
            if (file == null) {
                return Result.find(CfI.R_USER_FILEISNULL_FAIL).toBean();
            }
            String fileName = file.getOriginalFilename();
            if (!ExcelUtils.validExcel(fileName)) {
                return Result.find(CfI.R_USER_FORMATERROR_FAIL).toBean();
            }
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            /* 导入用户 */
            int count = userinfoService.importUser(sheet, request);

            fileDownOrShow("result.xlsx", FileContentType.xls,OutputStreamDown.to(out -> workbook.write(out)));

            return Result.find(CfI.R_USER_IMPORT_SUCCESS).toBean(String.format("共%d条，导入成功%d条", sheet.getPhysicalNumberOfRows(), count));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.find(CfI.R_USER_IMPORT_FAIL).toBean();
        }
    }

    /***
     * @param response HttpResponse
     * @Author yhl
     * @Description 下载用户导入模板
     * @Date 2018/2/1 11:35
     */
    @ApiOperation(value = "下载用户导入模板")
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody
    public void download(HttpServletResponse response) throws Exception{

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("file/userTemplate.xlsx");

        fileDownOrShow("用户导入模板.xlsx",FileContentType.xls,OutputStreamDown.to(out -> IOUtils.copy(inputStream,out)));
    }

    /***
     * @param id 用户id
     * @return 用户实体
     * @Author zlm
     * @Description 根据用户id查询用户信息
     * @Date 2018/2/1 10:32
     */
    @ApiOperation(value = "根据id查询用户", notes = "返回单一用户,无则返回Null", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "请输入用户id", paramType = "path", dataType = "int"),
    })
    @RequestMapping(value = "userById1/{id}", method = RequestMethod.GET)
    public ResponseBean userById1(@PathVariable Integer id) {
        Userinfo user = userinfoService.getOne(id);
        return Result.QUERY_SUCCESS.toBean(user);
    }

    /***
     * @param name  字段全名
     * @param value 需要验证的值
     * @param id    排除的id(新增时不需传入id，修改时请务必正确传入id)
     * @return 校验字段结果
     * @Author zlm
     * @Description 用户字段和值 唯一性校验
     * @Date 2018/2/7 10:57
     */
    @ApiOperation(value = "唯一性校验", notes = "修改时填写id的值，新增不用传id的值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", required = true, value = "请输入字段名", paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "value", required = true, value = "请输入要校验的值", paramType = "path", dataType = "String"),
            @ApiImplicitParam(name = "id", value = "请输入id", paramType = "path", dataType = "int"),
    })
    @GetMapping(value = "/validate")
    @ResponseBody
    public ResponseBean validateName(String name, String value, Integer id) {
        if (!userinfoService.isExist(name, value, id)) {
            return Result.VALIDATE_SUCCESS.toBean();
        } else {
            return Result.VALIDATE_FAIL.toBean();
        }
    }


    /**
     * @Author zlm
     * @Description
     * @Date 2018/1/25 9:11
     */
    @ApiOperation(value = "查询所有用户[USER-1]", notes = "查询所有未删除且已启用的用户", httpMethod = "GET")
    @RequestMapping(value = "/userAll", method = RequestMethod.GET)
    public ResponseBean userAll() {
        try {
            List<Userinfo> userList = userinfoService.findByDelAndAble(false, true);
            List<Userinfo> tempList = new ArrayList<Userinfo>(userList.size());
            userList.forEach(x -> tempList.add(x.u()));
            userList.clear();
            userList = tempList;
            return Result.QUERY_SUCCESS.toBean(userList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean(e.getLocalizedMessage());
        }
    }


    private String getSqlIn(List<String> ids, int count, String field) {
        count = Math.min(count, 1000);
        int len = ids.size();
        int size = len % count;
        if (size == 0) {
            size = len / count;
        } else {
            size = (len / count) + 1;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            int index = i * count;
            int toIndex = Math.min(index + count, len);
            String productId = StringUtils.defaultIfEmpty(StringUtils.join(ids.subList(index, toIndex),
                    ","), "");
            if (i != 0) {
                sb.append(" or ");
            }
            sb.append(field).append(" in (").append(productId).append(")");
        }
        return StringUtils.defaultIfEmpty(sb.toString(), field + " in ('')");
    }


    /**
     * @Author zlm
     * @Description
     * @Date 2018/1/25 9:11
     */
    @ApiOperation(value = "查询所有用户[USER-1]", notes = "查询所有未删除且已启用的用户", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isPage", value = "是否分页", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "page", value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页数量,必须大于0(不写默认为20)", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为lastModifyTime,asc),", example = "id,asc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "username", value = "用户名", example = "zhangsan", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "fullname", value = "真实姓名", example = "张三", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "cardId", value = "身份证号码", example = "610124199110010011", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "policeNum", value = "警号", example = "041525", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "ulevel", value = "级别", example = "1", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sex", value = "性别(男：true  女：false)", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "depart.id", value = "机构id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "duty.id", value = "职位id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "departIds", value = "机构ids", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "departPids", value = "机构父级ids", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "userIds", value = "用户ids", paramType = "query", dataType = "string")
    })
    @RequestMapping(value = "/queryUser", method = RequestMethod.GET)
    public ResponseBean queryUser(Boolean isPage, Pageable page, Userinfo userinfo, String departPids, String departIds, String userIds) {
        if (isPage == null || isPage) {
        } else {
            page = null;
        }
        try {
            userinfo.setAble(true);
            String sql = "";
            if (StringUtils.isNotBlank(departPids) && StringUtils.isNotBlank(departIds)) {
                List<String> pids = Arrays.asList(departPids.split(","));
                List<String> ids = Arrays.asList(departIds.split(","));
                sql += MessageFormat.format(" and ( e.departid in (select d.id from {0} d  start with ( {1} ) connect by prior d.id = d.parentid ) or ( {2} ) )"
                        , SqlUtils.tableName(Depart.class), getSqlIn(pids, 500, "d.id"), getSqlIn(ids, 500, "e.departid"));
            }
            if (StringUtils.isNotBlank(departPids) && StringUtils.isBlank(departIds)) {
                List<String> pids = Arrays.asList(departPids.split(","));
                sql += MessageFormat.format(" and e.departid in (select d.id  from {0} d  start with ( {1} ) connect by prior d.id = d.parentid )", SqlUtils.tableName(Depart.class), getSqlIn(pids, 500, "d.id"));
            }
            if (StringUtils.isBlank(departPids) && StringUtils.isNotBlank(departIds)) {
                List<String> ids = Arrays.asList(departIds.split(","));
                sql += " and ( " + getSqlIn(ids, 500, "e.departid") + " )";
            }
            if (StringUtils.isNotBlank(userIds)) {
                sql += " and e.id in ( " + userIds + " )";
            }
            if (page == null) {
                List<Userinfo> userList = userinfoService.query(userinfo, sql);
                List<Userinfo> tempList = new ArrayList<Userinfo>(userList.size());
                userList.forEach(x -> tempList.add(x.u()));
                userList.clear();
                userList = tempList;
                return Result.QUERY_SUCCESS.toBean(userList);
            } else {
                Page<Userinfo> userList = userinfoService.query(userinfo, page, sql);
                return Result.QUERY_SUCCESS.toBean(userList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean(e.getLocalizedMessage());
        }
    }

    /**
     * @Author zlm
     * @Date 2018/1/24 9:17
     */
    @ApiOperation(value = "根据id查询用户[USER-2]", notes = "返回单一用户，该用户为未删除用户，如果查询的是已删除用户，则返回Null", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "请输入用户id", paramType = "path", dataType = "int"),
    })
    @RequestMapping(value = "userById/{id}", method = RequestMethod.GET)
    public ResponseBean userById(@PathVariable Integer id) {
        try {
            Userinfo user = userinfoService.getUserInfoById(id);
            return Result.QUERY_SUCCESS.toBean(user);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }

    /**
     * @Author lizhidong
     * @Date 2019年1月17日17:14:13
     */
    @ApiOperation(value = "获取用户职位级别", httpMethod = "GET")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "query", dataType = "int")
    @RequestMapping(value = "/userDutyLevel", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean userDutyLevel(@RequestParam Integer userId) {
        try {
            Userinfo userinfo = userinfoService.getOne(userId);
            return Result.QUERY_SUCCESS.toBean(userinfo.getDuty().getDlevel());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }

    /**
     * @Author zlm
     * @Date 2018/1/24 9:17
     */
    @ApiOperation(value = "根据机构查询用户[USER-3]", notes = "查询该机构下的所有未删除用户", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departId", required = true, value = "请输入机构id", paramType = "path", dataType = "int"),
    })
    @RequestMapping(value = "/usersByDepart/{departId}", method = RequestMethod.GET)
    public ResponseBean usersByDepart(@PathVariable Integer departId) {
        try {
            List<Userinfo> userlist = userinfoService.findUserinfosByDepartid(departId);
            List<Userinfo> tempList = new ArrayList<Userinfo>(userlist.size());
            userlist.forEach(x -> tempList.add(x.u()));
            userlist.clear();
            userlist = tempList;
            return Result.QUERY_SUCCESS.toBean(userlist);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }

    @ApiOperation(value = "根据用户姓名查询用户[USER-4]", notes = "根据用户姓名查询用户实体", response = Userinfo.class, httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullname", required = true, value = "请输入用户姓名", paramType = "path", dataType = "String"),
    })
    @RequestMapping(value = "/userByFullname/{fullname}", method = RequestMethod.GET)
    public ResponseBean userByFullname(@ApiParam(value = "用户名称", required = true) @PathVariable(name = "fullname") String fullname) {
        try {
            List<Userinfo> users = userinfoService.findBy("fullname", fullname);
            return Result.QUERY_SUCCESS.toBean(users);
        } catch (Exception e) {
            return Result.QUERY_FAIL.toBean();
        }
    }

    /**
     * @Author zlm
     * @Date 2018/1/23 9:18
     */
    @ApiOperation(value = "同步基础数据[USER-5]", notes = "返回所有操作时间大于输入时间的用户、职位和机构，无论是否可见或是否启用，如果输入时间为空，则返回所有的数据", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "opttime", value = "请输入操作时间(格式：2018-01-02 16:49:50)", paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/udataByOpttime", method = RequestMethod.GET)
    public ResponseBean udataByOpttime(@RequestParam(required = false) String opttime) {
        try {
            Map<String, List> datas = new HashMap<>(3);
            Date date = CommonUtils.parseDate("1900-01-01", "yyyy-MM-dd");
            if (org.apache.commons.lang3.StringUtils.isNotBlank(opttime)) {
                date = CommonUtils.parseDate(opttime, "yyyy-MM-dd hh:mm:ss");
            }
            List<Userinfo> userinfos = userinfoService.findGeOptTime(date);
            List<Depart> departs = departService.findGeOptTime(date);
            List<Duty> dutys = dutyService.findGeOptTime(date);
            datas.put(Depart.class.getSimpleName(), departs);
            datas.put(Userinfo.class.getSimpleName(), userinfos);
            datas.put(Duty.class.getSimpleName(), dutys);
            return Result.QUERY_SUCCESS.toBean(datas);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }

    @RequestMapping(value = "/userinfoFail", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean userinfoFail() {
        return Result.NOPOWER_FAIL.toBean();
    }

    @ApiOperation(value = "获得用户签章信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "code", value = "类型:0签1章", required = false, paramType = "query", dataType = "int")
    })
    @RequestMapping(value = "/userSignet", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean userSignet(@RequestParam Integer userId, @RequestParam(required = false) Integer code) {
        Userinfo userinfo = this.userinfoService.getOne(userId);
        if (code == null) {
            code = 0;
        }
        try {
            String value = "";
            if (null != userinfo) {
                if (code == 0) {
                    value = userinfo.getQmcodeStr();
                } else {
                    value = userinfo.getGzcodeStr();
                }
            }
            return Result.QUERY_SUCCESS.toBean(value);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.QUERY_FAIL.toBean();
        }
    }

}
