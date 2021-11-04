package com.redmount.template.base.model;

import com.redmount.template.base.repo.UserMapper;
import com.redmount.template.core.BaseDOLogicDeletion;
import com.redmount.template.core.annotation.LogicDeletion;
import com.redmount.template.core.annotation.RelationData;
import com.redmount.template.core.annotation.Validate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

/**
 * @author Mybatis Generator
 */
@ApiModel("User（）")
@Data
@LogicDeletion
@RelationData(baseDOClass = User.class, baseDOMapperClass = UserMapper.class)
public class User extends BaseDOLogicDeletion implements Serializable {
    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String name;

    /**
     * 用户密码MD5
     */
    @ApiModelProperty(value = "用户密码MD5")
    private String password;

    /**
     * 用户名密码盐
     */
    @ApiModelProperty(value = "用户名密码盐")
    private String salt;

    /**
     * 用户真实姓名
     */
    @Column(name = "real_name")
    @ApiModelProperty(value = "用户真实姓名")
    private String realName;

    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    private String mobile;

    /**
     * 用户关联的email
     */
    @ApiModelProperty(value = "用户关联的email")
    private String email;

    /**
     * 是否为U盾用户
     */
    @Column(name = "u_key_user")
    @ApiModelProperty(value = "是否为U盾用户")
    private Boolean uKeyUser;

    /**
     * U盾的CN项
     */
    @Column(name = "u_key_cn")
    @ApiModelProperty(value = "U盾的CN项")
    private String uKeyCn;

    /**
     * 用户是否已被禁用
     */
    @ApiModelProperty(value = "用户是否已被禁用")
    private Boolean disabled;

    /**
     * 创建者PK
     */
    @ApiModelProperty(value = "创建者PK")
    private String creator;

    /**
     * 最后一次修改者PK
     */
    @ApiModelProperty(value = "最后一次修改者PK")
    private String updator;

    private static final long serialVersionUID = 1L;
}