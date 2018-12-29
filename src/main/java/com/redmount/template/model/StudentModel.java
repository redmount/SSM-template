package com.redmount.template.model;

import com.redmount.template.base.model.TestStudent;
import com.redmount.template.core.annotation.RelationData;
import lombok.Data;

@Data
@RelationData(BaseDOTypeName = "TestStudent")
public class StudentModel extends TestStudent {
    @RelationData(BaseDOTypeName = "TestClazz", foreignProperty = "clazzPk")
    TestClazzModel clazz;
}
