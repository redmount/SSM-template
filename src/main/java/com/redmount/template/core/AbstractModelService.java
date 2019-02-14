package com.redmount.template.core;

import com.google.common.base.CaseFormat;
import com.redmount.template.core.annotation.RelationData;
import com.redmount.template.core.annotation.Tombstoned;
import com.redmount.template.core.annotation.Validate;
import com.redmount.template.core.exception.ServiceException;
import com.redmount.template.core.exception.SysServiceExceptionDO;
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
        if (modelClass.isAnnotationPresent(RelationData.class)) {
            modelClassDOSimpleName = modelClass.getAnnotation(RelationData.class).baseDOTypeName();
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
    public List listAutomatic(String keywords, String condition, String relations, String orderBy) {
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
            if (modelClass.isAnnotationPresent(Tombstoned.class)) {
                example.and().andNotEqualTo("deleted", true).orIsNull("deleted");
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
        validateModelToSave(model);
        String mainPk = model.getPk();
        Annotation mainClassRelationDataAnnotation = modelClass.getAnnotation(RelationData.class);
        if (mainClassRelationDataAnnotation != null) {
            modelClassDOSimpleName = ((RelationData) mainClassRelationDataAnnotation).baseDOTypeName();
        }
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
            Annotation currentFieldRelationDataAnnotation = currentField.getAnnotation(RelationData.class);
            if (currentFieldRelationDataAnnotation == null) {
                continue;
            }
            if (((RelationData) currentFieldRelationDataAnnotation).isOneToMany()) {
                saveOneToManyRelation(model, currentField);
            } else if (((RelationData) currentFieldRelationDataAnnotation).isManyToMany()) {
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
     * @param condition
     * @return
     */
    @Override
    public int delByConditionAudomatic(String condition) {
        Annotation annotation = modelClass.getAnnotation(RelationData.class);
        if (annotation == null) {
            throw new RuntimeException("没有找到" + modelClass.getName() + "对应的DO");
        }
        Condition delCondition = initConditionBySimpleDOName(((RelationData) annotation).baseDOTypeName());
        delCondition.createCriteria().andCondition(getDBConditionString(condition));
        if (modelClass.isAnnotationPresent(Tombstoned.class)) {
            BaseDOTombstoned example = new BaseDOTombstoned();
            example.setDeleted(true);
            return mapper.updateByConditionSelective(example, delCondition);
        }
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
                condition = initConditionBySimpleDOName(((RelationData) fieldRelationDataAnnotation).baseDOTypeName());
                condition.createCriteria();
            }
            if (field.getType().isAnnotationPresent(Tombstoned.class)) {
                condition.and().andNotEqualTo("deleted", true).orIsNull("deleted");
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
            String pk = (String) ReflectUtil.getFieldValue(model, javaMainFieldName);
            if (pk == null) {
                pk = "";
            }
            if (condition == null) {
                condition = initConditionBySimpleDOName(((RelationData) fieldRelationDataAnnotation).baseDOTypeName());
                condition.createCriteria();
            }
            if (field.getType().isAnnotationPresent(Tombstoned.class)) {
                condition.and().andNotEqualTo("deleted", true).orIsNull("deleted");
            }
            condition.and().andEqualTo("pk", pk);
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
        if (field.getType().isAnnotationPresent(Tombstoned.class)) {
            condition.and().andNotEqualTo("deleted", true).orIsNull("deleted");
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
        Annotation currentFieldRelationDataAnnotation = field.getAnnotation(RelationData.class);
        Object currentFeildValue = ReflectUtil.getFieldValue(model, field.getName());
        if (StringUtils.isNotBlank(((RelationData) currentFieldRelationDataAnnotation).foreignProperty())) {
            String javaTargetFieldName = ((RelationData) currentFieldRelationDataAnnotation).foreignProperty();
            ReflectUtil.setFieldValue(model, javaTargetFieldName, ((BaseDO) currentFeildValue).getPk());
        }
        if (StringUtils.isNotBlank(((RelationData) currentFieldRelationDataAnnotation).mainProperty())) {
            mapper = initMapperByDOSimpleName(((RelationData) currentFieldRelationDataAnnotation).baseDOTypeName());
            BaseDO targetObject = null;
            try {
                targetObject = (BaseDO) Class.forName(ProjectConstant.MODEL_PACKAGE + "." + ((RelationData) currentFieldRelationDataAnnotation).baseDOTypeName()).newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            // ReflectUtil.setFieldValue(targetObject, "pk", ReflectUtil.getFieldValue(currentFeildValue, "pk"));
            targetObject.setPk((String) ReflectUtil.getFieldValue(currentFeildValue, "pk"));
            ReflectUtil.setFieldValue(targetObject, ((RelationData) currentFieldRelationDataAnnotation).mainProperty(), model.getPk());
            // ReflectUtil.setFieldValue(targetObject, "updated", new Date());
            targetObject.setUpdated(new Date());
            mapper.updateByPrimaryKeySelective(targetObject);
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
        List currentFeildValue = (List) ReflectUtil.getFieldValue(model, field.getName());
        Annotation currentFieldRelationDataAnnotation = field.getAnnotation(RelationData.class);
        String javaMainFieldName = ((RelationData) currentFieldRelationDataAnnotation).mainProperty();
        mapper = initMapperByDOSimpleName(((RelationData) currentFieldRelationDataAnnotation).baseDOTypeName());
        Condition condition = initConditionBySimpleDOName(((RelationData) currentFieldRelationDataAnnotation).baseDOTypeName());
        condition.createCriteria().andEqualTo(javaMainFieldName, model.getPk());
        BaseDO childDOWithoutMainPk = null;
        try {
            childDOWithoutMainPk = (BaseDO) getClassByDOSimpleName(((RelationData) currentFieldRelationDataAnnotation).baseDOTypeName()).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        ReflectUtil.setFieldValue(childDOWithoutMainPk, javaMainFieldName, "");
        mapper.updateByConditionSelective(childDOWithoutMainPk, condition);
        for (Object currentItem : currentFeildValue) {
            if (!StringUtils.isBlank(((BaseDO) currentItem).getPk())) {
                ReflectUtil.setFieldValue(currentItem, javaMainFieldName, model.getPk());
                // ReflectUtil.setFieldValue(currentItem, "updated", new Date());
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
        Annotation currentFieldRelationDataAnnotation = field.getAnnotation(RelationData.class);
        mapper = initMapperByDOSimpleName(((RelationData) currentFieldRelationDataAnnotation).relationDOTypeName());
        String realFieldClassFullName = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName();
        String realFieldClassShortNameWithoutModel = null;
        try {
            realFieldClassShortNameWithoutModel = Class.forName(realFieldClassFullName).getAnnotation(RelationData.class).baseDOTypeName();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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

        Condition condition = initConditionBySimpleDOName(field.getAnnotation(RelationData.class).relationDOTypeName());
        condition.createCriteria().andEqualTo(javaMainFieldName, model.getPk());
        mapper.deleteByCondition(condition);
        Field relationDataField = null;
        try {
            relationDataField = ReflectUtil.getRelationDataField(Class.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName()), ((RelationData) currentFieldRelationDataAnnotation).relationDOTypeName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        List currentFeildValue = (List) ReflectUtil.getFieldValue(model, field.getName());
        Object currentRelatioinedDO = new BaseDO();
        for (Object currentRelationedListItem : (List) currentFeildValue) {
            if (relationDataField != null) {
                currentRelatioinedDO = ReflectUtil.getFieldValue(currentRelationedListItem, relationDataField.getName());
            } else {
                try {
                    currentRelatioinedDO = Class.forName(ProjectConstant.MODEL_PACKAGE + "." + ((RelationData) currentFieldRelationDataAnnotation).relationDOTypeName()).newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
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
        Annotation fieldRelationDataAnnotation = field.getDeclaredAnnotation(RelationData.class);
        mapper = initMapperByDOSimpleName(((RelationData) fieldRelationDataAnnotation).relationDOTypeName());
        String javaMainFieldName = ((RelationData) fieldRelationDataAnnotation).mainProperty();
        Object result = null;
        Condition relationCondition = initConditionBySimpleDOName(((RelationData) fieldRelationDataAnnotation).relationDOTypeName());
        relationCondition.createCriteria().andEqualTo(javaMainFieldName, model.getPk());
        Object relationResults = mapper.selectByCondition(relationCondition);
        if (((List) relationResults).size() > 0) {
            List<String> targetPkList = new ArrayList<>();
            String javaTargetFieldName = ((RelationData) fieldRelationDataAnnotation).foreignProperty();
            for (Object target : (List) relationResults) {
                targetPkList.add(ReflectUtil.getFieldValue(target, javaTargetFieldName).toString());
            }
            if (condition == null) {
                condition = initConditionBySimpleDOName(((RelationData) fieldRelationDataAnnotation).baseDOTypeName());
                condition.createCriteria();
            }
            condition.and().andIn("pk", targetPkList);
            Class realSlaveDOClass = null;
            try {
                realSlaveDOClass = Class.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (realSlaveDOClass.isAnnotationPresent(Tombstoned.class)) {
                condition.and().andNotEqualTo("deleted", true).orIsNull("deleted");
            }
            mapper = initMapperByDOSimpleName(((RelationData) fieldRelationDataAnnotation).baseDOTypeName());


            result = mapper.selectByCondition(condition);
            Field relationDataField = ReflectUtil.getRelationDataField(realSlaveDOClass, ((RelationData) fieldRelationDataAnnotation).relationDOTypeName());
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
        Annotation ann = modelClass.getAnnotation(ApiModel.class);
        if (ann != null) {
            mainMap.put(modelClass.getSimpleName() + " (" + ((ApiModel) ann).value() + ")", genExample(modelClass, true));
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
                    if (field.getName().endsWith("Pk") || field.getName().equals("created") || field.getName().equals("updated")) {
                        continue;
                    }
                    if (relationAnn != null && ((RelationData) relationAnn).isRelation() && field.getName().equals("pk")) {
                        continue;
                    }
                    description.append(field.getType().getSimpleName());
                    map.put(field.getName() + " (" + ((ApiModelProperty) ann).value() + ")", description.toString());
                }
            } else if (relationAnn != null) {
                if (((RelationData) relationAnn).isRelation() || isMainType) {
                    if (field.getType() == List.class) {
                        if (ann != null) {
                            map.put(field.getName() + " (" + ((ApiModelProperty) ann).value() + ")", genListExample((Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]));
                        } else {
                            map.put(field.getName(), genListExample((Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]));
                        }
                    } else {
                        if (ann != null) {
                            map.put(field.getName() + " (" + ((ApiModelProperty) ann).value() + ")", genExample(field.getType(), false));
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
        Annotation annotation = field.getAnnotation(RelationData.class);
        if (annotation == null) {
            return null;
        }
        Condition condition = initConditionBySimpleDOName(((RelationData) annotation).baseDOTypeName());
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

    /**
     * 验证Model
     *
     * @param model
     */
    private void validateModelToSave(T model) {
        Class cls = getClassByDOSimpleName(modelClassDOSimpleName);
        for (Field field : cls.getDeclaredFields()) {
            if (field.isAnnotationPresent(Validate.class)) {
                if (field.getType() == String.class) {
                    validateStringField(model, field);
                }
            }
        }
    }

    /**
     * 验证字符串长度
     * @param model
     * @param field
     */
    private void validateStringField(T model, Field field) {
        Annotation validateAnnotation = field.getAnnotation(Validate.class);
        String value = (String) ReflectUtil.getFieldValue(model, field.getName());
        if (value.length() > ((Validate) validateAnnotation).stringMaxLength()) {
            SysServiceExceptionDO exceptionDO = new SysServiceExceptionDO();
            exceptionDO.setMessage("信息长度超过限制：" + ((Validate) validateAnnotation).stringMaxLength());
            exceptionDO.setCode(500);
            exceptionDO.setTitle("提交信息失败");
            exceptionDO.setReason(exceptionDO.getMessage());
            throw new ServiceException(exceptionDO);
        }
    }
}
