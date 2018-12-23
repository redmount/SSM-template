package ${basePackage}.base.dao.impl;

import ${basePackage}.base.repo.${modelNameUpperCamel}Mapper;
import ${basePackage}.base.model.${modelNameUpperCamel};
import ${basePackage}.base.dao.${modelNameUpperCamel}BaseDao;
import ${basePackage}.core.AbstractService;
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
public class ${modelNameUpperCamel}BaseDaoImpl extends AbstractService<${modelNameUpperCamel}> implements ${modelNameUpperCamel}BaseDao {
    @Resource
    private ${modelNameUpperCamel}Mapper ${modelNameLowerCamel}Mapper;

}
