package com.redmount.template.core;

import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * 定制版MyBatis Mapper插件接口，如需其他接口参考官方文档自行添加。
 * @author 朱峰
 * @date 2018年11月12日
 */
public interface Mapper<BaseDO>
        extends
        BaseMapper<BaseDO>,
        ConditionMapper<BaseDO>,
        IdsMapper<BaseDO>,
        InsertListMapper<BaseDO> {
}
