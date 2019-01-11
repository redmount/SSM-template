package com.redmount.template.core;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.redmount.template.model.ClazzModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class AbstractController implements Controller {

    protected ModelService service;

    @PostMapping
    @Override
    public Result saveAutomatic(@RequestBody Object model) {
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
    public Result getAutomatic(@PathVariable String pk, @RequestParam String relations) {
        if (service == null) {
            init();
        }
        return ResultGenerator.genSuccessResult(service.getAutomatic(pk, relations));
    }
}
