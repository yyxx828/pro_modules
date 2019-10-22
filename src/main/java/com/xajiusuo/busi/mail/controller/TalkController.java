package com.xajiusuo.busi.mail.controller;

import com.xajiusuo.busi.mail.entity.Talk;
import com.xajiusuo.busi.mail.send.WebMessageObserver;
import com.xajiusuo.busi.mail.service.CustomService;
import com.xajiusuo.busi.mail.service.TalkService;
import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.busi.user.entity.Userinfo;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(description = "用户群聊接口")
@RestController
@RequestMapping(value="/api/village/talk")
public class TalkController extends BaseController {

    @Autowired
    private TalkService talkService;

    @Autowired
    private CustomService customService;

    private static SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

    @ApiOperation(value = "群聊", httpMethod = "POST")
    @RequestMapping(value = "/speak", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "secret",  value = "是否悄悄话", paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "receiveUserId", value = "悄悄话对象", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "content",  value = "聊天内容", paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean speak(Integer receiveUserId,Boolean secret,String content) {
        UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);

        if(userInfoVo == null || userInfoVo.getUserId() == null){
            return Result.find(CfI.R_MAIL_SEND_FAIL).setData("用户未登陆").toBean();
        }

        if(StringUtils.isBlank(content)){
            return Result.find(CfI.R_MAIL_SEND_FAIL).setData("内容不能为空").toBean();
        }
        if(content.length() > 1000){
            return Result.find(CfI.R_MAIL_SEND_FAIL).setData(MessageFormat.format("内容长度[{0}],最长1000字",content.length())).toBean();
        }
        if(secret == null){
            secret = false;
        }
        if(!secret){
            receiveUserId = null;
        }
        Talk entity = new Talk();
        if(receiveUserId != null && receiveUserId != 0){
            Userinfo user = customService.getUserInfo(receiveUserId);
            if(user != null){
                entity.setReceiveUserId(user.getId());
                entity.setReceiveUserName(user.getFullname());
            }else{
                return Result.find(CfI.R_MAIL_SEND_FAIL).setData("悄悄话用户无法找到").toBean();
            }
        }
        entity.setCreateUID(userInfoVo.getUserId());
        entity.setSendUserName(userInfoVo.getFullName());
        entity.setContent(content);
        try{
            talkService.speak(entity);//群聊消息发送
            return Result.OPERATE_SUCCESS.toBean();
        }catch (Exception e){
            return Result.find(CfI.R_MAIL_SEND_SUCCESS).toBean();
        }
    }

    @ApiOperation(value = "在线用户(除自己)", httpMethod = "GET")
    @RequestMapping(value = "/onlineUser", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean onlineUser() {
        UserInfoVo user = UserUtil.getCurrentUser(request);
        List<Map<String, Object>> list = WebMessageObserver.getOnlineUser(user.getUserId());
        return Result.QUERY_SUCCESS.setData(list).toBean();
    }

    @ApiOperation(value = "群聊记录", httpMethod = "POST")
    @RequestMapping(value = "/talkHistory", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date", value = "选择日期[yyyy-MM-dd],空为当天", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "key",  value = "要查询内容,空未全部", paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean talkHistory(String date,String key) {
        UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
        if(userInfoVo == null || userInfoVo.getUserId() == null){
            return Result.find(CfI.R_USER_NOLOGIN_FAIL).toBean();
        }
        Date d = null,d1 = new Date();
        try {
            d = ymd.parse(date);
        } catch (Exception e) {
            d = null;
        }
        Talk.setUserId(userInfoVo.getUserId());
        Talk entity = null;
        if(d != null){
            entity = talkService.nextListTask(d,userInfoVo.getUserId(),key);
        }else{
            entity = talkService.prevListTask(d1,userInfoVo.getUserId(),key);
        }
        return Result.OPERATE_SUCCESS.toBean();
    }

    @ApiOperation(value = "群聊记录查询上一页", httpMethod = "POST")
    @RequestMapping(value = "/prevTalkHistory", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "time", value = "talk.min", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "key",  value = "要查询内容,空未全部", paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean prevTalkHistory(Long time,String key) {
        UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
        if(userInfoVo == null || userInfoVo.getUserId() == null){
            return Result.find(CfI.R_USER_NOLOGIN_FAIL).toBean();
        }
        if(time == null){
            return Result.find(CfI.R_MAIL_BEGINPAGE_FAIL).toBean();
        }
        Date d = new Date(time);
        Talk.setUserId(userInfoVo.getUserId());
        Talk entity = talkService.prevListTask(d,userInfoVo.getUserId(),key);
        return Result.OPERATE_SUCCESS.setData(entity).toBean();
    }

    @ApiOperation(value = "群聊记录查询下一页", httpMethod = "POST")
    @RequestMapping(value = "/nextTalkHistory", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "time", value = "talk.max", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "key",  value = "要查询内容,空未全部", paramType = "query", dataType = "string"),
    })
    @ResponseBody
    public ResponseBean nextTalkHistory(Long time,String key) {
        UserInfoVo userInfoVo = UserUtil.getCurrentUser(request);
        if(userInfoVo == null || userInfoVo.getUserId() == null){
            return Result.find(CfI.R_USER_NOLOGIN_FAIL).toBean();
        }
        if(time == null){
            return Result.find(CfI.R_MAIL_ENDPAGE_FAIL).toBean();
        }
        Date d = new Date(time);
        Talk.setUserId(userInfoVo.getUserId());
        Talk entity = talkService.nextListTask(d,userInfoVo.getUserId(),key);
        return Result.OPERATE_SUCCESS.setData(entity).toBean();
    }


}