package com.redmount.template.base.model;

import com.redmount.template.base.repo.AuthorityMapper;
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
@ApiModel("Authority（）")
@Data
@LogicDeletion
@RelationData(baseDOClass = Authority.class, baseDOMapperClass = AuthorityMapper.class)
public class Authority extends BaseDOLogicDeletion implements Serializable {
    /**
     * 权限码
     */
    @ApiModelProperty(value = "权限码")
    private String code;

    /**
     * 权限名称
     */
    @ApiModelProperty(value = "权限名称")
    private String name;

    /**
     * 权限说明
     */
    @ApiModelProperty(value = "权限说明")
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
    private String updater;

    private static final long serialVersionUID = 1L;
}