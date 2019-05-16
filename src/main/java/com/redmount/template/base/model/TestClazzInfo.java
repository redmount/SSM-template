package com.redmount.template.base.model;

import com.redmount.template.core.BaseDO;
import com.redmount.template.core.annotation.RelationData;
import com.redmount.template.core.annotation.Validate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;
import tk.mybatis.mapper.annotation.ColumnType;

/**
 * @author Mybatis Generator
 */
@Table(name = "test_clazz_info")
@ApiModel("TestClazzInfo（）")
@Data
@Accessors(chain = true)
@RelationData(baseDOTypeName = "TestClazzInfo")
public class TestClazzInfo extends BaseDO implements Serializable {
    @Column(name = "class_pk")
    @ApiModelProperty(value = "")
    @ColumnType(jdbcType = JdbcType.CHAR)
    private String classPk;

    @ApiModelProperty(value = "")
    @ColumnType(jdbcType = JdbcType.LONGVARBINARY)
    private byte[] img;

    private static final long serialVersionUID = 1L;
}