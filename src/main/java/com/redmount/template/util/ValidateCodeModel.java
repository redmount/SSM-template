package com.redmount.template.util;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

@Data
@Accessors(chain = true)
public class ValidateCodeModel {
    private String realCode;
    private String imgBase64;
    private String inputCode;

    public Boolean isValidate() {
        if (StringUtils.isBlank(realCode)) {
            return null;
        }
        if (StringUtils.isBlank(inputCode)) {
            return false;
        }
        try {
            inputCode = inputCode.toUpperCase();
            String code = MD5Utils.getMD5(inputCode);
            return code.equalsIgnoreCase(realCode);
        } catch (Exception e) {
            return false;
        }
    }

    public ValidateCodeModel initCode(String realCode) {
        this.realCode = MD5Utils.getMD5(realCode);
        return this;
    }
}
