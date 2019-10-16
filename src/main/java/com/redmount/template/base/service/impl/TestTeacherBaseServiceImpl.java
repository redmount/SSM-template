package com.redmount.template.base.service.impl;

import com.redmount.template.base.repo.TestTeacherMapper;
import com.redmount.template.base.model.TestTeacher;
import com.redmount.template.base.service.TestTeacherBaseService;
import com.redmount.template.core.AbstractBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2019/10/16.
 * @author CodeGenerator
 * @date 2019/10/16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestTeacherBaseServiceImpl extends AbstractBaseService<TestTeacher> implements TestTeacherBaseService {
    @Resource
    private TestTeacherMapper testTeacherMapper;

}
