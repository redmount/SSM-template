package com.redmount.template.model;

import com.redmount.template.base.model.TestClazz;
import com.redmount.template.base.model.TestStudent;
import com.redmount.template.base.model.TestTeacher;
import lombok.Data;

import javax.persistence.Table;
import java.util.List;

@Data
@Table(name = "test_clazz")
public class TestClazzModel extends TestClazz {
    private TestTeacher adviser;
    private List<TestStudent> students;
    private List<TestTeacher> teachers;
}
