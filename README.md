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

<!-- /code_chunk_output -->



# 0. 简介

本项目继承自 https://github.com/lihengming/spring-boot-api-project-seed

并在其基础上增加了以下功能:
1. 整合Swagger2
1. 业务异常的表级维护
1. 结合表设计规则,可以提供一对一/一对多/多对多/多对多并且带关系数据的数据关联查询
1. 可以提供一对一/一对多/多对多/多对多且带关系数据的数据保存/更新

# 1. 表设计规则
> 本规范中,表分为两种,一种是实体表,一种是关系表.
> 实体表表现在最终的返回结果上,是会把主键(pk)返回的,也就是具有业务意义的表.
> 例如: 班级/学生/老师等以及各种字典表
> 关系表的作用是记录两实体之间有关系,也可以描述两者之间是什么关系.
> 关系表中的数据的主键/外键都不会返回给调用方,但描述的关系数据会包含在子实体的relation属性中,作为对象返回.
## 1.1. 表名规则
1. ```实体表/关系表```所有表名采用全小写字母+下划线的形式,并且不能包含"model"以及"relation"字样.

    ```test_teacher```,```test_student```,```test_clazz```
1. ```关系表```命名规则为: ```r_表1_t_表2```,表示表1与表2的中间关系表.其中,表1与表2应为对应表的全名(如有前缀,则需要包含前缀),表1与表2的先后顺序没有要求.
    
    ```r_test_teacher_t_test_clazz```
1. ```实体表```表名不允许以```r_```开头.
## 1.2. 字段名规则
1. ```实体表/关系表```主键名称规定为"pk",类型为"CHAR",长度为36.(或"VARCHAR",但真实的主键为随机生成的UUID,建议使用定长的"CHAR").
1. ```实体表/关系表```表中字段也采用全小写字母_下划线的形式.

    ```clazz_pk```
1. ```关系表```字段命名规则为: ```表1_pk```,```表2_pk```,其中,表1与表2应为对应表的全名(如有前缀,则需要包含前缀).

    ```test_teacher_pk```,```test_clazz_pk```
1. ```关系表```可以存储其他的关系数据,例如,在班级与老师的关系表中,还可以存在某个老师在某个班级上什么课,上了多少节.则可以在关系表中创建此类字段:

    ```course 课程名称```,```count 上课节数```
1. ```实体表```表示一对一关系的外键,统一命名为"xxx_pk",其中"xxx"表示业务中的名称,不必要为真实的表名.后台处理中会以此字段对应的类型所对应的表去查找数据.
    
   比如: 班级中有个叫做"adviser"的属性,表示这个班级的班主任,则在班级表中,可以定义字段```adviser_pk```,以记录对应的老师的pk.

1. ```实体表```除了表示逻辑外键的字段外,所有字段名不能以"pk"结尾.

    **在后台处理中,如果有"pk"结尾的字段存在,程序会认为这是一个外键,从而取查找相关联的表.**
# 2. 实体创建规则
1. 所有实体必须继承自生成的DO对象,或者直接使用生成的DO对象.
   
   并且继承的实体必须使用"Model"进行结尾.

    ```public class TeacherModel extends TestTeacher // TestTeacher为生成的DO实体类```
1. 实体属性的get/set方法需要严格使用通用的命名方法,推荐使用Lombok的@Data注解,能减少犯错量和代码量.
1. 实体内的实体属性的字段名,必须与数据库中的外键字段名对应.
    
    班级实体内的表示班主任的字段"adviser",对应表中的字段为"adviser_pk".
    ```private TestTeacher adviser;```
1. 如果需要描述关系,则需要在Model中,新建relation字段,类型为Map<String,Object>.最终的relation的结果,会以数据库中的描述字段为准.

# 3. 最佳实践
## 3.1. 准备阶段
1. 先将```src/test/resources/sys_service_exception.sql``` 执行到数据库中,创建业务异常表.
1. 按照规则进行表结构的设计.
1. 删除样例里面的 ```base```、```controller```、```dao```、```model```、```service``` 文件夹里面的内容,**建议保留文件夹本身**。
1. 修改包名(建议在IntelliJ IDEA中进行修改,可以一并修改下属文件的包名).
1. 修改```src/core/ProjectConstant.java``` 里面的内容.

    这里面的内容一旦修改好,尽量不要做调整,以避免各种麻烦.

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
    ```sql
    /*取数据库中所有表名*/
    select concat('tableNames.add("',table_name,'");') from information_schema.tables where table_schema='test' and table_type='BASE TABLE';
    ```
1. 把执行的结果复制出来,粘贴到src/test/CodeGenerator.java文件中的main函数中.
1. 执行```src/test/CodeGenerator.java```文件中的```main```函数.

## 3.3. 编制Model阶段
1. 在```src/model(建议放在此文件夹)```中,新建业务需要的Model实体.

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
