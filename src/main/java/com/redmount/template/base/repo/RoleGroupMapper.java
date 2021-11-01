package com.redmount.template.base.repo;

import com.redmount.template.base.model.RoleGroup;
import com.redmount.template.core.Mapper;
import org.apache.ibatis.annotations.CacheNamespace;

@CacheNamespace
public interface RoleGroupMapper extends Mapper<RoleGroup> {
}