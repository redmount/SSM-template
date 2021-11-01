package com.redmount.template.base.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.base.model.User;
import com.redmount.template.base.service.UserBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* Created by CodeGenerator on 2021/11/01.
*/
@RestController
@RequestMapping("/user")
public class UserController extends AbstractController<User> {
    @Autowired
    private UserBaseService baseService;

    @Override
    public void init() {
        super.service = baseService;
    }
}
