package ${basePackage}.base.controller;

import ${basePackage}.core.AbstractController;
import ${basePackage}.base.model.${modelNameUpperCamel};
import ${basePackage}.base.service.${modelNameUpperCamel}BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* Created by ${author} on ${date}.
*/
@RestController
@RequestMapping("${baseRequestMapping}")
public class ${modelNameUpperCamel}Controller extends AbstractController<${modelNameUpperCamel}> {
@Autowired
private ${modelNameUpperCamel}BaseService service;

@Override
public void init() {
super.service = service;
    }
}
