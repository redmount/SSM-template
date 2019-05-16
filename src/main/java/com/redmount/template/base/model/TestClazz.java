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
@Table(name = "test_clazz")
@ApiModel("TestClazz（）")
@Data
@Accessors(chain = true)
@RelationData(baseDOTypeName = "TestClazz")
public class TestClazz extends BaseDO implements Serializable {
    /**
     * 班级名称
     */
    @ApiModelProperty(value = "班级名称")
    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private String name;

    /**
     * 班主任pk
     */
    @Column(name = "adviser_pk")
    @ApiModelProperty(value = "班主任pk")
    @ColumnType(jdbcType = JdbcType.CHAR)
    private String adviserPk;

    @Column(name = "nick_name")
    @ApiModelProperty(value = "")
    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private String nickName;

    @ApiModelProperty(value = "")
    @ColumnType(jdbcType = JdbcType.LONGVARCHAR)
    private String detail;

    private static final long serialVersionUID = 1L;
}