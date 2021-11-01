package com.redmount.template.base.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.base.model.RoleGroup;
import com.redmount.template.base.service.RoleGroupBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* Created by CodeGenerator on 2021/11/01.
*/
@RestController
@RequestMapping("/roleGroup")
public class RoleGroupController extends AbstractController<RoleGroup> {
    @Autowired
    private RoleGroupBaseService baseService;

    @Override
    public void init() {
        super.service = baseService;
    }
}
