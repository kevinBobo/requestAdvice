package com.bobo.request.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.Map;

public class SecurityParamsServletRequest extends HttpServletRequestWrapper {

    private Map<String,String[]> parameters;

    public SecurityParamsServletRequest(HttpServletRequest request,Map<String,String[]> parameters) {
        super(request);
        this.parameters = parameters;
    }

    @Override
    public String getParameter(String name) {
        return super.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return super.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }
}
