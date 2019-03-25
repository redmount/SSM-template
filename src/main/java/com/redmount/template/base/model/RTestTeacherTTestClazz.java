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
@Table(name = "r_test_teacher_t_test_clazz")
@ApiModel("RTestTeacherTTestClazz（）")
@Data
@Accessors(chain = true)
@RelationData(baseDOTypeName = "RTestTeacherTTestClazz")
public class RTestTeacherTTestClazz extends BaseDO implements Serializable {
    @Column(name = "teacher_pk")
    @ApiModelProperty(value = "")
    @ColumnType(jdbcType = JdbcType.CHAR)
    private String teacherPk;

    @Column(name = "clazz_pk")
    @ApiModelProperty(value = "")
    @ColumnType(jdbcType = JdbcType.CHAR)
    private String clazzPk;

    @ApiModelProperty(value = "")
    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private String course;

    @ApiModelProperty(value = "")
    @ColumnType(jdbcType = JdbcType.INTEGER)
    private Integer count;

    private static final long serialVersionUID = 1L;

    public enum FieldEnum {
        PK("pk","pk"),
		TEACHER_PK("teacherPk","teacher_pk"),
		CLAZZ_PK("clazzPk","clazz_pk"),
		COURSE("course","course"),
		COUNT("count","count"),
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