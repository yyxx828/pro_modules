package com.xajiusuo.busi.user.controller;

import com.xajiusuo.busi.flow.entity.Cascader;
import com.xajiusuo.busi.user.entity.Depart;
import com.xajiusuo.busi.user.entity.Operate;
import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.busi.user.service.DepartService;
import com.xajiusuo.busi.user.service.OperateService;
import com.xajiusuo.busi.user.service.UserinfoService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.param.entity.UserContainer;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.utils.CfI;
import com.xajiusuo.utils.Node;
import com.xajiusuo.utils.UserUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * @author zlm
 * @Date ate 2018/1/18
 * @Description 机构控制器
 */
@Slf4j
@Api(description = "机构管理")
@RestController
@RequestMapping(value = "/api/village/depart")
public class DepartController extends BaseController {

    @Autowired
    private DepartService departService;

    @Autowired
    private UserinfoService userinfoService;

    @Autowired
    private OperateService operateRepository;

    @ApiOperation(value = "获得所有的机构", notes = "获得所有机构的各种详细信息包括所有字段，默认查询未删除机构")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dname", value = "机构名称", allowableValues = "range[0, 100]", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "parent.id", value = "父级机构id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "dlevel", value = "机构级别", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "del", value = "是否删除", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string")
    })
    @RequestMapping(value = "/getAllDepart", method = RequestMethod.GET)
    public ResponseBean getAllDepart(Pageable pageable, Depart depart) {
        if (depart.getParent().getId() == null) {
            depart.getParent().setId(1);
        }
        String del = (String) UserContainer.info().get("delFlag");
        if ("true".equals(del)) {
            departService.queryRangeDelete();
        }
        Page<Depart> page = departService.queryDepart(depart, pageable);
        return getResponseBean(Result.QUERY_SUCCESS.setData(page));
    }

    @ApiOperation(value = "新增或修改机构", notes = "机构的各种详细信息，新增时不要传id", httpMethod = "POST", response = Depart.class)
    @PostMapping(value = "/save")
    @ResponseBody
    public ResponseBean save(@RequestBody Depart depart, HttpServletRequest request) {
        boolean flag = depart.getId() == null;
        if (flag && depart.getParent() == null) {
            //Configs.dynamicAdd("motor.import.lasttime", Configs.DataType.DATE,"", DateUtils.format(new Date(), P.S.fmtYmd41));
            return getResponseBean(Result.find(CfI.R_DEPART_EMPTY_FAIL));
        }
        if (!departService.isValidDname(depart.getDname(), depart.getParentid(), depart.getId())) {
            return getResponseBean(Result.find(CfI.R_DEPART_DNAMEEXIST_FAIL));
        }
        Depart p = departService.getOne(depart.getParentid());
        depart.setParent(p);
        if (p != null) {
            depart.setDlevel(p.getDlevel() + 1);
        } else {
            depart.setParent(departService.getOne(1));
            depart.setDlevel(1);
        }
        if (depart.getId() == null) {
            depart.setCreateUID(userinfoService.getCurrentUser(request).getId());
        }
        depart.setLastModifyUID(userinfoService.getCurrentUser(request).getId());
        depart.setDelFlag(false);
        departService.saveOrUpdate(depart);
        if (flag) {
            return getResponseBean(Result.SAVE_SUCCESS.setData(depart));
        } else {
            return getResponseBean(Result.UPDATE_SUCCESS.setData(depart));
        }
    }

    @ApiOperation(value = "恢复已经删除机构")
    @GetMapping(value = "/recover/{id}")
    @ResponseBody
    public ResponseBean recover(@PathVariable(value = "id") Integer id, HttpServletRequest request) {
        Depart depart = departService.getOne(id);
        if (depart != null) {
            depart.setLastModifyUID(userinfoService.getCurrentUser(request).getId());
        } else {
            return Result.find(CfI.R_DEPART_DEPART_NOTEXIST_FAIL).toBean();
        }
        departService.recover(depart);
        return Result.OPERATE_SUCCESS.toBean(depart);
    }

    @ApiOperation(value = "删除机构", notes = "逻辑删除，只有当机构不存在子机构和用户时才能删除", httpMethod = "GET")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean delete(@ApiParam(value = "机构id", required = true, example = "00") @PathVariable(value = "id") Integer id, HttpServletRequest request) {
        Depart depart = departService.getOne(id);
        if (depart != null) {
            depart.setLastModifyUID(userinfoService.getCurrentUser(request).getId());
        }
        return getResponseBean(departService.logicDelete(depart));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体", notes = "根据机构id查询未删除的机构实体，可用于查看机构", response = Depart.class)
    public ResponseBean getById(@ApiParam(value = "机构id", required = true, defaultValue = "00", example = "00") @PathVariable(value = "id") Integer id) {
        Depart depart = departService.findById(id);
        return Result.QUERY_SUCCESS.toBean(depart);
    }

    @ApiOperation(value = "机构的查询接口", notes = "支持字符串域的多条件模糊查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dname", value = "机构名称", allowableValues = "range[0, 100]", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "parent.id", value = "父级机构id", allowableValues = "range[0, 100]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "dlevel", value = "机构级别", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string")
    })
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseBean query(Depart depart, Pageable pageable) {
        Page<Depart> list = departService.queryDepart(depart, pageable);
        return getResponseBean(Result.QUERY_SUCCESS.setData(list));
    }

    @ApiOperation(value = "机构列表的查询接口", notes = "查询机构列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "机构id", paramType = "query", dataType = "integer")
    })
    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseBean queryList(@RequestParam(defaultValue = "0") Integer id) {
        if (id.equals(0)) {
            id = null;
        }
        List<Depart> list = departService.getDeptsByPId(id);
        return getResponseBean(Result.QUERY_SUCCESS.setData(list));
    }

    @ApiOperation(value = "机构名称唯一性校验", notes = "新增或修改机构名称时使用，注意校验时包含已删除的机构")
    @GetMapping(value = "/validate/dname")
    public ResponseBean validateDname(@RequestParam String dname, @RequestParam(required = false) Integer id) {
//        if (departService.isValidDname(dname,id)){
//            return getResponseBean(Message.CODE_SUCCESS,"message.depart.validate.useDname");
//        }else{
//            return getResponseBean(Message.CODE_FAIL, Message.Depart.DNAME_EXIST);
//        }
        return getResponseBean(Result.find(CfI.R_DEPART_DNAMEEXIST_FAIL));
    }

    /*@Author:liangxing
     *@Description:用于机构树数据查询
     *@Date:2018-8-15 10:10
     * */
    @ApiOperation(value = "用于机构树数据查询", notes = "用于机构树数据查询", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @RequestMapping(value = "/departByCascader", method = RequestMethod.GET)
    public ResponseBean departByCascader(HttpServletRequest request) {
        try {
            UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
            Depart departs = departService.findById(userInfoVo.getDepartmentId());
            if (departs != null) {
                Cascader cascaderP = new Cascader();
                cascaderP.setValue(departs.getId());
                cascaderP.setLabel(departs.getDname());
                cascaderP.setTitle(departs.getDname());
                List<Depart> children = departService.getDeptsByPId(departs.getId());
                if (children.size() > 0) {
                    cascaderP.setChildren(new HashSet<>());
                    cascaderP.setLoading(false);
                }
                if (children.size() > 0) {
                    cascaderP.setChildren(new HashSet<>());
                    cascaderP.setLoading(false);
                }
                children.clear();
                return getResponseBean(Result.QUERY_SUCCESS.setData(cascaderP));
            } else {
                return getResponseBean(Result.find(CfI.R_USER_NOTLOGIN_FAIL));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }


    @ApiOperation(value = "获取用户机构数")
    @GetMapping(value = "/getUserUnderDepartTree")
    public ResponseBean getUserUnderDepartTree() {
        return getResponseBean(Result.QUERY_SUCCESS.setData(departService.getUserUnderDepartTree()));
    }


    /**
     * @Author zlm
     * @Date 2018/1/21 19:18
     */
    @ApiOperation(value = "查询所有机构[DEPART-1]", notes = "查询所有未删除的机构", httpMethod = "GET")
    @RequestMapping(value = "/departAll", method = RequestMethod.GET)
    public ResponseBean departAll() {
        try {
            List<Depart> dlist = departService.findAllByDel(false);
            return getResponseBean(Result.QUERY_SUCCESS.setData(dlist));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @Author zlm
     * @Date 2018/1/27 9:18
     */
    @ApiOperation(value = "查询子机构[DEPART-2]", notes = "查询当前机构及子机构")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departId", required = true, value = "请输入父级机构id", paramType = "path", dataType = "int"),
    })
    @RequestMapping(value = "/departByPid/{departId}", method = RequestMethod.GET)
    public ResponseBean departByPid(@PathVariable Integer departId) {
        try {
            List<Depart> dlist = departService.findByParentId(departId);
            return getResponseBean(Result.QUERY_SUCCESS.setData(dlist));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @Author yhl
     * @Date 2018/2/2 11:19
     */
    @ApiOperation(value = "查询机构用户树[DEPART-3]", notes = "查询机构用户数据树", httpMethod = "GET")
    @RequestMapping(value = "/departUser", method = RequestMethod.GET)
    public ResponseBean departUser() {
        try {
            Node node = departService.getDepartUserTree();
            List<Object> dlist = new ArrayList<>();
            dlist.add(node);
            return getResponseBean(Result.QUERY_SUCCESS.setData(dlist));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /***
     * @param id 机构id
     * @return 机构实体
     * @Author yhl
     * @Description 根据id查询机构
     * @Date 2018/2/7 15:30
     */
    @ApiOperation(value = "根据id查询机构[DEPART-4]", notes = "根据机构id查询未删除的机构实体，可用于查看机构", response = Depart.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "请输入机构id", paramType = "path", dataType = "int"),
    })
    @RequestMapping(value = "departById/{id}", method = RequestMethod.GET)
    public ResponseBean departById(@ApiParam(value = "机构id", required = true, defaultValue = "00", example = "00") @PathVariable(value = "id") Integer id) {
        try {
            Depart depart = departService.findById(id);
            if (null == depart) {
                return getResponseBean(Result.find(CfI.R_DEPART_DEPART_NOTEXIST_FAIL));
            } else {
                return getResponseBean(Result.QUERY_SUCCESS.setData(depart));
            }
        } catch (Exception e) {
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @ApiOperation(value = "根据名称查询机构[DEPART-5]", notes = "根据名称查询机构实体", response = Depart.class, responseContainer = "List", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dname", value = "请输入机构名称", required = true, paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/departByDname/{dname}", method = RequestMethod.GET)
    public ResponseBean departByDname(@ApiParam(value = "机构名称", required = true) @PathVariable(name = "dname") String dname) {
        try {
            List<Depart> depart = departService.findBy("dname", dname);
            return getResponseBean(Result.QUERY_SUCCESS.setData(depart));
        } catch (Exception e) {
            return getResponseBean(Result.QUERY_FAIL);
        }
    }


    /**
     * @return 分页对象
     * @Author 杨勇
     * @Description 获取通过权限组获取操作
     * @Date 2018/3/14 14:50
     */
    @ApiOperation(value = "查询操作按typeName查询", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功", response = HashMap.class),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @RequestMapping(value = "/operaterByGroupList/{typeName}", method = RequestMethod.GET)
    public ResponseBean operaterByGroupList(@PathVariable(value = "typeName") String typeName) {
        //2018-03-30 wyb 修改参数接受方式
        List<Operate> list = null;
        if (StringUtils.isNotBlank(typeName)) {
            list = operateRepository.findBy("typeName", typeName);
        } else {
            list = operateRepository.getAll();
        }
        Map<String, List<Operate>> map = new HashMap<String, List<Operate>>();
        for (Operate o : list) {
            if (o.getTypeName() == null) {
                o.setTypeName("其它");
            }
            List<Operate> os = map.get(o.getTypeName());
            if (os == null) {
                os = new ArrayList<Operate>();
                map.put(o.getTypeName(), os);
            }
            os.add(o);
        }
        return getResponseBean(Result.QUERY_SUCCESS.setData(map));
    }

    @ApiOperation(value = "根据部门id查询机构用户树[DEPART-3]", notes = "根据部门id查询机构用户数据树", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "did", value = "请输入部门id", required = true, paramType = "query", dataType = "Integer"),
    })
    @RequestMapping(value = "/departUserByDid/{did}", method = RequestMethod.GET)
    public ResponseBean departUserByDid(@PathVariable(value = "did") Integer did) {
        try {
            Node node = departService.getDepartUserTreeByDid(did);
            if (node != null) {
                List<Object> dlist = new ArrayList<>();
                dlist.add(node);
                return getResponseBean(Result.QUERY_SUCCESS.setData(dlist));
            } else {
                return getResponseBean(Result.find(CfI.R_DEPART_EMPTY_FAIL));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @Author yhl
     * @Date 2018/2/2 11:19
     */
    @ApiOperation(value = "查询机构树[DEPART-6]", notes = "查询机构数据树", httpMethod = "GET")
    @RequestMapping(value = "/departTree", method = RequestMethod.GET)
    public ResponseBean departTree() {
        try {
            Node node = departService.getDepartTree();
            List<Object> dlist = new ArrayList<>();
            dlist.add(node);
            return getResponseBean(Result.QUERY_SUCCESS.setData(dlist));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

}
