package com.redmount.template.core;

/**
 * 响应码枚举，参考HTTP状态码的语义
 * @author 朱峰
 * @date 2018年11月9日
 */
public enum ResultCode {
    /**
     * 成功,200
     */
    SUCCESS(200),
    /**
     * 失败,400
     */
    FAIL(400),
    /**
     * 未认证,401
     */
    UNAUTHORIZED(401),
    /**
     * 接口不存在,404
     */
    NOT_FOUND(404),
    /**
     * 业务级异常,412
     */
    SERVICE_EXCEPTION(412),
    /**
     * 服务器内部异常,500
     */
    INTERNAL_SERVER_ERROR(500);

    private final int code;

    ResultCode(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
