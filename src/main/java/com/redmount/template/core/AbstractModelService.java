package com.redmount.template.core;

import com.google.common.base.CaseFormat;
import com.redmount.template.util.ReflectUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractModelService<T extends BaseDO> implements ModelService<T> {

    private Mapper mapper;

    @Autowired
    SqlSession sqlSession;

    /**
     * 当前泛型真实类型的Class
     */
    private Class<T> modelClass;

    private String modelClassShortName;

    public AbstractModelService() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        modelClass = (Class<T>) pt.getActualTypeArguments()[0];
        modelClassShortName = modelClass.getTypeName().split("\\.")[modelClass.getTypeName().split("\\.").length - 1].replaceAll("Model", "");
    }

    /**
     * 取单个实体
     * 此方法以TestClazzModel作为说明.
     * TestClazzModel定义如下:
     * public class TestClazzModel extends TestClazz {
     * private TestTeacher adviser; // 类型为生成的DO
     * private List<TestStudent> students; // 类型为生成的DO
     * private List<TestTeacherModel> teachers; // 自定义类型,继承自数据库的DO: TestTeacher
     * }
     * <p>
     * TestTeacherModel定义如下:
     * public class TestTeacherModel extends TestTeacher {
     * private Map<String, Object> relation; // 关系表的关系数据存放容器,现在只支持Map<String,Object>类型,以对应多种数据结构
     * }
     *
     * @param pk        单个实体pk
     * @param relations 关系数据
     * @return 带关系数据的单个实体
     */
    @Override
    public T getByPk(String pk, String relations) {
        try {
            /**
             * 实例化出来此Model类型对应的DO类型的Mapper,例如 TestClazzModel继承自TestClazz,则实例化TestClazz表的Mapper
             */
            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + modelClassShortName.replaceAll("Model", "") + "Mapper"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        /**
         * 取数据库中的元数据
         * 此时取出来的是test_clazz表中的数据,平表,单条
         */
        Object baseResult = mapper.selectByPrimaryKey(pk);

        if (baseResult == null) {
            return null;
        }
        /**
         * 实例化出来需要返回的Model实例,并将从数据库中取出的信息复制到新的实例中,此方法可以解决子类不能直接被基类实例化的问题.
         * java中, Animal person = new Person() 是可以的,但是Person person = new Animal()是不行的.
         * 但是我们的规范中,Model都是继承自DO类型的,仅仅是比DO类型多关联属性,所以DO里面的数据是完全可以复制到Model类型中的.
         */
        T model = ReflectUtil.cloneObj(baseResult, modelClass);
        /**
         * 判断传入的关系数据是否指定了关系,如果不指定关系的话,就没必要走下面那一大堆反射了,直接返回平表的数据.
         * 但是该有的属性还是有的,比如TestClazzModel中定义的adviser,teachers,students,这些字段在返回的时候让然存在,只不过值为null.
         * todo:以后需要解决关系数据在不load的时候不要返回,不返回的意思是连字段都没有.
         * 也就是说,光查单表的数据的话,都不知道这个实体有啥关联实体 2018年12月26日
         */
        if (StringUtils.isBlank(relations)) {
            return model;
        }
        /**
         * 主表的pk
         * 作为关系表中的查询条件值
         */
        String mainPk = model.getPk();
        /**
         * 取可以load的关系列表
         * 此方法取了relations与实际的Model类中的字段名称的交集,防止在以后的代码中反射取值时出现没有对应的属性值的问题.
         * 例如: 传进来的 relations=teachers,adviser,students,pk,jobs
         * 其中,pk是普通字段,无须load;jobs在Model里不存在.
         * 这两条都可以被去除
         */
        List<String> relationList = ReflectUtil.getFieldList(modelClass, relations);
        /**
         * 当前正在循环的字段
         * 可能的值为"adviser,students,teachers"
         */
        Field field;
        /**
         * 类型的全名称
         * 可能的值为"java.util.List<com.redmount.template.model.TestTeacherModel>","com.redmount.template.base.model.TestTeacher",...
         */
        String fullClassName;
        /**
         * 真实的类型全名称
         * 主要针对List<~>这种类型,取出里面的"~"这个值用的.
         * 可能的值为"com.redmount.template.model.TestTeacherModel"....
         */
        String realFullClassName;
        /**
         * 这个比较乱,目前还没弄清晰这个值应该怎么用,待整理.
         * 可能的值为"TestTeacher","TestTeacherModel","TestStudent",都是对应的Model或DO的类名(短类名)
         */
        String shortClassName;
        /**
         * 去除了"Model"的短类名,此类名更有用,反射产生Mapper之类的操作都需要这个短类名.
         * 如果shortClassName中本身不含"Model"的话,那么这个名称和shortClassName是一样的.
         * 主要为了解决类型仍为自定义类型的情形.
         * TestTeacherModel
         * ->
         * TestTeacher
         */
        String shortClassNameWithoutModel;
        /**
         * 将完全的类名以"./</>"这三种符号分隔开的数组,目的是要取到Model或DO的短类名
         * 可能的值为:["com","redmount","template","base","model","TestStudent"]....
         */
        String[] fullClassNamePath;
        /**
         * 实体属性的查询结果
         * 可能的值为TestTeacher的DO实体(针对adviser的查询结果),或者List<TestStudent>的列表结果.
         */
        Object result = null;
        /**
         * java实体中子表的字段名
         * teachers
         */
        String javaTargetFieldName;
        /**
         * clazzPk
         */
        String javaMainFieldName;
        /**
         * SQL语句中子表的关系字段名
         * test_teacher_pk
         */
        String sqlTargetFieldName;
        /**
         * SQL语句中主表的字段名
         * test_clazz_pk
         */
        String sqlMainFieldName;
        /**
         * 关系数据的暂存结果
         */
        Map<String, Object> relationMap = new HashMap<>();
        /**
         * 关系表的查询结果
         * 如果在实体中中,存在"relation"字段,则会将关系表中的所有不以"pk"结尾的字段值以Map<String,Object>的方式赋值给relation字段.
         * 在老师对班级(或者是班级对老师)的关系表中,不仅存着老师与班级的关系,并且还存着这个老师给这个班级上什么课,总共上了多少节等信息.
         */
        Object relationResult;
        /**
         * 循环可以load的关系列表
         * 此列表已经由前面的函数进行过过滤
         * 取值仅取非简单类型的实体字段名
         */
        for (String relation : relationList) {
            /**
             * todo:try是try了,出错之后怎么处理,是个问题...2018年12月26日
             */
            try {
                /**
                 * 取出来一个字段准备处理
                 * teachers
                 */
                field = modelClass.getDeclaredField(relation);
                /**
                 * 取出来这个字段的真实类型全名
                 * java.util.List<com.redmount.template.model.TestTeacherModel>
                 */
                fullClassName = field.getGenericType().getTypeName();
                /**
                 * 取这个字段对应的类型打散成数组,目的是下一步取短名称
                 * java.util.List<com.redmount.template.model.TestTeacherModel>
                 *     ->
                 * [java,util,List,com,redmount,template,model,TestTeacherModel]
                 */
                fullClassNamePath = fullClassName.split("\\.|<|>");
                /**
                 * 取类型的短名称
                 * [java,util,List,com,redmount,template,model,TestTeacherModel]
                 *      ->
                 * TestTeacherModel
                 */
                shortClassName = fullClassNamePath[fullClassNamePath.length - 1];
                /**
                 * 取对应的实体的短类名
                 * TestTeacherModel
                 * ->
                 * TestTeacher
                 */
                shortClassNameWithoutModel = shortClassName.replaceAll("Model", "");
                /**
                 * 总的处理步骤:
                 * 判断类型全名是否是"java.util.List"开头的.
                 * 如果不是,则说明这是个实体类型,取表中对应的pk,到数据库取出即可.
                 * 如果是,则说明这个字段对应的类型是个List.
                 *  然后再判断目标表里面是否含有主表的外键,如果有,则直接按照外键=主表pk的条件取出即可
                 *  如果没有,那麻烦了,得从中间表里取出子表的pk列表,然后把pk列表作为条件取子表的列表.
                 *  再判断目标类型中是否有"relation"这个字段,如果有,还得把关系表里的其他非"pk"结尾的数据作为关系数据,存到relation里.
                 */
                if (field.getGenericType().getTypeName().startsWith("java.util.List")) {
                    /**
                     * 走到这说明这个字段的类型是个数组
                     *
                     * 从MyBatis的Mapper工厂中,取出来对应的Mapper进行操作
                     * 这里使用了项目配置的常量: ProjectConstant.MAPPER_PACKAGE,以及"Mapper"结尾.
                     * 生成器生成的时候,也是使用这个常量作为包的存放地址,并且也是以硬编码的形式强制以"Mapper"结尾.
                     * 所以如果按照规则生成并使用的话,是没有问题的,可以定位到相应的表Mapper.
                     * todo:有空得把"Mapper"这个也提成常量,硬编码不是个事儿...2018年12月26日
                     */
                    mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + shortClassNameWithoutModel + "Mapper"));
                    /**
                     * fullClassName="java.util.List<com.redmount.template.model.TestTeacherModel>"
                     * ->
                     * realFullClassName="com.redmount.template.model.TestTeacherModel"
                     */
                    realFullClassName = fullClassName.split("<|>")[1];
                    /**
                     * 主表的Pk的java命名
                     * TestClazzModel
                     * ->
                     * testClazzPk
                     */
                    javaMainFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, modelClassShortName.replaceAll("Model", "") + "Pk");
                    /**
                     * 判断目标类型中是否含有主表表名的pk
                     * 类TestTeacherModel中(包含基类),是否含有testClazzPk
                     * Teacher类里是没有,但是Student类里有.
                     */
                    if (ReflectUtil.containsProperty(Class.forName(realFullClassName), javaMainFieldName)) {
                        /**
                         * 如果子表中含有主表的外键
                         * 例如 TestStudent 中,含有testClazzPk
                         *
                         * 取出来主表pk对应的SQL字段名
                         * testClazzPk
                         * ->
                         * test_clazz_pk
                         */
                        sqlMainFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, javaMainFieldName);
                        /**
                         * 创建Condition,用来查询子表
                         */
                        Condition condition = new Condition(Class.forName(realFullClassName));
                        /**
                         * 添加条件,主表pk=主表pk
                         */
                        condition.createCriteria().andEqualTo(javaMainFieldName, mainPk);
                        /**
                         * 取出来所有主表pk=主表pk的,就是要取的数据了.
                         * todo:逻辑删除的事儿....再议...2018年12月27日
                         */
                        result = mapper.selectByCondition(condition);
                        /**
                         * 把取出来的数据放到要返回的对象里.
                         */
                        ReflectUtil.setFieldValue(model, relation, result);
                    } else {
                        /**
                         * 如果没找到对应的主表PK,就得去关系表里边取了.
                         *
                         * 拼装表名,规则是R_表名A_T_表名B
                         * 其中表名是完全限定名,包括表名的前缀都得带上
                         */
                        // shortClassName = fullClassNamePath[fullClassNamePath.length - 1];
                        String relationClassName = "R" + modelClassShortName + "T" + shortClassNameWithoutModel;
                        try {
                            /**
                             * 尝试用拼装的表名取Mapper.
                             * 这一步可能会取不着,因为表A表B两表之间没有主从关系,谁在前谁在后都行.
                             */
                            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + relationClassName + "Mapper"));
                        } catch (ClassNotFoundException ex) {
                            /**
                             * 如果上一步没有取成的话,那就换个顺序再取一遍.
                             * 再要是取不着,那就没招了,报错了.
                             */
                            relationClassName = "R" + shortClassName.replaceAll("Model", "") + "T" + modelClassShortName;
                            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + relationClassName + "Mapper"));
                        }
                        /**
                         * 还是取主表pk的数据库字段名
                         * TestClazz
                         * ->
                         * test_clazz_pk
                         */
                        sqlMainFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, modelClassShortName + "Pk");
                        /**
                         * 取对应表的Condition
                         * relationClassName在上面已经试错过了,所以肯定会有效的.
                         */
                        Condition condition = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + relationClassName));
                        /**
                         * 添加条件,主表pk=主表pk
                         */
                        condition.createCriteria().andEqualTo(javaMainFieldName, mainPk);
                        /**
                         * 取关系表结果集
                         */
                        relationResult = mapper.selectByCondition(condition);
                        /**
                         * 判断是否取回来了结果集
                         */
                        if (((List) relationResult).size() > 0) {
                            /**
                             * 如果取回来了结果,则拼装 "in" 条件的List
                             */
                            List<String> targetPkList = new ArrayList<>();
                            /**
                             * 子表的对应java字段名
                             * 主要用于使用Condition拼装条件用
                             * 在关系表的对应DO中,关联到子表的外键的名称,在java类里面叫什么
                             * testTeacherPk
                             */
                            javaTargetFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, shortClassNameWithoutModel) + "Pk";
                            /**
                             * 把关系表结果集中的子表pk值都取出来
                             */
                            for (Object target : (List) relationResult) {
                                targetPkList.add(ReflectUtil.getFieldValue(target, javaTargetFieldName).toString());
                            }
                            /**
                             * 取子表的Mapper
                             * 依然是按照规则,Mapper的存放地+实体类名+Mapper,肯定能找到.
                             * 如果找不到,就说明要么表没生成全,要么就是生成之后挪地方了.
                             */
                            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + shortClassNameWithoutModel + "Mapper"));
                            /**
                             * 创建Condition对象,依然是按照规则走的,肯定能找到
                             */
                            condition = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + shortClassNameWithoutModel));
                            /**
                             * 把需要查的"in"条件扔里
                             * todo:逻辑删除的事儿...再议...2018年12月27日
                             */
                            condition.createCriteria().andIn("pk", targetPkList);
                            /**
                             * 查询子表的结果
                             */
                            result = mapper.selectByCondition(condition);
                            /**
                             * 取出来之后不能直接用,还得看java实体类中,是不是有relation,关系.
                             */
                            if (ReflectUtil.containsProperty(Class.forName(realFullClassName), "relation")) {
                                /**
                                 * 如果有relation关系,还得把刚才查出来的关系表里面的其他数据,给赋值到这个relation身上.
                                 * 这里写的很难看,值得继续抽象,继续优化
                                 */
                                List<Object> resultListContainsRelation = new ArrayList<>();
                                for (Object obj : (List) relationResult) {
                                    for (Object target : (List) result) {
                                        Object o = ReflectUtil.cloneObj(target, Class.forName(realFullClassName));
                                        if (ReflectUtil.getFieldValue(obj, shortClassName.replaceAll("Model", "").substring(0, 1).toLowerCase() + shortClassName.replaceAll("Model", "").substring(1) + "Pk").equals(ReflectUtil.getFieldValue(target, "pk"))) {
                                            relationMap = new HashMap<>();
                                            for (Field relationResultField : obj.getClass().getDeclaredFields()) {
                                                if (!StringUtils.endsWith(relationResultField.getName(), "Pk") && !StringUtils.endsWith(relationResultField.getName(), "pk")) {
                                                    relationMap.put(relationResultField.getName(), ReflectUtil.getFieldValue(obj, relationResultField.getName()));
                                                }
                                            }
                                            ReflectUtil.setFieldValue(o, "relation", relationMap);
                                            resultListContainsRelation.add(o);
                                        }
                                    }
                                }
                                result = resultListContainsRelation;
                            }
                        }
                        ReflectUtil.setFieldValue(model, relation, result);
                    }
                } else {
                    mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + shortClassNameWithoutModel + "Mapper"));
                    result = mapper.selectByPrimaryKey(ReflectUtil.getFieldValue(model, relation + "Pk"));
                    ReflectUtil.setFieldValue(model, relation, result);
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return model;
    }

    /**
     * 取符合条件的实体列表
     *
     * @param keywords  关键字
     * @param relations 关系数据
     * @param orderBy   排序
     * @return 带关系数据的排序的实体列表
     */
    @Override
    public List<T> list(String keywords, String relations, String orderBy) {
        return null;
    }

    @Override
    public T saveAutomatic(T model) {
        if (StringUtils.isBlank(model.getPk())) {
        }
        return model;
    }
}
