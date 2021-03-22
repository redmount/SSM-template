package com.redmount.template.core;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseDOLogicDeletion extends BaseDO {
    /**
     * 逻辑删除标记
     */
    private Boolean deleted;
}
