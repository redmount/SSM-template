package com.redmount.template.base.service.impl;

import com.redmount.template.base.repo.TestStudentMapper;
import com.redmount.template.base.model.TestStudent;
import com.redmount.template.base.service.TestStudentBaseService;
import com.redmount.template.core.AbstractModelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2023/05/23.
 * @author CodeGenerator
 * @date 2023/05/23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestStudentBaseServiceImpl extends AbstractModelService<TestStudent> implements TestStudentBaseService {
}
