package com.redmount.template.core;

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
    private String pk;

    private Date created;

    private Date updated;

}
