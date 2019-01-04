package com.redmount.template.model;

import com.redmount.template.base.model.TestTeacher;
import com.redmount.template.core.annotation.RelationData;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@RelationData(baseDOTypeName = "TestTeacher")
public class TeacherModel extends TestTeacher {
    @RelationData(baseDOTypeName = "TestClazz", isManyToMany = true, relationDOTypeName = "RTestTeacherTTestClazz",foreignProperty = "clazzPk",mainProperty = "teacherPk")
    private List<ClazzModel> clazzes;

    @RelationData(baseDOTypeName = "RTestTeacherTTestClazz", isRelation = true)
    private Map<String, Object> courseData;
}
