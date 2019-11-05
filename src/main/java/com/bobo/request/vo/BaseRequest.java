package com.bobo.request.vo;

import lombok.Data;

/**
 * @author bobo
 * @Description:
 * @date 2019/11/5 9:46 上午
 */
@Data
public class BaseRequest<T> {
    private T data;
}
