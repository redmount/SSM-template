package com.redmount.template.model;

import lombok.Data;

@Data
public class LoginModel {
    private String loginName;
    private String password;
    private String inputCode;
    private String cryptCode;
}
