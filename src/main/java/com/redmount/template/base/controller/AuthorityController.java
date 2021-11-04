package com.redmount.template.base.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.base.model.Authority;
import com.redmount.template.base.service.AuthorityBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* Created by CodeGenerator on 2021/11/04.
*/
@RestController
@RequestMapping("/authority")
public class AuthorityController extends AbstractController<Authority> {
    @Autowired
    private AuthorityBaseService baseService;

    @Override
    public void init() {
        super.service = baseService;
    }
}
