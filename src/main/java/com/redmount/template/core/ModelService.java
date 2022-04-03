package com.redmount.template.core;

import com.github.pagehelper.PageInfo;
import org.apache.ibatis.exceptions.TooManyResultsException;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Field;
import java.util.List;
import java.util.SortedMap;

public interface ModelService<T> {
    /**
     * 取单个实体
     *
     * @param pk        单个实体pk
     * @param relations 关系数据
     * @return 带关系数据的单个实体
     */
    T getAutomatically(String pk, String relations);

    /**
     * 给单个实体挂关系
     *
     * @param model     原始实体
     * @param relations 关系数据
     * @return 带关系的实体
     */
    T getAutomaticallyWithModel(T model, String relations);

    /**
     * 取带关系的列表
     *
     * @param pageInfo  带分页的主表数据
     * @param relations 关系数据
     * @return 带分页, 带关系的列表
     */
    PageInfo listAutomaticallyWithRelations(PageInfo pageInfo, String relations);

    /**
     * 取符合条件的实体列表
     *
     * @param keywords  关键字
     * @param relations 关系数据
     * @param orderBy   排序
     * @return 带关系数据的排序的实体列表
     */
    PageInfo listAutomaticallyWithoutRelations(String keywords, String condition, String relations, String orderBy, int page, int size);

    /**
     * 自动保存
     *
     * @param model 需要保存的数据,目前的限制是只保存表现层中的两层,带关系数据的,保存关系数据.再往下就不管了.
     * @return 保存之后的结果
     */
    T saveAutomatically(T model, boolean forceSaveNull);

    /**
     * 真实删除单条数据
     *
     * @param pk PK
     * @return 删除的条数
     */
    int delAutomaticallyByPk(String pk);

    /**
     * 按条件删除
     *
     * @param condition 条件
     * @return 删除的条数
     */
    int delByConditionAutomatically(String condition);

    /**
     * 加载一对一关系数据
     *
     * @param model 主实体对象
     * @param field 要取的属性
     * @return 增加了要取的属性的主实体对象
     */
    T loadOneToOneRelation(T model, Field field);

    /**
     * 按条件加载一对一关系
     * 主供后台使用
     *
     * @param model           主实体对象
     * @param field           要加载的属性
     * @param conditionString 子属性的条件(小驼峰字符串形式)
     * @return 按条件加载的一对多关系之后的主实体对象
     */
    T loadOneToOneRelation(T model, Field field, String conditionString);

    /**
     * 按条件加载一对一关系
     * 主供后台使用
     *
     * @param model     主实体对象
     * @param field     要加载的属性
     * @param condition 子属性的条件(小驼峰形式)
     * @return 按条件加载的一对多关系之后的主实体对象
     */
    T loadOneToOneRelation(T model, Field field, Condition condition);

    /**
     * 加载一对多关系
     *
     * @param model 主实体对象
     * @param field 要取的属性
     * @return 增加了要取的属性的主实体对象
     */
    T loadOneToManyRelation(T model, Field field);

    /**
     * 按条件加载一对多关系数据
     * 主供后台使用
     *
     * @param model           主实体对象
     * @param field           需要加载的属性
     * @param conditionString 针对子实体的条件(小驼峰字符串形式)
     * @return 增加了子实体列表的主实体对象
     */
    T loadOneToManyRelation(T model, Field field, String conditionString);

    /**
     * 按条件加载一对多关系数据
     * 主供后台使用
     *
     * @param model     主实体对象
     * @param field     需要加载的属性
     * @param condition 针对子实体的条件
     * @return 增加了子实体列表的主实体对象
     */
    T loadOneToManyRelation(T model, Field field, Condition condition);

    /**
     * 加载多对多关系
     *
     * @param model 主实体对象
     * @param field 要加载的属性
     * @return 增加了要加载的属性的主实体对象
     */
    T loadManyToManyRelation(T model, Field field);

    /**
     * 按条件加载多对多关系
     * 主供后台使用
     *
     * @param model           主实体对象
     * @param field           要加载的属性
     * @param conditionString 子属性的条件(小驼峰字符串形式)
     * @return 按条件加载的多对多关系之后的主实体对象
     */
    T loadManyToManyRelation(T model, Field field, String conditionString);

    /**
     * 按条件加载多对多关系
     * 主供后台使用
     *
     * @param model     主实体对象
     * @param field     要加载的属性
     * @param condition 子属性的条件(小驼峰形式)
     * @return 按条件加载的多对多关系之后的主实体对象
     */
    T loadManyToManyRelation(T model, Field field, Condition condition);

    /**
     * 保存实体中的一对一关系
     *
     * @param model 主实体
     * @param field 需要保存的字段
     * @return 保存后的主实体
     */
    T saveOneToOneRelation(T model, Field field);

    /**
     * 保存实体中的一对多关系(子实体中记录了主实体的pk)
     *
     * @param model 主实体
     * @param field 需要保存的字段
     * @return 保存后的主实体
     */
    T saveOneToManyRelation(T model, Field field);

    /**
     * 保存实体中的多对多关系(通过中间表连接的,并且关联数据记录在中间表的)
     *
     * @param model 主实体
     * @param field 需要保存的字段
     * @return 保存后的主实体
     */
    T saveManyToManyRelation(T model, Field field);

    /**
     * 持久化(忽略是否为新建对象)
     *
     * @param model 需要存储的实体,不用区分是否是新建
     * @return 已保存的实体
     */
    T save(T model);

    /**
     * 批量持久化
     *
     * @param models 需要存储的实体列表,未实现完毕.
     * @return 已保存的实体列表
     */
    List<T> save(List<T> models);

    /**
     * 通过主鍵刪除
     *
     * @param pk 主键
     * @return 是否删除成功, 不成功时会以异常的方式抛出
     */
    boolean deleteById(String pk);

    /**
     * 批量刪除 eg：ids -> “1,2,3,4”
     *
     * @param ids id的字符串列表,以逗号分隔.
     * @return 是否删除成功, 不成功时会以异常的方式抛出
     */
    boolean deleteByIds(String ids);

    /**
     * 更新实体,内部以pk为更新的依据
     *
     * @param model 需要更新的实体模型
     * @return 已更新的实体
     */
    T update(T model);

    /**
     * 通过pk查找实体
     *
     * @param pk 实体pk
     * @return 实体, 如果未查到, 将返回null
     */
    T findById(String pk);

    /**
     * 通过Model中某个成员变量名称（非数据表中column的名称）查找,value需符合unique约束
     *
     * @param fieldName 需要匹配的字段(数据库中的)
     * @param value     匹配值
     * @return 符合条件的单个对象, 如果查询出多个对象, 则抛出异常.
     * @throws TooManyResultsException 结果过多异常
     */
    T findBy(String fieldName, Object value) throws TooManyResultsException;

    /**
     * 通过多个ID查找//eg：ids -> “1,2,3,4”
     *
     * @param ids id列表
     * @return 符合条件的对象List
     */
    List<T> findByIds(String ids);

    /**
     * 根据条件查找
     *
     * @param condition condition 对象
     * @return 符合条件的对象List
     */
    List<T> findByCondition(Condition condition);

    /**
     * 取全表
     *
     * @return 全表对象List
     */
    List<T> findAll();

    /**
     * 根据条件删除
     *
     * @param condition condition对象
     * @return 删除的条数
     */
    int deleteByCondition(Condition condition); //根据条件删除

    /**
     * 根据条件字符串取数量
     *
     * @param conditionString 条件字符串
     * @return 符合条件的数量
     */
    int getCountByCondition(String conditionString);

    /**
     * 根据条件取数量
     *
     * @param condition condition对象
     * @return 符合条件的数量
     */
    int getCountByCondition(Condition condition);

    /**
     * 取实体注释模型
     *
     * @return 带注释的实体模型
     */
    SortedMap getSchema();

    /**
     * 自定义取数据的方法, 该方法会在挂完关系之后执行
     * 以满足自定义数据结构的需求
     *
     * @param model 已经查询到的实体
     * @param relations 关系数据字符串
     * @return 自定义实体
     */
    T afterGetModel(T model, String relations);

    /**
     * 保存自定莫模型之前需要做的事.
     * 这里可以实现自定义的存储.
     * 还可以通过返回值控制是否继续使用save方法进行保存
     * true: 继续保存
     * false: 不继续保存
     *
     * @param model 自定义数据模型
     * @return 是否继续使用save方法进行保存
     */
    boolean beforeSaveModel(T model);

    /**
     * 保存自定义模型之后需要做的事.
     * 这里也可以实现自定义存储, 多线程记录啊, 什么的.
     *
     * @param model 自定义数据模型
     * @return 保存完之后的数据模型
     */
    T afterSaveModel(T model);
}
