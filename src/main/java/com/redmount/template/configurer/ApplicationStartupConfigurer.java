package com.redmount.template.configurer;

import com.redmount.template.core.exception.ServiceException;
import com.redmount.template.system.model.SysServiceException;
import com.redmount.template.system.service.SysServiceExceptionBaseService;
import com.redmount.template.system.service.impl.SysServiceExceptionBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

/**
 * 启动后直接执行的注册类
 *
 * @author 朱峰
 * @date 2018年11月12日
 */
public class ApplicationStartupConfigurer implements ApplicationListener<ContextRefreshedEvent> {
    private final Logger logger = LoggerFactory.getLogger(ApplicationStartupConfigurer.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (ServiceException.ERROR_MAP.size() == 0) {
            loadServiceExceptions(contextRefreshedEvent);
        }
    }

    /**
     * 将数据库中的sys_service_exception表中的业务异常信息取出,放在内存中.
     *
     * @param contextRefreshedEvent 上下文刷新事件
     */
    private void loadServiceExceptions(ContextRefreshedEvent contextRefreshedEvent) {
        SysServiceExceptionBaseService service = contextRefreshedEvent.getApplicationContext().getBean(SysServiceExceptionBaseServiceImpl.class);
        List<SysServiceException> all = service.findAll();
        for (SysServiceException ex : all) {
            ServiceException.ERROR_MAP.put(ex.getCode(), ex);
        }
    }
}
