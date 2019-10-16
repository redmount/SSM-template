package com.redmount.template.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseDOTombstoned extends BaseDO {
    /**
     * 逻辑删除标记
     */
    private Boolean deleted;
}
