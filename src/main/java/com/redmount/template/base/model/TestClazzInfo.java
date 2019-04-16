package com.redmount.template.base.model;

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
@Table(name = "test_clazz_info")
@ApiModel("TestClazzInfo（）")
@Data
@RelationData(baseDOTypeName = "TestClazzInfo")
public class TestClazzInfo extends BaseDO implements Serializable {
    @Column(name = "class_pk")
    @ApiModelProperty(value = "")
    private String classPk;

    @ApiModelProperty(value = "")
    private byte[] img;

    private static final long serialVersionUID = 1L;
}