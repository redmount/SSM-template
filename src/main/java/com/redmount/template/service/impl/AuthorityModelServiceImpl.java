package com.redmount.template.service.impl;

import com.redmount.template.base.model.Authority;
import com.redmount.template.core.AbstractModelService;
import com.redmount.template.model.UserModel;
import com.redmount.template.repo.AuthorityModelRepo;
import com.redmount.template.service.AuthorityModelService;
import com.redmount.template.service.UserModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AuthorityModelServiceImpl extends AbstractModelService<Authority> implements AuthorityModelService {

    @Resource
    AuthorityModelRepo repo;

    @Override
    public List<String> getUserAuthorityCodeList(String userPk) {
        return repo.getUserAuthorityCodeList(userPk);
    }
}
