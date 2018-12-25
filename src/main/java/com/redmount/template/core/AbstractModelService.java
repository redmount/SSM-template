package com.redmount.template.core;

import com.redmount.template.util.ReflectUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractModelService<T, TBase> implements ModelService<T, TBase> {

    @Autowired
    private Mapper<TBase> mapper;

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
        List<String> relationList = Arrays.asList(relations.split(","));

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
