package com.redmount.template.base.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.base.model.AuditOperationHistory;
import com.redmount.template.base.service.AuditOperationHistoryBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* Created by CodeGenerator on 2021/11/01.
*/
@RestController
@RequestMapping("/auditOperationHistory")
public class AuditOperationHistoryController extends AbstractController<AuditOperationHistory> {
    @Autowired
    private AuditOperationHistoryBaseService baseService;

    @Override
    public void init() {
        super.service = baseService;
    }
}
