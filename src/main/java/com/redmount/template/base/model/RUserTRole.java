package com.redmount.template.base.model;

import com.redmount.template.base.repo.RUserTRoleMapper;
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
@Table(name = "r_user_t_role")
@ApiModel("RUserTRole（）")
@Data
@RelationData(baseDOClass = RUserTRole.class, baseDOMapperClass = RUserTRoleMapper.class)
public class RUserTRole extends BaseDO implements Serializable {
    /**
     * 用户pk
     */
    @Column(name = "user_pk")
    @ApiModelProperty(value = "用户pk")
    private String userPk;

    /**
     * 角色pk
     */
    @Column(name = "role_pk")
    @ApiModelProperty(value = "角色pk")
    private String rolePk;

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