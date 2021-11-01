package com.redmount.template.util;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

@Data
@Accessors(chain = true)
public class ValidateCodeModel {
    private String realCode;
    private String imgBase64;
    private String inputCode;

    /**
     * 验证
     *
     * @return 是否符合验证
     */
    public Boolean isValidate() {
        if (StringUtils.isBlank(realCode)) {
            return null;
        }
        if (StringUtils.isBlank(inputCode)) {
            return null;
        }
        try {
            inputCode = inputCode.toUpperCase().trim();
            String code = MD5Util.getMD5(inputCode);
            return code.equalsIgnoreCase(realCode);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将真实的实际值进行加密
     *
     * @param realCode 真实值
     * @return 加密后的验证码实体
     */
    ValidateCodeModel initCode(String realCode) {
        this.realCode = MD5Util.getMD5(realCode);
        return this;
    }

    public static boolean isEmail(String str) {
        String regex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        return Pattern.matches(regex, str);
    }
}
