package com.redmount.template.core;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 朱峰
 * @date 2018年11月19日
 */
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

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

}
