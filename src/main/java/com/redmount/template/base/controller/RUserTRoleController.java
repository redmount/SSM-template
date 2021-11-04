package com.redmount.template.base.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.base.model.RUserTRole;
import com.redmount.template.base.service.RUserTRoleBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* Created by CodeGenerator on 2021/11/04.
*/
@RestController
@RequestMapping("/rUserTRole")
public class RUserTRoleController extends AbstractController<RUserTRole> {
    @Autowired
    private RUserTRoleBaseService baseService;

    @Override
    public void init() {
        super.service = baseService;
    }
}
