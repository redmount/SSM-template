package com.redmount.template.base.model;

import com.redmount.template.core.BaseDO;
import javax.persistence.*;

@Table(name = "r_test_teacher_t_test_clazz")
public class RTestTeacherTTestClazz extends BaseDO {
    @Column(name = "test_teacher_pk")
    private String testTeacherPk;

    @Column(name = "test_clazz_pk")
    private String testClazzPk;

    private String course;

    private Integer count;

    /**
     * @return test_teacher_pk
     */
    public String getTestTeacherPk() {
        return testTeacherPk;
    }

    /**
     * @param testTeacherPk
     */
    public void setTestTeacherPk(String testTeacherPk) {
        this.testTeacherPk = testTeacherPk;
    }

    /**
     * @return test_clazz_pk
     */
    public String getTestClazzPk() {
        return testClazzPk;
    }

    /**
     * @param testClazzPk
     */
    public void setTestClazzPk(String testClazzPk) {
        this.testClazzPk = testClazzPk;
    }

    /**
     * @return course
     */
    public String getCourse() {
        return course;
    }

    /**
     * @param course
     */
    public void setCourse(String course) {
        this.course = course;
    }

    /**
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @param count
     */
    public void setCount(Integer count) {
        this.count = count;
    }
}