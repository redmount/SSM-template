package com.redmount.template.core;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.redmount.template.model.ClazzModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class AbstractController<T> implements Controller<T> {

    protected ModelService service;

    @PostMapping
    @Override
    public Result saveAutomatic(@RequestBody T model) {
        if (service == null) {
            init();
        }
        return ResultGenerator.genSuccessResult(service.saveAutomatic(model));
    }

    @GetMapping
    @Override
    public Result listAutomatic(@RequestParam(value = "keywords", defaultValue = "") String keywords,
                                @RequestParam(value = "condition", defaultValue = "") String condition,
                                @RequestParam(value = "relations", defaultValue = "") String relations,
                                @RequestParam(value = "orderBy", defaultValue = "updated desc") String orderBy,
                                @RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "size", defaultValue = "10") int size) {
        if (service == null) {
            init();
        }
        PageHelper.startPage(page, size);
        List<ClazzModel> list = service.list(keywords, condition, relations, orderBy);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @GetMapping("/{pk}")
    @Override
    public Result getAutomatic(@PathVariable String pk, @RequestParam(defaultValue = "") String relations) {
        if (service == null) {
            init();
        }
        return ResultGenerator.genSuccessResult(service.getAutomatic(pk, relations));
    }

    @DeleteMapping("/{pk}")
    public Result delAutomatic(@PathVariable String pk) {
        if (service == null) {
            init();
        }
        return ResultGenerator.genSuccessResult(service.delAutomaticByPk(pk));
    }

    @DeleteMapping
    public Result delByConditionAutomatic(@RequestParam(defaultValue = "") String condition) {
        if(StringUtils.isBlank(condition)){
            return ResultGenerator.genSuccessResult(0);
        }
        if (service == null) {
            init();
        }
        return ResultGenerator.genSuccessResult(service.delByConditionAudomatic(condition));
    }
}
