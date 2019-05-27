package com.redmount.template.system.model;

import com.redmount.template.core.BaseDO;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "sys_job_config")
@Data
@Accessors(chain = true)
public class SysJobConfig extends BaseDO {
    @Id
    private String pk;
    private String name;
    private Date created;
    private Date updated;
}
