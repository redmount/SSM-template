package com.redmount.template.model;

import com.redmount.template.base.model.TestTeacher;
import lombok.Data;

import java.util.Map;

@Data
public class TestTeacherModel extends TestTeacher {
    private Map<String, Object> relation;
}
