package com.redmount.template.base.model;

import com.redmount.template.base.repo.UserContactInfoMapper;
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
@Table(name = "user_contact_info")
@ApiModel("UserContactInfo（）")
@Data
@LogicDeletion
@RelationData(baseDOClass = UserContactInfo.class, baseDOMapperClass = UserContactInfoMapper.class)
public class UserContactInfo extends BaseDOLogicDeletion implements Serializable {
    /**
     * 用户PK
     */
    @Column(name = "user_pk")
    @ApiModelProperty(value = "用户PK")
    private String userPk;

    /**
     * 用户座机号
     */
    @Column(name = "fixed_line")
    @ApiModelProperty(value = "用户座机号")
    private String fixedLine;

    /**
     * 办公室地址
     */
    @Column(name = "office_address")
    @ApiModelProperty(value = "办公室地址")
    private String officeAddress;

    /**
     * 所属组织机构
     */
    @Column(name = "organization_pk")
    @ApiModelProperty(value = "所属组织机构")
    private String organizationPk;

    /**
     * 此条记录的创建者PK
     */
    @ApiModelProperty(value = "此条记录的创建者PK")
    private String creator;

    /**
     * 此条记录的最后一次修改者PK
     */
    @ApiModelProperty(value = "此条记录的最后一次修改者PK")
    private String updator;

    private static final long serialVersionUID = 1L;
}