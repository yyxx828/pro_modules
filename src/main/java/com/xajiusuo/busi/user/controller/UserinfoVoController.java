package com.xajiusuo.busi.user.controller;

import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.utils.UserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author zlm
 * @Date 2018/1/18
 * @Description 用户信息控制器
 */
@Slf4j
@Api(description = "用户管理")
@RestController
@RequestMapping(value = "/api/village")
public class UserinfoVoController extends BaseController {

    @ApiOperation(value = "获取当前登录信息,(user:登录人信息，copyright:版权信息)", httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功", response = UserInfoVo.class),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @ResponseBody
    @RequestMapping(value = "/getCurrentInfo", method = RequestMethod.GET)
    public ResponseBean getCurrentInfo(HttpServletRequest request) {
        Map<String, Object> info = UserUtil.getCurrentInfo(request);
        if (info != null && info.size() > 0) {
            return getResponseBean(Result.QUERY_SUCCESS.setData(info));
        }
        return getResponseBean(Result.QUERY_FAIL);
    }


    @ApiOperation(value = "退出当前登陆人", httpMethod = "GET")
    @ResponseBody
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseBean logout(HttpServletRequest request) {
        request.getSession().invalidate();
        Map<String, Object> info = UserUtil.getCurrentInfo(request);
        info.clear();
        if (info == null || info.size() == 0) {
            return getResponseBean(Result.QUERY_SUCCESS);
        }
        return getResponseBean(Result.QUERY_FAIL);
    }


    @RequestMapping(value = "/userinfoFail", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean userinfoFail() {
        return getResponseBean(Result.NOPOWER_FAIL);
    }

}
