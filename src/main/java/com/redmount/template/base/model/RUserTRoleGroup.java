package com.redmount.template.base.model;

import com.redmount.template.base.repo.RUserTRoleGroupMapper;
import com.redmount.template.core.BaseDO;
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
@Table(name = "r_user_t_role_group")
@ApiModel("RUserTRoleGroup（）")
@Data
@RelationData(baseDOClass = RUserTRoleGroup.class, baseDOMapperClass = RUserTRoleGroupMapper.class)
public class RUserTRoleGroup extends BaseDO implements Serializable {
    /**
     * 用户pk
     */
    @Column(name = "user_pk")
    @ApiModelProperty(value = "用户pk")
    private String userPk;

    /**
     * 角色组pk
     */
    @Column(name = "role_group_pk")
    @ApiModelProperty(value = "角色组pk")
    private String roleGroupPk;

    /**
     * 本条数据创建者PK
     */
    @ApiModelProperty(value = "本条数据创建者PK")
    private String creator;

    /**
     * 本条记录最后一次修改者
     */
    @ApiModelProperty(value = "本条记录最后一次修改者")
    private String updater;

    private static final long serialVersionUID = 1L;
}