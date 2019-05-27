package com.redmount.template.base.service.impl;

import com.redmount.template.base.repo.TestClazzInfoMapper;
import com.redmount.template.base.model.TestClazzInfo;
import com.redmount.template.base.service.TestClazzInfoBaseService;
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
public class TestClazzInfoBaseServiceImpl extends AbstractBaseService<TestClazzInfo> implements TestClazzInfoBaseService {
    @Resource
    private TestClazzInfoMapper testClazzInfoMapper;

}
