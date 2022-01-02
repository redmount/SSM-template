package com.redmount.template.service;

import com.redmount.template.base.model.Authority;
import org.apache.ibatis.annotations.CacheNamespace;

import java.util.List;

@CacheNamespace
public interface AuthorityModelService {
    List<String> getUserAuthorityCodeList(String userPk);
}
