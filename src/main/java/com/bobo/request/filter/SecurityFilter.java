package com.bobo.request.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter
public class SecurityFilter implements Filter {



    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        SecurityServletRequest securityServletRequest = new SecurityServletRequest((HttpServletRequest) servletRequest);
        filterChain.doFilter(securityServletRequest,servletResponse);

    }
}
