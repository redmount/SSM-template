package com.redmount.template.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

/**
 * @author 朱峰
 * @date 2018年11月19日
 */
@Data
public class BaseDO {

    @Id
    @ApiModelProperty("主键")
    private String pk;
    @ApiModelProperty("数据创建时间")
    @JsonIgnore
    private Date created;
    @ApiModelProperty("数据最后一次更新时间")
    @JsonIgnore
    private Date updated;

}
