package com.redmount.template.system.model;

import com.redmount.template.core.BaseDO;
import com.redmount.template.core.annotation.RelationData;
import com.redmount.template.system.repo.SysServiceExceptionMapper;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 朱峰
 * @date 2018年11月19日
 */
@Data
@Table(name = "sys_service_exception")
@RelationData(baseDOClass = SysServiceException.class, baseDOMapperClass = SysServiceExceptionMapper.class)
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
}
