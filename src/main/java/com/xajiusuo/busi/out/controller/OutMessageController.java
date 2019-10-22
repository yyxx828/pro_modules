package com.xajiusuo.busi.out.controller;

import com.xajiusuo.busi.mail.entity.Touch;
import com.xajiusuo.busi.mail.send.conf.TipsConf;
import com.xajiusuo.busi.mail.service.MsgService;
import com.xajiusuo.busi.user.service.UserinfoService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author 王明乐
 * @Date 2019年1月17日16:05:14
 */
@Api(description = "推送消息对外接口")
@RestController
@RequestMapping(value="/api/out/message")
public class OutMessageController extends BaseController {

    @Autowired
    private UserinfoService userinfoService;

    @Autowired
    private MsgService msgService;

    /**
     * @Author 杨勇
     * @Description 新增推送
     * @Date 2019年4月8日14:38:09
     * @return 成功标识
     */
    @ApiOperation(value = "消息发送", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 500, message = "保存失败")
    })
    @PostMapping(value = "/sendMessage")
    @ResponseBody
    public ResponseBean sendMessage(@RequestBody Touch entity) {
        msgService.sendMessage(entity);//消息发送对外接口

        return Result.SAVE_SUCCESS.toBean();
    }


    /**
     * @Author 杨勇
     * @Description 消息已读
     * @Date 2019年4月11日
     * @return 成功标识
     */
    @ApiOperation(value = "消息已读通知", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "保存成功"),
            @ApiResponse(code = 500, message = "保存失败")
    })
    @PostMapping(value = "/readMessage")
    @ResponseBody
    public ResponseBean readMessage(String tips,Integer userId,String entityId) {
        TipsConf conf = TipsConf.valueOf(tips);
        if(conf != null){
            msgService.readMsg(conf,userId,entityId);//消息已读对外接口
        }
        return Result.SAVE_SUCCESS.toBean();
    }


}