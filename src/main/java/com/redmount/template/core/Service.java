package com.redmount.template.core;

import org.apache.ibatis.exceptions.TooManyResultsException;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * Service 层 基础接口，其他Service 接口 请继承该接口
 * @author 朱峰
 * @date 2018年11月12日
 */
public interface Service<T> {
    /**
     * 持久化(忽略是否为新建对象)
     * @param model 需要存储的实体,不用区分是否是新建
     * @return 已保存的实体
     */
    T save(T model);

    /**
     * 批量持久化
     * @param models 需要存储的实体列表,未实现完毕.
     * @return 已保存的实体列表
     */
    List<T> save(List<T> models);

    /**
     * 通过主鍵刪除
     * @param pk 主键
     * @return 是否删除成功,不成功时会以异常的方式抛出
     */
    boolean deleteById(String pk);

    /**
     *批量刪除 eg：ids -> “1,2,3,4”
     * @param ids id的字符串列表,以逗号分隔.
     * @return 是否删除成功,不成功时会以异常的方式抛出
     */
    boolean deleteByIds(String ids);

    /**
     * 更新实体,内部以pk为更新的依据
     * @param model 需要更新的实体模型
     * @return 已更新的实体
     */
    T update(T model);

    /**
     * 通过pk查找实体
     * @param pk 实体pk
     * @return 实体,如果未查到,将返回null
     */
    T findById(String pk);

    /**
     *通过Model中某个成员变量名称（非数据表中column的名称）查找,value需符合unique约束
     * @param fieldName 需要匹配的字段(数据库中的)
     * @param value 匹配值
     * @return 符合条件的单个对象,如果查询出多个对象,则抛出异常.
     * @throws TooManyResultsException 结果过多异常
     */
    T findBy(String fieldName, Object value) throws TooManyResultsException;

    /**
     * 通过多个ID查找//eg：ids -> “1,2,3,4”
     * @param ids id列表
     * @return 符合条件的对象List
     */
    List<T> findByIds(String ids);

    /**
     * 根据条件查找
     * @param condition condition 对象
     * @return 符合条件的对象List
     */
    List<T> findByCondition(Condition condition);

    /**
     * 取全表
     * @return 全表对象List
     */
    List<T> findAll();

    /**
     * 根据条件删除
     * @param condition condition对象
     * @return 删除的条数
     */
    int deleteByCondition(Condition condition); //根据条件删除
}
