package com.redmount.template.util;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ValidateCodeModel {
    private String cryptCode;
    private String imgBase64;
    private String inputCode;

    /**
     * 将真实的实际值进行加密
     *
     * @param realCode 真实值
     * @return 加密后的验证码实体
     */
    ValidateCodeModel initCode(String realCode) {
        this.cryptCode = MD5Util.getMD5(realCode);
        return this;
    }
}
