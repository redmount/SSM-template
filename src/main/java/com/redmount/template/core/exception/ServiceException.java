package com.redmount.template.core.exception;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务（业务）异常如“ 账号或密码错误 ”，该异常只做INFO级别的日志记录 @see WebMvcConfigurer
 *
 * @author 朱峰
 * @date 2018年11月9日
 */
@Data
@Service
public class ServiceException extends RuntimeException {
    Logger logger = LoggerFactory.getLogger(ServiceException.class);
    private SysServiceExceptionDO exception;

    public ServiceException() {
    }

    public ServiceException(int code) {
        super("业务异常:"+code);
        this.exception = ERROR_MAP.get(code);
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public final static Map<Integer, SysServiceExceptionDO> ERROR_MAP = new HashMap<Integer, SysServiceExceptionDO>();

}
