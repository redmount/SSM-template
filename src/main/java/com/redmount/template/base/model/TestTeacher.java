package com.redmount.template.base.model;

import com.redmount.template.core.BaseDO;
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
 * @date 2019-01-19 16:16:48
 */
@Table(name = "test_teacher")
@ApiModel("TestTeacher（）")
@Data
@Accessors(chain = true)
public class TestTeacher extends BaseDO implements Serializable {
    /**
     * 教师名称
     */
    @ApiModelProperty(value = "教师名称", required = false)
    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private String name;

    private static final long serialVersionUID = 3740187666259360058L;

    public enum FieldEnum {
        PK("pk","pk"),
		NAME("name","name"),
		CREATED("created","created"),
		UPDATED("updated","updated");

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