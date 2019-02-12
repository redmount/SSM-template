package com.redmount.template.base.model;

import com.redmount.template.core.BaseDO;
import com.redmount.template.core.annotation.RelationData;
import com.redmount.template.core.annotation.Tombstoned;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Mybatis Generator
 */
@Table(name = "test_student")
@ApiModel("TestStudent（）")
@Data
@Accessors(chain = true)
@Tombstoned
@RelationData(baseDOTypeName = "TestStudent")
public class TestStudent extends BaseDO implements Serializable {
    /**
     * 学生名称
     */
    @ApiModelProperty(value = "学生名称")
    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private String name;

    /**
     * 所属班级pk
     */
    @Column(name = "clazz_pk")
    @ApiModelProperty(value = "所属班级pk")
    @ColumnType(jdbcType = JdbcType.CHAR)
    private String clazzPk;

    @ApiModelProperty(value = "")
    @ColumnType(jdbcType = JdbcType.BIT)
    private Boolean deleted;

    private static final long serialVersionUID = 1L;

    public enum FieldEnum {
        PK("pk","pk"),
		NAME("name","name"),
		CLAZZ_PK("clazzPk","clazz_pk"),
		UPDATED("updated","updated"),
		CREATED("created","created"),
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
