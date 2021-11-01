package com.redmount.template.base.model;

import com.redmount.template.base.repo.RRoleTAuthorityMapper;
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
@Table(name = "r_role_t_authority")
@ApiModel("RRoleTAuthority（）")
@Data
@RelationData(baseDOClass = RRoleTAuthority.class, baseDOMapperClass = RRoleTAuthorityMapper.class)
public class RRoleTAuthority extends BaseDO implements Serializable {
    /**
     * 角色pk
     */
    @Column(name = "role_pk")
    @ApiModelProperty(value = "角色pk")
    private String rolePk;

    /**
     * 权限pk
     */
    @Column(name = "authority_pk")
    @ApiModelProperty(value = "权限pk")
    private String authorityPk;

    /**
     * 本条数据创建者PK
     */
    @ApiModelProperty(value = "本条数据创建者PK")
    private String creator;

    private static final long serialVersionUID = 1L;
}