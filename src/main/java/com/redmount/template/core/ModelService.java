package com.redmount.template.core;

import java.util.List;

public interface ModelService<T> {
    /**
     * 取单个实体
     *
     * @param pk        单个实体pk
     * @param relations 关系数据
     * @return 带关系数据的单个实体
     */
    T getAutomatic(String pk, String relations);

    /**
     * 取符合条件的实体列表
     *
     * @param keywords  关键字
     * @param relations 关系数据
     * @param orderBy   排序
     * @return 带关系数据的排序的实体列表
     */
    List list(String keywords, String condition, String relations, String orderBy);

    /**
     * 自动保存
     *
     * @param model 需要保存的数据,目前的限制是只保存表现层中的两层,带关系数据的,保存关系数据.再往下就不管了.
     * @return 保存之后的结果
     */
    T saveAutomatic(T model);

    /**
     * 真实删除单条数据
     * @param pk
     * @return
     */
    int delAutomaticByPk(String pk);

    /**
     * 按条件删除
     * @param condition
     * @return
     */
    int delByConditionAudomatic(String condition);
}
