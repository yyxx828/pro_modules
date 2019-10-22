package com.xajiusuo.busi.user.controller;

import com.xajiusuo.busi.user.entity.OperGroup;
import com.xajiusuo.busi.user.entity.Operate;
import com.xajiusuo.busi.user.entity.Userinfo;
import com.xajiusuo.busi.user.service.OperGroupService;
import com.xajiusuo.busi.user.service.OperateService;
import com.xajiusuo.busi.user.service.UserinfoService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.CfI;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.*;


/***
 * 杨勇 2018-03-12
 * 公共用户权限
 */
@Slf4j
@Api(description = "权限管理")
@RestController
@RequestMapping(value = "/api/village/operate")
public class OperateController extends BaseController {


    @Autowired
    private OperateService operateService;

    @Autowired
    private OperGroupService operGroupService;

    @Autowired
    private UserinfoService userinfoService;

    /**
     * @return 操作
     * @Author 杨勇
     * @Description 创建操作
     * @Date 2018/3/12 14:50
     */
    @ApiOperation(value = "新增或修改操作", notes = "新增时不要传id", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 500, message = "保存失败")
    })
    @PostMapping(value = "/saveOperate")
    @ResponseBody
    public ResponseBean saveOperate(@RequestBody Operate entity) {
        operateService.saveOrUpdate(entity);
        return Result.SAVE_SUCCESS.setData(entity).toBean();
    }


    /**
     * @return 操作
     * @Author 杨勇
     * @Description 删除操作
     * @Date 2018/3/12 14:50
     */

    @ApiOperation(value = "删除操作", httpMethod = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 500, message = "保存失败")
    })
    @RequestMapping(value = "/deleteOperate/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseBean deleteOperate(@PathVariable(value = "id") Integer id) {
        //查询权限组是否包含
        List list = operateService.executeNativeQuerySql(MessageFormat.format("select * from {0} e where e.id = ? and e.id not in (select distinct oid from {1} where oid is not null)", SqlUtils.tableName(Operate.class), "S_OPERS_11"), id);
        if (list.size() < 1) {
            return getResponseBean(Result.find(CfI.R_OPER_NOTFOUNDORMAKEUSER_FAIL));
        }
        operateService.destroy(id);
        return getResponseBean(Result.DELETE_SUCCESS);
    }

    /**
     * @return 权限组
     * @Author 杨勇
     * @Description 创建权限组, 权限组和操作挂钩
     * @Date 2018/3/15 14:50
     */
    @ApiOperation(value = "创建权限组,权限组和操作挂钩", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 500, message = "保存失败")
    })
    @PostMapping(value = "/saveOperGroup")
    @ResponseBody
    public ResponseBean saveOperGroup(@RequestBody OperGroup entity) {
        int[] oids = entity.getOids();
        boolean add = entity.getId() == null;
        int all = operGroupService.getAll().size();
        if (all == 0) {
            entity.setInitUse(true);
        } else if (entity.getInitUse() != null && entity.getInitUse()) {
            List<OperGroup> list = operGroupService.findBy("initUse", true);
            if (list.size() > 0 && (entity.getId() == null || entity.getId().equals(list.get(0).getId()))) {
                for (OperGroup e : list) {
                    e.setInitUse(false);
                    e.setLastModifyTime(new Date());
                    operGroupService.update(e);
                }
            }
        }
        if (add) {
            entity.setLastModifyTime(new Date());
            operGroupService.save(entity);
        }

        if (entity.getOperateSet() == null) {
            entity.setOperateSet(new HashSet<Operate>(oids.length));
        }
        if (oids != null && oids.length > 0) {
            entity.setOperateSet(new HashSet<Operate>(oids.length));
            for (int id : oids) {
                entity.getOperateSet().add(new Operate(id));
            }
        }
        operGroupService.update(entity);
        return getResponseBean(Result.SAVE_SUCCESS.setData(entity));
    }


    /**
     * @return 权限组
     * @Author 杨勇
     * @Description 创建权限组, 权限组和操作挂钩
     * @Date 2018/3/15 14:50
     */
    @ApiOperation(value = "权限组默认设置", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 500, message = "保存失败")
    })
    @PostMapping(value = "/saveOperGroupInitUse")
    @ResponseBody
    public ResponseBean saveOperGroupInitUse(Integer id) {
        boolean initUse = true;
        List<OperGroup> list = operGroupService.findBy("initUse", initUse);
        if (list.size() > 0) {
            for (OperGroup e : list) {
                if (e.getId() != id) {
                    e.setInitUse(false);
                    e.setLastModifyTime(new Date());
                    operGroupService.update(e);
                }
            }
        }
        OperGroup entity = operGroupService.getOne(id);
        entity.setInitUse(true);
        entity.setLastModifyTime(new Date());
        operGroupService.update(entity);
        return getResponseBean(Result.SAVE_SUCCESS.setData(entity));
    }

    /**
     * @return 操作
     * @Author 杨勇
     * @Description 删除权限组
     * @Date 2018/3/15 14:50
     */
    @ApiOperation(value = "删除权限组", httpMethod = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "删除成功"),
            @ApiResponse(code = 500, message = "删除失败")
    })
    @RequestMapping(value = "/deleteOperGroup/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseBean deleteOperGroup(@PathVariable(value = "id") Integer id) {
        //查询权限组是否包含
        OperGroup entity = operGroupService.getOne(id);
        if (entity == null) {
            return getResponseBean(Result.find(CfI.R_OPER_NOTFOUND_FAIL));
        }
        if (entity.getInitUse() != null && entity.getInitUse()) {
            return getResponseBean(Result.find(CfI.R_OPER_ISINIT_FAIL));
        }

        List<Userinfo> allUserOG = userinfoService.findBy("operGroup", entity);//对占用已删除的用户进行处理
        List<Userinfo> userList = new ArrayList<>();
        allUserOG.forEach(u -> {
            if (u.getAble() == null || !u.getAble()) {
                userList.add(u);
            }
        });
        if (userList.size() > 0) {
            return getResponseBean(Result.find(CfI.R_OPER_ISMAKEUSER_FAIL));
        }
        allUserOG.removeAll(userList);
        allUserOG.forEach(u -> {//在删除权限组之前进行用户权限组移除
            u.setOperGroup(null);
            userinfoService.update(u);
        });

        operGroupService.destroy(id);
        return getResponseBean(Result.DELETE_SUCCESS);
    }


    /**
     * @return 操作
     * @Author 杨勇
     * @Description 创建操作
     * @Date 2018/3/12 14:50
     */
    @ApiOperation(value = "查看操作", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @RequestMapping(value = "/viewOperate", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean viewOperate(Integer id) {
        Operate entity = operateService.getOne(id);
        return getResponseBean(Result.QUERY_SUCCESS.setData(entity));
    }

    /**
     * @return 操作
     * @Author 杨勇
     * @Description 创建操作
     * @Date 2018/3/12 14:50
     */
    @ApiOperation(value = "查看权限组", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @RequestMapping(value = "/viewOperGroup", method = RequestMethod.GET)
    public ResponseBean viewOperGroup(Integer id) {
        OperGroup entity = operGroupService.getOne(id);
        return getResponseBean(Result.QUERY_SUCCESS.setData(entity));
    }

    /**
     * @param page 当前页码
     * @return 分页对象
     * @Author 杨勇
     * @Description 权限组列表
     * @Date 2018/3/14 14:50
     */
    @ApiOperation(value = "分页查询权限组", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "gname", value = "操作名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "gdesc", value = "操作描述", paramType = "query", dataType = "string"),
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功", response = PageImpl.class),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @RequestMapping(value = "/operGroupList", method = RequestMethod.GET)
    public ResponseBean operGroupList(Pageable page, OperGroup entity) {
        Page<OperGroup> pager = operGroupService.query(entity, page);
        return getResponseBean(Result.QUERY_SUCCESS.setData(pager));
    }

    /**
     * @return 分页对象
     * @Author 杨勇
     * @Description 权限组列表
     * @Date 2018/3/14 14:50
     */
    @ApiOperation(value = "查询全部权限组", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @RequestMapping(value = "/operGroupAllList", method = RequestMethod.GET)
    public ResponseBean operGroupAllList() {
        List<OperGroup> list = operGroupService.getAll();
        return getResponseBean(Result.QUERY_SUCCESS.setData(list));
    }


    /**
     * @param page 当前页码
     * @return 分页对象
     * @Author 杨勇
     * @Description 权限组列表
     * @Date 2018/3/14 14:50
     */
    @ApiOperation(value = "分页查询操作", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "ocode", value = "操作编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "typeName", value = "操作组名", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "odesc", value = "描述", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "ourl", value = "操作连接", paramType = "query", dataType = "string"),
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功", response = PageImpl.class),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @RequestMapping(value = "/operaterList", method = RequestMethod.GET)
    public ResponseBean operaterList(Pageable page, Operate entity) {
        Page<Operate> pager = operateService.query(entity, page);
        return getResponseBean(Result.QUERY_SUCCESS.setData(pager));
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
    @RequestMapping(value = "/operaterByGroupList", method = RequestMethod.GET)
    public ResponseBean operaterByGroupList(String typeName) {
        Map<String, List<Operate>> map = operateService.operaterByGroupList(typeName);
        return getResponseBean(Result.QUERY_SUCCESS.setData(map));

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
    @RequestMapping(value = "/operaterGroupByUserIdList", method = RequestMethod.GET)
    public ResponseBean operaterGroupByUserIdList(Integer id, String typeName) {
        Set<Operate> list = null;
        Userinfo user = userinfoService.getOne(id);
        if (user.getOperGroup() != null && user.getOperGroup().getId() != null) {
            list = operGroupService.getOne(user.getId()).getOperateSet();
        }
        Map<String, List<Operate>> map = new HashMap<String, List<Operate>>();
        for (Operate o : list) {
            if (o.getTypeName() == null) {
                o.setTypeName("其它");
            }
            if (StringUtils.isNotBlank(typeName) && !typeName.equals(o.getTypeName())) {
                continue;
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


    /**
     * @return 分页对象
     * @Author 杨勇
     * @Description 获取通过权限组获取操作
     * @Date 2018/3/14 14:50
     */
    @ApiOperation(value = "typeName去重查询", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功", response = ArrayList.class),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @RequestMapping(value = "/operTypenameList", method = RequestMethod.GET)
    public ResponseBean operTypenameList() {
        List<String> list = operateService.getOperTypenameList();
        return getResponseBean(Result.QUERY_SUCCESS.setData(list));

    }
}
