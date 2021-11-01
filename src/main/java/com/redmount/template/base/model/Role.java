package com.redmount.template.base.model;

import com.redmount.template.base.repo.RoleMapper;
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
@ApiModel("Role（）")
@Data
@LogicDeletion
@RelationData(baseDOClass = Role.class, baseDOMapperClass = RoleMapper.class)
public class Role extends BaseDOLogicDeletion implements Serializable {
    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称")
    private String name;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 本条数据创建者PK
     */
    @ApiModelProperty(value = "本条数据创建者PK")
    private String creator;

    /**
     * 本条数据最后一次修改者PK
     */
    @ApiModelProperty(value = "本条数据最后一次修改者PK")
    private String updator;

    private static final long serialVersionUID = 1L;
}