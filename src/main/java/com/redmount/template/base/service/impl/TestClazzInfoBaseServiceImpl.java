package com.redmount.template.base.service.impl;

import com.redmount.template.base.repo.TestClazzInfoMapper;
import com.redmount.template.base.model.TestClazzInfo;
import com.redmount.template.base.service.TestClazzInfoBaseService;
import com.redmount.template.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2019/02/02.
 * @author CodeGenerator
 * @date 2019/02/02
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestClazzInfoBaseServiceImpl extends AbstractService<TestClazzInfo> implements TestClazzInfoBaseService {
    @Resource
    private TestClazzInfoMapper testClazzInfoMapper;

}
