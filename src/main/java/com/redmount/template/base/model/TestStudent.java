package com.redmount.template.base.model;

import com.redmount.template.base.repo.TestStudentMapper;
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
@Table(name = "test_student")
@ApiModel("TestStudent（）")
@Data
@RelationData(baseDOClass = TestStudent.class, baseDOMapperClass = TestStudentMapper.class)
public class TestStudent extends BaseDO implements Serializable {
    /**
     * 学生名称
     */
    @ApiModelProperty(value = "学生名称")
    private String name;

    /**
     * 所属班级pk
     */
    @Column(name = "clazz_pk")
    @ApiModelProperty(value = "所属班级pk")
    private String clazzPk;

    private static final long serialVersionUID = 1L;
}