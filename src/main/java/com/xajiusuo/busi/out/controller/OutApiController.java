package com.xajiusuo.busi.out.controller;

import com.alibaba.fastjson.JSONObject;
import com.xajiusuo.busi.diction.entity.Diction;
import com.xajiusuo.busi.diction.service.DictionService;
import com.xajiusuo.busi.log.entity.LogSys;
import com.xajiusuo.busi.log.service.LogSysService;
import com.xajiusuo.busi.out.util.ExpAuth;
import com.xajiusuo.busi.out.vo.LoginUser;
import com.xajiusuo.busi.user.controller.DepartController;
import com.xajiusuo.busi.user.controller.DutyController;
import com.xajiusuo.busi.user.entity.Operate;
import com.xajiusuo.busi.user.entity.Userinfo;
import com.xajiusuo.busi.user.service.UserinfoService;
import com.xajiusuo.jpa.config.BaseController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.util.MD5utils;
import com.xajiusuo.jpa.util.ResponseBean;
import com.xajiusuo.jpa.util.SqlUtils;
import com.xajiusuo.utils.CfI;
import com.xajiusuo.utils.DateTools;
import com.xajiusuo.utils.JSONUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author zlm
 * @Date 2018/1/18
 */
@Slf4j
@Api(description = "对外接口")
@RestController
@RequestMapping(value = "/api/out/ufi")
public class OutApiController extends BaseController {
    @Value("${spring.application.name}")
    private String name;//模块名称
    @Value("${spring.datasource.url}")
    private String dataSourceUrl;//数据库地址
    @Value("${cas.client-host-url}")
    private String casClientUrl;//单点登录客户端地址
    @Value("${cas.server-login-url}")
    private String casLoginUrl;//单点登录地址
    @Value("${product.version}")
    private String version;//项目版本号

    @Autowired
    private DepartController departController;

    @Autowired
    private DutyController dutyController;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private UserinfoService userinfoService;

    @Autowired
    private DictionService dictionService;

    @Autowired
    private LogSysService logSysService;

    @Autowired
    private ExpAuth expAuth;


    /**
     * @Author wml
     * @Date 2018年11月26日15:32:28
     */
    @ApiOperation(value = "用户注册[USER-6]", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功"),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @RequestMapping(value = "/userRegist", method = RequestMethod.POST)
    @ResponseBody
    public ResponseBean userRegist(@RequestBody Userinfo userinfo) {
        try{
            userinfo.setRegflag(null);
            userinfo.setOperGroup(null);
            userinfo.setDelFlag(false);
            userinfo.setAble(true);
            userinfo.setUlevel(1);
            if (null == userinfo.getDepart().getId() || 0 == userinfo.getDepart().getId()) {
                userinfo.setDepart(null);
            }
            if (null == userinfo.getDuty().getId() || 0 == userinfo.getDuty().getId()) {
                userinfo.setDuty(null);
            }
            userinfoService.save(userinfo);
            return Result.OPERATE_SUCCESS.setData(userinfo).toBean();
        }catch (Exception e){
            return Result.OPERATE_FAIL.setData(userinfo).toBean();
        }

    }

    /**
     * @Author 杨勇
     * @Date 2019/5/9 16:19
     */
    @ApiOperation(value = "查询机构树[DEPART-6]", notes = "查询机构数据树", httpMethod = "GET")
    @RequestMapping(value = "/departTree", method = RequestMethod.GET)
    public ResponseBean departTree() {
        return departController.departTree();
    }

    /**
     * @Author zlm
     * @Date 2018/2/26 16:10
     */
    @ApiOperation(value = "查询所有职位[DUTY-1]", notes = "查询所有未删除且已启用的职位", httpMethod = "GET")
    @RequestMapping(value = "/dutyAll", method = RequestMethod.GET)
    public ResponseBean dutyAll() {
        return dutyController.dutyAll();
    }


    @ApiOperation(value = "查询版本信息及相关配置信息", httpMethod = "GET")
    @RequestMapping(value = "/getVersion", method = RequestMethod.GET)
    public ResponseBean getVersionInfo() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("name", name);
        map.put("version", version);
        map.put("dataSourceUrl", dataSourceUrl);
        map.put("casLoginUrl", casLoginUrl);
        map.put("casClientUrl", casClientUrl);
        return getResponseBean(Result.QUERY_SUCCESS.setData(map));
    }

    @ApiOperation(value = "登录，按照用户名和密码进行查询", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功", response = HashMap.class),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map login(HttpServletRequest request, @RequestBody Map<String, String> datas) {
        String authStr = request.getHeader("authorization");
        Map rst = new HashMap();
        String ip = datas.get("ip");
        String copyright = datas.get("copyright");
        if (authStr != null) {
            try {
                String authInfo = authStr.split(" ")[1];
                String userData = new String(Base64Utils.decodeFromString(authInfo), "UTF-8");
                String username = userData.split(":")[0];
                String password = userData.split(":")[1];
                Userinfo user = null;
                if (StringUtils.isBlank(username)) {
                    return null;
                }
                if (username.startsWith("PKIWAY")) {
                    username = username.split("\\|")[1];
                    user = authenticatedJDZYPki(username, password);
                } else {
                    user = userinfoService.findByUsernameAndPwd(username, MD5utils.getMd5(password));
                }
                if (user == null || !user.getAble() || user.getDelFlag() || (user.getRegflag() == null || !user.getRegflag())) {
                    return null;
                } else {
                    StringBuilder sb = new StringBuilder();
                    LoginUser lu = new LoginUser(user, copyright);
                    if (user.getOperGroup() != null) {
                        if (user.getOperGroup().getOperateSet() != null && user.getOperGroup().getOperateSet().size() > 0) {
                            for (Operate o : user.getOperGroup().getOperateSet()) {
                                sb.append(o.getOcode() + ",");
                            }
                        }
                    }
                    lu.setOcodes(sb.toString());
                    rst.put("@class", "org.apereo.cas.authentication.principal.SimplePrincipal");
                    rst.put("id", user.getUsername());
                    rst.put("attributes", lu);
                    return rst;
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }


    /**
     * 吉大正元CA认证登录
     *
     * @param userName
     * @param password
     * @return
     */
    public Userinfo authenticatedJDZYPki(String userName, String password) {
        try {
            boolean isSuccess = true;
            Map<String, String> attributeNodeMap = new HashMap<String, String>();
            log.info("keyOriginalData--userName-----》"+userName);
            log.info("keySignedData--password-----》"+password);
            attributeNodeMap.put("keyOriginalData", userName);
            attributeNodeMap.put("keySignedData", password);
            log.info("进入吉大正元CA认证登录");
            isSuccess = expAuth.checkPkiAuth(isSuccess, attributeNodeMap);
            log.info("吉大正元CA认证登录结束，开始判断证书有效期");
            if (isSuccess) {
                // @author yanpengwei 日期格式中含有"\u200e",西安格式为："yyyy年MM月dd日 HH:mm:ss"
                // 证书有效期开始日期
                String pki_datebefore = attributeNodeMap.get("X509Certificate.NotBefore").replace("\u200e", "");
                // 证书有效期截止日期
                String pki_dateafter = attributeNodeMap.get("X509Certificate.NotAfter").replace("\u200e", "");
                SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                Date pki_datebestart = datetimeFormat.parse(pki_datebefore);
                Date pki_datebeend = datetimeFormat.parse(pki_dateafter);
                log.info("证书时间：" + pki_datebestart + "-" + pki_datebeend);
                if (DateTools.between(pki_datebestart, pki_datebeend, DateTools.now())) {
                    // 用户信息
                    String pki_userinfo = attributeNodeMap.get("X509Certificate.SubjectDN");
                    log.info(pki_userinfo);
                    String[] userinfos = pki_userinfo.split(",");
                    String keyInfo = userinfos[0];
                    for (String userinfo : userinfos) {
                        if (userinfo.contains("CN=")) {
                            keyInfo = userinfo;
                        }
                    }
                    keyInfo = keyInfo.replaceAll("CN=", "");
                    String[] keyInfos = keyInfo.split(" ");
                    //TODO 20180207 待正常使用后废除测试证书的验证方式
                    String uName = null;
                    String uIDCard = null;
                    if ("test20150608".equals(keyInfo) || "安全审计员_L".equals(keyInfo)) {
                        uName = keyInfo;
                        uIDCard = keyInfo;
                    } else {
                        if (keyInfos.length < 2) {
                            log.error("获取key信息长度不正确");
                            return null;
                        }
                        uName = keyInfos[0].trim();
                        uIDCard = keyInfos[1].trim();
                    }
                    log.info("证书提供的用户名：" + uName);
                    log.info("证书提供的用户身份证号：" + uIDCard);
                    //根据登录名获取用户信息
                    Map param = new HashMap();
                    param.put("idCode", uIDCard);
                    Userinfo user = userinfoService.findByCardId(uIDCard);
                    return user;
                } else {
                    log.error("证书已不在有效期内");
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return new ResponseDatas("登录认证时发生错误！", HttpStatus.UNAUTHORIZED);
        return null;
    }


    @ApiOperation(value = "登录，按照用户名和密码进行查询", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "查询成功", response = HashMap.class),
            @ApiResponse(code = 500, message = "查询失败")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", required = true, value = "用户名", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", required = true, value = "密码", paramType = "query", dataType = "string"),
    })
    @RequestMapping(value = "/login1", method = RequestMethod.POST)
    public ResponseBean login1(@Param(value = "username") String username, @Param(value = "password") String password) {
        if (userinfoService.findBy("username", username).size() < 1) {
            return Result.find(CfI.R_USER_NOTEXIST_FAIL).toBean();
        }
        Userinfo user = userinfoService.findByUsernameAndPwd(username, MD5utils.getMd5(password));
        if (user == null) {
            return Result.find(CfI.R_USER_LOGINFAIL_FAIL).toBean();
        }
        if (user.getDelFlag()) {
            return Result.find(CfI.R_USER_NOTEXIST_FAIL).toBean();
        }
        if (!user.getAble()) {
            return Result.find(CfI.R_USER_DISABLE_FAIL).toBean();
        }
        return Result.find(CfI.R_USER_LOGIN_SUCCESS).setData(user).toBean();
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
        Userinfo user = userinfoService.getUserInfoById(id);
        return getResponseBean(Result.QUERY_SUCCESS.setData(user));
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
        List<Userinfo> userlist = userinfoService.findUserinfosByDepartid(departId);
        List<Userinfo> tempList = new ArrayList<Userinfo>(userlist.size());
        userlist.forEach(x -> tempList.add(x.u()));userlist.clear();
        userlist = tempList;
        return getResponseBean(Result.QUERY_SUCCESS.setData(userlist));
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
        Userinfo userinfo = userinfoService.getOne(userId);
        return getResponseBean(Result.QUERY_SUCCESS.setData(userinfo.getDuty().getDlevel()));
    }


    /***
     * @return
     * @desc 字段/分类列表
     * @author 杨勇 18-8-20
     */
    @ApiOperation(value = "[01] - 字典下拉")
    @RequestMapping(value = "/listDictions", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keys", value = "父类keys", paramType = "query", dataType = "String"),
    })
    @ResponseBody
    public ResponseBean listDictions(String keys) {
        if (StringUtils.isBlank(keys)) {
            return getResponseBean(Result.QUERY_SUCCESS);
        }
        List<Diction> list = dictionService.executeNativeQuerySql(MessageFormat.format("select * from {0} e where e.pid in (select id from {0} where keys = ?) order by keys", SqlUtils.tableName(Diction.class)), keys);
        return getResponseBean(Result.QUERY_SUCCESS.setData( JSONUtils.autoRemoveField(JSONObject.toJSON(list), true, "pid", "dlevel", "id", "loading", "expand", "children")));
    }

    /**
     * @Author lizhidong
     * @Date 2019年1月22日17:03:42
     */
    @ApiOperation(value = "获得字典值", httpMethod = "GET")
    @ApiImplicitParam(name = "key", value = "父类KEY", required = true, paramType = "query", dataType = "string")
    @RequestMapping(value = "/dictionKV", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean dictionKV(@RequestParam String key) {
        if (StringUtils.isNotBlank(key)) {
            List<Diction> list = dictionService.executeNativeQuerySql(MessageFormat.format( "select * from {0} e where e.pid in (select id from {0} where keys = ?) order by keys",SqlUtils.tableName(Diction.class)), key);
            Map<String, Object> map = new HashMap<>();
            for (Diction diction : list) {
                map.put(diction.getKeys(), diction.getVal());
            }
            return getResponseBean(Result.QUERY_SUCCESS.setData(map));
        }
        return getResponseBean(Result.QUERY_FAIL.setData("未传参"));
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
            return getResponseBean(Result.QUERY_SUCCESS.setData(value));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    @ApiOperation(value = "根据用户账号查询用户[USER-4]", notes = "根据用户账号查询用户实体", response = Userinfo.class,httpMethod = "GET")
    @RequestMapping(value = "/userByUsername", method = RequestMethod.GET)
    @ApiImplicitParam(name = "username", value = "用户账号", required = true, paramType = "query", dataType = "string")
    @ResponseBody
    public ResponseBean userByUsername(@ApiParam(value = "用户账号", required = true) @RequestParam String username) {
        try{
            List<Userinfo> users = userinfoService.findBy("username",username);
            StringBuilder sb = new StringBuilder();
            Userinfo user = new Userinfo();
            if (0 < users.size()) {
                user = users.get(0);
                if (user.getOperGroup() != null) {
                    if (user.getOperGroup().getOperateSet() != null && user.getOperGroup().getOperateSet().size() > 0) {
                        for (Operate o : user.getOperGroup().getOperateSet()) {
                            sb.append(o.getOcode() + ",");
                        }
                    }
                }
                user.setOcodeList(getOcodeList(sb.toString()));
            }
            if(users != null && users.size() == 1){
                return getResponseBean(Result.QUERY_SUCCESS.setData(user));
            }else{
                return getResponseBean(Result.QUERY_SUCCESS);
            }
        }catch (Exception e){
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    /**
     * @Author fanhua
     */
    @ApiOperation(value = "根据id查询用户权限", notes = "返回用户权限，该用户为未删除用户，如果查询的是已删除用户，则返回空", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "请输入用户id", paramType = "path", dataType = "int"),
    })
    @RequestMapping(value = "codeById/{id}", method = RequestMethod.GET)
    @CrossOrigin(allowCredentials = "true", maxAge = 3600)
    public ResponseBean ucodeById(@PathVariable Integer id) {
        try {
            Userinfo user = userinfoService.getUserInfoById(id);
            StringBuilder sb = new StringBuilder();
            if (user.getOperGroup() != null) {
                if (user.getOperGroup().getOperateSet() != null && user.getOperGroup().getOperateSet().size() > 0) {
                    for (Operate o : user.getOperGroup().getOperateSet()) {
                        sb.append(o.getOcode() + ",");
                    }
                }
            }
            return getResponseBean(Result.QUERY_SUCCESS.setData(sb.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            return getResponseBean(Result.QUERY_FAIL);
        }
    }

    public Set<String> getOcodeList(String ocodes) {
        Set<String> ocodeList = new HashSet<>();
        if (StringUtils.isNotBlank(ocodes)) {
            String[] ocodeArr = ocodes.split(",");
            for (String code : ocodeArr) {
                ocodeList.add(code);
            }
        }
        return ocodeList;
    }

}