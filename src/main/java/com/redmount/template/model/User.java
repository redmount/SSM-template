package com.redmount.template.model;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private String pk;
    private String userName;
    private String password;
    private Date lastUpdated;
}
