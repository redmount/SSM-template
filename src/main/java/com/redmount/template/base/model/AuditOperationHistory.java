package com.redmount.template.base.model;

import com.redmount.template.base.repo.AuditOperationHistoryMapper;
import com.redmount.template.core.BaseDOLogicDeletion;
import com.redmount.template.core.annotation.LogicDeletion;
import com.redmount.template.core.annotation.RelationData;
import com.redmount.template.core.annotation.Validate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

/**
 * @author Mybatis Generator
 */
@Table(name = "audit_operation_history")
@ApiModel("AuditOperationHistory（）")
@Data
@LogicDeletion
@RelationData(baseDOClass = AuditOperationHistory.class, baseDOMapperClass = AuditOperationHistoryMapper.class)
public class AuditOperationHistory extends BaseDOLogicDeletion implements Serializable {
    /**
     * 操作人PK
     */
    @Column(name = "operator_pk")
    @ApiModelProperty(value = "操作人PK")
    private String operatorPk;

    /**
     * 操作人真实姓名
     */
    @Column(name = "operator_real_name")
    @ApiModelProperty(value = "操作人真实姓名")
    private String operatorRealName;

    /**
     * 操作人用户名
     */
    @Column(name = "operator_user_name")
    @ApiModelProperty(value = "操作人用户名")
    private String operatorUserName;

    /**
     * 操作的接口方法名
     */
    @Column(name = "operation_function")
    @ApiModelProperty(value = "操作的接口方法名")
    private String operationFunction;

    /**
     * 操作
     */
    @ApiModelProperty(value = "操作")
    private String operation;

    /**
     * 操作body data
     */
    @Column(name = "operation_arguments")
    @ApiModelProperty(value = "操作body data")
    private String operationArguments;

    /**
     * 操作结果
     */
    @Column(name = "operation_result")
    @ApiModelProperty(value = "操作结果")
    private String operationResult;

    /**
     * 执行时长
     */
    @ApiModelProperty(value = "执行时长")
    private Long duration;

    /**
     * 此条记录的创建者PK
     */
    @ApiModelProperty(value = "此条记录的创建者PK")
    private String creator;

    /**
     * 此条记录的最后一次修改者PK
     */
    @ApiModelProperty(value = "此条记录的最后一次修改者PK")
    private String updater;

    private static final long serialVersionUID = 1L;
}