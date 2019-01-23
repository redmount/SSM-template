package com.redmount.template.model;

import com.redmount.template.base.model.RTestTeacherTTestClazz;
import com.redmount.template.base.model.TestTeacher;
import com.redmount.template.core.annotation.RelationData;
import lombok.Data;

import java.util.List;

@Data
@RelationData(baseDOTypeName = "TestTeacher")
public class TeacherModel extends TestTeacher {
    @RelationData(baseDOTypeName = "TestClazz", isManyToMany = true, relationDOTypeName = "RTestTeacherTTestClazz",foreignProperty = "clazzPk",mainProperty = "teacherPk")
    private List<ClazzModel> clazzes;

    @RelationData(baseDOTypeName = "RTestTeacherTTestClazz", isRelation = true)
    private RTestTeacherTTestClazz courseData;
}
