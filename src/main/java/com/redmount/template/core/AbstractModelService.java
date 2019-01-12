package com.redmount.template.core;

import com.google.common.base.CaseFormat;
import com.redmount.template.core.annotation.RelationData;
import com.redmount.template.util.NameUtil;
import com.redmount.template.util.ReflectUtil;
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

    private String modelClassShortName;

    public AbstractModelService() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        modelClass = (Class<T>) pt.getActualTypeArguments()[0];
        modelClassShortName = modelClass.getAnnotation(RelationData.class).baseDOTypeName();
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
        String mainPk = model.getPk();
        List<String> relationList = ReflectUtil.getFieldList(modelClass, relations);
        Field field;
        String fieldBaseDOTypeName;
        Object result;
        String javaTargetFieldName;
        String javaMainFieldName;
        Map<String, Object> relationMap;
        Object relationResults;
        Class realSlaveDOClass;
        for (String relation : relationList) {
            result = null;
            try {
                field = modelClass.getDeclaredField(relation);
                Annotation fieldRelationDataAnnotation = field.getDeclaredAnnotation(RelationData.class);
                if (fieldRelationDataAnnotation != null) {
                    fieldBaseDOTypeName = ((RelationData) fieldRelationDataAnnotation).baseDOTypeName();
                } else {
                    continue;
                }
                if (((RelationData) fieldRelationDataAnnotation).isOneToMany()) {
                    mapper = initMapperByDOSimpleName(fieldBaseDOTypeName);
                    realSlaveDOClass = getClassByDOSimpleName(((RelationData) fieldRelationDataAnnotation).baseDOTypeName());
                    javaMainFieldName = ((RelationData) fieldRelationDataAnnotation).mainProperty();
                    Condition condition = new Condition(realSlaveDOClass);
                    condition.createCriteria().andEqualTo(javaMainFieldName, mainPk);
                    result = mapper.selectByCondition(condition);
                    ReflectUtil.setFieldValue(model, relation, result);
                } else if (((RelationData) fieldRelationDataAnnotation).isManyToMany()) {
                    mapper = initMapperByDOSimpleName(((RelationData) fieldRelationDataAnnotation).relationDOTypeName());
                    if (StringUtils.isNotBlank(((RelationData) fieldRelationDataAnnotation).mainProperty())) {
                        javaMainFieldName = ((RelationData) fieldRelationDataAnnotation).mainProperty();
                    } else {
                        javaMainFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, modelClass.getAnnotation(RelationData.class).baseDOTypeName() + "Pk");
                    }
                    Condition condition = getConditionBySimpleDOName(((RelationData) fieldRelationDataAnnotation).relationDOTypeName());
                    condition.createCriteria().andEqualTo(javaMainFieldName, mainPk);
                    relationResults = mapper.selectByCondition(condition);
                    if (((List) relationResults).size() > 0) {
                        List<String> targetPkList = new ArrayList<>();
                        if (StringUtils.isNotBlank(((RelationData) fieldRelationDataAnnotation).foreignProperty())) {
                            javaTargetFieldName = ((RelationData) fieldRelationDataAnnotation).foreignProperty();
                        } else {
                            javaTargetFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, fieldBaseDOTypeName) + "Pk";
                        }
                        for (Object target : (List) relationResults) {
                            targetPkList.add(ReflectUtil.getFieldValue(target, javaTargetFieldName).toString());
                        }
                        mapper = initMapperByDOSimpleName(fieldBaseDOTypeName);
                        realSlaveDOClass = Class.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName());
                        condition = getConditionBySimpleDOName(fieldBaseDOTypeName);
                        condition.createCriteria().andIn("pk", targetPkList);
                        result = mapper.selectByCondition(condition);
                        Field relationDataField = ReflectUtil.getRelationDataField(realSlaveDOClass);
                        if (relationDataField != null) {
                            List<Object> resultListContainsRelation = new ArrayList<>();
                            for (Object relationResult : (List) relationResults) {
                                for (Object sourceResult : (List) result) {
                                    Object relationTarget = ReflectUtil.cloneObj(sourceResult, realSlaveDOClass);
                                    if (ReflectUtil.getFieldValue(relationResult, javaTargetFieldName).equals(ReflectUtil.getFieldValue(sourceResult, "pk"))) {
                                        relationMap = new HashMap<>();
                                        for (Field relationResultField : relationResult.getClass().getDeclaredFields()) {
                                            if (!StringUtils.endsWith(relationResultField.getName(), "Pk") && !StringUtils.endsWith(relationResultField.getName(), "pk")) {
                                                relationMap.put(relationResultField.getName(), ReflectUtil.getFieldValue(relationResult, relationResultField.getName()));
                                            }
                                        }
                                        ReflectUtil.setFieldValue(relationTarget, relationDataField.getName(), relationMap);
                                        resultListContainsRelation.add(relationTarget);
                                    }
                                }
                            }
                            result = resultListContainsRelation;
                        }
                    }
                    ReflectUtil.setFieldValue(model, relation, result);
                } else {
                    mapper = initMapperByDOSimpleName(fieldBaseDOTypeName);
                    if (StringUtils.isNotBlank(((RelationData) fieldRelationDataAnnotation).mainProperty())) {
                        javaMainFieldName = ((RelationData) fieldRelationDataAnnotation).mainProperty();
                        Condition condition = getConditionBySimpleDOName(fieldBaseDOTypeName);
                        condition.createCriteria().andEqualTo(javaMainFieldName, mainPk);
                        result = mapper.selectByCondition(condition);
                        if (((List) result).size() > 1) {
                            throw new TooManyResultsException("查询出的结果过多:表:" + fieldBaseDOTypeName + ",字段:" + javaMainFieldName + ",值:" + mainPk);
                        }
                        if (((List) result).size() > 0) {
                            ReflectUtil.setFieldValue(model, relation, ((List) result).get(0));
                        } else {
                            ReflectUtil.setFieldValue(model, relation, null);
                        }
                    } else if (StringUtils.isNotBlank(((RelationData) fieldRelationDataAnnotation).foreignProperty())) {
                        javaMainFieldName = ((RelationData) fieldRelationDataAnnotation).foreignProperty();
                        result = mapper.selectByPrimaryKey(ReflectUtil.getFieldValue(model, javaMainFieldName));
                        result = ReflectUtil.cloneObj(result, field.getType());
                        ReflectUtil.setFieldValue(model, relation, result);
                    }
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
    public List list(String keywords, String condition, String relations, String orderBy) {
        List<T> retList = new ArrayList<>();
        List<Field> fields = ReflectUtil.getKeywordsFields(modelClass);
        List<Object> results;
        try {
            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + modelClassShortName + "Mapper"));
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
                criteriaCondition.andCondition(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, condition));
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

    @Override
    public T saveAutomatic(T model, boolean forceSaveNull) {
        String mainPk = model.getPk();
        Annotation mainClassRelationDataAnnotation = modelClass.getAnnotation(RelationData.class);
        if (mainClassRelationDataAnnotation != null) {
            modelClassShortName = ((RelationData) mainClassRelationDataAnnotation).baseDOTypeName();
        }
        List<Field> relationFields;
        String realFieldClassFullName;
        String realFieldClassShortNameWithoutModel;
        Object currentFeildValue;
        String javaTargetFieldName;
        String javaMainFieldName;
        Object currentRelatioinedDO;
        Condition condition;
        Map<String, Object> relationDataMap;
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
                    Field relationDataField = ReflectUtil.getRelationDataField(Class.forName(((ParameterizedType) currentField.getGenericType()).getActualTypeArguments()[0].getTypeName()));
                    for (Object currentRelationedListItem : (List) currentFeildValue) {
                        currentRelatioinedDO = Class.forName(ProjectConstant.MODEL_PACKAGE + "." + ((RelationData) currentFieldRelationDataAnnotation).relationDOTypeName()).newInstance();
                        ((BaseDO) currentRelatioinedDO).setPk(UUID.randomUUID().toString());
                        ReflectUtil.setFieldValue(currentRelatioinedDO, javaMainFieldName, mainPk);
                        ReflectUtil.setFieldValue(currentRelatioinedDO, javaTargetFieldName, ((BaseDO) currentRelationedListItem).getPk());
                        if (relationDataField != null) {
                            relationDataMap = (Map<String, Object>) ReflectUtil.getFieldValue(currentRelationedListItem, relationDataField.getName());
                            if (relationDataMap != null) {
                                List<String> fieldsListOfDO = ReflectUtil.getFieldListNamesList(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + ((RelationData) currentFieldRelationDataAnnotation).relationDOTypeName()));
                                List<String> fieldsListOfDataMap = new ArrayList<>();
                                for (String key : relationDataMap.keySet()) {
                                    fieldsListOfDataMap.add(key);
                                }
                                fieldsListOfDO = NameUtil.getRetain(fieldsListOfDO, fieldsListOfDataMap);

                                for (String key : fieldsListOfDO) {
                                    ReflectUtil.setFieldValue(currentRelatioinedDO, key, relationDataMap.get(key));
                                }
                            }
                            ReflectUtil.setFieldValue(currentRelatioinedDO, "created", new Date());
                            mapper.insert(currentRelatioinedDO);
                        }
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

    @Override
    public int delAutomaticByPk(String pk) {
        Annotation annotation = modelClass.getAnnotation(RelationData.class);
        if (annotation == null) {
            throw new RuntimeException("没有找到" + modelClass.getName() + "对应的DO");
        }
        mapper = initMapperByDOSimpleName(((RelationData) annotation).baseDOTypeName());
        return mapper.deleteByPrimaryKey(pk);
    }

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

    private Condition getConditionBySimpleDOName(String simpleNameOfDO) {
        Condition condition = null;
        try {
            condition = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + simpleNameOfDO));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return condition;
    }

    private String getDBConditionString(String condition) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, condition);
    }

    private Mapper initMainMapper() {
        Annotation annotation = modelClass.getAnnotation(RelationData.class);
        if (annotation == null) {
            return null;
        }
        return initMapperByDOSimpleName(((RelationData) annotation).baseDOTypeName());
    }

    private Mapper initMapperByDOSimpleName(String simpleNameOfDO) {
        Mapper ret = null;
        try {
            ret = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + simpleNameOfDO + "Mapper"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private Class getClassByDOSimpleName(String simpleNameOfDO) {
        try {
            return Class.forName(ProjectConstant.MODEL_PACKAGE + "." + simpleNameOfDO);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
