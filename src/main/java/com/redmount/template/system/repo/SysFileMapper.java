package com.redmount.template.system.repo;

import com.redmount.template.system.model.SysFile;
import com.redmount.template.core.Mapper;
import org.apache.ibatis.annotations.CacheNamespace;

@CacheNamespace
public interface SysFileMapper extends Mapper<SysFile> {
}