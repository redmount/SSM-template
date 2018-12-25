package com.redmount.template.core;

import com.redmount.template.util.ReflectUtil;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class AbstractModelService<T, TBase> implements ModelService<T, TBase> {

    @Autowired
    private Mapper<TBase> mapper;

    @Autowired
    SqlSession sqlSession;

    /**
     * 当前泛型真实类型的Class
     */
    private Class<T> modelClass;

    public AbstractModelService() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        modelClass = (Class<T>) pt.getActualTypeArguments()[0];
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
        TBase baseResult = mapper.selectByPrimaryKey(pk);
        T model = ReflectUtil.cloneObj(baseResult, modelClass);
        List<String> relationList = ReflectUtil.getFieldList(modelClass, relations);
        Field field;
        String className;
        String methodName;
        String[] fullClassNamePath;
        Mapper mapper;
        Object result;
        for (String relation : relationList) {
            try {
                field = modelClass.getDeclaredField(relation);
                fullClassNamePath = field.getGenericType().getTypeName().split("\\.|<|>");
                className = fullClassNamePath[fullClassNamePath.length - 1];
                mapper = (Mapper) sqlSession.getMapper(Class.forName(ProjectConstant.MAPPER_PACKAGE + "." + className + "Mapper"));
                result = mapper.selectByPrimaryKey("t1");
                if (field.getGenericType().getTypeName().startsWith("java.util.List")) {
                    // 是数组

                } else {
                    methodName = "findById";
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
}
