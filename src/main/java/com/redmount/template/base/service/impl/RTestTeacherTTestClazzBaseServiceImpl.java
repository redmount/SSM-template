package com.redmount.template.base.service.impl;

import com.redmount.template.base.repo.RTestTeacherTTestClazzMapper;
import com.redmount.template.base.model.RTestTeacherTTestClazz;
import com.redmount.template.base.service.RTestTeacherTTestClazzBaseService;
import com.redmount.template.core.AbstractBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2019/05/27.
 * @author CodeGenerator
 * @date 2019/05/27
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RTestTeacherTTestClazzBaseServiceImpl extends AbstractBaseService<RTestTeacherTTestClazz> implements RTestTeacherTTestClazzBaseService {
    @Resource
    private RTestTeacherTTestClazzMapper rTestTeacherTTestClazzMapper;

}
