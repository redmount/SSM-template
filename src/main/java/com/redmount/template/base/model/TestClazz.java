package com.redmount.template.base.model;

import com.redmount.template.core.BaseDO;
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
 * @date 2019-01-19 16:16:48
 */
@Table(name = "test_clazz")
@ApiModel("TestClazz（）")
@Data
@Accessors(chain = true)
public class TestClazz extends BaseDO implements Serializable {
    /**
     * 班级名称
     */
    @ApiModelProperty(value = "班级名称", required = false)
    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private String name;

    /**
     * 班主任pk
     */
    @Column(name = "adviser_pk")
    @ApiModelProperty(value = "班主任pk", required = false)
    @ColumnType(jdbcType = JdbcType.CHAR)
    private String adviserPk;

    @Column(name = "nick_name")
    @ApiModelProperty(value = "", required = false)
    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private String nickName;

    @ApiModelProperty(value = "", required = false)
    @ColumnType(jdbcType = JdbcType.BIT)
    private Boolean deleted;

    private static final long serialVersionUID = 1263308691369888731L;

    public enum FieldEnum {
        PK("pk","pk"),
		NAME("name","name"),
		ADVISER_PK("adviserPk","adviser_pk"),
		UPDATED("updated","updated"),
		CREATED("created","created"),
		NICK_NAME("nickName","nick_name"),
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