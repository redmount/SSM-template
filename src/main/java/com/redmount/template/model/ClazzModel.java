package com.redmount.template.model;

import com.redmount.template.base.model.TestClazz;
import com.redmount.template.base.model.TestStudent;
import com.redmount.template.base.model.TestTeacher;
import com.redmount.template.core.annotation.Keywords;
import com.redmount.template.core.annotation.RelationData;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@RelationData(baseDOTypeName = "TestClazz")
public class ClazzModel extends TestClazz {
    @RelationData(baseDOTypeName = "TestTeacher", foreignProperty = "adviserPk")
    private TestTeacher adviser;

    @RelationData(baseDOTypeName = "TestStudent", foreignProperty = "clazzPk", isOneToMany = true)
    private List<TestStudent> students;

    @RelationData(baseDOTypeName = "TestTeacher",
            isManyToMany = true,
            relationDOTypeName = "RTestTeacherTTestClazz",
            foreignProperty = "teacherPk",
            mainProperty = "clazzPk")
    private List<TeacherModel> teachers;

    @RelationData(baseDOTypeName = "RTestTeacherTTestClazz", isRelation = true)
    private Map<String, Object> courseCount;

    @Keywords
    private String pk;

    @Keywords
    private String name;

    private Integer count;

    @Keywords
    private String nickName;
}
