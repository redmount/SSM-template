package com.redmount.template.core;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseDOTombstoned extends BaseDO {
    /**
     * 逻辑删除标记
     */
    private Boolean deleted;
}
