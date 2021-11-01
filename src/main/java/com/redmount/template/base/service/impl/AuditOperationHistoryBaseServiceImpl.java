package com.redmount.template.base.service.impl;

import com.redmount.template.base.repo.AuditOperationHistoryMapper;
import com.redmount.template.base.model.AuditOperationHistory;
import com.redmount.template.base.service.AuditOperationHistoryBaseService;
import com.redmount.template.core.AbstractModelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2021/11/01.
 * @author CodeGenerator
 * @date 2021/11/01
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AuditOperationHistoryBaseServiceImpl extends AbstractModelService<AuditOperationHistory> implements AuditOperationHistoryBaseService {
}
