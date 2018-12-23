package com.redmount.template.base.model;

import com.redmount.template.core.BaseDO;
import javax.persistence.*;

@Table(name = "test_teacher")
public class TestTeacher extends BaseDO {
    /**
     * 教师名称
     */
    private String name;

    /**
     * 获取教师名称
     *
     * @return name - 教师名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置教师名称
     *
     * @param name 教师名称
     */
    public void setName(String name) {
        this.name = name;
    }
}