package com.redmount.template.base.service.impl;

import com.redmount.template.base.repo.TestClazzMapper;
import com.redmount.template.base.model.TestClazz;
import com.redmount.template.base.service.TestClazzBaseService;
import com.redmount.template.core.AbstractModelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2019/11/06.
 * @author CodeGenerator
 * @date 2019/11/06
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestClazzBaseServiceImpl extends AbstractModelService<TestClazz> implements TestClazzBaseService {
}
