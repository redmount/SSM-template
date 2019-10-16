package com.redmount.template.model;

import com.redmount.template.base.model.TestClazz;
import com.redmount.template.base.model.TestStudent;
import com.redmount.template.base.repo.TestClazzMapper;
import com.redmount.template.base.repo.TestStudentMapper;
import com.redmount.template.core.annotation.RelationData;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@RelationData(baseDOClass = TestStudent.class, baseDOMapperClass = TestStudentMapper.class)
@ApiModel("学生实体")
public class StudentModel extends TestStudent {
    @RelationData(baseDOClass = TestClazz.class, baseDOMapperClass = TestClazzMapper.class, foreignProperty = "clazzPk")
    ClazzModel clazz;
}
