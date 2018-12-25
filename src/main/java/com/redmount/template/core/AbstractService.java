package com.redmount.template.core;

import com.redmount.template.core.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 基于通用MyBatis Mapper插件的Service接口的实现
 *
 * @author 朱峰
 * @date 2018年11月12日
 */
public abstract class AbstractService<T extends BaseDO> implements Service<T> {

    @Autowired
    protected Mapper<T> mapper;

    /**
     * 当前泛型真实类型的Class
     */
    private Class<T> modelClass;

    public AbstractService() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        modelClass = (Class<T>) pt.getActualTypeArguments()[0];
    }

    @Override
    public T save(T model) {
        if (StringUtils.isBlank(model.getPk())) {
            model.setPk(UUID.randomUUID().toString());
            model.setCreated(new Date());
            mapper.insertSelective(model);
        } else {
            model.setUpdated(new Date());
            mapper.updateByPrimaryKeySelective(model);
        }
        return model;
    }

    @Override
    public List<T> save(List<T> models) {
        List<T> insertList = new ArrayList<>();
        List<T> updateList = new ArrayList<>();
        for (T item : models) {
            if (StringUtils.isBlank(item.getPk())) {
                item.setPk(UUID.randomUUID().toString());
                insertList.add(item);
            } else {
                updateList.add(item);
            }
        }
        List<T> retList = new ArrayList<>();
        for (T model : updateList) {
            retList.add(this.save(model));
        }
        return retList;
    }

    @Override
    public boolean deleteById(String id) {
        mapper.deleteByPrimaryKey(id);
        return true;
    }

    @Override
    public boolean deleteByIds(String ids) {
        mapper.deleteByIds(ids);
        return true;
    }

    @Override
    public T update(T model) {
        mapper.updateByPrimaryKeySelective(model);
        return model;
    }

    @Override
    public T findById(String id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public T findBy(String fieldName, Object value) throws TooManyResultsException {
        try {
            T model = modelClass.newInstance();
            Field field = modelClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(model, value);
            return mapper.selectOne(model);
        } catch (ReflectiveOperationException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<T> findByIds(String ids) {
        return mapper.selectByIds(ids);
    }

    @Override
    public List<T> findByCondition(Condition condition) {
        return mapper.selectByCondition(condition);
    }

    @Override
    public List<T> findAll() {
        return mapper.selectAll();
    }

    @Override
    public int deleteByCondition(Condition condition) {
        return mapper.deleteByCondition(condition);
    }
}
