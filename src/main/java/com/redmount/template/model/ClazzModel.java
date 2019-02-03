package com.redmount.template.model;

import com.redmount.template.base.model.RTestTeacherTTestClazz;
import com.redmount.template.base.model.TestClazz;
import com.redmount.template.base.model.TestClazzInfo;
import com.redmount.template.base.model.TestStudent;
import com.redmount.template.core.annotation.Keywords;
import com.redmount.template.core.annotation.RelationData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data // 引入Lombok,使代码更简洁
@Accessors(chain = true)
@RelationData(baseDOTypeName = "TestClazz") // 本类继承的DO类型
@ApiModel("班级sdfasfdsfsadfsaf实体")
public class ClazzModel extends TestClazz {
    public static final String BaseDOTypeName="TestClazz";

    @RelationData(baseDOTypeName = "TestTeacher", // 这个属性对应的DO类型
            foreignProperty = "adviserPk") // 一对一关系,从表的主键记录在主表中,记录的字段为 adviser_pk,对应到Java里的属性为adviserPk
    @ApiModelProperty("班主任")
    private TeacherModel adviser;

    @RelationData(baseDOTypeName = "TestStudent", // 这个属性对应的DO类型
            mainProperty = "clazzPk", // 在这个DO中,哪个属性代表本类的实体
            isOneToMany = true) // 一对多关系,会查询出多条对象
    @ApiModelProperty("学生列表")
    private List<TestStudent> students;

    @RelationData(baseDOTypeName = "TestTeacher", // 这个属性对应的DO类型
            isManyToMany = true, // 多对多关系,会到中间表(relationDOTypeName)中查询关系
            relationDOTypeName = "RTestTeacherTTestClazz", // 关系表对应的DO类型
            foreignProperty = "teacherPk", // 从表的pk对应字段
            mainProperty = "clazzPk") // 主表的pk对应字段
    @ApiModelProperty("教师列表")
    private List<TeacherModel> teachers;

    @RelationData(baseDOTypeName = "RTestTeacherTTestClazz", // 关系数据对应的DO类型
            isRelation = true) // 表示是关系数据
    @ApiModelProperty("课程列表")
    private RTestTeacherTTestClazz courseCount;

    @RelationData(baseDOTypeName = "TestClazzInfo", // 这个属性对应的DO类型
            mainProperty = "clazzPk") // 主表pk对应的字段
    @ApiModelProperty("班级信息")
    private TestClazzInfo info;

    @Keywords // 模糊查询生效的字段
    private String name;

    @ApiModelProperty("学生数量")
    private Integer studentsCount; // 没有标记,则此体系不掌管这个属性

    @Keywords // 模糊查询生效的字段
    private String nickName;
}
