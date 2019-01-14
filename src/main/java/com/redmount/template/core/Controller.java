package com.redmount.template.core;

import java.util.List;

public interface Controller<T> {
    /**
     * 新增或修改资源
     *
     * @param model 资源实体
     * @return 被影响的实体结果
     */
    Result<T> saveAutomatic(T model);

    /**
     * 强制增加实体
     *
     * @param model 资源实体
     * @return 增加的实体
     */
    Result addAutomatic(T model);

    /**
     * 强制修改实体
     *
     * @Param pk 待更新的实体pk
     * @param model 待修改的资源实体
     * @return 修改后的资源实体
     */
    Result modifyAutomatic(String pk,T model);

    /**
     * 取资源列表
     *
     * @param keywords  关键字
     * @param condition 条件(小驼峰形式,SQL子语句)
     * @param relations 带的关系数据
     * @param orderBy   排序(仅支持主表字段排序)
     * @param page      取第几页
     * @param size      每页第几条
     * @return 带分页信息的实体列表
     */
    Result<List<T>> listAutomatic(String keywords, String condition, String relations, String orderBy, int page, int size);

    /**
     * 按pk取实体
     *
     * @param pk        实体pk
     * @param relations 带的关系数据
     * @return 带关系数据的单一实体
     */
    Result<T> getAutomatic(String pk, String relations);

    /**
     * 按pk物理删除
     *
     * @param pk 实体pk
     * @return 删除了多少条(1或0, 1代表删除成功, 0代表没有删除成功)
     */
    Result delAutomatic(String pk);

    /**
     * 按条件物理删除
     *
     * @param condition 条件(小驼峰形式,SQL子语句)
     * @return 删除了多少条数据
     */
    Result delByConditionAutomatic(String condition);

    /**
     * 初始化(目前主要用于初始化service,使之不为空)
     */
    void init();
}
