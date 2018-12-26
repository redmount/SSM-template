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
        modelClassShortName = modelClass.getTypeName().split("\\.")[modelClass.getTypeName().split("\\.").length - 1];
    }

    /**
     * 取单个实体
     *
     * @param pk        单个实体pk
     * @param relations 关系数据
     * @return 带关系数据的单个实体
     */
    @Override
    public T getByPk(String pk, String relations) {
        try {
            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + modelClassShortName.replaceAll("Model", "") + "Mapper"));
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
        List<String> relationList = ReflectUtil.getFieldList(modelClass, relations);
        Field field;
        String className;
        String[] fullClassNamePath;
        Mapper mapper;
        Object result = null;
        Object relationResult;
        String mainPk;
        String sqlFieldName;
        Map<String, Object> relationMap = new HashMap<>();
        for (String relation : relationList) {
            try {
                field = modelClass.getDeclaredField(relation);
                fullClassNamePath = field.getGenericType().getTypeName().split("\\.|<|>");
                className = fullClassNamePath[fullClassNamePath.length - 1];

                if (field.getGenericType().getTypeName().startsWith("java.util.List")) {
                    // 是数组
                    // 先判断子表中是否有关联到主表的外键
                    // 取子表类型

                    mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + className.replaceAll("Model", "") + "Mapper"));
                    fullClassNamePath = field.getGenericType().getTypeName().split("<|>");
                    className = fullClassNamePath[1];
                    mainPk = model.getPk();
                    if (ReflectUtil.containsProperty(Class.forName(className), modelClassShortName.replaceAll("Model", "") + "Pk")) {
                        // 如果有外键,则load进来
                        System.out.println("找到单个对应属性:" + relation + "Pk");
                        sqlFieldName = modelClassShortName.replaceAll("Model", "") + "Pk";
                        sqlFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, sqlFieldName);
                        Condition condition = new Condition(Class.forName(className));
                        condition.createCriteria().andCondition(sqlFieldName + "='" + mainPk + "'");
                        result = mapper.selectByCondition(condition);
                        ReflectUtil.setFieldValue(model, relation, result);
                    } else {
                        className = className.split("\\.")[className.split("\\.").length - 1];
                        String relationClassName = "R" + modelClassShortName.replace("Model", "") + "T" + className.replaceAll("Model", "");
                        try {
                            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + relationClassName + "Mapper"));
                        } catch (ClassNotFoundException ex) {
                            relationClassName = "R" + className.replaceAll("Model", "") + "T" + modelClassShortName.replace("Model", "");
                            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + relationClassName + "Mapper"));
                        }
                        sqlFieldName = modelClassShortName.replaceAll("Model", "") + "Pk";
                        sqlFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, sqlFieldName);
                        Condition condition = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + relationClassName));
                        condition.createCriteria().andCondition(sqlFieldName + "='" + mainPk + "'");
                        relationResult = mapper.selectByCondition(condition);
                        if (((List) relationResult).size() > 0) {
                            String strIn = "";
                            for (int i = 0; i < ((List) relationResult).size(); i++) {
                                strIn += ",'" + ReflectUtil.getFieldValue(((List) relationResult).get(i), className.replaceAll("Model", "").substring(0, 1).toLowerCase() + className.replaceAll("Model", "").substring(1) + "Pk") + "'";
                            }
                            strIn = "(" + strIn.substring(1) + ")";
                            mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + className.replaceAll("Model", "") + "Mapper"));
                            condition = new Condition(Class.forName(ProjectConstant.MODEL_PACKAGE + "." + className.replaceAll("Model", "")));
                            condition.createCriteria().andCondition("pk in " + strIn);
                            result = mapper.selectByCondition(condition);
                            // 如果包含关系信息
                            if (ReflectUtil.containsProperty(Class.forName(fullClassNamePath[1]), "relation")) {
                                List<Object> resultListContainsRelation = new ArrayList<>();
                                for (Object obj : (List) relationResult) {
                                    for (Object target : (List) result) {
                                        Object o = ReflectUtil.cloneObj(target, Class.forName(fullClassNamePath[1]));
                                        if (ReflectUtil.getFieldValue(obj, className.replaceAll("Model", "").substring(0, 1).toLowerCase() + className.replaceAll("Model", "").substring(1) + "Pk").equals(ReflectUtil.getFieldValue(target, "pk"))) {
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
                    mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + className.replaceAll("Model", "") + "Mapper"));
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
