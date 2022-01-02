package com.redmount.template.core.aspect;

import com.alibaba.fastjson.JSON;
import com.redmount.template.base.model.AuditOperationHistory;
import com.redmount.template.base.model.User;
import com.redmount.template.base.service.AuditOperationHistoryBaseService;
import com.redmount.template.core.annotation.Audit;
import com.redmount.template.util.UserUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

@Aspect
@Order(2)
@Component
public class AuditAspect {
    @Autowired
    AuditOperationHistoryBaseService service;

    @Pointcut("(execution(public * com.redmount.template.controller.*.*(..))) || (execution(public * com.redmount.template.system.controller.*.*(..)))")
    public void audit() {
    }

    @Around("audit()")
    public Object auditToDb(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (!method.isAnnotationPresent(Audit.class)) {
            return joinPoint.proceed();
        }
        long startTimestamp = System.currentTimeMillis();
        Object obj = joinPoint.proceed();
        User user = (User) UserUtil.getUserByToken(User.class);
        new Thread(() -> {
            AuditOperationHistory audit = new AuditOperationHistory();
            audit.setCreator(user.getPk());
            audit.setOperatorPk(user.getPk());
            audit.setDuration(System.currentTimeMillis() - startTimestamp);
            String operation = "";
            if (StringUtils.isNotBlank(method.getAnnotation(Audit.class).value())) {
                operation = method.getAnnotation(Audit.class).value();
            } else if (method.isAnnotationPresent(ApiOperation.class)) {
                operation = method.getAnnotation(ApiOperation.class).value();
            }
            audit.setOperation(operation);
            audit.setOperationArguments(Arrays.toString(joinPoint.getArgs()));
            audit.setOperationFunction(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
            audit.setOperationResult(JSON.toJSONString(obj));
            audit.setOperatorRealName(user.getRealName());
            audit.setOperatorUserName(user.getName());
            audit.setCreated(new Date());
            service.save(audit);
        }).start();
        return obj;
    }
}
