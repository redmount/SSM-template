package com.redmount.template.base.service.impl;

import com.redmount.template.base.repo.RTestTeacherTTestClazzMapper;
import com.redmount.template.base.model.RTestTeacherTTestClazz;
import com.redmount.template.base.service.RTestTeacherTTestClazzBaseService;
import com.redmount.template.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * @author CodeGenerator
 * @date 2019/01/11
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RTestTeacherTTestClazzBaseServiceImpl extends AbstractService<RTestTeacherTTestClazz> implements RTestTeacherTTestClazzBaseService {
    @Resource
    private RTestTeacherTTestClazzMapper rTestTeacherTTestClazzMapper;

}
