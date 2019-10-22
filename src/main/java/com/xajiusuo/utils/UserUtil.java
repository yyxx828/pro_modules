package com.xajiusuo.utils;

import com.xajiusuo.busi.user.entity.UserInfoVo;
import com.xajiusuo.busi.user.entity.Userinfo;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

//import org.jasig.cas.client.authentication.AttributePrincipal;
//import org.jasig.cas.client.validation.Assertion;

/**
 * @Author: yanpeng
 * @Description:
 * @Date:Created in 13:54 2018/08/21
 * @Modified By:
 */
@Slf4j
public class UserUtil {

    /**
     * 不一定实现，不建议使用
     *
     * @param request
     * @return
     */
    @Deprecated
    public static Userinfo getCurrentUserInfo(HttpServletRequest request) {
        return (Userinfo) request.getSession().getAttribute("userInfo");
    }

    public static UserInfoVo getCurrentUser(HttpServletRequest request) {
        UserInfoVo userInfoVo = (UserInfoVo) getCurrentInfo(request).get("user");
        if (userInfoVo.getUserId() == null) {
            //log.error("用户获取失败");
            return null;
        }
        return userInfoVo;
    }

    public static Map<String, Object> getCurrentInfo(HttpServletRequest request) {
        Map<String, Object> infos = new HashMap<String, Object>();
        UserInfoVo user = new UserInfoVo();
        String copyright = "";
        Object casAssertion = request.getSession().getAttribute("_const_cas_assertion_");

        Assertion assertion = (Assertion) casAssertion;
        if (assertion != null) {
            AttributePrincipal attributePrincipal = assertion.getPrincipal();
            String name = attributePrincipal.getName();
            Map<String, Object> cas_map = attributePrincipal.getAttributes();
            copyright = String.valueOf(cas_map.get("copyright"));
            if (cas_map != null && cas_map.size() > 0) {
                String userid = cas_map.get("id") != null && !"".equals(cas_map.get("id")) ? cas_map.get("id").toString() : "0";
                user.setUserId(Integer.parseInt(userid));
                String departmentid = cas_map.get("did") != null && !"".equals(cas_map.get("did")) ? cas_map.get("did").toString() : "0";
                user.setDepartmentId(Integer.parseInt(departmentid));
                String roleid = cas_map.get("duid") != null && !"".equals(cas_map.get("duid")) ? cas_map.get("duid").toString() : "0";
                user.setRoleId(Integer.parseInt(roleid));
                String username = cas_map.get("username") != null && !"".equals(cas_map.get("username")) ? cas_map.get("username").toString() : "";
                user.setUserName(username);
                String fullname = cas_map.get("fullname") != null && !"".equals(cas_map.get("fullname")) ? cas_map.get("fullname").toString() : "";
                user.setFullName(fullname);
                String departmentname = cas_map.get("dname") != null && !"".equals(cas_map.get("dname")) ? cas_map.get("dname").toString() : "";
                user.setDepartmentName(departmentname);
                String rolename = cas_map.get("dutyname") != null && !"".equals(cas_map.get("dutyname")) ? cas_map.get("dutyname").toString() : "";
                user.setRoleName(rolename);
                String carid = cas_map.get("cardid") != null && !"".equals(cas_map.get("cardid")) ? cas_map.get("cardid").toString() : "";
                user.setCardId(carid);
                String ocodes = cas_map.get("ocodes") != null && !"".equals(cas_map.get("ocodes")) ? cas_map.get("ocodes").toString() : "";
                user.setOcodes(ocodes);
                String gname = cas_map.get("gname") != null && !"".equals(cas_map.get("gname")) ? cas_map.get("gname").toString() : "";
                user.setGname(gname);
            }
        }
        infos.put("user", user);
        infos.put("copyright", copyright);
        return infos;
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip =request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


}
