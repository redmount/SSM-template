package com.redmount.template.model;

import com.redmount.template.base.model.TestTeacher;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TestTeacherModel extends TestTeacher {
    private List<TestClazzModel> clazzes;
    private Map<String, Object> relation;
}
