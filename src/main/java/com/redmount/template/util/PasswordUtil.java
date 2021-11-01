package com.redmount.template.util;

public class PasswordUtil {
    public static String MD5WithSalt(String str, String salt) {
        str = salt + str + salt;
        return MD5Util.getMD5(str);
    }

    public static boolean validatePasswordWithSalt(String inputPassword, String password, String salt) {
        String passwordSalted = MD5WithSalt(inputPassword, salt);
        return password == passwordSalted;
    }
}
