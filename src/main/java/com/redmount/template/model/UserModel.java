package com.redmount.template.model;

import com.redmount.template.base.model.*;
import com.redmount.template.base.repo.*;
import com.redmount.template.core.annotation.RelationData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@RelationData(baseDOClass = User.class, baseDOMapperClass = UserMapper.class)
public class UserModel extends User {
    @RelationData(baseDOClass = UserBaseInfo.class, baseDOMapperClass = UserBaseInfoMapper.class, mainProperty = "userPk")
    @ApiModelProperty("用户基础信息")
    private UserBaseInfo baseInfo;

    @RelationData(baseDOClass = UserContactInfo.class, baseDOMapperClass = UserContactInfoMapper.class, mainProperty = "userPk")
    @ApiModelProperty("用户联系信息")
    private UserContactInfo contactInfo;

    @RelationData(baseDOClass = Role.class, baseDOMapperClass = RoleMapper.class, isManyToMany = true,
            relationDOClass = RUserTRole.class, relationDOMapperClass = RUserTRoleMapper.class,
            foreignProperty = "rolePk", mainProperty = "userPk")
    @ApiModelProperty("角色列表")
    private List<Role> roleList;


}
