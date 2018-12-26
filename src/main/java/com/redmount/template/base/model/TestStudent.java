package com.redmount.template.base.model;

import com.redmount.template.core.BaseDO;
import javax.persistence.*;

@Table(name = "test_student")
public class TestStudent extends BaseDO {
    /**
     * 学生名称
     */
    private String name;

    /**
     * 所属班级pk
     */
    @Column(name = "test_clazz_pk")
    private String testClazzPk;

    /**
     * 获取学生名称
     *
     * @return name - 学生名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置学生名称
     *
     * @param name 学生名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取所属班级pk
     *
     * @return test_clazz_pk - 所属班级pk
     */
    public String getTestClazzPk() {
        return testClazzPk;
    }

    /**
     * 设置所属班级pk
     *
     * @param testClazzPk 所属班级pk
     */
    public void setTestClazzPk(String testClazzPk) {
        this.testClazzPk = testClazzPk;
    }
}