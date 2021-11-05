package com.redmount.template.util;

import com.redmount.template.core.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class ValidateUtil {
    /**
     * 验证验证码是否输入正确
     *
     * @return 是否符合验证
     */
    public static boolean isValidateCodeCorrect(String inputCode, String cryptCode) {
        if (StringUtils.isBlank(cryptCode) || StringUtils.isBlank(inputCode)) {
            throw new ServiceException(100003);
        }
        try {
            inputCode = inputCode.trim();
            String code = MD5Util.getMD5(inputCode);
            return code.equalsIgnoreCase(cryptCode);
        } catch (Exception e) {
            throw new ServiceException(100003);
        }
    }

    /**
     * 验证是否为电子邮件
     *
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
    public static boolean isMobile(String str) {
        String regex = "^1(3[0-9]|4[01456879]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[0-35-9])\\d{8}$";
        return Pattern.matches(regex, str);
    }
}
