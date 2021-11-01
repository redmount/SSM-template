package com.redmount.template.base.repo;

import com.redmount.template.base.model.Authority;
import com.redmount.template.core.Mapper;
import org.apache.ibatis.annotations.CacheNamespace;

@CacheNamespace
public interface AuthorityMapper extends Mapper<Authority> {
}