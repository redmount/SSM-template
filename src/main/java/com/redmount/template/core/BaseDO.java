package com.redmount.template.core;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 朱峰
 * @date 2018年11月19日
 */
@Data
public class BaseDO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @ApiModelProperty("主键")
    private String pk;

    @ApiModelProperty("数据创建时间")
    @JSONField(serialize = false)
    private Date created;

    @ApiModelProperty("数据最后一次更新时间")
    @JSONField(serialize = false)
    private Date updated;
}
