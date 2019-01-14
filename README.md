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
1. 每次请求的请求方式/URL/QueryString/返回值的控制台打印

# 1. 表设计规则
本规范中,表分为两种,一种是实体表,一种是关系表.

实体表表现在最终的返回结果上,是会把主键(pk)返回的,也就是具有业务意义的表.

例如: 班级/学生/老师等以及各种字典表.

关系表的作用是记录**两个**实体之间有关系,也可以描述两者之间是什么关系.

关系表中的数据的主键/外键都不会返回给调用方,但描述的关系数据会包含在子实体的relation属性中,作为对象返回.
## 1.1. 表名规则
> 表名全部采用小写+下划线的形式
## 1.2. 字段名规则
>	1. 字段名也采用小写字母+下划线的形式
>	1. 主键名强制为"pk","CHAR"或"VARCHAR",36位长度.(在框架内部,将采用UUID.Random().toString()方法生成主键.)	
	
# 2. 实体创建规则
实体必须继承自一个DO实体(也就是生成的数据实体)
# 3. 基础功能介绍
## 3.1. 抽象Controller
### 3.1.1. 提供列表的分页/条件/关键字查询
请求全参数
```
Method: GET
Path: /class?page=1&size=10&keywords=班&condition=nickName like '%一班%'&relations=students,teachers,adviser&orderBy=nickName desc
```
```
在进行列表查询时,可以URI中指定page和size参数.其中size表示每页多少条数据,page表示要取多少条.
默认值: page=1,size=10
当page=0时,会查询出所有的数据,但是数据结构仍然为分页形式.
```
```
可以指定relations参数,返回值将按照relations指定的属性名返回相应的数据.
属性名为小驼峰风格,与Java代码中的属性名对应.
仅能够返回实体中包含的属性,多余的属性忽略不计,也不会抛出异常.
例如: /class?relations=students,teachers,adviser,project
其中,project并不是class的属性,此属性将不会返回,也不会抛出异常.
```
```
可以指定condition参数,此参数本质上是SQL语句的where子句,但使用的并不是字段名,而是Java实体中的属性(字段)名.
例: /class?condition=nickName like '%一年%'
限制: 
1. 目前只支持单表的条件查询,暂不支持关系数据的条件.
    例如查询班级的时候,只能按照班级的属性进行筛选,并不能按照班级的班主任,学生,教师的属性进行查询.
2. 由于在程序内部使用了小驼峰转下划线的转换,因此,在条件字符串的内部如果出现了应为的小驼峰字符串的话,将不会得到正确的结果.
```
```
可以指定keywords参数,并与实体定义配合,将实体中含有"@Keywords"注解的属性进行模糊匹配.
"@Keywords"属性只能标记在String类型的属性上,否则会产生报错.
例: /class?keywords=班
```
```
可以指定orderBy参数,指定按照主表的那个属性进行排序
例: /class?orderBy=nickName desc
```
### 3.1.2. 根据pk查询单条实体
与查询列表类似,区别仅在于没有page和size参数.返回的实体为单条实体.
### 3.1.3. 保存/修改实体
1. 强制创建实体
使用POST方式会强制新建一个实体.
新建实体的值完全传入的Body.
也就是如果传入的值中包含null,则也会将数据库中的对应字段强制更改为null.
```
Method: POST
Path: /class
Body: {}
```

2. 新建或更新实体
使用PUT方式会新建或更新一个实体
具体是新建还是更新,取决于实体中是否含有pk.
如果pk属性有值,那么则按照pk值对应的记录进行更新,如果没有,则新建.
新建或更新后的实体的值等于传入的Body.
也就是如果传入的值中包含null,则也会将数据库中的对应字段强制更改为null.
```
Method: PUT
Path: /class
Body: {}
```

3. 部分更新实体
使用PATCH方式会按照给定的值(不含null)更新一个实体
值为null的属性(无论是基础属性还是对象/列表属性)都会被忽略,保持数据库的原值.
```
Method: PATCH
Path: /class/{pk}
Body:{}
```

## 3.1.3 删除实体
1. 按pk删除
```
Method: DELETE
Path: /class/{pk}
```
2. 按条件删除
使用此接口时,condition为必填参数,并且不能为空字符串.否则会抛出异常.
```
Method: DELETE
Path: /class?condition=nickName like '%一年%'
```


# 4. 最佳实践
## 4.1. 准备阶段
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

## 4.2. 生成阶段
1. 数据库中执行```src/test/resources/ToolSql.sql```,以生成数据库中全部的表结构.
其中```table_schema='test'```中的```test```需要替换为实际的数据库.
    ```sql
    /*取数据库中所有表名*/
    select concat('tableNames.add("',table_name,'");') from information_schema.tables where table_schema='test' and table_type='BASE TABLE';
    ```
1. 把执行的结果复制出来,粘贴到src/test/CodeGenerator.java文件中的main函数中.
1. 执行```src/test/CodeGenerator.java```文件中的```main```函数.
## 4.3. 编制Model阶段
1. 在```src/main/java/.../model(建议放在此文件夹)```中,新建业务需要的Model实体.
1. 在实体上加上描述注解:

	@RelationData

	| 注解 | 类型(默认值) | 作用 | 备注 |
	|----|----|----|----|
	|baseDOTypeName|必填字段|标明本类或属性对应的DO实体名称|对于标记在类上面的关系注解,该值就为所继承的类的名称(不含所在包)|
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
    |从主实体视角看来的多对多关系|@RelationData(baseDOTypeName = "TestTeacher", isManyToMany = true, relationDOTypeName = "RTestTeacherTTestClazz", foreignProperty = "teacherPk", mainProperty = "clazzPk")||
    
    
## 4.4. 创建通用Service.
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

# 5.代码样例
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
@RequestMapping("/class") // 此Controller对应的接口地址
@RestController // 声明是Rest风格的Controller
// 继承自抽象Controller,即具有抽象Controller提供的功能.
public class ClazzController extends AbstractController<ClazzModel> { 
    @Autowired
    ClazzService service;

    // 继承抽象Controller时,必须实现的方法.
    // 实际上此方法定义在Controller的接口中,抽象Controller对Controller是不完全实现.
    @Override
    public void init() {
        super.service = service;
    }
}
```
```java
// Service.java
public interface ClazzService extends ModelService<ClazzModel> {

}
```
```java
// ServiceImpl.java
// 继承自抽象Service,即拥有抽象Service提供的功能.
@Service
public class ClazzServiceImpl extends AbstractModelService<ClazzModel> implements ClazzService {

}
```

# 6.测试用例
## 6.1. 查询班级列表,带关系(学生列表,教师列表,班主任)
请求
```
GET
/class?relations=students,teachers,adviser
```
返回值
```json
{
    "code": 200,
    "data": {
        "endRow": 2,
        "hasNextPage": false,
        "hasPreviousPage": false,
        "isFirstPage": true,
        "isLastPage": true,
        "list": [
            {
                "adviser": {
                    "name": "一班班主任",
                    "pk": "t1"
                },
                "deleted": true,
                "nickName": "一年一班",
                "pk": "c1",
                "students": [
                    {
                        "name": "一班学生1",
                        "pk": "s11"
                    },
                    {
                        "name": "一班学生2",
                        "pk": "s12"
                    }
                ],
                "teachers": [
                    {
                        "courseData": {
                            "count": 210,
                            "course": "数学课"
                        },
                        "name": "数学老师",
                        "pk": "t3"
                    },
                    {
                        "courseData": {
                            "count": 1310,
                            "course": "语文课"
                        },
                        "name": "一班班主任",
                        "pk": "t1"
                    },
                    {
                        "courseData": {
                            "count": 1,
                            "course": "体育老师教的语文课"
                        },
                        "name": "语文老师",
                        "pk": "t2"
                    }
                ]
            },
            {
                "adviser": {
                    "name": "二班班主任",
                    "pk": "t4"
                },
                "deleted": false,
                "name": "班级2",
                "nickName": "二年二班",
                "pk": "c2",
                "students": [
                    {
                        "name": "二班学生1",
                        "pk": "s21"
                    }
                ],
                "teachers": [
                    {
                        "courseData": {
                            "count": 0,
                            "course": "体育老师教的英语课"
                        },
                        "name": "语文老师",
                        "pk": "t2"
                    },
                    {
                        "courseData": {
                            "count": 210,
                            "course": "数学课"
                        },
                        "name": "数学老师",
                        "pk": "t3"
                    },
                    {
                        "courseData": {
                            "count": 310,
                            "course": "英语课"
                        },
                        "name": "二班班主任",
                        "pk": "t4"
                    }
                ]
            },
            {
                "deleted": true,
                "name": "班级2",
                "nickName": "二年二班",
                "pk": "c3",
                "students": []
            }
        ],
        "navigateFirstPage": 1,
        "navigateLastPage": 1,
        "navigatePages": 8,
        "navigatepageNums": [
            1
        ],
        "nextPage": 0,
        "pageNum": 1,
        "pageSize": 3,
        "pages": 1,
        "prePage": 0,
        "size": 3,
        "startRow": 0,
        "total": 3
    },
    "message": "SUCCESS"
}
```

## 6.2 按pk取单个班级实体,带关系数据(学生列表,教师列表,班主任)
请求
```
GET
/class/c1?relations=students,teachers,adviser
```
返回值
```json
{
    "code": 200,
    "data": {
        "adviser": {
            "name": "一班班主任",
            "pk": "t1"
        },
        "deleted": true,
        "nickName": "一年一班",
        "pk": "c1",
        "students": [
            {
                "name": "一班学生1",
                "pk": "s11"
            },
            {
                "name": "一班学生2",
                "pk": "s12"
            }
        ],
        "teachers": [
            {
                "courseData": {
                    "count": 210,
                    "course": "数学课"
                },
                "name": "数学老师",
                "pk": "t3"
            },
            {
                "courseData": {
                    "count": 1310,
                    "course": "语文课"
                },
                "name": "一班班主任",
                "pk": "t1"
            },
            {
                "courseData": {
                    "count": 1,
                    "course": "体育老师教的语文课"
                },
                "name": "语文老师",
                "pk": "t2"
            }
        ]
    },
    "message": "SUCCESS"
}
```
## 6.3.按pk删除班级
