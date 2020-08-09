package com.redmount.template.base.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.base.model.SysFile;
import com.redmount.template.base.service.SysFileBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* Created by CodeGenerator on 2020/08/09.
*/
@RestController
@RequestMapping("/sysFile")
public class SysFileController extends AbstractController<SysFile> {
    @Autowired
    private SysFileBaseService baseService;

    @Override
    public void init() {
        super.service = baseService;
    }
}
