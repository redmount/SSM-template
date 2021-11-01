package com.redmount.template.base.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.base.model.UserBaseInfo;
import com.redmount.template.base.service.UserBaseInfoBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* Created by CodeGenerator on 2021/11/01.
*/
@RestController
@RequestMapping("/userBaseInfo")
public class UserBaseInfoController extends AbstractController<UserBaseInfo> {
    @Autowired
    private UserBaseInfoBaseService baseService;

    @Override
    public void init() {
        super.service = baseService;
    }
}
