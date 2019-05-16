package com.redmount.template.base.model;

import com.redmount.template.core.BaseDOTombstoned;
import com.redmount.template.core.annotation.RelationData;
import com.redmount.template.core.annotation.Tombstoned;
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
@Table(name = "test_teacher")
@ApiModel("TestTeacher（）")
@Data
@Accessors(chain = true)
@Tombstoned
@RelationData(baseDOTypeName = "TestTeacher")
public class TestTeacher extends BaseDOTombstoned implements Serializable {
    /**
     * 教师名称
     */
    @ApiModelProperty(value = "教师名称")
    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private String name;

    private static final long serialVersionUID = 1L;
}