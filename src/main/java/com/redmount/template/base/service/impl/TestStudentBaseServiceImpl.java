package com.redmount.template.base.service.impl;

import com.redmount.template.base.repo.TestStudentMapper;
import com.redmount.template.base.model.TestStudent;
import com.redmount.template.base.service.TestStudentBaseService;
import com.redmount.template.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2018/12/24.
 * @author CodeGenerator
 * @date 2018/12/24
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestStudentBaseServiceImpl extends AbstractService<TestStudent> implements TestStudentBaseService {
    @Resource
    private TestStudentMapper testStudentMapper;

}
