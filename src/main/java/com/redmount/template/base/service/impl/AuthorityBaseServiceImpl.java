package com.redmount.template.base.service.impl;

import com.redmount.template.base.repo.AuthorityMapper;
import com.redmount.template.base.model.Authority;
import com.redmount.template.base.service.AuthorityBaseService;
import com.redmount.template.core.AbstractModelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2021/11/04.
 * @author CodeGenerator
 * @date 2021/11/04
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AuthorityBaseServiceImpl extends AbstractModelService<Authority> implements AuthorityBaseService {
}
