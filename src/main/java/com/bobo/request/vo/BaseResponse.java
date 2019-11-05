package com.bobo.request.vo;

import lombok.Data;

/**
 * @author bobo
 * @Description:
 * @date 2019/11/5 9:48 上午
 */
@Data
public class BaseResponse<T> {

    public BaseResponse(T t) {
        this.code = "0";
        this.msg = "success";
        this.data = t;
    }

    private String code;
    private String msg;
    private T data;
}
