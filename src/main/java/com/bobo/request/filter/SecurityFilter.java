package com.bobo.request.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bobo.request.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebFilter
@Slf4j
public class SecurityFilter implements Filter {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        SecurityServletRequest securityServletRequest = new SecurityServletRequest((HttpServletRequest) servletRequest);
        SecurityServletResponse securityServletResponse = new SecurityServletResponse((HttpServletResponse) servletResponse);
        filterChain.doFilter(securityServletRequest, securityServletResponse);
        ServletOutputStream out = servletResponse.getOutputStream();
        byte[] content = securityServletResponse.getContent();
        JSONObject jsonObject = JSON.parseObject(new String(content));
        jsonObject.put("data",SecurityUtils.encry(jsonObject.getString("data")));
        out.write(jsonObject.toJSONString().getBytes());
        out.flush();
    }
}
