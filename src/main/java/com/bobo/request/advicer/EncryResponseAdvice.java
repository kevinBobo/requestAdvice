package com.bobo.request.advicer;

import com.alibaba.fastjson.JSON;
import com.bobo.request.util.SecurityUtils;
import com.bobo.request.vo.BaseResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

//@ControllerAdvice
public class EncryResponseAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof BaseResponse) {
            BaseResponse baseResponse = (BaseResponse) body;
            Object data = baseResponse.getData();
            baseResponse.setData(SecurityUtils.encry(JSON.toJSONString(data)));
            return baseResponse;
        }
        return body;
    }
}
