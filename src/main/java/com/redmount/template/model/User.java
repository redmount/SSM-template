package com.redmount.template.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {
    private String pk;
    private String userName;
    private String password;
    private long id;
}
