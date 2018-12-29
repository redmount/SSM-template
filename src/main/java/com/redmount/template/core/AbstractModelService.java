package com.redmount.template.core;

import com.google.common.base.CaseFormat;
import com.redmount.template.core.annotation.RelationData;
import com.redmount.template.util.ReflectUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

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
        Annotation mainClassRelationDataAnnotation = modelClass.getAnnotation(RelationData.class);
        if (mainClassRelationDataAnnotation != null) {
            modelClassShortName = ((RelationData) mainClassRelationDataAnnotation).BaseDOTypeName();
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
                    fieldBaseDOTypeName = ((RelationData) fieldRelationDataAnnotation).BaseDOTypeName();
                } else {
                    continue;
                }
                if (((RelationData) fieldRelationDataAnnotation).isOneToMany()) {
                    mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + fieldBaseDOTypeName + "Mapper"));
                    realSlaveDOClass = Class.forName(ProjectConstant.MODEL_PACKAGE + "." + ((RelationData) fieldRelationDataAnnotation).BaseDOTypeName());
                    javaMainFieldName = ((RelationData) fieldRelationDataAnnotation).foreignProperty();
                    Condition condition = new Condition(realSlaveDOClass);
                    condition.createCriteria().andEqualTo(javaMainFieldName, mainPk);
                    result = mapper.selectByCondition(condition);
                    ReflectUtil.setFieldValue(model, relation, result);
                } else if (((RelationData) fieldRelationDataAnnotation).isManyToMany()) {
                    mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + ((RelationData) fieldRelationDataAnnotation).relationTableName() + "Mapper"));
                    javaMainFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, modelClass.getAnnotation(RelationData.class).BaseDOTypeName() + "Pk");
                    Condition condition = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + ((RelationData) fieldRelationDataAnnotation).relationTableName()));
                    condition.createCriteria().andEqualTo(javaMainFieldName, mainPk);
                    relationResults = mapper.selectByCondition(condition);
                    if (((List) relationResults).size() > 0) {
                        List<String> targetPkList = new ArrayList<>();
                        javaTargetFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, fieldBaseDOTypeName) + "Pk";
                        for (Object target : (List) relationResults) {
                            targetPkList.add(ReflectUtil.getFieldValue(target, javaTargetFieldName).toString());
                        }
                        mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + fieldBaseDOTypeName + "Mapper"));
                        realSlaveDOClass = Class.forName(ProjectConstant.MODEL_PACKAGE + "." + ((RelationData) fieldRelationDataAnnotation).BaseDOTypeName());
                        condition = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + fieldBaseDOTypeName));
                        condition.createCriteria().andIn("pk", targetPkList);
                        result = mapper.selectByCondition(condition);
                        Field RelationDataField = ReflectUtil.getRelationDataField(realSlaveDOClass);
                        if (RelationDataField != null) {
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
                                        ReflectUtil.setFieldValue(relationTarget, RelationDataField.getName(), relationMap);
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
    public List<T> list(String keywords, String relations, String orderBy) {
        return null;
    }

    @Override
    public T saveAutomatic(T model) {
        return null;
//        String mainPk = model.getPk();
//        Annotation mainClassBaseDOType = modelClass.getAnnotation(BaseDOType.class);
//        if (mainClassBaseDOType != null) {
//            modelClassShortName = ((BaseDOType) mainClassBaseDOType).value();
//        }
//        List<Field> relationFields;
//        String realFieldShortNameWithoutModel;
//        Object currentFeildValue;
//        String javaTargetFieldName;
//        String javaMainFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, modelClassShortName) + "Pk";
//        String relationClassName;
//        Object currentRelatioinedDO;
//        boolean isContainsRelation;
//        Map<String, Object> relationDataMap;
//        try {
//            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + modelClassShortName + "Mapper"));
//            if (StringUtils.isBlank(mainPk)) {
//                mainPk = UUID.randomUUID().toString();
//                model.setPk(mainPk);
//                model.setCreated(new Date());
//                mapper.insert(model);
//            } else {
//                mapper.updateByPrimaryKeySelective(model);
//            }
//            relationFields = ReflectUtil.getRelationFields(model);
//            for (Field currentField : relationFields) {
//                currentFeildValue = ReflectUtil.getFieldValue(model, currentField.getName());
//                if (currentFeildValue == null) {
//                    continue;
//                }
//                Annotation baseDOTypeAnnotation = currentField.getAnnotation(BaseDOType.class);
//                if (baseDOTypeAnnotation == null) {
//                    continue;
//                }
//                Annotation collectionAnnotation = currentField.getAnnotation(Collection.class);
//                if (collectionAnnotation != null && ((Collection) collectionAnnotation).value()) {
//                    javaMainFieldName = ((Collection) collectionAnnotation).foreignProperty();
//                    realFieldShortNameWithoutModel = ((BaseDOType) baseDOTypeAnnotation).value();
//                    if (StringUtils.isNoneBlank(((Collection) collectionAnnotation).foreignProperty())) {
//                        System.out.println(currentField.getGenericType() + ":有主表pk");
//                        mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + ((BaseDOType) baseDOTypeAnnotation).value() + "Mapper"));
//                        Condition condition = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + ((BaseDOType) baseDOTypeAnnotation).value()));
//                        condition.createCriteria().andEqualTo(((Collection) collectionAnnotation).foreignProperty(), mainPk);
//                        Object childDOWithoutMainPk = Class.forName(ProjectConstant.MODEL_PACKAGE + "." + ((BaseDOType) baseDOTypeAnnotation).value()).newInstance();
//                        ReflectUtil.setFieldValue(childDOWithoutMainPk, ((Collection) collectionAnnotation).foreignProperty(), "");
//                        mapper.updateByConditionSelective(childDOWithoutMainPk, condition);
//                        for (Object currentItem : (List) currentFeildValue) {
//                            if (!StringUtils.isBlank(((BaseDO) currentItem).getPk())) {
//                                ReflectUtil.setFieldValue(currentItem, javaMainFieldName, mainPk);
//                                mapper.updateByPrimaryKeySelective(currentItem);
//                            } else {
//                                System.out.println("子表没有pk");
//                            }
//                        }
//                    } else {
//                        javaTargetFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, realFieldShortNameWithoutModel) + "Pk";
//                        relationClassName = "R" + modelClassShortName + "T" + realFieldShortNameWithoutModel;
//                        try {
//                            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + relationClassName + "Mapper"));
//                        } catch (ClassNotFoundException ex) {
//                            relationClassName = "R" + realFieldShortNameWithoutModel + "T" + modelClassShortName;
//                            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + relationClassName + "Mapper"));
//                        }
//                        Condition condition = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + relationClassName));
//                        condition.createCriteria().andEqualTo(javaMainFieldName, mainPk);
//                        mapper.deleteByCondition(condition);
//                        isContainsRelation = ReflectUtil.isRelationMap(currentField.getClass(), "relation");
//                        for (Object currentRelationedListItem : (List) currentFeildValue) {
//                            currentRelatioinedDO = Class.forName(ProjectConstant.MODEL_PACKAGE + "." + relationClassName).newInstance();
//                            ((BaseDO) currentRelatioinedDO).setPk(UUID.randomUUID().toString());
//                            ReflectUtil.setFieldValue(currentRelatioinedDO, javaMainFieldName, mainPk);
//                            ReflectUtil.setFieldValue(currentRelatioinedDO, javaTargetFieldName, ((BaseDO) currentRelationedListItem).getPk());
//                            if (isContainsRelation) {
//                                relationDataMap = (Map<String, Object>) ReflectUtil.getFieldValue(currentRelationedListItem, "relation");
//                                List<String> fieldsListOfDO = ReflectUtil.getFieldListNamesList(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + relationClassName));
//                                List<String> fieldsListOfDataMap = new ArrayList<>();
//                                for (String key : relationDataMap.keySet()) {
//                                    fieldsListOfDataMap.add(key);
//                                }
//                                fieldsListOfDO = NameUtil.getRetain(fieldsListOfDO, fieldsListOfDataMap);
//
//                                for (String key : fieldsListOfDO) {
//                                    ReflectUtil.setFieldValue(currentRelatioinedDO, key, relationDataMap.get(key));
//                                }
//                                ReflectUtil.setFieldValue(currentRelatioinedDO, "created", new Date());
//                                mapper.insert(currentRelatioinedDO);
//                            }
//                        }
//                    }
//                } else {
//                    javaTargetFieldName = currentField.getName() + "Pk";
//                    ReflectUtil.setFieldValue(model, javaTargetFieldName, ((BaseDO) currentFeildValue).getPk());
//                }
//            }
//            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + modelClassShortName + "Mapper"));
//            model.setUpdated(new Date());
//            mapper.updateByPrimaryKeySelective(model);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        }
//        return model;
    }
}
