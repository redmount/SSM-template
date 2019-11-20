package com.redmount.template.base.service.impl;

import com.redmount.template.base.repo.TestTeacherMapper;
import com.redmount.template.base.model.TestTeacher;
import com.redmount.template.base.service.TestTeacherBaseService;
import com.redmount.template.core.AbstractModelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2019/11/19.
 * @author CodeGenerator
 * @date 2019/11/19
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestTeacherBaseServiceImpl extends AbstractModelService<TestTeacher> implements TestTeacherBaseService {
}
