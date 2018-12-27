package com.redmount.template.core;

import com.google.common.base.CaseFormat;
import com.redmount.template.util.ReflectUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

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
     *
     * @param pk        单个实体pk
     * @param relations 关系数据
     * @return 带关系数据的单个实体
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
         * 关系表的对应实体类名
         */
        String relationClassName;
        /**
         * 关系数据的暂存结果
         */
        Map<String, Object> relationMap;
        /**
         * 关系表的查询结果
         * 如果在实体中中,存在"relation"字段,则会将关系表中的所有不以"pk"结尾的字段值以Map<String,Object>的方式赋值给relation字段.
         * 在老师对班级(或者是班级对老师)的关系表中,不仅存着老师与班级的关系,并且还存着这个老师给这个班级上什么课,总共上了多少节等信息.
         */
        Object relationResults;
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
                        relationClassName = "R" + modelClassShortName + "T" + shortClassNameWithoutModel;
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
                        relationResults = mapper.selectByCondition(condition);
                        /**
                         * 判断是否取回来了结果集
                         */
                        if (((List) relationResults).size() > 0) {
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
                            for (Object target : (List) relationResults) {
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
                            if (ReflectUtil.isRelationMap(Class.forName(realFullClassName), "relation")) {
                                /**
                                 * 如果有relation关系,还得把刚才查出来的关系表里面的其他数据,给赋值到这个relation身上.
                                 * 这里写的很难看,值得继续抽象,继续优化
                                 */
                                List<Object> resultListContainsRelation = new ArrayList<>();
                                /**
                                 * 循环关系表中的每一条记录
                                 * [
                                 *  {"clazzPk":"c1","teacherPk":"t1","course":"思想政治课","count":10}, // 每次取出一条这个
                                 *  ...]
                                 */
                                for (Object relationResult : (List) relationResults) {
                                    /**
                                     * 循环结果集表中的每一条记录
                                     * [
                                     *  {"pk":"t1","name":"一班班主任"}, // 每次取一条这个
                                     *  ...
                                     * ]
                                     */
                                    for (Object sourceResult : (List) result) {
                                        /**
                                         * 创建一个与结果集对应的Model类的实例,并把结果集中的数据复制过去.
                                         * 规则里Model必须继承自DO.
                                         * 结果为:
                                         * {
                                         *  "pk":"",
                                         *  "name":"一班班主任",
                                         *  "relation":{} // 主要多了一个这个,而这个是Model类里面定义的Map<String,Object>
                                         *  }
                                         */
                                        Object relationTarget = ReflectUtil.cloneObj(sourceResult, Class.forName(realFullClassName));
                                        /**
                                         * 取出对应的两条数据
                                         * 条件为关系结果集中的"teacherPk"的值等于结果中的"pk"的值
                                         */
                                        if (ReflectUtil.getFieldValue(relationResult, javaTargetFieldName).equals(ReflectUtil.getFieldValue(sourceResult, "pk"))) {
                                            /**
                                             * 取到了,则创建一个Map,作为装在relation的容器
                                             */
                                            relationMap = new HashMap<>();
                                            /**
                                             * 循环关系结果集中的每一个字段
                                             * count,course等
                                             */
                                            for (Field relationResultField : relationResult.getClass().getDeclaredFields()) {
                                                /**
                                                 * 如果不以pk作为结尾,则说明是有意义的数据
                                                 * 对于关系表,他自己的pk是无意义的.
                                                 * 对于数据的观察者来说,关系表中的两表的pk也是无意义的,已经在返回的数据结构中体现出了关系.
                                                 * 所以不需要返回所有"pk"结尾的字段了.
                                                 */
                                                if (!StringUtils.endsWith(relationResultField.getName(), "Pk") && !StringUtils.endsWith(relationResultField.getName(), "pk")) {
                                                    /**
                                                     * 把有意义的数据放在Map中
                                                     */
                                                    relationMap.put(relationResultField.getName(), ReflectUtil.getFieldValue(relationResult, relationResultField.getName()));
                                                }
                                            }
                                            /**
                                             * 把拼装好的关系数据Map赋值给刚才拷贝出来的,带关系的Model的relation字段中.
                                             * 就是拿这个"relation"进行的判断,所以这个肯定是能赋值的.
                                             */
                                            ReflectUtil.setFieldValue(relationTarget, "relation", relationMap);
                                            /**
                                             * 带关系的Model列表里,添加组织好的数据.
                                             */
                                            resultListContainsRelation.add(relationTarget);
                                        }
                                    }
                                }
                                /**
                                 * 这层是在有relation的情况下进行的.
                                 * 为了代码逻辑统一,统一的将result最为最终的结果,赋值给最终的返回实体类.
                                 */
                                result = resultListContainsRelation;
                            }
                        }
                        /**
                         * 无论取没取出来列表类型的数据,都把取出来的值赋值给最终的返回结果.
                         */
                        ReflectUtil.setFieldValue(model, relation, result);
                    }
                } else {
                    /**
                     * 一对一关系,一个班级取唯一的班主任,而且主表知道自己的从表是谁的情况下.
                     * 班级知道自己的班主任是谁,班主任不知道自己属于哪个班级.
                     * 现在取的是班级,所以班级表中一定含有班主任的pk.
                     * 规则...
                     */
                    mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + shortClassNameWithoutModel + "Mapper"));
                    /**
                     * 拼装查询条件,在班级中取adviserPk对应的值.
                     */
                    result = mapper.selectByPrimaryKey(ReflectUtil.getFieldValue(model, relation + "Pk"));
                    /**
                     * 取出来的值放到最终的返回结果的相应字段中
                     */
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
        /**
         * 首先保存主表,并拿到主表的pk
         */
        String mainPk = model.getPk();
        /**
         * 主实体包含的关系实体列表
         */
        List<Field> relationFields;
        /**
         * 关系实体对应的
         */
        String realFieldFullClassName;
        /**
         * 关系实体对应的短类名
         */
        String realFieldShortName;
        /**
         * 关系实体对应的DO短类名
         */
        String realFieldShortNameWithoutModel;
        /**
         * 关系数据的实际值
         */
        Object currentFeildValue;
        /**
         * java实体中子表的字段名
         * teachers
         */
        String javaTargetFieldName;
        /**
         * clazzPk
         */
        String javaMainFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, modelClassShortName) + "Pk";
        /**
         * 关系表对应的实体名
         */
        String relationClassName;
        /**
         * 关系表对应的DO对象
         */
        Object currentRelatioinedDO;
        /**
         * 关系表中是否有relation
         */
        boolean isContainsRelation;
        Map<String, Object> relationDataMap;
        /**
         * 上来就开始try,有点那啥哈..
         * 先实现功能再说.
         */
        try {
            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + modelClassShortName + "Mapper"));
            /**
             * 如果传进来的model没有pk,则说明是条新记录
             */
            if (StringUtils.isBlank(mainPk)) {
                /**
                 * 新的数据,要给pk.UUID的形式
                 */
                mainPk = UUID.randomUUID().toString();
                model.setPk(mainPk);
                /**
                 * 插入新记录
                 */
                mapper.insert(model);
            } else {
                /**
                 * 更新实体
                 */
                mapper.updateByPrimaryKeySelective(model);
            }
            /**
             * 取出所有的关系字段
             */
            relationFields = ReflectUtil.getRelationFields(model);
            /**
             * 循环每一个关系字段
             */
            for (Field currentField : relationFields) {
                /**
                 * 先判断这个字段是否为空
                 */
                currentFeildValue = ReflectUtil.getFieldValue(model, currentField.getName());
                /**
                 * 如果取出来的值是空,就啥也不管了,继续下一个
                 * 不管是实体还是list还是啥,都不管.
                 */
                if (currentFeildValue == null) {
                    continue;
                }
                /**
                 * 先判断这个关系是不是实体
                 */
                if (currentField.getType().getName().startsWith("java.util.List")) {
                    /**
                     * 取出List包含的真正完全类型
                     * java.util.List<com.redmount.template.model.TestTeacherModel>
                     *     ->
                     * com.redmount.template.model.TestTeacherModel
                     */
                    realFieldFullClassName = currentField.getGenericType().getTypeName().split("<|>")[1];
                    /**
                     * 取出实体的短类名
                     * com.redmount.template.model.TestTeacherModel
                     * ->
                     * TestTeacherModel
                     */
                    realFieldShortName = realFieldFullClassName.split("\\.")[realFieldFullClassName.split("\\.").length - 1];
                    /**
                     * 取出实体对应的DO短类名
                     * 这个名称是取mapper啥的用的.
                     * TestTeacherModel
                     * ->
                     * TestTeacher
                     */
                    realFieldShortNameWithoutModel = realFieldShortName.replaceAll("Model", "");
                    /**
                     * 如果是list,先判断从表里面有没有主表的pk
                     */
                    if (ReflectUtil.containsProperty(Class.forName(realFieldFullClassName), modelClassShortName + "Pk")) {

                        /**
                         * 有主表pk的情况下,走的是从表的主表pk
                         */
                        System.out.println(currentField.getGenericType() + ":有主表pk");
                        /**
                         * 得到子表操作的Mapper
                         */
                        mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + realFieldShortNameWithoutModel + "Mapper"));
                        /**
                         * 先清除原有的父级pk
                         * 注意: 这样就可能产生没有父级的野数据
                         */
                        Condition condition = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + realFieldShortNameWithoutModel));
                        condition.createCriteria().andEqualTo(javaMainFieldName, mainPk);
                        Object childDOWithoutMainPk = Class.forName(ProjectConstant.MODEL_PACKAGE + "." + realFieldShortNameWithoutModel).newInstance();
                        ReflectUtil.setFieldValue(childDOWithoutMainPk, javaMainFieldName, "");
                        mapper.updateByConditionSelective(childDOWithoutMainPk, condition);
                        /**
                         * 把从表的主表pk值更新
                         * 这得循环赋值
                         */
                        for (Object currentItem : (List) currentFeildValue) {
                            /**
                             * 判断子表是不是有pk,如果有,则update
                             * todo:没有咋办?
                             */
                            if (!StringUtils.isBlank(((BaseDO) currentItem).getPk())) {
                                ReflectUtil.setFieldValue(currentItem, javaMainFieldName, mainPk);
                                mapper.updateByPrimaryKeySelective(currentItem);
                            } else {
                                System.out.println("子表没有pk");
                            }
                        }
                        /**
                         * 至此,从表知道自己属于哪个主表的这种情况就完事儿了.
                         */
                    } else {

                        /**
                         * 没有主表pk的情况下,走的是关系表
                         *
                         * 取从表pk的字段名
                         * testTeacherPk
                         */
                        javaTargetFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, realFieldShortNameWithoutModel) + "Pk";
                        /**
                         * 先取出关系表的Mapper
                         */
                        relationClassName = "R" + modelClassShortName + "T" + realFieldShortNameWithoutModel;
                        try {
                            /**
                             * 先抱着"试试看"的心态,取一下mapper
                             * 没取到的时候就换个马甲再取一遍
                             */
                            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + relationClassName + "Mapper"));
                        } catch (ClassNotFoundException ex) {
                            /**
                             * 到这了,就说明刚才没试成功,换个马甲
                             */
                            relationClassName = "R" + realFieldShortNameWithoutModel + "T" + modelClassShortName;
                            /**
                             * 再取一遍
                             */
                            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + relationClassName + "Mapper"));
                        }
                        /**
                         * 删除主表对应的关系表
                         */
                        Condition condition = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + relationClassName));
                        condition.createCriteria().andEqualTo(javaMainFieldName, mainPk);
                        mapper.deleteByCondition(condition);
                        /**
                         * 看这个类里面是否有关系数据
                         */
                        isContainsRelation = ReflectUtil.isRelationMap(Class.forName(realFieldFullClassName), "relation");
                        /**
                         * 到这了,说明关系表的mapper就已经取到了
                         *
                         * 应该循环关系数据里面的数据了
                         */
                        for (Object currentRelationedListItem : (List) currentFeildValue) {
                            /**
                             * 创建一个要进数据库的关系表DO
                             * 所有生成的DO都包含在MODEL_PACKAGE里,这是规范...
                             * 所以一定能找得到
                             */
                            currentRelatioinedDO = Class.forName(ProjectConstant.MODEL_PACKAGE + "." + relationClassName).newInstance();
                            /**
                             * 给关系表生成pk
                             */
                            ((BaseDO) currentRelatioinedDO).setPk(UUID.randomUUID().toString());
                            /**
                             * 关系表里的主表pk
                             */
                            ReflectUtil.setFieldValue(currentRelatioinedDO, javaMainFieldName, mainPk);
                            /**
                             * 关系表里的从表pk
                             */
                            ReflectUtil.setFieldValue(currentRelatioinedDO, javaTargetFieldName, ((BaseDO) currentRelationedListItem).getPk());
                            /**
                             * 如果有relation数据,还得把relation数据放在关系表DO里面
                             */
                            if (isContainsRelation) {
                                /**
                                 * 取出传入数据的relation
                                 */
                                relationDataMap = (Map<String, Object>) ReflectUtil.getFieldValue(currentRelationedListItem, "relation");
                                for (String key : relationDataMap.keySet()) {
                                    /**
                                     * 把每个relation的值灌到DO实体中,以便保存
                                     */
                                    ReflectUtil.setFieldValue(currentRelatioinedDO, key, relationDataMap.get(key));
                                }
                                /**
                                 * 赋值数据创建时间
                                 */
                                ReflectUtil.setFieldValue(currentRelatioinedDO, "created", new Date());
                                /**
                                 * 单条插入关系表数据.
                                 * mapper.insertList(List)只支持主键有默认值的.
                                 * 看看以后如何解决此事,是从数据结构上解决,还是自己写个批量插入语句解决.
                                 */
                                mapper.insert(currentRelatioinedDO);
                            }
                        }
                    }
                } else {
                    javaTargetFieldName = currentField.getName() + "Pk";
                    ReflectUtil.setFieldValue(model, javaTargetFieldName, ((BaseDO) currentFeildValue).getPk());
                }
            }
            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + modelClassShortName + "Mapper"));
            mapper.updateByPrimaryKeySelective(model);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return model;
    }
}
