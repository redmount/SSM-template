package ${basePackage}.base.service.impl;

import ${basePackage}.base.repo.${modelNameUpperCamel}Mapper;
import ${basePackage}.base.model.${modelNameUpperCamel};
import ${basePackage}.base.service.${modelNameUpperCamel}BaseService;
import ${basePackage}.core.AbstractModelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by ${author} on ${date}.
 * @author ${author}
 * @date ${date}
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ${modelNameUpperCamel}BaseServiceImpl extends AbstractModelService<${modelNameUpperCamel}> implements ${modelNameUpperCamel}BaseService {
}
