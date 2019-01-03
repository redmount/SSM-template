package com.redmount.template.base.model;

import com.redmount.template.core.BaseDO;
import javax.persistence.*;

@Table(name = "r_test_teacher_t_test_clazz")
public class RTestTeacherTTestClazz extends BaseDO {
    @Column(name = "teacher_pk")
    private String teacherPk;

    @Column(name = "clazz_pk")
    private String clazzPk;

    private String course;

    private Integer count;

    /**
     * @return teacher_pk
     */
    public String getTeacherPk() {
        return teacherPk;
    }

    /**
     * @param teacherPk
     */
    public void setTeacherPk(String teacherPk) {
        this.teacherPk = teacherPk;
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