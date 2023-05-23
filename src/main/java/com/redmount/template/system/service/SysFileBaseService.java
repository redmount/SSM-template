package com.redmount.template.system.service;
import com.redmount.template.system.model.SysFile;
import com.redmount.template.core.ModelService;

import java.io.File;


/**
 * @author CodeGenerator
 * @date 2020/08/09
 */
public interface SysFileBaseService extends ModelService<SysFile> {
    public File getFileByPk(String pk);
}
