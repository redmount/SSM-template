package com.redmount.template.base.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.base.model.UserContactInfo;
import com.redmount.template.base.service.UserContactInfoBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* Created by CodeGenerator on 2021/11/01.
*/
@RestController
@RequestMapping("/userContactInfo")
public class UserContactInfoController extends AbstractController<UserContactInfo> {
    @Autowired
    private UserContactInfoBaseService baseService;

    @Override
    public void init() {
        super.service = baseService;
    }
}
