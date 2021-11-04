package com.redmount.template.base.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.base.model.RRoleTAuthority;
import com.redmount.template.base.service.RRoleTAuthorityBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* Created by CodeGenerator on 2021/11/04.
*/
@RestController
@RequestMapping("/rRoleTAuthority")
public class RRoleTAuthorityController extends AbstractController<RRoleTAuthority> {
    @Autowired
    private RRoleTAuthorityBaseService baseService;

    @Override
    public void init() {
        super.service = baseService;
    }
}
