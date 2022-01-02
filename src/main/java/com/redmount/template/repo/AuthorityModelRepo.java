package com.redmount.template.repo;

import com.redmount.template.base.model.Authority;
import com.redmount.template.base.repo.AuthorityMapper;
import org.apache.ibatis.annotations.CacheNamespace;

import java.util.List;

@CacheNamespace
public interface AuthorityModelRepo {
    public List<String> getUserAuthorityCodeList(String userPk);
}
