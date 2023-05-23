package com.redmount.template.core;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.SortedMap;

public interface Controller<T> {
    /**
     * 新增或修改资源
     *
     * @param model 资源实体
     * @return 被影响的实体结果
     */
    @ApiOperation(value="新增或修改资源")
    Result<T> saveAutomatic(T model);

    /**
     * 强制增加实体
     *
     * @param model 资源实体
     * @return 增加的实体
     */
    @ApiOperation(value="强制添加资源")
    Result<T> addAutomatic(T model);

    /**
     * 强制修改实体
     *
     * @param model 待修改的资源实体
     * @return 修改后的资源实体
     * @param pk 待更新的实体pk
     */
    @ApiOperation(value="局部更新资源")
    Result<T> modifyAutomatic(String pk, T model);

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
    @ApiOperation(value="取资源列表(带分页信息)")
    Result<PageInfo<T>> listAutomatic(String keywords, String condition, String relations, String orderBy, int page, int size);

    /**
     * 按pk取实体
     *
     * @param pk        实体pk
     * @param relations 带的关系数据
     * @return 带关系数据的单一实体
     */
    @ApiOperation(value="按pk取单个资源")
    Result<T> getAutomatic(String pk, String relations);

    /**
     * 按pk物理删除
     *
     * @param pk 实体pk
     * @return 删除了多少条(1或0, 1代表删除成功, 0代表没有删除成功)
     */
    @ApiOperation(value="按pk物理删除资源")
    Result<Integer> delAutomatic(String pk);

    /**
     * 按条件物理删除
     *
     * @param condition 条件(小驼峰形式,SQL Where子语句)
     * @return 删除了多少条数据
     */
    @ApiOperation(value="按条件物理删除资源")
    Result<Integer> delByConditionAutomatic(String condition);

    /**
     * 按条件取数量
     * @param condition 条件(小驼峰形式,SQL Where子语句)
     * @return 符合条件的条数
     */
    @ApiOperation("按条件取数量")
    Result<Integer> getCountByCondition(@RequestParam("condition") String condition);
    /**
     * 取实体说明
     *
     * @return 带注释的实体说明
     */
    @ApiOperation(value="取资源的全部可见结构")
    Result<SortedMap> getSchema();

    /**
     * 初始化(目前主要用于初始化service,使之不为空)
     */
    void init();
}
