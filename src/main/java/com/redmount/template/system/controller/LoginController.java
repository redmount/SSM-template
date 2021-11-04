package com.redmount.template.system.controller;

import com.redmount.template.base.model.User;
import com.redmount.template.base.service.UserBaseService;
import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.core.exception.ServiceException;
import com.redmount.template.util.JwtUtil;
import com.redmount.template.util.PasswordUtil;
import com.redmount.template.util.ValidateCodeModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Condition;

import java.net.UnknownServiceException;
import java.util.List;

@RestController
@RequestMapping("login")
public class LoginController {
    @Autowired
    UserBaseService baseService;

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        Condition condition = new Condition(User.class);

        if (ValidateCodeModel.isEmail(user.getName())) { // 输入的是电子邮件地址
            condition.createCriteria().andEqualTo("email", user.getName());
        } else if (ValidateCodeModel.isMobile(user.getName())) { // 输入的是手机号
            condition.createCriteria().andEqualTo("mobile", user.getName());
        } else { // 非电子邮件地址, 非手机号, 即为用户名登录
            condition.createCriteria().andEqualTo("name", user.getName());
        }
        List<User> userInDBList = baseService.findByCondition(condition);
        if (userInDBList.size() == 0) {
            // 没有查到用户
            throw new ServiceException(100001);
        }
        User userInDB = userInDBList.get(0);
        boolean isValidate = PasswordUtil.validatePasswordWithSalt(user.getPassword(), userInDB.getPassword(), userInDB.getSalt());
        if (!isValidate) {
            throw new ServiceException(100002);
        }
        // 至此, 已经验证成功, 需要生成JWT
        // 生成之前, 去掉salt和password属性
        userInDB.setPassword(null);
        userInDB.setSalt(null);
        String jwt = JwtUtil.createJWT(userInDB);
        return ResultGenerator.genSuccessResult(jwt);
    }
}
