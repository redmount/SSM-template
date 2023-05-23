package com.redmount.template.core;

import com.github.pagehelper.PageInfo;
import com.redmount.template.core.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.SortedMap;

public abstract class AbstractController<T extends BaseDO> implements Controller<T> {

    /**
     * 当前的运行模式
     */
    @Value("${spring.profiles.active}")
    private String env;

    protected ModelService service;

    /**
     * 新增或修改资源
     *
     * @param model 资源实体
     * @return 被影响的实体结果
     */
    @PutMapping
    @Override
    public Result<T> saveAutomatic(@RequestBody T model) {
        initService();
        return ResultGenerator.genSuccessResult((T) service.saveAutomatically(model, true));
    }

    /**
     * 取资源列表
     * 4
     *
     * @param keywords  关键字
     * @param condition 条件(小驼峰形式,SQL子语句)
     * @param relations 带的关系数据
     * @param orderBy   排序(仅支持主表字段排序)
     * @param page      取第几页
     * @param size      每页第几条
     * @return 带分页信息的实体列表
     */
    @GetMapping
    @Override
    public Result<PageInfo<T>> listAutomatic(@RequestParam(value = "keywords", defaultValue = "") String keywords,
                                             @RequestParam(value = "condition", defaultValue = "") String condition,
                                             @RequestParam(value = "relations", defaultValue = "") String relations,
                                             @RequestParam(value = "orderBy", defaultValue = "updated desc") String orderBy,
                                             @RequestParam(value = "page", defaultValue = "1") int page,
                                             @RequestParam(value = "size", defaultValue = "10") int size) {
        initService();
        PageInfo pageInfo = service.listAutomaticallyWithoutRelations(keywords, condition, relations, orderBy, page, size);
        if (StringUtils.isNotBlank(relations)) {
            pageInfo = service.listAutomaticallyWithRelations(pageInfo, relations);
        }
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    /**
     * 按pk取实体
     *
     * @param pk        实体pk
     * @param relations 带的关系数据
     * @return 带关系数据的单一实体
     */
    @GetMapping("/{pk}")
    @Override
    public Result<T> getAutomatic(@PathVariable String pk, @RequestParam(defaultValue = "") String relations) {
        initService();
        return ResultGenerator.genSuccessResult((T) service.getAutomatically(pk, relations));
    }

    /**
     * 按pk删除
     *
     * @param pk 实体pk
     * @return 删除了多少条(1或0, 1代表删除成功, 0代表没有删除成功)
     */
    @DeleteMapping("/{pk}")
    @Override
    public Result<Integer> delAutomatic(@PathVariable String pk) {
        initService();
        return ResultGenerator.genSuccessResult(service.delAutomaticallyByPk(pk));
    }

    /**
     * 按条件物理删除
     *
     * @param condition 条件(小驼峰形式,SQL子语句)
     * @return 删除了多少条数据
     */
    @DeleteMapping
    @Override
    public Result<Integer> delByConditionAutomatic(@RequestParam("condition") String condition) {
        if (StringUtils.isBlank(condition)) {
            throw new ServiceException(999902);
        }
        initService();
        return ResultGenerator.genSuccessResult(service.delByConditionAutomatically(condition));
    }

    /**
     * 强制增加实体
     *
     * @param model 资源实体
     * @return 增加的实体
     */
    @PostMapping
    @Override
    public Result<T> addAutomatic(@RequestBody T model) {
        initService();
        model.setPk(null);
        return ResultGenerator.genSuccessResult((T) service.saveAutomatically(model, false));
    }

    /**
     * 增量修改实体
     *
     * @param model 待修改的资源实体
     * @return 修改后的资源实体
     */
    @PatchMapping("/{pk}")
    @Override
    public Result<T> modifyAutomatic(@PathVariable("pk") String pk, @RequestBody T model) {
        initService();
        model.setPk(pk);
        return ResultGenerator.genSuccessResult((T) service.saveAutomatically(model, false));
    }

    @GetMapping("/count")
    @Override
    public Result<Integer> getCountByCondition(@RequestParam("condition") String condition) {
        initService();
        return ResultGenerator.genSuccessResult(service.getCountByCondition(condition));
    }

    private void initService() {
        if (service == null) {
            init();
        }
    }

    /**
     * 取本实体的结构说明
     *
     * @return 实体结构说明
     */
    @GetMapping("/schema")
    public Result<SortedMap> getSchema() {
        if (!"dev".equalsIgnoreCase(env)) {
            try {
                throw new Exception("禁止访问");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        initService();
        return ResultGenerator.genSuccessResult(service.getSchema());
    }
}
