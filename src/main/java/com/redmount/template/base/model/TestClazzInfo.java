package com.redmount.template.base.model;

import com.redmount.template.base.repo.TestClazzInfoMapper;
import com.redmount.template.core.BaseDO;
import com.redmount.template.core.annotation.RelationData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Mybatis Generator
 */
@Table(name = "test_clazz_info")
@ApiModel("TestClazzInfo（）")
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