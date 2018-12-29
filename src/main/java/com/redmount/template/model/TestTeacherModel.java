package com.redmount.template.model;

import com.redmount.template.base.model.TestTeacher;
import com.redmount.template.core.annotation.RelationData;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@RelationData(BaseDOTypeName = "TestTeacher")
public class TestTeacherModel extends TestTeacher {
    @RelationData(BaseDOTypeName = "TestClazz", isManyToMany = true, relationTableName = "RTestTeacherTTestClazz")
    private List<TestClazzModel> clazzes;

    @RelationData(BaseDOTypeName = "RTestTeacherTTestClazz", isRelation = true)
    private Map<String, Object> courseData;
}
