package com.redmount.template.model;

import com.redmount.template.base.model.RTestTeacherTTestClazz;
import com.redmount.template.base.model.TestTeacher;
import com.redmount.template.core.annotation.RelationData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@RelationData(baseDOTypeName = "TestTeacher")
@Accessors(chain = true)
public class TeacherModel extends TestTeacher {
    @RelationData(baseDOTypeName = "TestClazz", isManyToMany = true, relationDOTypeName = "RTestTeacherTTestClazz",foreignProperty = "clazzPk",mainProperty = "teacherPk")
    private List<ClazzModel> clazzes;

    @RelationData(baseDOTypeName = "RTestTeacherTTestClazz", isRelation = true)
    @ApiModelProperty("教师和班级的关系描述数据")
    private RTestTeacherTTestClazz courseData;
}
