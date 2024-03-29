package com.redmount.template.system.service.impl;

import com.redmount.template.system.model.SysFile;
import com.redmount.template.system.service.SysFileBaseService;
import com.redmount.template.core.AbstractModelService;
import com.redmount.template.core.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;


/**
 * Created by CodeGenerator on 2020/08/09.
 * @author CodeGenerator
 * @date 2020/08/09
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysFileBaseServiceImpl extends AbstractModelService<SysFile> implements SysFileBaseService {
    @Override
    public File getFileByPk(String pk) {
        SysFile fileRecord = findById(pk);
        if (fileRecord == null) {
            throw new ServiceException("附件记录不存在");
        }

        File file = new File(fileRecord.getServerFileName());
        return file;
    }
}
