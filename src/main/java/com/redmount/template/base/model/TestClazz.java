package com.redmount.template.base.model;

import com.redmount.template.core.BaseDO;
import com.redmount.template.core.annotation.RelationData;
import com.redmount.template.core.annotation.Validate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Mybatis Generator
 */
@Table(name = "test_clazz")
@ApiModel("TestClazz（）")
@Data
@Accessors(chain = true)
@RelationData(baseDOTypeName = "TestClazz")
public class TestClazz extends BaseDO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "")
    @Validate(nullable = false, stringMaxLength = 36)
    private String pk;

    /**
     * 班级名称
     */
    @ApiModelProperty(value = "班级名称")
    private String name;

    /**
     * 班主任pk
     */
    @Column(name = "adviser_pk")
    @ApiModelProperty(value = "班主任pk")
    private String adviserPk;

    @ApiModelProperty(value = "")
    private Date updated;

    @ApiModelProperty(value = "")
    private Date created;

    @Column(name = "nick_name")
    @ApiModelProperty(value = "")
    private String nickName;

    @ApiModelProperty(value = "")
    private String detail;

    private static final long serialVersionUID = 1L;
}