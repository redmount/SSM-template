package com.redmount.template.model;

import com.redmount.template.base.model.TestClazz;
import com.redmount.template.base.model.TestStudent;
import com.redmount.template.base.model.TestTeacher;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class TestClazzModel extends TestClazz {
    private TestTeacher adviser;
    private List<TestStudent> students;
    private List<TestTeacherModel> teachers;
    private Map<String,Object> relation;
}
