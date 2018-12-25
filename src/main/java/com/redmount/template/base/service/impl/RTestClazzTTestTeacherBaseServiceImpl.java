package com.redmount.template.base.service.impl;

import com.redmount.template.base.repo.RTestClazzTTestTeacherMapper;
import com.redmount.template.base.model.RTestClazzTTestTeacher;
import com.redmount.template.base.service.RTestClazzTTestTeacherBaseService;
import com.redmount.template.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2018/12/25.
 * @author CodeGenerator
 * @date 2018/12/25
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RTestClazzTTestTeacherBaseServiceImpl extends AbstractService<RTestClazzTTestTeacher> implements RTestClazzTTestTeacherBaseService {
    @Resource
    private RTestClazzTTestTeacherMapper rTestClazzTTestTeacherMapper;

}
