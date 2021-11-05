package com.redmount.template.system.controller;

import com.redmount.template.base.model.User;
import com.redmount.template.base.service.UserBaseService;
import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.core.exception.ServiceException;
import com.redmount.template.util.JwtUtil;
import com.redmount.template.util.PasswordUtil;
import com.redmount.template.util.ValidateCodeModel;
import com.redmount.template.util.ValidateUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import java.net.UnknownServiceException;
import java.util.List;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    UserBaseService baseService;

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        Condition condition = new Condition(User.class);

        if (ValidateUtil.isEmail(user.getName())) { // 输入的是电子邮件地址
            condition.createCriteria().andEqualTo("email", user.getName());
        } else if (ValidateUtil.isMobile(user.getName())) { // 输入的是手机号
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
        String token = getTokenFromUser(userInDB);
        return ResultGenerator.genSuccessResult(token);
    }

    @PostMapping("/refreshToken")
    public Result refreshToken(@RequestHeader("token") String oldToken) {
        User user = (User) JwtUtil.getUserByToken(oldToken, User.class, true);
        String token = getTokenFromUser(user);
        return ResultGenerator.genSuccessResult(token);
    }

    /**
     * 用户换Token
     * 在转换Token前, 将password和salt置空.
     *
     * @param user 用户实体
     * @return 用户Token
     */
    private String getTokenFromUser(User user) {
        // 生成之前, 去掉salt和password属性
        user.setPassword(null);
        user.setSalt(null);
        return JwtUtil.createJWT(user);
    }
}
