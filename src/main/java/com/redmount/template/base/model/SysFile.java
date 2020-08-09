package com.redmount.template.base.model;

import com.redmount.template.base.repo.SysFileMapper;
import com.redmount.template.core.BaseDO;
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
@Table(name = "sys_file")
@ApiModel("SysFile（）")
@Data
@RelationData(baseDOClass = SysFile.class, baseDOMapperClass = SysFileMapper.class)
public class SysFile extends BaseDO implements Serializable {
    @Column(name = "ori_file_name")
    @ApiModelProperty(value = "")
    private String oriFileName;

    @Column(name = "server_file_name")
    @ApiModelProperty(value = "")
    private String serverFileName;

    @ApiModelProperty(value = "")
    private String suffix;

    @ApiModelProperty(value = "")
    private Long size;

    private static final long serialVersionUID = 1L;
}