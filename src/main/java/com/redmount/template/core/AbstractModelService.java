package com.redmount.template.core;

import com.google.common.base.CaseFormat;
import com.redmount.template.core.annotation.RelationData;
import com.redmount.template.util.NameUtil;
import com.redmount.template.util.ReflectUtil;
import org.apache.commons.lang3.StringUtils;
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
        Annotation mainClassRelationDataAnnotation = modelClass.getAnnotation(RelationData.class);
        if (mainClassRelationDataAnnotation != null) {
            modelClassShortName = ((RelationData) mainClassRelationDataAnnotation).baseDOTypeName();
        }
        try {
            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + modelClassShortName + "Mapper"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
                    mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + fieldBaseDOTypeName + "Mapper"));
                    realSlaveDOClass = Class.forName(ProjectConstant.MODEL_PACKAGE + "." + ((RelationData) fieldRelationDataAnnotation).baseDOTypeName());
                    javaMainFieldName = ((RelationData) fieldRelationDataAnnotation).foreignProperty();
                    Condition condition = new Condition(realSlaveDOClass);
                    condition.createCriteria().andEqualTo(javaMainFieldName, mainPk);
                    result = mapper.selectByCondition(condition);
                    ReflectUtil.setFieldValue(model, relation, result);
                } else if (((RelationData) fieldRelationDataAnnotation).isManyToMany()) {
                    mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + ((RelationData) fieldRelationDataAnnotation).relationDOTypeName() + "Mapper"));
                    if (StringUtils.isNotBlank(((RelationData) fieldRelationDataAnnotation).mainProperty())) {
                        javaMainFieldName = ((RelationData) fieldRelationDataAnnotation).mainProperty();
                    } else {
                        javaMainFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, modelClass.getAnnotation(RelationData.class).baseDOTypeName() + "Pk");
                    }
                    Condition condition = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + ((RelationData) fieldRelationDataAnnotation).relationDOTypeName()));
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
                        mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + fieldBaseDOTypeName + "Mapper"));
                        realSlaveDOClass = Class.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName());
                        condition = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + fieldBaseDOTypeName));
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
                    javaMainFieldName = ((RelationData) fieldRelationDataAnnotation).foreignProperty();
                    mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + fieldBaseDOTypeName + "Mapper"));
                    result = mapper.selectByPrimaryKey(ReflectUtil.getFieldValue(model, javaMainFieldName));
                    result = ReflectUtil.cloneObj(result, field.getType());
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
    public T saveAutomatic(T model) {
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
            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + modelClassShortName + "Mapper"));
            if (StringUtils.isBlank(mainPk)) {
                mainPk = UUID.randomUUID().toString();
                model.setPk(mainPk);
                model.setCreated(new Date());
                mapper.insert(model);
            } else {
                mapper.updateByPrimaryKeySelective(model);
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
                    javaMainFieldName = ((RelationData) currentFieldRelationDataAnnotation).foreignProperty();
                    mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + ((RelationData) currentFieldRelationDataAnnotation).baseDOTypeName() + "Mapper"));
                    condition = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + ((RelationData) currentFieldRelationDataAnnotation).baseDOTypeName()));
                    condition.createCriteria().andEqualTo(((RelationData) currentFieldRelationDataAnnotation).foreignProperty(), mainPk);
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
                    mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + ((RelationData) currentFieldRelationDataAnnotation).relationDOTypeName() + "Mapper"));
                    realFieldClassFullName = ((ParameterizedType) currentField.getGenericType()).getActualTypeArguments()[0].getTypeName();
                    realFieldClassShortNameWithoutModel = Class.forName(realFieldClassFullName).getAnnotation(RelationData.class).baseDOTypeName();
                    javaMainFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, modelClass.getAnnotation(RelationData.class).baseDOTypeName() + "Pk");
                    javaTargetFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, realFieldClassShortNameWithoutModel + "Pk");
                    condition = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + currentField.getAnnotation(RelationData.class).relationDOTypeName()));
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
                    javaTargetFieldName = ((RelationData) currentFieldRelationDataAnnotation).foreignProperty();
                    ReflectUtil.setFieldValue(model, javaTargetFieldName, ((BaseDO) currentFeildValue).getPk());
                }
            }
            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + modelClassShortName + "Mapper"));
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
}
