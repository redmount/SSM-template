package com.redmount.template.base.repo;

import com.redmount.template.base.model.RUserTRoleGroup;
import com.redmount.template.core.Mapper;
import org.apache.ibatis.annotations.CacheNamespace;

@CacheNamespace
public interface RUserTRoleGroupMapper extends Mapper<RUserTRoleGroup> {
}