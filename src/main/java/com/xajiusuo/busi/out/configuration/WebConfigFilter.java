package com.xajiusuo.busi.out.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

//@Configuration
public class WebConfigFilter {

    @Value("${url-patterns}")
    private String patterns;

    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        //新建过滤器注册类
        FilterRegistrationBean registration = new FilterRegistrationBean();
        // 添加我们写好的过滤器
        registration.setFilter( new UserFilter());
        // 设置过滤器的URL模式
        registration.addUrlPatterns("/*");
        //设置过滤器顺序
        registration.setOrder(888);
        return registration;
    }
    public class UserFilter  implements Filter {

        //不需要登录就可以访问的路径(比如:注册登录等)
        //String[] includeUrls = new String[]{"/api/uf/login/goLogin"};


        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            HttpSession session = request.getSession(false);
            String uri = request.getRequestURI();
            //是否需要过滤
            boolean needFilter = isNeedFilter(uri);
            if (!needFilter) { //不需要过滤直接传给下一个过滤器
                filterChain.doFilter(servletRequest, servletResponse);
            } else { //需要过滤器
                // session中包含user对象,则是登录状态
                if (session != null && session.getAttribute("userInfo") != null) {
                    // System.out.println("user:"+session.getAttribute("user"));
                    filterChain.doFilter(request, response);
                } else {
                    response.setStatus(505);
                    return;
                }
            }
        }

        public boolean isNeedFilter(String uri) {
            String[] includeUrls  =patterns.split(";");
            for (String includeUrl : includeUrls) {
                if (includeUrl.equals(uri)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void destroy() {

        }
    }
}
