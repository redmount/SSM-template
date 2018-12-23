package com.redmount.template.core;

import com.alibaba.fastjson.JSON;
import com.redmount.template.core.exception.SysServiceExceptionDO;

/**
 * 统一API响应结果封装
 *
 * @author 朱峰
 * @date 2018年11月12日
 */
public class Result<T> {
    private int code;
    private String message;
    private T data;
    private SysServiceExceptionDO exception;

    public static String fillResultString(Integer code, String message, Object data) {
        Result result = new Result();
        result.code = code;
        result.message = message;
        result.data = data;
        return result.toString();
    }

    public Result setException(SysServiceExceptionDO exception) {
        this.exception = exception;
        return this;
    }

    public SysServiceExceptionDO getException() {
        return this.exception;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public Result setCode(ResultCode resultCode) {
        this.code = resultCode.code();
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
