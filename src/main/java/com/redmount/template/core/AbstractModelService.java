package com.redmount.template.core;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.CaseFormat;
import com.redmount.template.core.annotation.RelationData;
import com.redmount.template.core.annotation.Tombstoned;
import com.redmount.template.core.annotation.Validate;
import com.redmount.template.core.exception.ServiceException;
import com.redmount.template.system.model.SysServiceException;
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
    private RelationData modelAnnotation;
    private Class modelBaseDOClass;

    public AbstractModelService() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        modelClass = (Class<T>) pt.getActualTypeArguments()[0];
        if (modelClass.isAnnotationPresent(RelationData.class)) {
            modelBaseDOClass = modelClass.getAnnotation(RelationData.class).baseDOClass();
            modelAnnotation = modelClass.getAnnotation(RelationData.class);
        } else {
            throw new RuntimeException(modelClass.getName() + "没有RelationData注解,不能使用ModeService");
        }
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
        return getAutomaticWithModel(model, relations);
    }

    /**
     * 给单个实体挂关系
     *
     * @param model     原始实体
     * @param relations 关系数据
     * @return 已经挂上关系的实体模型
     */
    @Override
    public T getAutomaticWithModel(T model, String relations) {
        List<String> relationList = ReflectUtil.getFieldList(modelClass, relations);
        Field field;
        for (String relation : relationList) {
            try {
                field = modelClass.getDeclaredField(relation);
                RelationData fieldRelationDataAnnotation = field.getDeclaredAnnotation(RelationData.class);
                if (fieldRelationDataAnnotation == null) {
                    continue;
                }
                if (fieldRelationDataAnnotation.isOneToMany()) {
                    model = loadOneToManyRelation(model, field);
                } else if (fieldRelationDataAnnotation.isManyToMany()) {
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
     * 取带关系的列表
     *
     * @param pageInfo  带分页信息的主表列表
     * @param relations 关系数据
     * @return 带关系的实体列表
     */
    @Override
    public PageInfo listAutomaticWithRelations(PageInfo pageInfo, String relations) {
        List<Object> resultWithRelations = new ArrayList<>();
        List<T> list = pageInfo.getList();
        for (T item : list) {
            resultWithRelations.add(getAutomaticWithModel(item, relations));
        }
        pageInfo.setList(resultWithRelations);
        return pageInfo;
    }

    /**
     * 取符合条件的实体列表(不带关系)
     *
     * @param keywords  关键字
     * @param relations 关系数据
     * @param orderBy   排序
     * @return 带排序和条件的数据实体列表
     */
    @Override
    public PageInfo listAutomaticWithoutRelations(String keywords, String condition, String relations, String orderBy, int page, int size) {
        List<T> retList = new ArrayList<>();
        List<Field> fields = ReflectUtil.getKeywordsFields(modelClass);
        List<Object> results;
        PageInfo pageInfo = null;
        try {
            mapper = (Mapper) sqlSession.getMapper(modelAnnotation.baseDOMapperClass());
            Example example = new Condition(modelAnnotation.baseDOClass());
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
            if (modelClass.isAnnotationPresent(Tombstoned.class)) {
                example.and().andNotEqualTo(ProjectConstant.TOMSTONED_FIELD, true).orIsNull(ProjectConstant.TOMSTONED_FIELD);
            }
            example.setOrderByClause(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, orderBy));
            PageHelper.startPage(page, size);
            results = mapper.selectByCondition(example);
            pageInfo = new PageInfo(results);
            for (Object result : results) {
                retList.add(ReflectUtil.cloneObj(result, modelClass));
            }
            pageInfo.setList(retList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageInfo;
    }

    /**
     * 自动保存
     *
     * @param model 需要保存的数据,目前的限制是只保存表现层中的两层,带关系数据的,保存关系数据.再往下就不管了.
     * @return 保存之后的结果
     */
    @Override
    public T saveAutomatic(T model, boolean forceSaveNull) {
        validateModelToSave(model);
        String mainPk = model.getPk();
        List<Field> relationFields;
        Object currentFeildValue;

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
            RelationData currentFieldRelationDataAnnotation = currentField.getAnnotation(RelationData.class);
            if (currentFieldRelationDataAnnotation == null) {
                continue;
            }
            if (currentFieldRelationDataAnnotation.isOneToMany()) {
                saveOneToManyRelation(model, currentField);
            } else if (currentFieldRelationDataAnnotation.isManyToMany()) {
                saveManyToManyRelation(model, currentField);
            } else {
                saveOneToOneRelation(model, currentField);
            }
        }
        mapper = initMainMapper();
        model.setUpdated(new Date());
        mapper.updateByPrimaryKeySelective(model);

        return model;
    }

    /**
     * 真实删除单条数据
     *
     * @param pk PK
     * @return 删除的条数
     */
    @Override
    public int delAutomaticByPk(String pk) {
        mapper = initMapperByMapperClass(modelAnnotation.baseDOMapperClass());
        if (modelClass.isAnnotationPresent(Tombstoned.class)) {
            BaseDOTombstoned example = new BaseDOTombstoned();
            example.setDeleted(true);
            example.setPk(pk);
            Object obj = ReflectUtil.cloneObj(example, modelClass);
            return mapper.updateByPrimaryKeySelective(obj);
        }
        return mapper.deleteByPrimaryKey(pk);
    }

    /**
     * 按条件删除
     *
     * @param condition 条件
     * @return 删除的条数
     */
    @Override
    public int delByConditionAudomatic(String condition) {
        RelationData annotation = modelClass.getAnnotation(RelationData.class);
        if (annotation == null) {
            throw new RuntimeException("没有找到" + modelClass.getName() + "对应的DO");
        }
        Condition delCondition = new Condition(annotation.baseDOMapperClass());
        delCondition.createCriteria().andCondition(getDBConditionString(condition));
        if (modelClass.isAnnotationPresent(Tombstoned.class)) {
            BaseDOTombstoned example = new BaseDOTombstoned();
            example.setDeleted(true);
            return mapper.updateByConditionSelective(example, delCondition);
        }
        mapper = initMapperByMapperClass(annotation.baseDOMapperClass());
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
        RelationData fieldRelationDataAnnotation = field.getAnnotation(RelationData.class);
        mapper = initMapperByMapperClass(fieldRelationDataAnnotation.baseDOMapperClass());
        Object result;
        if (StringUtils.isNotBlank(fieldRelationDataAnnotation.mainProperty())) {
            String javaMainFieldName = fieldRelationDataAnnotation.mainProperty();
            if (condition == null) {
                condition = new Condition(fieldRelationDataAnnotation.baseDOClass());
                condition.createCriteria();
            }
            if (field.getType().isAnnotationPresent(Tombstoned.class)) {
                condition.and().andNotEqualTo(ProjectConstant.TOMSTONED_FIELD, true).orIsNull(ProjectConstant.TOMSTONED_FIELD);
            }
            condition.and().andEqualTo(javaMainFieldName, model.getPk());
            result = mapper.selectByCondition(condition);
            if (((List) result).size() > 1) {
                throw new TooManyResultsException("查询出的结果过多:表:" + fieldRelationDataAnnotation.baseDOClass() + ",字段:" + javaMainFieldName + ",值:" + model.getPk());
            }
            if (((List) result).size() > 0) {
                ReflectUtil.setFieldValue(model, field.getName(), ((List) result).get(0));
            } else {
                ReflectUtil.setFieldValue(model, field.getName(), null);
            }
        } else if (StringUtils.isNotBlank(fieldRelationDataAnnotation.foreignProperty())) {
            String javaMainFieldName = fieldRelationDataAnnotation.foreignProperty();
            String pk = (String) ReflectUtil.getFieldValue(model, javaMainFieldName);
            if (pk == null) {
                pk = "";
            }
            if (condition == null) {
                condition = new Condition(fieldRelationDataAnnotation.baseDOClass());
                condition.createCriteria();
            }
            if (field.getType().isAnnotationPresent(Tombstoned.class)) {
                condition.and().andNotEqualTo(ProjectConstant.TOMSTONED_FIELD, true).orIsNull(ProjectConstant.TOMSTONED_FIELD);
            }
            condition.and().andEqualTo(ProjectConstant.PRIMARY_KEY_FIELD_NAME, pk);
            result = mapper.selectByCondition(condition);
            if (((List) result).size() > 0) {
                if (((List) result).size() > 1) {
                    throw new TooManyResultsException();
                }
                result = ((List) result).get(0);
                result = ReflectUtil.cloneObj(result, field.getType());
                ReflectUtil.setFieldValue(model, field.getName(), result);
            }

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
        RelationData fieldAnnotation = field.getAnnotation(RelationData.class);
        mapper = initMapperByMapperClass(fieldAnnotation.baseDOMapperClass());
        Class realSlaveDOClass = fieldAnnotation.baseDOClass();
        String javaMainFieldName = fieldAnnotation.mainProperty();
        if (condition == null) {
            condition = new Condition(realSlaveDOClass);
            condition.createCriteria();
        }
        condition.and().andEqualTo(javaMainFieldName, model.getPk());
        if (field.getType().isAnnotationPresent(Tombstoned.class)) {
            condition.and().andNotEqualTo(ProjectConstant.TOMSTONED_FIELD, true).orIsNull(ProjectConstant.TOMSTONED_FIELD);
        }
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
     * 保存实体中的一对一关系
     *
     * @param model 主实体
     * @param field 需要保存的字段
     * @return 保存后的主实体
     */
    @Override
    public T saveOneToOneRelation(T model, Field field) {
        RelationData currentFieldRelationDataAnnotation = field.getAnnotation(RelationData.class);
        Object currentFeildValue = ReflectUtil.getFieldValue(model, field.getName());
        if (StringUtils.isNotBlank(currentFieldRelationDataAnnotation.foreignProperty())) {
            String javaTargetFieldName = currentFieldRelationDataAnnotation.foreignProperty();
            assert currentFeildValue != null;
            ReflectUtil.setFieldValue(model, javaTargetFieldName, ((BaseDO) currentFeildValue).getPk());
        }
        if (StringUtils.isNotBlank(currentFieldRelationDataAnnotation.mainProperty())) {
            mapper = initMapperByMapperClass(currentFieldRelationDataAnnotation.baseDOMapperClass());
            BaseDO targetObject = null;
            try {
                targetObject = (BaseDO) currentFieldRelationDataAnnotation.baseDOClass().newInstance();
                targetObject = ReflectUtil.cloneObj(currentFeildValue, targetObject.getClass());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            assert targetObject != null;
            String pk = (String) ReflectUtil.getFieldValue(currentFeildValue, ProjectConstant.PRIMARY_KEY_FIELD_NAME);
            boolean isNewPk = false;
            if (StringUtils.isBlank(pk)) {
                pk = UUID.randomUUID().toString();
                isNewPk = true;
            }
            targetObject.setPk(pk);
            ReflectUtil.setFieldValue(targetObject, currentFieldRelationDataAnnotation.mainProperty(), model.getPk());
            ReflectUtil.setFieldValue(targetObject, "updated", new Date());
            targetObject.setUpdated(new Date());
            if (isNewPk) {
                targetObject.setCreated(new Date());
                mapper.insert(targetObject);
            } else {

                mapper.updateByPrimaryKeySelective(targetObject);
            }
        }
        return model;
    }

    /**
     * 保存实体中的一对多关系(子实体中记录了主实体的pk)
     *
     * @param model 主实体
     * @param field 需要保存的字段
     * @return 保存后的主实体
     */
    @Override
    public T saveOneToManyRelation(T model, Field field) {
        List currentFieldValue = (List) ReflectUtil.getFieldValue(model, field.getName());
        RelationData currentFieldRelationDataAnnotation = field.getAnnotation(RelationData.class);
        String javaMainFieldName = currentFieldRelationDataAnnotation.mainProperty();
        mapper = initMapperByMapperClass(currentFieldRelationDataAnnotation.baseDOMapperClass());
        Condition condition = new Condition(currentFieldRelationDataAnnotation.baseDOClass());
        condition.createCriteria().andEqualTo(javaMainFieldName, model.getPk());
        BaseDO childDOWithoutMainPk = null;
        try {
            childDOWithoutMainPk = (BaseDO) currentFieldRelationDataAnnotation.baseDOClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        ReflectUtil.setFieldValue(childDOWithoutMainPk, javaMainFieldName, "");
        mapper.updateByConditionSelective(childDOWithoutMainPk, condition);
        assert currentFieldValue != null;
        for (Object currentItem : currentFieldValue) {
            if (!StringUtils.isBlank(((BaseDO) currentItem).getPk())) {
                ReflectUtil.setFieldValue(currentItem, javaMainFieldName, model.getPk());
                ((BaseDO) currentItem).setUpdated(new Date());
                mapper.updateByPrimaryKeySelective(currentItem);
            } else {
                System.out.println("子表没有pk");
            }
        }
        return model;
    }

    /**
     * 保存实体中的多对多关系(通过中间表连接的,并且关联数据记录在中间表的)
     *
     * @param model 主实体
     * @param field 需要保存的字段
     * @return 保存后的主实体
     */
    @Override
    public T saveManyToManyRelation(T model, Field field) {
        String javaMainFieldName, javaTargetFieldName;
        RelationData currentFieldRelationDataAnnotation = field.getAnnotation(RelationData.class);
        mapper = initMapperByMapperClass(currentFieldRelationDataAnnotation.relationDOMapperClass());
        javaMainFieldName = currentFieldRelationDataAnnotation.mainProperty();
        javaTargetFieldName = currentFieldRelationDataAnnotation.foreignProperty();

        // Condition condition = initConditionBySimpleDOName(field.getAnnotation(RelationData.class).relationDOTypeName());
        Condition condition = new Condition(field.getAnnotation(RelationData.class).relationDOClass());
        condition.createCriteria().andEqualTo(javaMainFieldName, model.getPk());
        mapper.deleteByCondition(condition);
        Field relationDataField = null;
        try {
            relationDataField = ReflectUtil.getRelationDataField(Class.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName()), currentFieldRelationDataAnnotation.relationDOClass());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        List currentFeildValue = (List) ReflectUtil.getFieldValue(model, field.getName());
        Object currentRelatioinedDO = new BaseDO();
        assert currentFeildValue != null;
        for (Object currentRelationedListItem : currentFeildValue) {
            if (relationDataField != null) {
                currentRelatioinedDO = ReflectUtil.getFieldValue(currentRelationedListItem, relationDataField.getName());
            } else {
                try {
                    currentRelatioinedDO = currentFieldRelationDataAnnotation.relationDOClass().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            assert currentRelatioinedDO != null;
            ((BaseDO) currentRelatioinedDO).setPk(UUID.randomUUID().toString());
            ReflectUtil.setFieldValue(currentRelatioinedDO, javaMainFieldName, model.getPk());
            ReflectUtil.setFieldValue(currentRelatioinedDO, javaTargetFieldName, ((BaseDO) currentRelationedListItem).getPk());
            ((BaseDO) currentRelatioinedDO).setCreated(new Date());
            ((BaseDO) currentRelatioinedDO).setUpdated(new Date());
            mapper.insert(currentRelatioinedDO);
            ((BaseDO) currentRelatioinedDO).setPk(null);
        }
        return model;
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
        RelationData fieldRelationDataAnnotation = field.getDeclaredAnnotation(RelationData.class);
        mapper = initMapperByMapperClass(fieldRelationDataAnnotation.relationDOMapperClass());
        String javaMainFieldName = fieldRelationDataAnnotation.mainProperty();
        Object result = null;
        // Condition relationCondition = initConditionBySimpleDOName(fieldRelationDataAnnotation.relationDOTypeName());
        Condition relationCondition = new Condition(fieldRelationDataAnnotation.relationDOClass());
        relationCondition.createCriteria().andEqualTo(javaMainFieldName, model.getPk());
        Object relationResults = mapper.selectByCondition(relationCondition);
        if (((List) relationResults).size() > 0) {
            List<String> targetPkList = new ArrayList<>();
            String javaTargetFieldName = fieldRelationDataAnnotation.foreignProperty();
            for (Object target : (List) relationResults) {
                targetPkList.add(Objects.requireNonNull(ReflectUtil.getFieldValue(target, javaTargetFieldName)).toString());
            }
            if (condition == null) {
                condition = new Condition(fieldRelationDataAnnotation.baseDOClass());
                condition.createCriteria();
            }
            condition.and().andIn(ProjectConstant.PRIMARY_KEY_FIELD_NAME, targetPkList);
            Class realSlaveDOClass = null;
            try {
                realSlaveDOClass = Class.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            assert realSlaveDOClass != null;
            if (realSlaveDOClass.isAnnotationPresent(Tombstoned.class)) {
                condition.and().andNotEqualTo(ProjectConstant.TOMSTONED_FIELD, true).orIsNull(ProjectConstant.TOMSTONED_FIELD);
            }
            mapper = initMapperByMapperClass(fieldRelationDataAnnotation.baseDOMapperClass());

            result = mapper.selectByCondition(condition);
            Field relationDataField = ReflectUtil.getRelationDataField(realSlaveDOClass, fieldRelationDataAnnotation.relationDOClass());
            if (relationDataField != null) {
                result = fillRelationData(field, relationDataField, realSlaveDOClass, (List) relationResults, (List) result);
            }
        }
        ReflectUtil.setFieldValue(model, field.getName(), result);
        return model;
    }

    /**
     * 取实体注释模型
     *
     * @return 带注释的实体模型
     */
    @Override
    public SortedMap getSchema() {
        SortedMap<String, Object> mainMap = new TreeMap<>();
        ApiModel ann = modelClass.getAnnotation(ApiModel.class);
        if (ann != null) {
            mainMap.put(modelClass.getSimpleName() + " (" + ann.value() + ")", genExample(modelClass, true));
        } else {
            mainMap.put(modelClass.getSimpleName(), genExample(modelClass, true));
        }

        return mainMap;
    }

    /**
     * 生成List类型的实体注释实例
     *
     * @param cls 主类型
     * @return 包含一个实体注释实例的List
     */
    private List genListExample(Class cls) {
        List<Object> list = new ArrayList<>();
        list.add(genExample(cls, false));
        return list;
    }

    /**
     * 创建实体的注释实例
     *
     * @param cls        实体类型
     * @param isMainType 是否是主实体.如果是主实体,则不产生关系数据的数据模型
     * @return 带注释的实体模型Map
     */
    private Map genExample(Class cls, boolean isMainType) {
        ApiModelProperty ann;
        RelationData relationAnn;
        Map<String, Object> map = new HashMap<>();
        StringBuilder description;
        if (cls == List.class) {
            return null;
        }
        List<Field> fieldList = ReflectUtil.getFieldList(cls);
        assert fieldList != null;
        for (Field field : fieldList) {
            description = new StringBuilder();
            ann = field.getAnnotation(ApiModelProperty.class);
            relationAnn = field.getAnnotation(RelationData.class);
            if (relationAnn != null) {
                if (isMainType && relationAnn.isRelation()) {
                    continue;
                }
            }
            if (ReflectUtil.isWrapType(field)) {
                if (ann != null) {
                    if (field.getName().endsWith(ProjectConstant.PRIMARY_KEY_FIELD_NAME) || field.getName().equals("created") || field.getName().equals("updated")) {
                        continue;
                    }
                    if (relationAnn != null && relationAnn.isRelation() && field.getName().equals(ProjectConstant.PRIMARY_KEY_FIELD_NAME)) {
                        continue;
                    }
                    description.append(field.getType().getSimpleName());
                    map.put(field.getName() + " (" + ann.value() + ")", description.toString());
                }
            } else if (relationAnn != null) {
                if (relationAnn.isRelation() || isMainType) {
                    if (field.getType() == List.class) {
                        if (ann != null) {
                            map.put(field.getName() + " (" + ann.value() + ")", genListExample((Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]));
                        } else {
                            map.put(field.getName(), genListExample((Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]));
                        }
                    } else {
                        if (ann != null) {
                            map.put(field.getName() + " (" + ann.value() + ")", genExample(field.getType(), false));
                        } else {
                            map.put(field.getName(), genExample(field.getType(), false));
                        }
                    }
                }
            }
        }
        return map;
    }

    /**
     * 按DO的名称创建Condition条件对象
     *
     * @param simpleNameOfDO DO类短名称
     * @return 对应此DO的条件对象
     */
    private Condition initConditionBySimpleDOName(String simpleNameOfDO) {
        Condition condition = null;
        try {
            condition = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + simpleNameOfDO));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return condition;
    }

    private Condition getConditionByFieldAndConditionString(Field field, String conditionString) {
        RelationData annotation = field.getAnnotation(RelationData.class);
        if (annotation == null) {
            return null;
        }
        Condition condition = new Condition(annotation.baseDOClass());
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
        return initMapperByMapperClass(modelAnnotation.baseDOMapperClass());
    }

    /**
     * 根据Mapper类名取对应的mapper
     *
     * @param mapperClass Mapper类名
     * @return 对应的单表mapper对象
     */
    private Mapper initMapperByMapperClass(Class mapperClass) {
        return (Mapper) sqlSession.getMapper(mapperClass);
    }

    /**
     * 灌入关系描述数据
     *
     * @param field             主实体中的关系数据属性
     * @param relationDataField 子实体中存储关系描述数据的属性
     * @param realSlaveDOClass  真实的关系实体类型(包含Map<String,Object>的真正的Model类
     * @param relationResults   数据库中查询出来的关系表数据结果
     * @param resultFromDB      数据库中查询出来的真正的子实体列表
     * @return 已经灌完关系数据的实体
     */
    private Object fillRelationData(Field field, Field relationDataField, Class realSlaveDOClass, List relationResults, List resultFromDB) {
        List<Object> resultListContainsRelation = new ArrayList<>();
        RelationData fieldRelationDataAnnotation = field.getAnnotation(RelationData.class);
        String javaTargetFieldName = fieldRelationDataAnnotation.foreignProperty();

        for (Object relationResult : relationResults) {
            for (Object sourceResult : resultFromDB) {
                if (Objects.requireNonNull(ReflectUtil.getFieldValue(sourceResult, ProjectConstant.PRIMARY_KEY_FIELD_NAME)).equals(ReflectUtil.getFieldValue(relationResult, javaTargetFieldName))) {
                    Object relationTarget = ReflectUtil.cloneObj(sourceResult, realSlaveDOClass);
                    ReflectUtil.setFieldValue(relationResult, ProjectConstant.PRIMARY_KEY_FIELD_NAME, null);
                    ReflectUtil.setFieldValue(relationTarget, relationDataField.getName(), relationResult);
                    resultListContainsRelation.add(relationTarget);
                }
            }
        }
        return resultListContainsRelation;
    }

    /**
     * 验证Model
     *
     * @param model 输入的实体
     */
    private void validateModelToSave(T model) {
        for (Field field : modelBaseDOClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Validate.class)) {
                if (field.getType() == String.class) {
                    validateStringField(model, field);
                }
            }
        }
    }

    /**
     * 验证字符串长度
     *
     * @param model 输入的实体
     * @param field 要判断的属性
     */
    private void validateStringField(T model, Field field) {
        Validate validateAnnotation = field.getAnnotation(Validate.class);
        String value = (String) ReflectUtil.getFieldValue(model, field.getName());
        assert value != null;
        if (validateAnnotation.stringMaxLength() < value.length()) {
            SysServiceException exceptionDO = new SysServiceException();
            exceptionDO.setMessage("信息长度超过限制：" + validateAnnotation.stringMaxLength());
            exceptionDO.setCode(500);
            exceptionDO.setTitle("提交信息失败");
            exceptionDO.setReason(exceptionDO.getMessage());
            throw new ServiceException(exceptionDO);
        }
    }
}
