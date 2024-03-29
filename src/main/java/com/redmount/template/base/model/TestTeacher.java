package com.redmount.template.base.model;

import com.redmount.template.base.repo.TestTeacherMapper;
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
@Table(name = "test_teacher")
@ApiModel("TestTeacher（教师对象）")
@Data
@RelationData(baseDOClass = TestTeacher.class, baseDOMapperClass = TestTeacherMapper.class)
public class TestTeacher extends BaseDO implements Serializable {
    /**
     * 教师名称
     */
    @ApiModelProperty(value = "教师名称")
    private String name;

    private static final long serialVersionUID = 1L;
}