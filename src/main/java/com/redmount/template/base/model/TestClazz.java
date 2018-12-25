package com.redmount.template.base.model;

import com.redmount.template.core.BaseDO;
import javax.persistence.*;

@Table(name = "test_clazz")
public class TestClazz extends BaseDO {
    /**
     * 班级名称
     */
    private String name;

    /**
     * 班主任pk
     */
    @Column(name = "adviser_pk")
    private String adviserPk;

    /**
     * 获取班级名称
     *
     * @return name - 班级名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置班级名称
     *
     * @param name 班级名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取班主任pk
     *
     * @return adviser_pk - 班主任pk
     */
    public String getAdviserPk() {
        return adviserPk;
    }

    /**
     * 设置班主任pk
     *
     * @param adviserPk 班主任pk
     */
    public void setAdviserPk(String adviserPk) {
        this.adviserPk = adviserPk;
    }
}