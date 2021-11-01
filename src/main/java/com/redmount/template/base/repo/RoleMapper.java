package com.redmount.template.base.repo;

import com.redmount.template.base.model.Role;
import com.redmount.template.core.Mapper;
import org.apache.ibatis.annotations.CacheNamespace;

@CacheNamespace
public interface RoleMapper extends Mapper<Role> {
}