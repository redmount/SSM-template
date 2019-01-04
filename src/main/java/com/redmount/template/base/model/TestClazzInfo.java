package com.redmount.template.base.model;

import com.redmount.template.core.BaseDO;
import javax.persistence.*;

@Table(name = "test_clazz_info")
public class TestClazzInfo extends BaseDO {
    private String detail;

    @Column(name = "clazz_pk")
    private String clazzPk;

    /**
     * @return detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     * @param detail
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * @return clazz_pk
     */
    public String getClazzPk() {
        return clazzPk;
    }

    /**
     * @param clazzPk
     */
    public void setClazzPk(String clazzPk) {
        this.clazzPk = clazzPk;
    }
}