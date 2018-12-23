package com.redmount.template.core.exception;

import com.redmount.template.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2018/11/13.
 * @author CodeGenerator
 * @date 2018/11/13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysServiceExceptionDOBaseDaoImpl extends AbstractService<SysServiceExceptionDO> implements SysServiceExceptionDOBaseDao {
    @Resource
    private SysServiceExceptionDOMapper sysServiceExceptionMapper;

}
