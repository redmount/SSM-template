package com.redmount.template.model;

import com.redmount.template.base.model.RTestTeacherTTestClazz;
import com.redmount.template.base.model.TestClazz;
import com.redmount.template.base.model.TestTeacher;
import com.redmount.template.base.repo.RTestTeacherTTestClazzMapper;
import com.redmount.template.base.repo.TestClazzMapper;
import com.redmount.template.base.repo.TestTeacherMapper;
import com.redmount.template.core.annotation.RelationData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@RelationData(baseDOClass = TestTeacher.class, baseDOMapperClass = TestTeacherMapper.class)
public class TeacherModel extends TestTeacher {
    @RelationData(baseDOClass = TestClazz.class,
            baseDOMapperClass = TestClazzMapper.class,
            isManyToMany = true,
            relationDOClass = RTestTeacherTTestClazz.class,
            relationDOMapperClass = RTestTeacherTTestClazzMapper.class,
            foreignProperty = "clazzPk",
            mainProperty = "teacherPk")
    private List<ClazzModel> clazzes;

    @RelationData(baseDOClass = RTestTeacherTTestClazz.class, isRelation = true)
    @ApiModelProperty("教师和班级的关系描述数据")
    private RTestTeacherTTestClazz courseData;
}
