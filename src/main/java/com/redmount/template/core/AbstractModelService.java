package com.redmount.template.core;

import com.google.common.base.CaseFormat;
import com.redmount.template.core.annotation.RelationData;
import com.redmount.template.util.NameUtil;
import com.redmount.template.util.ReflectUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.lang.annotation.Annotation;
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

    /**
     * 主体Model对应的DO名称
     */
    private String modelClassDOSimpleName;

    public AbstractModelService() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        modelClass = (Class<T>) pt.getActualTypeArguments()[0];
        modelClassDOSimpleName = modelClass.getAnnotation(RelationData.class).baseDOTypeName();
    }

    /**
     * 取单个实体
     *
     * @param pk        单个实体pk
     * @param relations 关系数据
     * @return 带关系数据的单个实体
     * 此方法以TestClazzModel作为说明.
     */
    @Override
    public T getAutomatic(String pk, String relations) {
        mapper = initMainMapper();
        if (mapper == null) {
            throw new RuntimeException(modelClass.getName() + "没有RelationData注解");
        }
        Object baseResult = mapper.selectByPrimaryKey(pk);
        if (baseResult == null) {
            return null;
        }
        T model = ReflectUtil.cloneObj(baseResult, modelClass);
        if (StringUtils.isBlank(relations)) {
            return model;
        }
        List<String> relationList = ReflectUtil.getFieldList(modelClass, relations);
        Field field;
        for (String relation : relationList) {
            try {
                field = modelClass.getDeclaredField(relation);
                Annotation fieldRelationDataAnnotation = field.getDeclaredAnnotation(RelationData.class);
                if (fieldRelationDataAnnotation == null) {
                    continue;
                }
                if (((RelationData) fieldRelationDataAnnotation).isOneToMany()) {
                    model = loadOneToManyRelation(model, field);
                } else if (((RelationData) fieldRelationDataAnnotation).isManyToMany()) {
                    model = loadManyToManyRelation(model, field);
                } else {
                    loadOneToOneRelation(model, field);
                }
            } catch (NoSuchFieldException e) {
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
    public List list(String keywords, String condition, String relations, String orderBy) {
        List<T> retList = new ArrayList<>();
        List<Field> fields = ReflectUtil.getKeywordsFields(modelClass);
        List<Object> results;
        try {
            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + modelClassDOSimpleName + "Mapper"));
            Example example = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + modelClass.getAnnotation(RelationData.class).baseDOTypeName()));
            Condition.Criteria criteriaKeywords = example.createCriteria();
            Condition.Criteria criteriaCondition = example.createCriteria();
            if (StringUtils.isNotBlank(keywords)) {
                for (Field field : fields) {
                    if (field.getType() != String.class) {
                        throw new IllegalArgumentException("@Keywords 注解只能标记在String类型的字段上:" + field.toString());
                    }
                    criteriaKeywords.orLike(field.getName(), "%" + keywords + "%");
                }
            }
            if (StringUtils.isNotBlank(condition)) {
                criteriaCondition.andCondition(NameUtil.transToDBCondition(condition));
                example.and(criteriaCondition);
            }
            example.setOrderByClause(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, orderBy));
            results = mapper.selectByCondition(example);
            if (StringUtils.isNotBlank(relations)) {
                List<Object> resultWithRelations = new ArrayList<>();
                for (Object item : results) {
                    resultWithRelations.add(getAutomatic(((BaseDO) item).getPk(), relations));
                }
                return resultWithRelations;
            }
            for (Object result : results) {
                retList.add(ReflectUtil.cloneObj(result, modelClass));
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return retList;
    }

    /**
     * 自动保存
     *
     * @param model 需要保存的数据,目前的限制是只保存表现层中的两层,带关系数据的,保存关系数据.再往下就不管了.
     * @return 保存之后的结果
     */
    @Override
    public T saveAutomatic(T model, boolean forceSaveNull) {
        String mainPk = model.getPk();
        Annotation mainClassRelationDataAnnotation = modelClass.getAnnotation(RelationData.class);
        if (mainClassRelationDataAnnotation != null) {
            modelClassDOSimpleName = ((RelationData) mainClassRelationDataAnnotation).baseDOTypeName();
        }
        List<Field> relationFields;
        String realFieldClassFullName;
        String realFieldClassShortNameWithoutModel;
        Object currentFeildValue;
        String javaTargetFieldName;
        String javaMainFieldName;
        Object currentRelatioinedDO;
        Condition condition;
        Object relationData;
        try {
            mapper = initMainMapper();
            if (StringUtils.isBlank(mainPk)) {
                mainPk = UUID.randomUUID().toString();
                model.setPk(mainPk);
                model.setCreated(new Date());
                mapper.insert(model);
            } else {
                if (forceSaveNull) {
                    mapper.updateByPrimaryKey(model);
                } else {
                    mapper.updateByPrimaryKeySelective(model);
                }
            }
            relationFields = ReflectUtil.getRelationFields(model);
            for (Field currentField : relationFields) {
                currentFeildValue = ReflectUtil.getFieldValue(model, currentField.getName());
                if (currentFeildValue == null) {
                    continue;
                }
                Annotation currentFieldRelationDataAnnotation = currentField.getAnnotation(RelationData.class);
                if (currentFieldRelationDataAnnotation == null) {
                    continue;
                }
                if (((RelationData) currentFieldRelationDataAnnotation).isOneToMany()) {
                    javaMainFieldName = ((RelationData) currentFieldRelationDataAnnotation).mainProperty();
                    mapper = initMapperByDOSimpleName(((RelationData) currentFieldRelationDataAnnotation).baseDOTypeName());
                    condition = getConditionBySimpleDOName(((RelationData) currentFieldRelationDataAnnotation).baseDOTypeName());
                    condition.createCriteria().andEqualTo(javaMainFieldName, mainPk);
                    Object childDOWithoutMainPk = Class.forName(ProjectConstant.MODEL_PACKAGE + "." + ((RelationData) currentFieldRelationDataAnnotation).baseDOTypeName()).newInstance();
                    ReflectUtil.setFieldValue(childDOWithoutMainPk, javaMainFieldName, "");
                    mapper.updateByConditionSelective(childDOWithoutMainPk, condition);
                    for (Object currentItem : (List) currentFeildValue) {
                        if (!StringUtils.isBlank(((BaseDO) currentItem).getPk())) {
                            ReflectUtil.setFieldValue(currentItem, javaMainFieldName, mainPk);
                            ReflectUtil.setFieldValue(currentItem, "updated", new Date());
                            mapper.updateByPrimaryKeySelective(currentItem);
                        } else {
                            System.out.println("子表没有pk");
                        }
                    }
                } else if (((RelationData) currentFieldRelationDataAnnotation).isManyToMany()) {
                    mapper = initMapperByDOSimpleName(((RelationData) currentFieldRelationDataAnnotation).relationDOTypeName());
                    realFieldClassFullName = ((ParameterizedType) currentField.getGenericType()).getActualTypeArguments()[0].getTypeName();
                    realFieldClassShortNameWithoutModel = Class.forName(realFieldClassFullName).getAnnotation(RelationData.class).baseDOTypeName();
                    if (StringUtils.isNotBlank(((RelationData) currentFieldRelationDataAnnotation).mainProperty())) {
                        javaMainFieldName = ((RelationData) currentFieldRelationDataAnnotation).mainProperty();
                    } else {
                        javaMainFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, modelClass.getAnnotation(RelationData.class).baseDOTypeName() + "Pk");
                    }
                    if (StringUtils.isNotBlank(((RelationData) currentFieldRelationDataAnnotation).foreignProperty())) {
                        javaTargetFieldName = ((RelationData) currentFieldRelationDataAnnotation).foreignProperty();
                    } else {
                        javaTargetFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, realFieldClassShortNameWithoutModel + "Pk");
                    }

                    condition = getConditionBySimpleDOName(currentField.getAnnotation(RelationData.class).relationDOTypeName());
                    condition.createCriteria().andEqualTo(javaMainFieldName, mainPk);
                    mapper.deleteByCondition(condition);
                    Field relationDataField = ReflectUtil.getRelationDataField(Class.forName(((ParameterizedType) currentField.getGenericType()).getActualTypeArguments()[0].getTypeName()), ((RelationData) currentFieldRelationDataAnnotation).relationDOTypeName());
                    for (Object currentRelationedListItem : (List) currentFeildValue) {
                        if (relationDataField != null) {
                            currentRelatioinedDO = ReflectUtil.getFieldValue(currentRelationedListItem, relationDataField.getName());
                        } else {
                            currentRelatioinedDO = Class.forName(ProjectConstant.MODEL_PACKAGE + "." + ((RelationData) currentFieldRelationDataAnnotation).relationDOTypeName()).newInstance();
                        }
                        ((BaseDO) currentRelatioinedDO).setPk(UUID.randomUUID().toString());
                        ReflectUtil.setFieldValue(currentRelatioinedDO, javaMainFieldName, mainPk);
                        ReflectUtil.setFieldValue(currentRelatioinedDO, javaTargetFieldName, ((BaseDO) currentRelationedListItem).getPk());
                        ((BaseDO) currentRelatioinedDO).setCreated(new Date());
                        ((BaseDO) currentRelatioinedDO).setUpdated(new Date());
                        mapper.insert(currentRelatioinedDO);
                        ((BaseDO) currentRelatioinedDO).setPk(null);
                    }
                } else {
                    if (StringUtils.isNotBlank(((RelationData) currentFieldRelationDataAnnotation).foreignProperty())) {
                        javaTargetFieldName = ((RelationData) currentFieldRelationDataAnnotation).foreignProperty();
                        ReflectUtil.setFieldValue(model, javaTargetFieldName, ((BaseDO) currentFeildValue).getPk());
                    }
                    if (StringUtils.isNotBlank(((RelationData) currentFieldRelationDataAnnotation).mainProperty())) {
                        mapper = initMapperByDOSimpleName(((RelationData) currentFieldRelationDataAnnotation).baseDOTypeName());
                        Object targetObject = Class.forName(ProjectConstant.MODEL_PACKAGE + "." + ((RelationData) currentFieldRelationDataAnnotation).baseDOTypeName()).newInstance();
                        ReflectUtil.setFieldValue(targetObject, "pk", ReflectUtil.getFieldValue(currentFeildValue, "pk"));
                        ReflectUtil.setFieldValue(targetObject, ((RelationData) currentFieldRelationDataAnnotation).mainProperty(), mainPk);
                        ReflectUtil.setFieldValue(targetObject, "updated", new Date());
                        mapper.updateByPrimaryKeySelective(targetObject);
                    }
                }
            }
            mapper = initMainMapper();
            model.setUpdated(new Date());
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

    /**
     * 真实删除单条数据
     *
     * @param pk
     * @return
     */
    @Override
    public int delAutomaticByPk(String pk) {
        Annotation annotation = modelClass.getAnnotation(RelationData.class);
        if (annotation == null) {
            throw new RuntimeException("没有找到" + modelClass.getName() + "对应的DO");
        }
        mapper = initMapperByDOSimpleName(((RelationData) annotation).baseDOTypeName());
        return mapper.deleteByPrimaryKey(pk);
    }

    /**
     * 按条件删除
     *
     * @param condition
     * @return
     */
    @Override
    public int delByConditionAudomatic(String condition) {
        Annotation annotation = modelClass.getAnnotation(RelationData.class);
        if (annotation == null) {
            throw new RuntimeException("没有找到" + modelClass.getName() + "对应的DO");
        }
        Condition delCondition = getConditionBySimpleDOName(((RelationData) annotation).baseDOTypeName());
        delCondition.createCriteria().andCondition(getDBConditionString(condition));
        mapper = initMapperByDOSimpleName(((RelationData) annotation).baseDOTypeName());
        return mapper.deleteByCondition(delCondition);
    }

    /**
     * 加载一对一关系数据
     *
     * @param model 主实体对象
     * @param field 要取的属性
     * @return 增加了要取的属性的主实体对象
     */
    @Override
    public T loadOneToOneRelation(T model, Field field) {
        return loadOneToOneRelation(model, field, (Condition) null);
    }

    /**
     * 按条件加载一对一关系
     * 主供后台使用
     *
     * @param model           主实体对象
     * @param field           要加载的属性
     * @param conditionString 子属性的条件(小驼峰字符串形式)
     * @return 按条件加载的一对多关系之后的主实体对象
     */
    @Override
    public T loadOneToOneRelation(T model, Field field, String conditionString) {
        Condition condition = getConditionByFieldAndConditionString(field, conditionString);
        if (condition == null) {
            return model;
        }
        return loadOneToOneRelation(model, field, condition);
    }

    /**
     * 按条件加载一对一关系
     * 主供后台使用
     *
     * @param model     主实体对象
     * @param field     要加载的属性
     * @param condition 子属性的条件(小驼峰形式)
     * @return 按条件加载的一对多关系之后的主实体对象
     */
    @Override
    public T loadOneToOneRelation(T model, Field field, Condition condition) {
        Annotation fieldRelationDataAnnotation = field.getAnnotation(RelationData.class);
        mapper = initMapperByDOSimpleName(((RelationData) fieldRelationDataAnnotation).baseDOTypeName());
        Object result;
        if (StringUtils.isNotBlank(((RelationData) fieldRelationDataAnnotation).mainProperty())) {
            String javaMainFieldName = ((RelationData) fieldRelationDataAnnotation).mainProperty();
            if (condition == null) {
                condition = getConditionBySimpleDOName(((RelationData) fieldRelationDataAnnotation).baseDOTypeName());
                condition.createCriteria();
            }
            condition.and().andEqualTo(javaMainFieldName, model.getPk());
            result = mapper.selectByCondition(condition);
            if (((List) result).size() > 1) {
                throw new TooManyResultsException("查询出的结果过多:表:" + ((RelationData) fieldRelationDataAnnotation).baseDOTypeName() + ",字段:" + javaMainFieldName + ",值:" + model.getPk());
            }
            if (((List) result).size() > 0) {
                ReflectUtil.setFieldValue(model, field.getName(), ((List) result).get(0));
            } else {
                ReflectUtil.setFieldValue(model, field.getName(), null);
            }
        } else if (StringUtils.isNotBlank(((RelationData) fieldRelationDataAnnotation).foreignProperty())) {
            String javaMainFieldName = ((RelationData) fieldRelationDataAnnotation).foreignProperty();
            result = mapper.selectByPrimaryKey(ReflectUtil.getFieldValue(model, javaMainFieldName));
            result = ReflectUtil.cloneObj(result, field.getType());
            ReflectUtil.setFieldValue(model, field.getName(), result);
        }
        return model;
    }

    /**
     * 加载一对多关系
     *
     * @param model 主实体对象
     * @param field 要取的属性
     * @return 增加了要取的属性的主实体对象
     */
    @Override
    public T loadOneToManyRelation(T model, Field field) {
        return loadOneToManyRelation(model, field, (Condition) null);
    }

    /**
     * 按条件加载一对多关系数据
     * 主供后台使用
     *
     * @param model           主实体对象
     * @param field           需要加载的属性
     * @param conditionString 针对子实体的条件(小驼峰字符串形式)
     * @return 增加了子实体列表的主实体对象
     */
    @Override
    public T loadOneToManyRelation(T model, Field field, String conditionString) {
        Condition condition = getConditionByFieldAndConditionString(field, conditionString);
        if (condition == null) {
            return model;
        }
        return loadOneToManyRelation(model, field, condition);
    }

    /**
     * 按条件加载一对多关系数据
     * 主供后台使用
     *
     * @param model     主实体对象
     * @param field     需要加载的属性
     * @param condition 针对子实体的条件
     * @return 增加了子实体列表的主实体对象
     */
    @Override
    public T loadOneToManyRelation(T model, Field field, Condition condition) {
        Annotation fieldAnnotation = field.getAnnotation(RelationData.class);
        String fieldBaseDOTypeName = ((RelationData) fieldAnnotation).baseDOTypeName();
        mapper = initMapperByDOSimpleName(fieldBaseDOTypeName);
        Class realSlaveDOClass = getClassByDOSimpleName(fieldBaseDOTypeName);
        String javaMainFieldName = ((RelationData) fieldAnnotation).mainProperty();
        if (condition == null) {
            condition = new Condition(realSlaveDOClass);
            condition.createCriteria();
        }
        condition.and().andEqualTo(javaMainFieldName, model.getPk());
        Object result = mapper.selectByCondition(condition);
        ReflectUtil.setFieldValue(model, field.getName(), result);
        return model;
    }

    /**
     * 加载多对多关系
     *
     * @param model 主实体对象
     * @param field 要加载的属性
     * @return 增加了要加载的属性的主实体对象
     */
    @Override
    public T loadManyToManyRelation(T model, Field field) {
        return loadManyToManyRelation(model, field, (Condition) null);
    }

    /**
     * 按条件加载多对多关系
     * 主供后台使用
     *
     * @param model           主实体对象
     * @param field           要加载的属性
     * @param conditionString 子属性的条件(小驼峰字符串形式)
     * @return 按条件加载的多对多关系之后的主实体对象
     */
    @Override
    public T loadManyToManyRelation(T model, Field field, String conditionString) {
        Condition condition = getConditionByFieldAndConditionString(field, conditionString);
        if (condition == null) {
            return model;
        }
        return loadManyToManyRelation(model, field, condition);
    }

    /**
     * 按条件加载多对多关系
     * 主供后台使用
     *
     * @param model     主实体对象
     * @param field     要加载的属性
     * @param condition 子属性的条件(小驼峰形式)
     * @return 按条件加载的多对多关系之后的主实体对象
     */
    @Override
    public T loadManyToManyRelation(T model, Field field, Condition condition) {
        Annotation fieldRelationDataAnnotation = field.getDeclaredAnnotation(RelationData.class);
        mapper = initMapperByDOSimpleName(((RelationData) fieldRelationDataAnnotation).relationDOTypeName());
        String javaMainFieldName = ((RelationData) fieldRelationDataAnnotation).mainProperty();
        Object result = null;
        Condition relationCondition = getConditionBySimpleDOName(((RelationData) fieldRelationDataAnnotation).relationDOTypeName());
        relationCondition.createCriteria().andEqualTo(javaMainFieldName, model.getPk());
        Object relationResults = mapper.selectByCondition(relationCondition);
        if (((List) relationResults).size() > 0) {
            List<String> targetPkList = new ArrayList<>();
            String javaTargetFieldName = ((RelationData) fieldRelationDataAnnotation).foreignProperty();
            for (Object target : (List) relationResults) {
                targetPkList.add(ReflectUtil.getFieldValue(target, javaTargetFieldName).toString());
            }
            if (condition == null) {
                condition = getConditionBySimpleDOName(((RelationData) fieldRelationDataAnnotation).baseDOTypeName());
                condition.createCriteria();
            }
            condition.and().andIn("pk", targetPkList);
            mapper = initMapperByDOSimpleName(((RelationData) fieldRelationDataAnnotation).baseDOTypeName());
            Class realSlaveDOClass = null;
            try {
                realSlaveDOClass = Class.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            result = mapper.selectByCondition(condition);
            Field relationDataField = ReflectUtil.getRelationDataField(realSlaveDOClass, ((RelationData) fieldRelationDataAnnotation).relationDOTypeName());
            if (relationDataField != null) {
                result = fillRelationData(field, relationDataField, realSlaveDOClass, (List) relationResults, (List) result);
            }
        }
        ReflectUtil.setFieldValue(model, field.getName(), result);
        return model;
    }

    @Override
    public SortedMap getSchema() {
        SortedMap<String, Object> mainMap = new TreeMap<>();
        Annotation ann = modelClass.getAnnotation(ApiModel.class);
        if (ann == null) {
            return mainMap;
        }
        mainMap.put(modelClass.getSimpleName(), genExample(modelClass, true));
        return mainMap;
    }

    private List genListExample(Class cls) {
        List<Object> list = new ArrayList<>();
        list.add(genExample(cls, false));
        return list;
    }

    private Map genExample(Class cls, boolean isMainType) {
        Annotation ann;
        Annotation relationAnn;
        Map<String, Object> map = new HashMap<>();
        StringBuilder description;
        if (cls == List.class) {
            return null;
        }
        List<Field> fieldList = ReflectUtil.getFieldList(cls);
        for (Field field : fieldList) {
            description = new StringBuilder();
            ann = field.getAnnotation(ApiModelProperty.class);
            relationAnn = field.getAnnotation(RelationData.class);
            if (relationAnn != null) {
                if (isMainType && ((RelationData) relationAnn).isRelation()) {
                    continue;
                }
            }
            if (ReflectUtil.isWrapType(field)) {
                if (ann != null) {
                    description.append("(" + field.getType().getSimpleName() + ")" + ((ApiModelProperty) ann).value());
                    map.put(field.getName(), description.toString());
                }
            } else if (relationAnn != null) {
                if (((RelationData) relationAnn).isRelation() || isMainType) {
                    if (field.getType() == List.class) {
                        map.put(field.getName(), genListExample((Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]));
                    } else {
                        map.put(field.getName(), genExample(field.getType(), false));
                    }
                }
            }
        }
        return map;
    }

    /**
     * 根据DO短名称取对应的类型
     *
     * @param simpleNameOfDO DO短名称
     * @return DO对应的类型
     */
    private Class getClassByDOSimpleName(String simpleNameOfDO) {
        try {
            return Class.forName(ProjectConstant.MODEL_PACKAGE + "." + simpleNameOfDO);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 按DO的名称创建Condition条件对象
     *
     * @param simpleNameOfDO DO类短名称
     * @return 对应此DO的条件对象
     */
    private Condition getConditionBySimpleDOName(String simpleNameOfDO) {
        Condition condition = null;
        try {
            condition = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + simpleNameOfDO));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return condition;
    }

    private Condition getConditionByFieldAndConditionString(Field field, String conditionString) {
        Annotation annotation = field.getAnnotation(RelationData.class);
        if (annotation == null) {
            return null;
        }
        Condition condition = getConditionBySimpleDOName(((RelationData) annotation).baseDOTypeName());
        condition.createCriteria();
        if (StringUtils.isNotBlank(conditionString)) {
            condition.and().andCondition(conditionString);
        }
        return condition;
    }

    /**
     * 将小驼峰条件转化为数据库格式条件
     * 此方法不影响单引号以内的内容
     *
     * @param condition 小驼峰的条件
     * @return 数据库中的下划线条件
     */
    private String getDBConditionString(String condition) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, condition);
    }

    /**
     * 初始化本类的mapper对象
     */
    private Mapper initMainMapper() {
        Annotation annotation = modelClass.getAnnotation(RelationData.class);
        if (annotation == null) {
            return null;
        }
        return initMapperByDOSimpleName(((RelationData) annotation).baseDOTypeName());
    }

    /**
     * 根据DO的短名称取对应的mapper
     *
     * @param simpleNameOfDO DO短名称
     * @return 对应的单表mapper对象
     */
    private Mapper initMapperByDOSimpleName(String simpleNameOfDO) {
        Mapper ret = null;
        try {
            ret = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + simpleNameOfDO + "Mapper"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 灌入关系描述数据
     *
     * @param field             主实体中的关系数据属性
     * @param relationDataField 子实体中存储关系描述数据的属性
     * @param realSlaveDOClass  真实的关系实体类型(包含Map<String,Object>的真正的Model类
     * @param relationResults   数据库中查询出来的关系表数据结果
     * @param resultFromDB      数据库中查询出来的真正的子实体列表
     * @return
     */
    private Object fillRelationData(Field field, Field relationDataField, Class realSlaveDOClass, List relationResults, List resultFromDB) {
        List<Object> resultListContainsRelation = new ArrayList<>();
        Annotation fieldRelationDataAnnotation = field.getAnnotation(RelationData.class);
        String javaTargetFieldName = ((RelationData) fieldRelationDataAnnotation).foreignProperty();

        for (Object relationResult : relationResults) {
            for (Object sourceResult : resultFromDB) {
                if (ReflectUtil.getFieldValue(sourceResult, "pk").equals(ReflectUtil.getFieldValue(relationResult, javaTargetFieldName))) {
                    Object relationTarget = ReflectUtil.cloneObj(sourceResult, realSlaveDOClass);
                    ReflectUtil.setFieldValue(relationResult, "pk", null);
                    ReflectUtil.setFieldValue(relationTarget, relationDataField.getName(), relationResult);
                    resultListContainsRelation.add(relationTarget);
                }
            }
        }
        return resultListContainsRelation;
    }

}
