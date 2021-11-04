package com.redmount.template.base.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.base.model.Role;
import com.redmount.template.base.service.RoleBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* Created by CodeGenerator on 2021/11/04.
*/
@RestController
@RequestMapping("/role")
public class RoleController extends AbstractController<Role> {
    @Autowired
    private RoleBaseService baseService;

    @Override
    public void init() {
        super.service = baseService;
    }
}
