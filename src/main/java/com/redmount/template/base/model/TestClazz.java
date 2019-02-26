package com.redmount.template.base.model;

import com.redmount.template.core.BaseDO;
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
@Table(name = "test_clazz")
@ApiModel("TestClazz（测试班级对象）")
@Data
@Accessors(chain = true)
@Tombstoned
@RelationData(baseDOTypeName = "TestClazz")
public class TestClazz extends BaseDO implements Serializable {
    /**
     * 班级名称
     */
    @ApiModelProperty(value = "班级名称")
    @Validate(nullable = false, stringMaxLength = 255)
    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private String name;

    /**
     * 班主任pk
     */
    @Column(name = "adviser_pk")
    @ApiModelProperty(value = "班主任pk")
    @ColumnType(jdbcType = JdbcType.CHAR)
    private String adviserPk;

    /**
     * 班级昵称
     */
    @Column(name = "nick_name")
    @ApiModelProperty(value = "班级昵称")
    @ColumnType(jdbcType = JdbcType.VARCHAR)
    private String nickName;

    /**
     * 是否被删除
     */
    @ApiModelProperty(value = "是否被删除")
    @ColumnType(jdbcType = JdbcType.BIT)
    private Boolean deleted;

    private static final long serialVersionUID = 1L;

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