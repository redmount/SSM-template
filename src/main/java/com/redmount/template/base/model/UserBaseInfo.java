package com.redmount.template.base.model;

import com.redmount.template.base.repo.UserBaseInfoMapper;
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
@Table(name = "user_base_info")
@ApiModel("UserBaseInfo（）")
@Data
@LogicDeletion
@RelationData(baseDOClass = UserBaseInfo.class, baseDOMapperClass = UserBaseInfoMapper.class)
public class UserBaseInfo extends BaseDOLogicDeletion implements Serializable {
    /**
     * 用户PK
     */
    @Column(name = "user_pk")
    @ApiModelProperty(value = "用户PK")
    private String userPk;

    /**
     * 用户真实姓名
     */
    @Column(name = "real_name")
    @ApiModelProperty(value = "用户真实姓名")
    private String realName;

    /**
     * 用户性别, 以字符串记录
     */
    @ApiModelProperty(value = "用户性别, 以字符串记录")
    private String gender;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

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