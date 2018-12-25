package com.redmount.template.base.model;

import com.redmount.template.core.BaseDO;
import javax.persistence.*;

@Table(name = "r_test_clazz_t_test_teacher")
public class RTestClazzTTestTeacher extends BaseDO {
    @Column(name = "teacher_pk")
    private String teacherPk;

    @Column(name = "clazz_pk")
    private String clazzPk;

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
}