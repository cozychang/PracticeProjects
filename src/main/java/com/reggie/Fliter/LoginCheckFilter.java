package com.reggie.Fliter;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符*
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取当前请求路径
        String requestURI = request.getRequestURI();

        //定义可放行路径
        String[] noCheckURIs = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",//移动端发送短信
                "/user/login"//移动端登录
        };

        //判断此要求是否要处理，写个方法
        boolean check = checkURI(requestURI, noCheckURIs);

        //匹配上了，放行
        if(check){
            //放行
            filterChain.doFilter(request,response);

            return;
        }


        //判断客户端登录状态，已登录则放行
        if(request.getSession().getAttribute("employee") != null){
            //放行

            long id = Thread.currentThread().getId();
            BaseContext.setCurrentId(id);
            filterChain.doFilter(request,response);
            return;
        }

        //判断移动端登录状态，已登录则放行
        if(request.getSession().getAttribute("user") != null){
            //放行

            long id = Thread.currentThread().getId();
            BaseContext.setCurrentId(id);
            filterChain.doFilter(request,response);
            return;
        }

        //未登录则跳转页面
        //前端已经写了跳转页面的拦截器，我们只需要发送JSON触发即可
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    public boolean checkURI(String requestUrl, String[] noCheckUrls){
        for (String checkUrl : noCheckUrls) {
            if(PATH_MATCHER.match(checkUrl, requestUrl)){//匹配上了
                return true;
            }
        }

        return false;
    }
}
