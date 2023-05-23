package com.redmount.template.base.model;

import com.redmount.template.base.repo.TestClazzInfoMapper;
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
@ApiModel("TestClazzInfo（班级信息扩展对象）")
@Data
@RelationData(baseDOClass = TestClazzInfo.class, baseDOMapperClass = TestClazzInfoMapper.class)
public class TestClazzInfo extends BaseDO implements Serializable {
    @Column(name = "clazz_pk")
    @ApiModelProperty(value = "")
    private String clazzPk;

    @ApiModelProperty(value = "")
    private String introduction;

    @ApiModelProperty(value = "")
    private String info;

    private static final long serialVersionUID = 1L;
}