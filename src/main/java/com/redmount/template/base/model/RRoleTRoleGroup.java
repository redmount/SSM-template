package com.redmount.template.base.model;

import com.redmount.template.base.repo.RRoleTRoleGroupMapper;
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
@Table(name = "r_role_t_role_group")
@ApiModel("RRoleTRoleGroup（）")
@Data
@RelationData(baseDOClass = RRoleTRoleGroup.class, baseDOMapperClass = RRoleTRoleGroupMapper.class)
public class RRoleTRoleGroup extends BaseDO implements Serializable {
    /**
     * 角色组pk
     */
    @Column(name = "role_group_pk")
    @ApiModelProperty(value = "角色组pk")
    private String roleGroupPk;

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