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
    @ApiModelProperty(value = "")
    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private String name;

    @ApiModelProperty(value = "")
    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private String course;

    private static final long serialVersionUID = 1L;

    public enum FieldEnum {
        PK("pk","pk"),
		NAME("name","name"),
		COURSE("course","course"),
		CREATED("created","created"),
		UPDATED("updated","updated"),
		DELETED("deleted","deleted");

        private String javaFieldName;

        private String dbFieldName;

        FieldEnum(String javaFieldName, String dbFieldName) {
            this.javaFieldName = javaFieldName;
            this.dbFieldName = dbFieldName;
        }

        public String javaFieldName() {
            return javaFieldName;
        }

        public String dbFieldName() {
            return dbFieldName;
        }
    }
}