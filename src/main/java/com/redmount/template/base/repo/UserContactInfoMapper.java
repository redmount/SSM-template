package com.redmount.template.base.repo;

import com.redmount.template.base.model.UserContactInfo;
import com.redmount.template.core.Mapper;
import org.apache.ibatis.annotations.CacheNamespace;

@CacheNamespace
public interface UserContactInfoMapper extends Mapper<UserContactInfo> {
}