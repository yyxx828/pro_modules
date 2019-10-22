package com.xajiusuo.busi.mail.controller;

import com.xajiusuo.busi.mail.entity.Custom;
import com.xajiusuo.busi.mail.entity.Msg;
import com.xajiusuo.busi.mail.send.WebMessageObserver;
import com.xajiusuo.busi.mail.send.conf.TipsConf;
import com.xajiusuo.busi.mail.service.CustomService;
import com.xajiusuo.busi.mail.service.MsgService;
import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.busi.user.service.UserinfoService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.utils.CfI;
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
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(description = "用户推送接口")
@RestController
@RequestMapping(value="/api/village/message")
public class MailBoxController extends BaseController {

    @Autowired
    private UserinfoService userinfoService;

    @Autowired
    private CustomService customService;

    @Autowired
    private MsgService msgService;

    @ApiOperation(value = "[MESSAGE - 01]获取用户消息类型", httpMethod = "GET")
    @RequestMapping(value = "/loadMsgType", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean loadMsgType() {
        List<Map<String,String>> list = new ArrayList<Map<String,String>>();

        HashMap<String,String> m = new HashMap<String,String>();
        m.put("name","全部");
        m.put("value","");
        list.add(m);

        for(TipsConf tc:TipsConf.values()){
            if(!tc.isSubscribe()){
                continue;
            }
            m = new HashMap<String,String>();
            m.put("name",tc.getName());
            m.put("value",tc.toString());
            list.add(m);
        }
        return getResponseBean(Result.QUERY_SUCCESS.setData(list));
    }

    @ApiOperation(value = "[MESSAGE - 02]获取用户订阅配置", httpMethod = "GET")
    @RequestMapping(value = "/loadCustoms", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean loadCustoms() {
        UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
        if(userInfoVo == null){
            return Result.NOPOWER_FAIL.toBean();
        }
        Map<TipsConf, Custom> map = customService.getUserConfMap(userInfoVo.getUserId());

        List<Custom> list = new ArrayList<Custom>();
        for(TipsConf tc:TipsConf.values()){
            list.add(map.get(tc));
        }
        list.remove(null);
        return getResponseBean(Result.QUERY_SUCCESS.setData(list));
    }

    @ApiOperation(value = "[MESSAGE - 03]消息配置订阅保存", httpMethod = "POST")
    @RequestMapping(value = "/saveSubscribe/{id}/{subscribe}", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subscribe", value = "订阅", required = true, paramType = "path", dataType = "boolean"),
    })
    @ResponseBody
    public ResponseBean saveSubscribe(@PathVariable(value = "id") Integer id,@PathVariable(value = "subscribe") Boolean subscribe) {
        subscribe = subscribe == null ? false : subscribe;
        Custom entity = customService.getOne(id);
        TipsConf tipsConf = TipsConf.valueOf(entity.getTipsConf());
        if(tipsConf == null){
            customService.delete(entity);
            return Result.find(CfI.R_MAIL_TYPEERR_FAIL).toBean();
        }
        if(!tipsConf.isSubscribe() && subscribe){
            return Result.find(CfI.R_MAIL_NOTSUBSCRIBE_FAIL).setData(MessageFormat.format("[{0}]类型不能进行订阅操作",tipsConf.getName())).toBean();
        }
        entity.setSubscribe(subscribe);

        Custom e = customService.getUserConf(entity.getUserId(),tipsConf);
        e.setSubscribe(entity.getSubscribe());
        return Result.OPERATE_SUCCESS.setData(entity).toBean();
    }

    @ApiOperation(value = "[MESSAGE - 04]消息配置提醒保存", httpMethod = "POST")
    @RequestMapping(value = "/saveRemind/{id}/{remind}", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "remind", value = "提醒", required = true, paramType = "path", dataType = "boolean")
    })
    @ResponseBody
    public ResponseBean saveRemind(@PathVariable(value = "id") Integer id,@PathVariable(value = "remind") Boolean remind) {
        remind = remind == null ? false : remind;
        Custom entity = customService.getOne(id);

        TipsConf tipsConf = TipsConf.valueOf(entity.getTipsConf());
        if(tipsConf == null){
            customService.delete(entity);
            return Result.find(CfI.R_MAIL_TYPEERR_FAIL).toBean();
        }

        entity.setRemind(remind);
        customService.save(entity);

        Custom e = customService.getUserConf(entity.getUserId(),tipsConf);
        e.setRemind(entity.getRemind());
        return Result.OPERATE_SUCCESS.setData(entity).toBean();
    }

    @ApiOperation(value = "[MESSAGE - 05]未读消息", httpMethod = "GET")
    @RequestMapping(value = "/unReadMsg", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean unReadMsg() {
        UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
        if(userInfoVo == null){
            return Result.NOPOWER_FAIL.toBean();
        }
        //初始消息数据
        String sql = MessageFormat.format("select * from {0} e where read is null or msgStar is null",msgService.tableName());

        List<Msg> list = msgService.executeNativeQuerySql(sql);
        list.forEach(m -> {
            if(m.getMsgTop() == null){
                m.setMsgTop(false);
            }
            if(m.getMsgStar() == null){
                m.setMsgStar(false);
            }
        });
        msgService.batchUpdate(list);

        sql = MessageFormat.format("select * from {0} e where receiveUserId = ? and (read = ? or msgStar = ?) order by createTime desc",msgService.tableName());
        list = msgService.executeNativeQuerySql(sql, userInfoVo.getUserId(),false,true);
        return getResponseBean(Result.QUERY_SUCCESS.setData(list));
    }

    @ApiOperation(value = "[MESSAGE - 06]获取消息记录", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "当前页码，从0开始", allowableValues = "range[0, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "size", required = true, value = "每页大小,必须大于0", allowableValues = "range[1, infinity]", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sort", value = "排序说明(默认为id,asc),", example = "id,asc", paramType = "query", dataType = "string")
    })
    @RequestMapping(value = "/listMsg", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean listMsg(Pageable pageable,String tips) {
        String sql = MessageFormat.format("select * from {0} e where receiveUserId = ?{1} order by msgTop desc,createTime desc",msgService.tableName(),StringUtils.isBlank(tips) ? "" : " and tips = '" + tips + "'");
        UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
        if(userInfoVo == null){
            return Result.NOPOWER_FAIL.toBean();
        }
        Page<Msg> pager = msgService.executeNativeQuerySqlForPage(pageable,sql, userInfoVo.getUserId());
        return getResponseBean(Result.QUERY_SUCCESS.setData(pager));
    }

    @ApiOperation(value = "[MESSAGE - 07]消息标记已读", httpMethod = "POST")
    @RequestMapping(value = "/readMsg", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean readMsg(@RequestBody Integer[] ids) {
        UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
        if(userInfoVo == null){
            return Result.NOPOWER_FAIL.toBean();
        }
        if(ids == null || ids.length == 0 || (ids.length == 1 && ids[0] == 0)){
            ids = null;
        }
        int num = msgService.markReadMsg(ids,userInfoVo.getUserId(), true);
        return Result.OPERATE_SUCCESS.toBean(num);
    }



    @ApiOperation(value = "[MESSAGE - 08]消息删除", httpMethod = "POST")
    @RequestMapping(value = "/deleteMsg", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean deleteMsg(@RequestBody Integer[] ids) {
        UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
        if(userInfoVo == null){
            return Result.NOPOWER_FAIL.toBean();
        }
        if(ids == null || ids.length == 0 || (ids.length == 1 && ids[0] == 0)){
            ids = null;
        }
        int num = msgService.deleteMsg(ids,userInfoVo.getUserId());
        return Result.OPERATE_SUCCESS.setData(num).toBean();
    }


    @ApiOperation(value = "[MESSAGE - 09]消息标记未读", httpMethod = "POST")
    @RequestMapping(value = "/unReadMsg", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean unReadMsg(@RequestBody Integer[] ids) {
        UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
        if(userInfoVo == null){
            return Result.NOPOWER_FAIL.toBean();
        }
        if(ids == null || ids.length == 0 || (ids.length == 1 && ids[0] == 0)){
            return Result.OPERATE_FAIL.toBean();
        }
        int num = msgService.markReadMsg(ids,userInfoVo.getUserId(), false);
        return Result.OPERATE_SUCCESS.toBean(num);
    }


    @ApiOperation(value = "[MESSAGE - 11]消息置顶", httpMethod = "POST")
    @RequestMapping(value = "/topMsg", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean topMsg(@RequestBody Integer id) {
        UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
        if(userInfoVo == null){
            return Result.NOPOWER_FAIL.toBean();
        }
        Msg msg = msgService.getOne(id);
        msg.setMsgTop(true);
        msgService.update(msg);
        return Result.OPERATE_SUCCESS.toBean(msg);
    }

    @ApiOperation(value = "[MESSAGE - 12]消息取消置顶", httpMethod = "POST")
    @RequestMapping(value = "/unTopMsg", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean unTopMsg(@RequestBody Integer id) {
        UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
        if(userInfoVo == null){
            return Result.NOPOWER_FAIL.toBean();
        }
        Msg msg = msgService.getOne(id);
        msg.setMsgTop(false);
        msgService.update(msg);
        return Result.OPERATE_SUCCESS.toBean(msg);
    }


    @ApiOperation(value = "[MESSAGE - 13]消息星标", httpMethod = "POST")
    @RequestMapping(value = "/starMsg", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean starMsg(@RequestBody Integer id) {
        UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
        if(userInfoVo == null){
            return Result.NOPOWER_FAIL.toBean();
        }
        Msg msg = msgService.getOne(id);
        msg.setMsgStar(true);
        msgService.update(msg);
        return Result.OPERATE_SUCCESS.toBean(msg);
    }

    @ApiOperation(value = "[MESSAGE - 14]消息取消星标", httpMethod = "POST")
    @RequestMapping(value = "/unStarMsg", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean unStarMsg(@RequestBody Integer id) {
        UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
        if(userInfoVo == null){
            return Result.NOPOWER_FAIL.toBean();
        }
        Msg msg = msgService.getOne(id);
        msg.setMsgStar(false);
        msgService.update(msg);
        return Result.OPERATE_SUCCESS.toBean(msg);
    }

    @ApiOperation(value = "获取在线用户数量", httpMethod = "GET")
    @RequestMapping(value = "/onlineCount", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean onlineCount() {
        String sql = MessageFormat.format("from {0} t where t.regflag = ?",userinfoService.tableName());
        long all = 0;
        try{
            all = userinfoService.getBaseDao().count(sql,1);
        }catch (Exception e){
            e.printStackTrace();
            log.warn("全部用户统计错误");
        }
        Map<String,Object> result = new HashMap<>();
        result.put("totalCount",all);
        result.put("onLineCount", WebMessageObserver.getOnlineNum());
        return Result.QUERY_SUCCESS.setData(result).toBean();
    }

}