package com.redmount.template.system.controller;

import com.redmount.template.base.model.User;
import com.redmount.template.base.service.UserBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Condition;

import java.net.UnknownServiceException;

@RestController
@RequestMapping("login")
public class LoginController {
    @Autowired
    UserBaseService baseService;
    public User login(@RequestBody User user){
        Condition condition=new Condition(User.class);
//        condition.createCriteria().andEqualTo(propertyName,);
//        User userInDB=baseService.findByCondition()
        return user;
    }
}
