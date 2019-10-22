package com.xajiusuo.busi.filter;

import com.alibaba.fastjson.JSON;
import com.xajiusuo.busi.out.controller.OutLogController;
import com.xajiusuo.jpa.param.e.Result;
import com.xajiusuo.jpa.param.entity.UserContainer;
import com.xajiusuo.jpa.util.RsaKeyUtil;
import com.xajiusuo.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by Administrator on 2018/3/6.
 */
//@Configuration
@Slf4j
@WebFilter(urlPatterns = "/api/*",filterName = "userFlowFilter")
public class UserFlowFilter implements Filter{

    private FilterConfig filterConfig;


    private OutLogController logController;



    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        WebApplicationContext wc = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        logController = wc.getBean(OutLogController.class);

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        UserContainer.setUser(UserUtil.getCurrentUser((HttpServletRequest) servletRequest),null);
        filterChain.doFilter(servletRequest,servletResponse);
    }


//    @Bean
//    public RemoteIpFilter remoteIpFilter() {
//        return new RemoteIpFilter();
//    }
//
//    @Value("${cas.authentication-url-patterns[0]}")
//    private String patterns;
//
//    @Bean
//    public FilterRegistrationBean indexFilterRegistrationBean() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        registrationBean.setFilter(new MyUserInfoFilter());
//        registrationBean.setName("MyUserInfoFilter");
//        registrationBean.setOrder(889);
//        registrationBean.addUrlPatterns(patterns);
//        return registrationBean;
//    }
//
//    public class MyUserInfoFilter implements Filter {
//
//        public OperateService operateService;
//
//        @Override
//        public void init(FilterConfig filterConfig) throws ServletException {
//            WebApplicationContext wc = WebApplicationContextUtils
//                    .getWebApplicationContext(filterConfig.getServletContext());
//            System.out.println("进入init!");
//            if (operateService == null) {
//                operateService = wc.getBean(OperateService.class);
//            }
//        }
//
//        @Override
//        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//            HttpServletRequest request = (HttpServletRequest) servletRequest;
//            HttpServletResponse response = (HttpServletResponse) servletResponse;
//            Object casAssertion = request.getSession().getAttribute("_const_cas_assertion_");
//            Assertion assertion = (Assertion) casAssertion;
//            if (null == assertion && (request.getRequestURI().indexOf("/out/") != -1) ) {
//                filterChain.doFilter(servletRequest, servletResponse);
//            } else {
//                AttributePrincipal attributePrincipal = assertion.getPrincipal();
//                Map<String, Object> cas_map = attributePrincipal.getAttributes();
//                ArrayList userCode = new ArrayList();
//                //获得用户拥有的操作CODE
//                for (String k : cas_map.keySet()) {
//                    if ("ocodes".equals(k)) {
//                        String[] os = ((String) cas_map.get(k)).split(",");
//                        for (String s : os) {
//                            userCode.add(s);
//                        }
//                    }
//                }
//                //先从session中拿url 如果没有再去组织机构中获取
//                ArrayList<String> caseManageOperation = (ArrayList<String>) request.getSession().getAttribute("userManageOperation");//
//                if (caseManageOperation == null) {
//                    ArrayList<String> arrayList = new ArrayList<String>();
//                    Map<String, List<Operate>> map1 = operateService.operaterByGroupList("用户");
//                    List<Operate> ls = map1.get("用户");
//                    if (ls != null) {
//                        for (Operate o : ls) {
//                            if (!userCode.contains(o.getOcode())) {
//                                arrayList.add(o.getOurl());
//                            }
//                        }
//                    }
//                    request.getSession().setAttribute("userManageOperation", arrayList);
//                }
//                if (isExcludedUri((ArrayList<String>) request.getSession().getAttribute("userManageOperation"), request.getRequestURI())) {
//                    response.sendRedirect(request.getContextPath() + "/api/village/userinfo/userinfoFail");
//                } else {
//                    filterChain.doFilter(servletRequest, servletResponse);
//                }
//            }
//
//        }
//
//        @Override
//        public void destroy() {
//
//        }
//
//        private boolean isExcludedUri(ArrayList<String> al, String uri) {
//            if (al != null && al.size() > 0 && al.contains(uri)) {
//                return true;
//            }
//            return false;
//        }
//    }

}
