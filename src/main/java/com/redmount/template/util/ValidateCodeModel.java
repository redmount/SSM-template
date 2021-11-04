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

    /**
     * 验证是否为电子邮件
     * @param str
     * @return
     */
    public static boolean isEmail(String str) {
        String regex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        return Pattern.matches(regex, str);
    }

    /**
     * 验证是否为手机号
     *
     * @param str
     * @return
     */
    public static boolean isMobile(String str){
        String regex="^1(3[0-9]|4[01456879]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[0-35-9])\\d{8}$";
        return Pattern.matches(regex,str);
    }
}
