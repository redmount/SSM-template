package com.redmount.template.base.repo;

import com.redmount.template.base.model.UserBaseInfo;
import com.redmount.template.core.Mapper;
import org.apache.ibatis.annotations.CacheNamespace;

@CacheNamespace
public interface UserBaseInfoMapper extends Mapper<UserBaseInfo> {
}