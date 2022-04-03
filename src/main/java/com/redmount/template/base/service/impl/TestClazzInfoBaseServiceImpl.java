package com.redmount.template.base.service.impl;

import com.redmount.template.base.model.TestClazzInfo;
import com.redmount.template.base.service.TestClazzInfoBaseService;
import com.redmount.template.core.AbstractModelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by CodeGenerator on 2020/08/09.
 * @author CodeGenerator
 * @date 2020/08/09
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestClazzInfoBaseServiceImpl extends AbstractModelService<TestClazzInfo> implements TestClazzInfoBaseService {
}
