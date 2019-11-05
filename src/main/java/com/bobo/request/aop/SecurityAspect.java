package com.bobo.request.aop;

import com.bobo.request.util.SecurityUtils;
import com.bobo.request.vo.BaseRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 由于data在接收之前就要转换为接收的参数导致转换报错,放弃aop这种方式
 * 强行使用的话需要用map或者jsonobject这种通用参数,不推荐
 *
 * @author bobo
 * @Description:
 * @date 2019/11/5 9:11 上午
 */
//@Aspect
//@Component
@Slf4j
public class SecurityAspect {

    @Pointcut("execution(* com.bobo.request.controller.*.*(..))")
    public void security() {
    }

    ;


    @Before("security()")
    public void before(JoinPoint joinPoint) {

    }


    @Around("security()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        return pjp.proceed();
    }

    @After("security()")
    public void after() {
        log.info("after");
    }
}
