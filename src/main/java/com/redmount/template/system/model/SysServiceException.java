package com.redmount.template.system.model;

import com.redmount.template.core.BaseDO;

import javax.persistence.*;

/**
 * @author 朱峰
 * @date 2018年11月19日
 */
@Table(name = "sys_service_exception")
public class SysServiceException extends BaseDO {

    private Integer code;

    /**
     * 异常标题
     */
    private String title;

    /**
     * 异常信息主体
     */
    private String message;

    /**
     * 造成异常的原因
     */
    private String reason;

    /**
     * 建议操作
     */
    private String suggest;

    /**
     * @return code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @param code
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 获取异常标题
     *
     * @return title - 异常标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置异常标题
     *
     * @param title 异常标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取异常信息主体
     *
     * @return message - 异常信息主体
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置异常信息主体
     *
     * @param message 异常信息主体
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取造成异常的原因
     *
     * @return reason - 造成异常的原因
     */
    public String getReason() {
        return reason;
    }

    /**
     * 设置造成异常的原因
     *
     * @param reason 造成异常的原因
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * 获取建议操作
     *
     * @return suggest - 建议操作
     */
    public String getSuggest() {
        return suggest;
    }

    /**
     * 设置建议操作
     *
     * @param suggest 建议操作
     */
    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }
}
