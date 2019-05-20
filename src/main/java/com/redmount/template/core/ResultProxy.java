package com.redmount.template.core;

import com.redmount.template.core.exception.ServiceException;

/**
 * 节点调用的结果转换类
 *
 * @author 朱峰
 * @date 2018年12月5日
 */
public class ResultProxy {
    /**
     * 取结果,并处理异常
     *
     * @param result 其他节点返回的结果
     * @return 包装后的结果
     */
    public static Object getResult(Result result) {
        if (result.getCode() == ResultCode.SUCCESS.code()) {
            return result.getData();
        }
        if (result.getCode() == ResultCode.SERVICE_EXCEPTION.code()) {
            throw new ServiceException(result.getException().getCode());
        }
        return null;
    }
}
