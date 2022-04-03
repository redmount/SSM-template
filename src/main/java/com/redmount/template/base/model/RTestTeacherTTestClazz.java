package com.redmount.template.base.model;

import com.redmount.template.base.repo.RTestTeacherTTestClazzMapper;
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
@Table(name = "r_test_teacher_t_test_clazz")
@ApiModel("RTestTeacherTTestClazz（）")
@Data
@RelationData(baseDOClass = RTestTeacherTTestClazz.class, baseDOMapperClass = RTestTeacherTTestClazzMapper.class)
public class RTestTeacherTTestClazz extends BaseDO implements Serializable {
    @Column(name = "teacher_pk")
    @ApiModelProperty(value = "")
    private String teacherPk;

    @Column(name = "clazz_pk")
    @ApiModelProperty(value = "")
    private String clazzPk;

    @ApiModelProperty(value = "")
    private String course;

    @ApiModelProperty(value = "")
    private Integer count;

    private static final long serialVersionUID = 1L;
}