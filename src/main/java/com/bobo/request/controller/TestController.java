package com.bobo.request.controller;

import com.bobo.request.entity.User;
import com.bobo.request.vo.BaseRequest;
import com.bobo.request.vo.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟请求加密的controller
 */
@RestController
@Slf4j
public class TestController {

    @PostMapping("/")
    public BaseResponse test(@RequestBody BaseRequest<User> baseRequest) {

        User user = baseRequest.getData();
        log.info("请求user:{}", user);
        return new BaseResponse<>(user);
    }

    @PostMapping("/parameter")
    public BaseResponse testParameter(@RequestBody BaseRequest<User> baseRequest, @RequestParam("id") long id) {

        User user = baseRequest.getData();
        log.info("请求user:{},id:{}", user, id);
        return new BaseResponse<>(user);
    }



}
