package com.redmount.template.base.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.base.model.RRoleTRoleGroup;
import com.redmount.template.base.service.RRoleTRoleGroupBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* Created by CodeGenerator on 2021/11/01.
*/
@RestController
@RequestMapping("/rRoleTRoleGroup")
public class RRoleTRoleGroupController extends AbstractController<RRoleTRoleGroup> {
    @Autowired
    private RRoleTRoleGroupBaseService baseService;

    @Override
    public void init() {
        super.service = baseService;
    }
}
