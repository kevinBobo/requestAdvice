package com.bobo.request.filter;

import com.bobo.request.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 对parameter参数进行加解密
 */
//@WebFilter
@Slf4j
public class SecurityParamsFilter implements Filter {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Map<String, String[]> parameterMap = servletRequest.getParameterMap();
        Set<String> keys = parameterMap.keySet();
        Map<String, String[]> map = new HashMap<>();
        for (String key : keys) {
            String[] values = parameterMap.get(key);
            for (int i = 0; i < values.length; i++) {
                values[i] = SecurityUtils.decry(values[i]);
            }
            map.put(key, values);
        }
        SecurityParamsServletRequest request = new SecurityParamsServletRequest((HttpServletRequest) servletRequest, map);
        filterChain.doFilter(request, servletResponse);
    }
}
