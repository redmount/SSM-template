package com.redmount.template.model;

import com.redmount.template.base.model.*;
import com.redmount.template.base.repo.*;
import com.redmount.template.core.annotation.Keywords;
import com.redmount.template.core.annotation.RelationData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data // 引入Lombok,使代码更简洁
@RelationData(baseDOClass = TestClazz.class, // 本类继承的DO类型
        baseDOMapperClass = TestClazzMapper.class) // 本类所使用的Mapper类
@ApiModel("班级实体") // Swagger注解
public class ClazzModel extends TestClazz {
    // 一对一关系,从表的主键记录在主表中
    // 班级知道自己的班主任是谁,但教师并不知道自己是哪个班的班主任
    @RelationData(
            baseDOClass = TestTeacher.class, // 实际关联的DO的类型
            baseDOMapperClass = TestTeacherMapper.class, // 实际应该调用的Mapper类
            foreignProperty = "adviserPk")
    // 从表的pk对应字段,所谓从表,就是代表我当前正在写的这个属性所对应的表.我们现在写的是adviser属性,所以标识出Teacher的pk字段即为"FOREIGN"Property
    @ApiModelProperty("班主任") // Swagger注解
    private TeacherModel adviser; // 班主任属性

    // 一对一关系,主表的主键记录在从表中
    // 班级信息知道自己属于哪个班级,但班级不知道自己的班级信息是谁
    @RelationData(baseDOClass = TestClazzInfo.class, // 实际关联的DO的类型
            baseDOMapperClass = TestClazzInfoMapper.class, // 实际应该调用的Mapper类
            mainProperty = "clazzPk") // 主表pk对应的字段
    @ApiModelProperty("班级信息")
    private TestClazzInfo info;

    // 一对多关系,主表的主键记录在从表中
    // 学生知道自己属于哪个班级,而班级不知道自己的学生都由哪些.
    @RelationData(baseDOClass = TestStudent.class, // 实际关联的DO的类型
            baseDOMapperClass = TestStudentMapper.class, // 实际应该调用的Mapper类
            mainProperty = "clazzPk", // 在这个DO中,哪个属性代表本类的实体 // 在表结构中,每个学生都记录了自己属于哪个班级,字段为"clazz_pk",对应的Java属性名为"clazzPk"
            isOneToMany = true) // 一对多关系,会查询出多条对象
    @ApiModelProperty("学生列表") // Swagger注解
    private List<TestStudent> students; // 学生列表属性

    // 多对多关系,通过中间关系表来描述与是否存在关系
    // 班级不知道自己有哪些老师,老师也不知道自己有哪些班级,但是中间表知道
    @RelationData(baseDOClass = TestTeacher.class, // 实际关联的DO的类型
            baseDOMapperClass = TestTeacherMapper.class, // 实际应该调用的Mapper类
            isManyToMany = true, // 多对多关系,会到中间表(relationDOTypeName)中查询关系
            relationDOClass = RTestTeacherTTestClazz.class, // 关系表对应的DO类型
            relationDOMapperClass = RTestTeacherTTestClazzMapper.class, // 关系表对应的Mapper类
            foreignProperty = "teacherPk", // 从表的pk对应字段,所谓从表,就是代表我当前正在写的这个属性所对应的表.我们现在写的是List<TeacherModel> teachers属性,所以标识出Teacher的pk字段即为"FOREIGN"Property
            mainProperty = "clazzPk") // 主表的pk对应字段,所谓主表就是代表当前类的pk字段.我们现在正在写的是ClassModel,所以标识Class的pk的字段即为"MAIN"Property
    @ApiModelProperty("教师列表") // Swagger注解
    private List<TeacherModel> teachers; // 教师列表属性

    // 关系描述数据
    // 关系数据仅当别的表通过多对多带出本类实体时,才有意义.
    // 比如当查询教师时,带出了这个老师所教学的班级列表,则在带出的每个班级中,会有此字段作为存储关系数据的容器出现.
    // 单独查询班级时,这个属性始终为空
    @RelationData(baseDOClass = RTestTeacherTTestClazz.class, // 实际关联的DO的类型
            baseDOMapperClass = RTestTeacherTTestClazzMapper.class, // 实际应该调用的Mapper类
            isRelation = true) // 表示是关系数据,此字段
    @ApiModelProperty("课程列表")
    private RTestTeacherTTestClazz courseCount; // 班级与教师描述数据

    // @Keywords注解的应用
    // @Keywords注解只能标注在String类型上,否在在查询时会报错
    // @Keywords注解可以同时标注在多个字段上,此时,如果有任意一个标注@Keywords注解的值模糊匹配上了keywords参数,即认为满足查询条件.
    // 例如,在此,name和nickName都标注了@Keywords注解,那么当输入的keywords参数为"班",则name模糊匹配上课"班"或者nickName模糊匹配上了"班",都会作为查询结果进行返回.
    @Keywords // 模糊查询生效的字段
    private String name;

    @Keywords // 模糊查询生效的字段
    private String nickName;

    // 没有标记任何本框架下的注解,则本框架忽略不计,此属性可以作为正常属性正常使用.
    @ApiModelProperty("学生数量")
    private Integer studentsCount; // 没有标记,则此体系不掌管这个属性

    @ApiModelProperty("是否为大班")
    private Boolean isBig;
}
