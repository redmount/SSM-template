package com.redmount.template.core;

import lombok.Data;

@Data
public class BaseDOTombstoned extends BaseDO {
    /**
     * 逻辑删除标记
     */
    private Boolean deleted;
}
