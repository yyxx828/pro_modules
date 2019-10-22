package com.xajiusuo.busi.diction.controller;

import com.alibaba.fastjson.JSONObject;
import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.busi.diction.service.DictionService;
import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.CfI;
import com.xajiusuo.utils.JSONUtils;
import com.xajiusuo.utils.UserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.List;

/**
 * Created by 杨勇 on 18-8-21.
 */
@Slf4j
@Api(description = "字典管理")
@RestController
@RequestMapping(value = "/api/village/diction")
public class DictionController extends BaseController {


    @Autowired
    private DictionService dictionService;


    /***
     * @desc 字段/分类列表
     * @author 杨勇 18-8-20
     * @return
     */
    @ApiOperation(value = "[01] - 字典列表")
    @RequestMapping(value = "/baseDictions", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "父类id",paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从1开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public ResponseBean baseDictions(Pageable pageable, Integer pid) {
        Page<Diction> page = dictionService.baseDictions(pageable,pid);
        return Result.QUERY_SUCCESS.toBean(JSONUtils.autoRemoveField(JSONObject.toJSON(page),true,"pid","dlevel","loading","expand","children","title"));
    }


    /***
     * @desc 字段/分类列表
     * @author 杨勇 18-8-20
     * @return
     */
    @ApiOperation(value = "[02] - 字典树表")
    @RequestMapping(value = "/treeDictions", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean treeDictions() {
        List<Diction> list = dictionService.findBy("dlevel",1);
        for(Diction d:list){
            d.setChildren(dictionService.findBy("pid",d.getId()));
        }
        return Result.QUERY_SUCCESS.toBean(JSONUtils.autoRemoveField(JSONObject.toJSON(list),true,"dlevel","val","keys","descs"));
    }


    /***
     * @desc 字段/分类列表
     * @author 杨勇 18-8-20
     * @return
     */
    @ApiOperation(value = "[04] - 字典下拉")
    @RequestMapping(value = "/listDictions", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keys", value = "父类keys",paramType = "query", dataType = "String"),
    })
    @ResponseBody
    public ResponseBean listDictions(String keys) {
        if(StringUtils.isBlank(keys)){
            return Result.QUERY_SUCCESS.toBean();
        }
        List<Diction> list = dictionService.listDictions(keys);
        return Result.QUERY_SUCCESS.toBean(JSONUtils.autoRemoveField(JSONObject.toJSON(list),true,"pid","dlevel","id","loading","expand","children"));
    }

    /***
     * @desc 原始信息保存
     * @author 杨勇 18-8-22
     * @return
     */
    @ApiOperation(value = "[05] - 字典保存", httpMethod = "POST")
    @RequestMapping(value = "/saveDiction", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "父类ID", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "val", value = "对应中文值", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "descs", value = "描述", paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean saveDiction(Integer pid, String val, String descs) {

        String sql = MessageFormat.format( "select * from {0} ", SqlUtils.tableName(Diction.class));

        String sql_ = " pid is null";
        if(pid != null){
            sql_ = " pid = " + pid;
        }

        List<Diction> list = dictionService.executeNativeQuerySql(MessageFormat.format( "select * from {0} e where val = ? and {1}" , SqlUtils.tableName(Diction.class), sql_), val);
        if(list.size() > 0){
            return Result.find(CfI.R_DICTION_FIELDEXIST_FAIL).toBean();
        }

        Diction entity = new Diction();
        entity.setPid(pid);
        entity.setVal(val);
        entity.setDescs(descs);

        dictionService.clearMap();

        Result r = dictionService.saveDiction(entity);
        return r.toBean();
    }

    /***
     * @desc 原始信息保存
     * @author 杨勇 18-8-22
     * @return
     */
    @ApiOperation(value = "[06] * 字典修改", httpMethod = "POST",response = Diction.class)
    @RequestMapping(value = "/updateDiction", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "父类ID", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "keys", value = "编码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "val", value = "对应中文", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "descs", value = "描述", paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean updateDiction(Integer id, String keys, String val, String descs) {
        if((StringUtils.isBlank(keys) || !keys.trim().toLowerCase().startsWith("k")) || !(keys.trim().length() == 3 || keys.trim().length() == 5)){
            return Result.find(CfI.R_DICTION_DICTIONHASERROR_FAIL).toBean();
        }
        keys = keys.trim();
        Diction entity = null;
        if(id != null){
            entity = dictionService.getOne(id);
            if(entity.getId() == null){
                return Result.find(CfI.R_DICTION_DICTIONNOTEXIST_FAIL).toBean();
            }
            if(entity.getDlevel() == 2){
                try{
                    Integer.parseInt(keys.substring(3));
                }catch (Exception e){
                    return Result.find(CfI.R_DICTION_KEYSRULEERROR_FAIL).toBean();
                }
            }
            try{
                Integer.parseInt(keys.substring(1,3));
            }catch (Exception e){
                return Result.find(CfI.R_DICTION_KEYSRULEERROR_FAIL).toBean();
            }
        }
        String sql = MessageFormat.format( "select * from {0} e where id <> ? and keys = ?", SqlUtils.tableName(Diction.class));
        if(dictionService.executeNativeQuerySql(sql, id,keys).size() > 0){
            return Result.find(CfI.R_DICTION_DICTIONHASEXIST_FAIL).toBean();
        }

        String sql_ = " pid is null";
        if(entity.getPid() != null){
            sql_ = " pid = " + entity.getPid();
        }

        List<Diction> list = dictionService.executeNativeQuerySql(MessageFormat.format( "select * from {0} e where descs = ? and id <> ? and {1}" , SqlUtils.tableName(Diction.class) , sql_), descs,id);
        if(list.size() > 0){
            return Result.find(CfI.R_DICTION_FIELDHASEXIST_FAIL).toBean();
        }

        UserInfoVo user = UserUtil.getCurrentUser(request);
        entity.setKeys(keys);
        entity.setDescs(descs);
        entity.setVal(val);

        dictionService.clearMap();
        dictionService.save(entity);
        return Result.SAVE_SUCCESS.toBean(entity);
    }


    /***
     * @desc 原始信息获取
     * @author 杨勇 18-8-22
     * @return
     */
    @ApiOperation(value = "[07] - 字典查看", httpMethod = "POST",response = Diction.class)
    @RequestMapping(value = "/viewDiction", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "字典ID", paramType = "query", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean viewDiction(Integer id) {
        Diction entity = dictionService.getOne(id);
        return Result.QUERY_SUCCESS.toBean(JSONUtils.autoRemoveField(JSONObject.toJSON(entity),true,"loading","expand","children"));
    }

    /***
     * @desc 原始信息获取
     * @author 杨勇 18-8-22
     * @return
     */
    @ApiOperation(value = "[07] - 字典删除", httpMethod = "DELETE",response = Diction.class)
    @RequestMapping(value = "/deleteDiction", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "字典ID", paramType = "query", dataType = "int"),
    })
    @ResponseBody
    public ResponseBean deleteDiction(Integer id) {
        Diction entity = dictionService.getOne(id);
        String sql = MessageFormat.format( "select * from {0} e where e.pid = ?", SqlUtils.tableName(Diction.class));
        if(dictionService.executeNativeQuerySql(sql, id).size() > 0){
            //字典存在子类不能删除
            return Result.find(CfI.R_DICTION_DICTIONHASCHILD_FAIL).toBean();
        }
        dictionService.destroy(id);
        return Result.DELETE_SUCCESS.toBean();
    }


}