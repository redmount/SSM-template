package com.redmount.template.base.model;

import com.redmount.template.base.repo.TestClazzMapper;
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
@Table(name = "test_clazz")
@ApiModel("TestClazz（）")
@Data
@RelationData(baseDOClass = TestClazz.class, baseDOMapperClass = TestClazzMapper.class)
public class TestClazz extends BaseDO implements Serializable {
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

    @Column(name = "nick_name")
    @ApiModelProperty(value = "")
    private String nickName;

    @ApiModelProperty(value = "")
    private String detail;

    private static final long serialVersionUID = 1L;
}