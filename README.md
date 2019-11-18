![Licence](https://img.shields.io/badge/licence-none-green.svg)
<!-- TOC -->

- [1. 简介](#1-简介)
- [2. 表设计规则](#2-表设计规则)
    - [2.1. 表名规则](#21-表名规则)
    - [2.2. 字段名规则](#22-字段名规则)
- [3. 实体创建规则](#3-实体创建规则)
    - [3.1 实体分类](#31-实体分类)
    - [3.2 基础实体规则](#32-基础实体规则)
    - [3.3 复杂实体](#33-复杂实体)
    - [3.4 自定义实体](#34-自定义实体)
- [4. 基础功能介绍](#4-基础功能介绍)
    - [4.1. 抽象Controller](#41-抽象controller)
        - [4.1.1. 提供列表的分页/条件/关键字查询](#411-提供列表的分页条件关键字查询)
        - [4.1.2. 根据pk查询单条实体](#412-根据pk查询单条实体)
        - [4.1.3. 保存/修改实体](#413-保存修改实体)
        - [4.1.4. 删除实体](#414-删除实体)
- [5. 最佳实践](#5-最佳实践)
    - [5.1. 准备阶段](#51-准备阶段)
    - [5.2. 生成阶段](#52-生成阶段)
    - [5.3. 编制Model阶段](#53-编制model阶段)
    - [5.4. 创建通用Service/ServiceImpl/Controller.](#54-创建通用serviceserviceimplcontroller)
- [6. 代码样例](#6-代码样例)
    - [6.1. *Model.java](#61-modeljava)
    - [6.2. *Controller.java](#62-controllerjava)
    - [6.3. *Service.java](#63-servicejava)
    - [6.4. *ServiceImpl.java](#64-serviceimpljava)
- [7. 测试用例](#7-测试用例)
    - [7.1. 查询班级列表](#71-查询班级列表)
    - [7.2. 按pk取单个班级实体](#72-按pk取单个班级实体)
    - [7.3. 按pk删除班级](#73-按pk删除班级)
    - [7.4. 按条件删除班级](#74-按条件删除班级)
    - [7.5. 创建班级](#75-创建班级)
    - [7.6. 强制创建/修改班级](#76-强制创建修改班级)
    - [7.7. 局部修改班级](#77-局部修改班级)
    - [7.8. 查看实体的全部结构及说明(仅在"dev"模式下生效)](#78-查看实体的全部结构及说明仅在dev模式下生效)
- [8. 异常的抛出](#8-异常的抛出)
    - [8.1. 业务异常](#81-业务异常)
        - [8.1.1. 业务异常表的结构](#811-业务异常表的结构)
        - [8.1.2. 建议操作](#812-建议操作)
    - [8.2 运行异常](#82-运行异常)
    - [8.3 注意](#83-注意)
- [9. 其他功能](#9-其他功能)
    - [9.1. 生成数据库和js模型文档](#91-生成数据库和js模型文档)
    - [9.2. 定时Job功能](#92-定时job功能)

<!-- /TOC -->
# 1. 简介

本项目的宗旨:
- 减少CRUD等重复性劳动
- 以牺牲运行效率换取极快的开发效率
- 统一接口格式(正常返回和异常返回)

本项目继承自 https://github.com/lihengming/spring-boot-api-project-seed

并在其基础上增加了以下功能:

1. 整合Swagger2
1. 可以开启Mybatis二级缓存(Mapper级)
1. 业务异常的表级维护
1. 基础表更多的查询/写入方式
1. 结合表设计规则,可以提供一对一/一对多/多对多/多对多带关系数据的数据关联查询
1. 可以提供一对一/一对多/多对多/多对多带关系数据的数据保存/更新
1. 列表条件查询/排序
1. 逻辑删除
1. 每次请求的请求方式/URL/QueryString/处理方法名/返回值/请求耗时的控制台打印/方便调试和日志跟踪
1. 生成数据库说明文档
1. 生成数据模型说明(调用形式)
1. 生成基础数据模型js文件
1. 整合异步定时Job功能(支持CRON表达式)

# 2. 表设计规则
本规范中表分为两种:一种是实体表,一种是关系表.

实体表表现在最终的返回结果上,会把主键(pk)返回的,也就是具有业务意义的表.

例如: 班级、学生、老师等以及各种字典表等.

关系表的作用是记录两个实体之间有关系,以及他们之间是什么关系.

例如：班级与教师之间的关系表；另外还可以在关系表中描述他们之间的关系数据,比如：t1教师在c1班级上数学课,总共上了210节等.

关系表中的数据的主键、外键都不会返回给调用方,但描述的关系数据会包含在子实体的属性中,作为对象返回.

例如：上述例子中的体现格式如下(courseData即代表两者之间的关系描述)：

```json
{
"pk": "c1",
"teachers": [
              {
                "courseData": {
                    "count": 122,
                    "course": "t2课1班"
                },
                "name": "语文老师",
                "pk": "t2"
              }
            ]
}
```
## 2.1. 表名规则
> 表名全部采用小写+下划线的形式
## 2.2. 字段名规则
>	1. 字段名也采用小写字母+下划线的形式
>	1. 主键名强制为"pk","CHAR"或"VARCHAR",36位长度.(在框架内部,将采用UUID.Random().toString()方法生成主键,此方法生成的主键带中间的减号,需要额外占用4个字符位置.)	
```
原因:
项目中实体/属性与表名/字段名的对应关系,就是驼峰形式与下划线形式的互相转换.
如果表设计中不遵循此规则,可能会导致找不到表或者字段的情况,从而报错.
```
```
补充: 名称转换使用的是com.google.common.base.CaseFormat进行转换
```

# 3. 实体创建规则
## 3.1 实体分类
本规范中,实体分为三种
- 基础实体(生成器生成的实体)
```
直接由实体生成器生成的(默认状态下生成至base.model包下).此实体与数据库表一一对应,并且都是平表的形式,不具有其他复杂属性.
```
- 复杂实体(自定义复杂实体)
```
由生成的实体继承而来,一般用于描述带有关系的实体.
```
- 规范外实体(不受框架管制的实体)
```
有开发者自定义,如果没有在类上标记"@RelationData"注解的,不受本框架影响,即一般实体.
```


## 3.2 基础实体规则
基础实体是由代码生成器生成而来.默认情况下,存在于项目中的base.model包下.
默认情况下会实现以下注解:
```
@RelationData   /// 本框架提供的数据查询引擎注解,用以确定查询的基类和基类Mapper
@Data           /// Lombok注解,用以简化掉普通的setter/getter,使代码更简洁
@ApiModel       /// Swagger注释注解,用以生成文档注释
@Table          /// MyBatis使用,用以确定对应的表
```

## 3.3 复杂实体
复杂实体必须继承自一个DO实体,并且标注RelationData注解,从而使框架能够找到对应的表和Mapper,从而进行进一步的操作.

建议将复杂实体与基础实体分开存放,例如/model文件夹内

例:
```java
@RelationData(baseDOClass = TestClazz.class, baseDOMapperClass = TestClazzMapper.class)
public class ClazzModel extends TestClazz{}
```

## 3.4 自定义实体
只要不标记"@RelationData"注解,框架将会对此实体忽略,不予管理.

# 4. 基础功能介绍
## 4.1. 抽象Controller
### 4.1.1. 提供列表的分页/条件/关键字查询
请求全参数
```
Method: GET
Path:   /class?page=1&size=10&keywords=班&condition=nickName like '%一班%'&relations=students,teachers,adviser&orderBy=nickName desc
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
### 4.1.2. 根据pk查询单条实体
与查询列表类似,区别仅在于没有page和size参数.返回的实体为单条实体.
### 4.1.3. 保存/修改实体
1. 强制创建实体
使用POST方式会强制新建一个实体.
新建实体的值完全传入的Body.
也就是如果传入的值中包含null,则也会将数据库中的对应字段强制更改为null.
```
Method: POST
Path:   /class
Body:   {}
```

2. 新建或更新实体
使用PUT方式会新建或更新一个实体
具体是新建还是更新,取决于实体中是否含有pk.
如果pk属性有值,那么则按照pk值对应的记录进行更新,如果没有,则新建.
新建或更新后的实体的值等于传入的Body.
也就是如果传入的值中包含null,则也会将数据库中的对应字段强制更改为null.
```
Method: PUT
Path:   /class
Body:   {}
```

3. 部分更新实体
使用PATCH方式会按照给定的值(不含null)更新一个实体
值为null的属性(无论是基础属性还是对象/列表属性)都会被忽略,保持数据库的原值.
```
Method: PATCH
Path:   /class/{pk}
Body:   {}
```
### 4.1.4. 删除实体
1. 按pk删除
```
Method: DELETE
Path:   /class/{pk}
```
2. 按条件删除
使用此接口时,condition为必填参数,并且不能为空字符串.否则会抛出异常.
```
Method: DELETE
Path:   /class?condition=nickName like '%一年%'
```

# 5. 最佳实践
## 5.1. 准备阶段
1. 先将```src/test/resources/sys_service_exception.sql``` 执行到数据库中,创建业务异常表.
```
    此步必须执行,否则项目启动会失败.
    项目在启动之后,会加载所有的sys_service_exception表的数据至内存中,如果此表不存在或结构有变化,会导致异常退出.
```
1. 删除样例里面的 ```base```文件夹下的```controller```、```dao```、```model```、```service``` 文件夹里面的文件,**建议保留文件夹本身**.
1. 按照数据库命名规则设计业务数据库.
1. 修改src/main/java/com/redmount/template的名称,修改为对应的项目名称.(建议采用IntelliJ IDEA中的"Refector->Rename(Shift+F6)进行重命名.
```
    使用Rename功能会连带修改其他引用过此包的所有文件,降低修改的工作量.
```
1. 修改```src/core/aspect/WebLogAspect.java```文件中的切点位置(@Pointcut("(execution(public * com.redmount.template.controller.*.*(..))) || (execution(public * com.redmount.template.core.Controller.*(..)))")),将里面的切点改为您项目的Controller包下.
```
    具体的表达式可参考 https://my.oschina.net/u/2474629/blog/1083448
```
1. 修改```src/main/java/.../core/ProjectConstant.java```里面的内容.


    这里面的内容一旦修改好,尽量不要做调整,以避免各种麻烦.

    可能出现的情况是再次生成成功之后,项目会瘫痪,报各种类找不到的错误

    除了BASE_PACKAGE外,其余值不建议修改.

    其中生成的代码建议放在base文件夹下,以便维护.

    |常量名|作用|默认值|说明|
    |-----|----|----|---|
    |BASE_PACKAGE|生成代码所在的基础包名称,可根据自己公司的项目修改<br/>(注意：这个配置修改之后需要手工修改src目录项目默认的包路径,使其保持一致,不然会找不到类)|com.redmount.template|根据项目进行修改|
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

## 5.2. 生成阶段
1. 数据库中执行```src/test/resources/ToolSql.sql```,以生成数据库中全部的表结构.
其中```table_schema='test'```中的```test```需要替换为实际的数据库.
    ```sql
    /*取数据库中所有表名*/
    select concat('tableNames.add("',table_name,'");') from information_schema.tables where table_schema='test' and table_type='BASE TABLE';
    ```
1. 把执行的结果复制出来,粘贴到src/test/CodeGenerator.java文件中的main函数中.
1. 执行```src/test/CodeGenerator.java```文件中的```main```函数.
## 5.3. 编制Model阶段
1. 在```src/main/java/.../model(建议放在此文件夹)```中,新建业务需要的Model实体.
1. 在实体上加上描述注解:

	@RelationData

	| 注解 | 类型(默认值) | 作用 | 备注 |
	|----|----|----|----|
	|baseDOClass|class(Object.class)|标明本类或属性对应的DO实体类|对于标记在类上面的关系注解,该值就为所继承的类|
    |baseDOMapperClass|class(Object.class)|标明本类对应的DOMapper类|实体表的Mapper类|
    |relationDOClass|class(Object.class)|表示关系表对应的类|isRelation=true时生效|
    |relationDOMapperClass|class(Object.class)|标识关系表对应的DOMapper类|与relationDOClass同时出现|
    |foreignProperty|String("")|表示在关联查询的时候,需要以哪个属性作为外键使用|存在于主表中的外键对应的属性名|
    |mainProperty|String("")|表示在查询关系表时,主表的外键对应的属性|存在于子表中的主表主键属性名|
    |isOneToMany|boolean(false)|表示此关系字段是否是一对多的|此值为true时,需要指定"foreignProperty"|
    |isManyToMany|boolean(false)|表示此字段是否是多对多的|此值为true时,需要指定"foreignProperty","mainProperty","relationDOClass","relationDOMapperClass"|
    |isRelation|boolean(false)|表示此字段是否为多对多关系中的关系描述字段|此字段的类型一般都是关系表的实体|

    以上的解释相对准确,但难以理解.

    以下列出常用的@RelationData的组合情况
    
    | 情况 | 使用注解 | 说明及举例 | 
    |-----|-----|---|
    |主实体类对应的DO|@RelationData(baseDOClass = TestClazz.class, <br/>baseDOMapperClass = TestClazzMapper.class)<br/>public class ClazzModel extends TestClazz{}| 1. 一个班级实体<br/>2. 此实体的类型为ClazzModel,继承自TestClazz<br/>3. 此实体对应的DO类型为TestClazz,<br/>4. 此实体对应的Mapper是TestClazzMapper |
    |从主实体视角看来的一对一关系(关系数据在主表中)|@RelationData(baseDOClass = TestTeacher.class,<br/> baseDOMapperClass = TestTeacherMapper.class, <br/>foreignProperty = "adviserPk")<br/>private TestTeacher adviser;| 1. 定义一个班主任属性<br/>2. 类型为TestTeacher,属性名为adviser<br/>3. 该属性对应的DO实体是TestTeacher<br/>4. 对应的Mapper是TestTeacherMapper<br/>5. 班级表中存着班主任的pk,实体中的名称为"adviserPk" |
    |从主实体视角看来的一对一关系(关系数据在子表中)| @RelationData(baseDOClass = TestClazzInfo.class, <br/>baseDOMapperClass = TestClazzInfoMapper.class, <br/>mainProperty = "clazzPk")<br/>private TestClazzInfo clazzInfo; | 1. 定义一个班级信息属性<br/>2. 类型为TestClazzInfo,属性名为clazzInfo<br/>3. 该属性对应的DO实体是TestClazzInfo<br/>4. 对应的Mapper是TestClazzInfoMapper<br/>5. 班级信息中存着班级的pk,实体中的名称为"clazzPk" |
    |从主实体视角看来的一对多关系|@RelationData(baseDOClass = TestStudent.class,<br/> baseDOMapperClass = TestStudent.class, <br/>mainProperty = "clazzPk"<br/>private List<StudentModel> students;| 1. 定义一个学生列表属性<br/>2. 类型为List<StudentModel>,属性名为students<br/>3. 而StudentModel实际上是对应Student类,类型为Student<br/>4. 对应的Mapper是StudentMapper<br/>5. 在Student类中,存着班级的主键,储存的属性名为"clazzPk" |
    |从主实体视角看来的多对多关系|@RelationData(baseDOClass = TestTeacher.class, <br/>baseDOMapperClass = TestTeacherMapper.class,<br/> isManyToMany = true, <br/>relationDOClass = RTestTeacherTTestClazz.class, <br/>relationDOMapperClass = RTestTeacherTTestClazzMapper.class,<br/> foreignProperty = "teacherPk",<br/> mainProperty = "clazzPk")<br/>private List<TeacherModel> teachers;| 1. 定义一个教师列表属性<br/>2. 类型为List<TeacherModel>,属性名为teachers<br/>3. 而TeacherModel对应的DO实体是TestTeacher,对应的Mapper是TestTeacherMapper<br/>4. 这个属性在数据库角度看来,是多对多的关系(一个教师可以与多个班级产生关系,一个班级也可以有多个教师)<br/>5. 维护两者关系的中间关系实体(关系表)为RTestTeacherTTestClazz,对应的Mapper是RTestTeacherTTestClazzMapper<br/>6. 关系表中,标识主实体(在此为班级)的属性为"clazzPk",标识子实体(在此为教师)的属性为"teacherPk" |
    |此实体作为其他的主实体时,中间的关联数据|@RelationData(baseDOClass = RTestTeacherTTestClazz.class, <br/>baseDOMapperClass = RTestTeacherTTestClazzMapper.class, <br/>isRelation = true)<br/>private RTestTeacherTTestClazz courseCount;| 1. 定义一个关联数据的描述属性<br/>2. 类型为RTestTeacherTTestClazz,属性名为courseCount<br/>3. 该属性对应的DO实体是RTestTeacherTTestClazz<br/>4. 对应的Mapper是RTestTeacherTTestClazzMapper<br/>5. 标记这个属性是一个"关系"属性 |
    
    以上即为常用的组合,初期使用时,可以根据实际情况拷贝响应的注解并做修改即可使用.
## 5.4. 创建通用Service/ServiceImpl/Controller.
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
1. 在```src/controller(建议放在此文件夹)```中新建controller.
    ```java
        @RequestMapping("/Mapping地址")
        @RestController
        @Api(description = "班级资源")
        public class 实体Controller类名 extends AbstractController<服务对应的实体Model> {
            @Autowired
            实体服务名 service;
            
            // 固定动作,需要把Controller声明的service注入到AbstractController中
            @Override
            public void init() {
                super.service = service;
            }
        }
    ```

# 6. 代码样例
## 6.1. *Model.java
```java
//  ClazzModel.java

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
            foreignProperty = "adviserPk") // 从表的pk对应字段,所谓从表,就是代表我当前正在写的这个属性所对应的表.我们现在写的是adviser属性,所以标识出Teacher的pk字段即为"FOREIGN"Property
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
}

```
## 6.2. *Controller.java
```java
// Controller.java
@RequestMapping("/class") // 此Controller对应的接口地址
@RestController // 声明是Rest风格的Controller
// 继承自抽象Controller,即具有抽象Controller提供的功能.
// 自身的业务逻辑可在此文件中进行实现.
public class ClazzController extends AbstractController<ClazzModel> { 
    @Autowired
    ClazzService service;

    // 继承抽象Controller时,必须实现的方法.
    // 实际上此方法定义在Controller的接口中,AbstractController是对Controller是不完全实现.
    @Override
    public void init() {
        super.service = service;
    }
}
```
## 6.3. *Service.java
```java
// Service.java
// 自身的业务逻辑可在此文件中进行定义.
public interface ClazzService extends ModelService<ClazzModel> {

}
```
## 6.4. *ServiceImpl.java
```java
// ServiceImpl.java
// 继承自抽象Service,即拥有抽象Service提供的功能.
// 自身的业务逻辑可在此文件中进行实现.
@Service
public class ClazzServiceImpl extends AbstractModelService<ClazzModel> implements ClazzService {

}
```

# 7. 测试用例
方法概览:

| 需求 | 请求方式 |地址| 参数 | 效果 | 返回值 |
|------|-------|--|------|-----|---|
|取实体列表|GET|/model|relations (关系)<br/>condition (条件,仅对主表生效,where子句,小驼峰命名)<br/>keywords (关键字,多字段模糊匹配)<br/>page (取第几页)<br/>size (每页多少条)<br/>orderBy (orderBy子句,小驼峰命名,缺省值为"update desc")<br/>|取实体列表|带分页信息的列表|
|按pk取单个实体|GET|/model/{pk}|relations|取单个实体|带关系的单个实体|
|强制增加实体|POST|/model|{}|强制新建实体|写入后的实体|
|增量修改实体|PATCH|/model/{pk}|{}|将传入的实体中,非null的字段更新到指定pk的实体中|更新后的结果|
|新增或修改实体|PUT|/model|{}|如果传入的实体中有pk,则按照此pk进行更新(null值也会更新到数据库中)<br/>如果传入的实体中没有pk,则在库中新建实体|新增或修改后的实体|
|按pk删除单个实体|DELETE|/model/{pk}||如果是逻辑删除表,则按pk进行逻辑删除<br/>如果不是逻辑删除表,则按pk物理删除该条记录|删除的条数|
|按条件删除|DELETE|/model|condition (条件,仅对主表生效,where子句,小驼峰命名)|如果是逻辑删除表,则按条件进行逻辑删除<br/>如果不是逻辑删除表,则按条件物理删除该条记录|
|取完整逻辑结构|GET|/model/schema|||带注释的完整实体结构|


## 7.1. 查询班级列表
带关系(学生列表,教师列表,班主任)
请求
```
Method: GET
Path:   /class?relations=students,teachers,adviser
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

## 7.2. 按pk取单个班级实体
带关系数据(学生列表,教师列表,班主任)
请求
```
Method: GET
Path:   /class/c1?relations=students,teachers,adviser
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
## 7.3. 按pk删除班级
请求
```
Method: DELETE
Path:   /class/c1
```
返回值
```json
{
    "code": 200,
    "data": 1,
    "message": "SUCCESS"
}
```
## 7.4. 按条件删除班级
请求
```
Method: DELETE
Path:   /class?condition=name like '&一班&'
```
返回值
```json
{
    "code": 200,
    "data": 3,
    "message": "SUCCESS"
}
```
## 7.5. 创建班级
请求
```
Method: POST
Path:   /class
BODY:   {}
```
返回值
```
{
    "code": 200,
    "data": {
        "pk": "d15c38a5-47a4-460e-b2c2-60bbf468c1ee"
    },
    "message": "SUCCESS"
}
```
## 7.6. 强制创建/修改班级
班级本身的null值也进行保存,不包含关系,但如果关系数据记录在班级表中,则也进行null的保存,会丢失关系,即保存实体的即为最终结果
请求
```
Method: PUT
Path:   /class
BODY:   {}
```
返回值
```json
{
    "code": 200,
    "data": {
        "pk": "ac55e6be-25b0-4609-87ad-8ed27d47ccaf"
    },
    "message": "SUCCESS"
}
```
## 7.7. 局部修改班级
null值不保存,无论是关系数据还是班级本身的属性,适用于对数据的修改.
请求
```
Method: Patch
Path:   /class/c1
BODY:   {}
```
返回值
```json
{
    "code": 200,
    "data": {
        "pk": "c1"
    },
    "message": "SUCCESS"
}
```
## 7.8. 查看实体的全部结构及说明(仅在"dev"模式下生效)
请求
```
Method: GET
Path:   /class/schema
```
返回值
```json
{
    "code": 200,
    "data": {
        "ClazzModel (班级实体)": {
            "teachers (教师列表)": [
                {
                    "name (教师名称)": "String",
                    "pk (主键)": "String",
                    "courseData (教师和班级的关系描述数据)": {
                        "count (此教师在此班级内的上课数量)": "Integer",
                        "course (此教师在此班级所上的课程名称    没有外关联关系表)": "String",
                        "pk (主键)": "String"
                    }
                }
            ],
            "adviser (班主任)": {
                "name (教师名称)": "String",
                "pk (主键)": "String",
                "courseData (教师和班级的关系描述数据)": {
                    "count (此教师在此班级内的上课数量)": "Integer",
                    "course (此教师在此班级所上的课程名称    没有外关联关系表)": "String",
                    "pk (主键)": "String"
                }
            },
            "nickName (班级昵称)": "String",
            "students (学生列表)": [
                {
                    "name (学生名称)": "String",
                    "pk (主键)": "String"
                }
            ],
            "studentsCount (学生数量)": "Integer",
            "name (班级名称)": "String",
            "info (班级信息)": {
                "detail ()": "String",
                "pk (主键)": "String"
            },
            "deleted (是否被删除)": "Boolean",
            "pk (主键)": "String"
        }
    },
    "message": "SUCCESS"
}
```
# 8. 异常的抛出
## 8.1. 业务异常
### 8.1.1. 业务异常表的结构
sys_service_exception 表
|字段名|类型|说明|举例|
|-----|----|----|---|
|pk|char(36)|主键,无业务意义||
|code|int|异常编码,抛出异常时的标志位,不允许重复,如果重复,则以后写入的数据为准|100001|
|title|varchar(255)|业务异常的标题|登录失败|
|message|varchar(255)|异常信息主体|用户名不存在|
|reason|varchar(255)|造成异常的原因|您输入的用户名没有找到|
|suggest|varchar(255)|建议操作|请重新输入用户名|

### 8.1.2. 建议操作
在处理请求的过程中,在任意位置都可以通过以下形式进行业务异常的抛出
```java
throw new ServiceException(100101);
``` 
调用方会得到以下样式的结果:
```json
{
    "code": 412,
    "exception": {
        "code": 100001,
        "message": "用户名不存在",
        "pk": "100001",
        "reason": "您输入的用户名没有找到",
        "suggest": "请重新输入用户名",
        "title": "登录失败"
    },
    "message": "登录失败"
}
```
其中,所有的业务异常的code码,均为412,以便调用方进行判断.
调用方得到的exception内的数据,即为```sys_service_exception``` 表中的内容.
message字段中的内容默认为exception中的title.
当代码中抛出的异常码在数据库中不存在时,message字段中的内容为"未知异常".此时,应检查异常表的数据和代码中的code两者是否对应上.

## 8.2 运行异常
运行异常时,会统一返回500状态,且会显示异常的概要,如下:
```json
{
    "code": 500,
    "data": null,
    "message": "接口 [/test/ex] 内部错误：【/ by zero】，请联系管理员"
}
```
特别的,当在dev运行模式下,data中还会携带具体的堆栈信息,如下:
```json
{
    "code": 500,
    "data": [
        {
            "className": "com.redmount.template.controller.TestController",
            "fileName": "TestController.java",
            "lineNumber": 44,
            "methodName": "testException",
            "nativeMethod": false
        },
        ......
        {
            "className": "java.lang.Thread",
            "fileName": "Thread.java",
            "lineNumber": 748,
            "methodName": "run",
            "nativeMethod": false
        }
    ],
    "message": "接口 [/test/ex] 内部错误：【/ by zero】，请联系管理员"
}
```
## 8.3 注意
由于框架在最顶层已经抓住了所有的异常,在非特殊情况下,无须进行try...catch等操作.
特殊情况指需要在发生异常的情况下进行进一步操作的时候,需要进行catch拦截.

# 9. 其他功能
## 9.1. 生成数据库和js模型文档
首先通过```CodeGenerator.java```中的```main()```方法生成基础的```baseModel.java```文件.
运行```test/DocumentGenerator.java```中的```main()```方法,即可在根目录中生成/重写```数据库说明文档.md```和```baseModel.js```文件.

数据库文档会根据数据库的全表进行生成.

baseModel考虑到可能有些数据由于某些原因不公开给前端,所以采用从"base.model"文件夹中进行读取.

baseModel.js在生成后可以根据需要自行进行修改.

## 9.2. 定时Job功能
Job的主业务代码,定义在```job/```下,推荐实现```job.base.JobImpl```抽象类.

在```configurer/ScheduledTaskConfigurer.java```中,将```Job```通过@Autowired注解注入到变量中.

通过```CRON```表达式,启动定时任务.(可依据样例代码进行实现)
