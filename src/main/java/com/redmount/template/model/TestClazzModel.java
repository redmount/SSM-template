package com.redmount.template.model;

import com.redmount.template.base.model.TestClazz;
import com.redmount.template.base.model.TestStudent;
import com.redmount.template.base.model.TestTeacher;
import com.redmount.template.core.annotation.RelationData;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@RelationData(BaseDOTypeName = "TestClazz")
public class TestClazzModel extends TestClazz {
    @RelationData(BaseDOTypeName = "TestTeacher", foreignProperty = "adviserPk")
    private TestTeacher adviser;

    @RelationData(BaseDOTypeName = "TestStudent", foreignProperty = "clazzPk", isOneToMany = true)
    private List<TestStudent> students;

    @RelationData(BaseDOTypeName = "TestTeacher", isManyToMany = true, relationTableName = "RTestTeacherTTestClazz")
    private List<TestTeacherModel> teachers;

    @RelationData(BaseDOTypeName = "RTestTeacherTTestClazz", isRelation = true)
    private Map<String, Object> courseCount;
}
