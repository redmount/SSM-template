![Licence](https://img.shields.io/badge/licence-none-green.svg)


<!-- @import "[TOC]" {cmd="toc" depthFrom=1 depthTo=6 orderedList=false} -->

<!-- code_chunk_output -->

* [0. 简介](#0-简介)
* [1. 表设计规则](#1-表设计规则)
	* [1.1. 表名规则](#11-表名规则)
	* [1.2. 字段名规则](#12-字段名规则)
* [2. 实体创建规则](#2-实体创建规则)
* [3. 最佳实践](#3-最佳实践)
	* [3.1. 准备阶段](#31-准备阶段)
	* [3.2. 生成阶段](#32-生成阶段)
	* [3.3. 编制Model阶段](#33-编制model阶段)
	* [3.4. 创建通用Service.](#34-创建通用service)
* [4.代码样例](#4代码样例)

<!-- /code_chunk_output -->



# 0. 简介

本项目继承自 https://github.com/lihengming/spring-boot-api-project-seed

并在其基础上增加了以下功能:
1. 整合Swagger2
1. 业务异常的表级维护
1. 结合表设计规则,可以提供一对一/一对多/多对多/多对多并且带关系数据的数据关联查询
1. 可以提供一对一/一对多/多对多/多对多且带关系数据的数据保存/更新
1. 条件查询/排序

# 1. 表设计规则
> 本规范中,表分为两种,一种是实体表,一种是关系表.
> 实体表表现在最终的返回结果上,是会把主键(pk)返回的,也就是具有业务意义的表.
> 例如: 班级/学生/老师等以及各种字典表.
> 关系表的作用是记录**两个**实体之间有关系,也可以描述两者之间是什么关系.
> 关系表中的数据的主键/外键都不会返回给调用方,但描述的关系数据会包含在子实体的relation属性中,作为对象返回.
## 1.1. 表名规则
> 表名全部采用小写+下划线的形式
## 1.2. 字段名规则
>	1. 字段名也采用小写字母+下划线的形式
>	1. 主键名强制为"pk","CHAR"或"VARCHAR",36位长度.(在框架内部,将采用UUID.Random().toString()方法生成主键.)	
	
# 2. 实体创建规则
实体必须继承自一个DO实体(也就是生成的数据实体)
# 3. 最佳实践
## 3.1. 准备阶段
1. 先将```src/test/resources/sys_service_exception.sql``` 执行到数据库中,创建业务异常表.
1. 删除样例里面的 ```base```、```controller```、```dao```、```model```、```service``` 文件夹里面的内容,**建议保留文件夹本身**。
1. 按照数据库命名规则设计业务数据库.
1. 修改src/main/java/com/redmount/template的名称,修改为对应的项目名称.(建议采用IntelliJ IDEA中的"Refector->Rename(Shift+F6)进行重命名.
1. 修改```src/main/java/.../core/ProjectConstant.java``` 里面的内容.
   > 这里面的内容一旦修改好,尽量不要做调整,以避免各种麻烦.
    除了BASE_PACKAGE外,其余值不建议修改.
    
    其中生成的代码建议放在base文件夹下,以便维护.

    |常量名|作用|默认值|说明|
    |-----|----|----|---|
    |BASE_PACKAGE|生成代码所在的基础包名称，可根据自己公司的项目修改（注意：这个配置修改之后需要手工修改src目录项目默认的包路径，使其保持一致，不然会找不到类）|com.redmount.template|根据项目进行修改|
    |MODEL_PACKAGE|生成的Model所在包|BASE_PACKAGE + ".base.model"|不建议修改|
    |MAPPER_PACKAGE|生成的Mapper所在包|BASE_PACKAGE + ".base.repo"|不建议修改|
    |SERVICE_PACKAGE|生成的Service所在包|BASE_PACKAGE + ".base.service"|不建议修改|
    |SERVICE_IMPL_PACKAGE|生成的ServiceImpl所在包|SERVICE_PACKAGE + ".impl"|不建议修改|
    |CONTROLLER_PACKAGE|生成的Controller所在包|BASE_PACKAGE + ".base.controller"|不建议修改|
    |MAPPER_INTERFACE_REFERENCE|Mapper插件基础接口的完全限定名|BASE_PACKAGE + ".core.Mapper"|不能修改|

1. 调整```src/test/CodeGenerator.java```中的配置值.

    |常量名|作用|默认值|
    |-----|----|-----|
    |JDBC_URL|数据库链接地址|jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8|
    |JDBC_USERNAME|连接时使用的用户名|root|
    |JDBC_PASSWORD|连接时使用的密码|root|
    |JDBC_DIVER_CLASS_NAME|连接时使用的数据库驱动|com.mysql.cj.jdbc.Driver|

## 3.2. 生成阶段
1. 数据库中执行```src/test/resources/ToolSql.sql```,以生成数据库中全部的表结构.
其中```table_schema='test'```中的```test```需要替换为实际的数据库.
    ```sql
    /*取数据库中所有表名*/
    select concat('tableNames.add("',table_name,'");') from information_schema.tables where table_schema='test' and table_type='BASE TABLE';
    ```
1. 把执行的结果复制出来,粘贴到src/test/CodeGenerator.java文件中的main函数中.
1. 执行```src/test/CodeGenerator.java```文件中的```main```函数.
## 3.3. 编制Model阶段
1. 在```src/main/java/.../model(建议放在此文件夹)```中,新建业务需要的Model实体.
1. 在实体上加上描述注解:

	@RelationData

	| 注解 | 类型(默认值) | 作用 | 备注 |
	|----|----|----|----|
	|baseDOTypeName|String("")|标明本类或属性对应的DO实体名称|对于标记在类上面的关系注解,该值就为所继承的类的名称(不含所在包)|
    |relationDOTypeName|String("")|表示关系表的表名|当isManyToMany=true时生效|
    |foreignProperty|String("")|表示在关联查询的时候,需要以哪个属性作为外键使用|当 isOneToMany=true或isManyToMany=true时,生效|
    |mainProperty|""|表示在查询关系表时,主表的外键对应的属性|当isManyToMany=true时生效|
    |isOneToMany|boolean(false)|表示此关系字段是否是一对多的|此值为true时,需要指定"foreignProperty"|
    |isManyToMany|boolean(false)|表示此字段是否是多对多的|此值为true时,需要指定"foreignProperty","mainProperty","relationDOTypeName"|
    |isRelation|boolean(false)|表示此字段是否为多对多关系中的关系描述字段|此字段为true时,需要指定"baseDOTypeName",表示关系字段所在的DO类名|

    @RelationData的组合情况
    | 情况 | 使用注解 |说明|
    |-----|-----|---|
    |主实体类对应的DO|@RelationData(baseDOTypeName = "TestClazz")||
    |从主实体视角看来的一对一关系(关系数据在主表中)|@RelationData(baseDOTypeName = "TestTeacher", foreignProperty = "adviserPk")||
    |从主实体视角看来的一对一关系(关系数据在子表中)|@RelationData(baseDOTypeName = "TestStudent", mainProperty = "clazzPk"||
    |从主实体视角看来的一对多关系|@RelationData(baseDOTypeName = "TestStudent", mainProperty = "clazzPk",isOneToMany = true)||
    |从主实体视角看来是多对多关系|@RelationData(baseDOTypeName = "TestTeacher", isManyToMany = true, relationDOTypeName = "RTestTeacherTTestClazz", foreignProperty = "teacherPk", mainProperty = "clazzPk")||
    
	

## 3.4. 创建通用Service.
1. 在```src/service(建议放在此文件夹)```中,新建Service的接口.
    ``` java 
    public interface 实体服务名 extends ModelService<服务对应的实体Model>
    ```
1. 在```src/service/impl(建议放在此文件夹)```中,新建Service的实现类.
    ``` java
        @Service
        public class 实体服务实现类名 extends AbstractModelService<服务对应的实体Model> implements ClazzService {
        }
    ```
1. 在```src/controller(建议放在此文件夹)```中新建controller,调用实体服务即可.

# 4.代码样例
```java
// BaseModel.java
@Table(name = "test_clazz")
public class TestClazz extends BaseDO {
    /**
     * 班级名称
     */
    private String name;

    /**
     * 班主任pk
     */
    @Column(name = "adviser_pk")
    private String adviserPk;

    @Column(name = "nick_name")
    private String nickName;

    // ... getter,setter
}
```
```java
// Model.java
@Data // 引入Lombok,使代码更简洁
@RelationData(baseDOTypeName = "TestClazz") // 本类继承的DO类型
public class ClazzModel extends TestClazz {

    @RelationData(baseDOTypeName = "TestTeacher", // 这个属性对应的DO类型
            foreignProperty = "adviserPk") // 一对一关系,从表的主键记录在主表中,记录的字段为 adviser_pk,对应到Java里的属性为adviserPk
    private TeacherModel adviser;

    @RelationData(baseDOTypeName = "TestStudent", // 这个属性对应的DO类型
            mainProperty = "clazzPk", // 在这个DO中,哪个属性代表本类的实体
            isOneToMany = true) // 一对多关系,会查询出多条对象
    private List<TestStudent> students;

    @RelationData(baseDOTypeName = "TestTeacher", // 这个属性对应的DO类型
            isManyToMany = true, // 多对多关系,会到中间表(relationDOTypeName)中查询关系
            relationDOTypeName = "RTestTeacherTTestClazz", // 关系表对应的DO类型
            foreignProperty = "teacherPk", // 从表的pk对应字段
            mainProperty = "clazzPk") // 主表的pk对应字段
    private List<TeacherModel> teachers;

    @RelationData(baseDOTypeName = "RTestTeacherTTestClazz", // 关系数据对应的DO类型
            isRelation = true) // 表示是关系数据
    private Map<String, Object> courseCount;

    @RelationData(baseDOTypeName = "TestClazzInfo", // 这个属性对应的DO类型
            mainProperty = "clazzPk") // 主表pk对应的字段
    private TestClazzInfo info;

    @Keywords // 模糊查询生效的字段
    private String name;

    private Integer count; // 没有标记,则此体系不掌管这个属性

    @Keywords // 模糊查询生效的字段
    private String nickName;
}
```
```java
// Controller.java
@RequestMapping("/class")
@RestController
public class ClazzController {
    @Autowired
    ClazzService service;

    @GetMapping("/{pk}")
    public Result getByPk(@PathVariable(value = "pk") String pk,
                          @RequestParam(value = "relations", defaultValue = "") String relations) {
        return ResultGenerator.genSuccessResult(service.getAutomatic(pk, relations));
    }
}
```
```java
// Service.java
public interface ClazzService extends ModelService<ClazzModel> {

}
```