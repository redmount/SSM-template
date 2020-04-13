package com.redmount.template.repo;

import org.apache.ibatis.annotations.CacheNamespace;

@CacheNamespace
public interface ClazzModelRepo {
    Integer getStudentsCountByClassPk(String classPk);
}
