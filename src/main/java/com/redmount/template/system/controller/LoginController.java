package com.redmount.template.system.controller;

import com.redmount.template.base.model.User;
import com.redmount.template.base.service.UserBaseService;
import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.core.annotation.Audit;
import com.redmount.template.core.exception.ServiceException;
import com.redmount.template.model.LoginModel;
import com.redmount.template.service.AuthorityModelService;
import com.redmount.template.service.UserModelService;
import com.redmount.template.system.model.ValidateCodeModel;
import com.redmount.template.util.*;
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

    @Autowired
    AuthorityModelService authorityModelService;

    /**
     * 登陆
     *
     * @param user 仅包含用户名, 密码的用户实体
     * @return 用户Token
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        Condition condition = new Condition(User.class);
        condition.createCriteria();
        if (ValidateUtil.isEmail(user.getName())) { // 输入的是电子邮件地址
            condition.and().andEqualTo("email", user.getName());
        } else if (ValidateUtil.isMobile(user.getName())) { // 输入的是手机号
            condition.and().andEqualTo("mobile", user.getName());
        } else { // 非电子邮件地址, 非手机号, 即为用户名登录
            condition.and().andEqualTo("name", user.getName());
        }
        // and ( deleted is null or deleted <> true )
        condition.and().orIsNull("deleted").orNotEqualTo("deleted",true);
        List<User> userInDBList = baseService.findByCondition(condition);
        if (userInDBList.size() == 0) {
            // 没有查到用户
            throw new ServiceException(100001);
        }
        User userInDB = userInDBList.get(0);
        if(userInDB.getDisabled()){
            throw new ServiceException(100003);
        }
        boolean isValidate = PasswordUtil.validatePassword(user.getPassword(), userInDB.getPassword(), userInDB.getSalt());
        if (!isValidate) {
            throw new ServiceException(100002);
        }
        // 至此, 已经验证成功, 需要生成JWT
        String token = getTokenFromUser(userInDB);
        return ResultGenerator.genSuccessResult(token);
    }

    /**
     * 待验证码的登录
     * @param model 用户名/密码/验证码/验证码掩码
     * @return 用户token
     */
    @PostMapping("/loginWithValidateCode")
    public Result loginWithValidateCode(@RequestBody LoginModel model) {
        if (!ValidateUtil.isValidateCodeCorrect(model.getInputCode(), model.getCryptCode())) {
            throw new ServiceException(100005);
        }
        User user = new User();
        user.setName(model.getLoginName());
        user.setPassword(model.getPassword());
        return login(user);
    }

    /**
     * 刷新Token
     *
     * @param oldToken
     * @return 新Token
     */
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

    /**
     * 取图片验证码
     *
     * @return 验证码实体
     */
    @GetMapping("/getValidateCode")
    public Result getValidateCode() {
        return ResultGenerator.genSuccessResult(RandomValidateCodeUtil.getRandomCode());
    }

    /**
     * 验证图片验证码
     *
     * @param model 图片验证码实体
     * @return 是否验证通过
     */
    @PostMapping("/validateValidateCode")
    public Result validateValidateCode(@RequestBody ValidateCodeModel model) {
        return ResultGenerator.genSuccessResult(ValidateUtil.isValidateCodeCorrect(model.getInputCode(), model.getCryptCode()));
    }

    /**
     * 取用户权限列表
     *
     * @return 用户权限列表
     */
    @PostMapping("/getUserAuthorityCodeList")
    public Result getUserAuthorityCodeList() {
        User user = (User) UserUtil.getUserByToken(User.class);
        return ResultGenerator.genSuccessResult(authorityModelService.getUserAuthorityCodeList(user.getPk()));
    }
}
