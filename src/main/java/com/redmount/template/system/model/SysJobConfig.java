package com.redmount.template.system.model;

import com.redmount.template.core.BaseDO;
import lombok.Data;

import javax.persistence.Table;

@Table(name = "sys_job_config")
@Data
public class SysJobConfig extends BaseDO {
    private String name;
}
