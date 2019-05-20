package com.redmount.template.util;

import com.google.common.base.CaseFormat;
import com.redmount.template.core.exception.AuthorizationException;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;

public class NameUtil {
    private static void validateConditionString(String condition) {
        // 单引号个数不匹配
        if (StringUtils.countMatches(condition, "'") % 2 != 0) {
            throw new AuthorizationException();
        }
        // 含注释
        if (StringUtils.contains(condition, "--")) {
            throw new AuthorizationException();
        }
    }

    public static String transToDBCondition(String condition) {
        validateConditionString(condition);
        StringBuilder stringBuilder = new StringBuilder();
        String[] arr = condition.split("'");
        if (arr.length == 1) {
            return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, condition);
        }
        for (int i = 0; i < arr.length; i++) {
            if (i % 2 == 0) {
                arr[i] = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, arr[i]);
                stringBuilder.append(arr[i]);
            } else {
                stringBuilder.append('\'');
                stringBuilder.append(arr[i]);
                stringBuilder.append('\'');
            }
        }
        return stringBuilder.toString();
    }

    /**
     * "file:/home/whf/cn/fh" -> "/home/whf/cn/fh"
     * "jar:file:/home/whf/foo.jar!cn/fh" -> "/home/whf/foo.jar"
     */
    public static String getRootPath(URL url) {
        String fileUrl = url.getFile();
        int pos = fileUrl.indexOf('!');

        if (-1 == pos) {
            return fileUrl;
        }

        return fileUrl.substring(5, pos);
    }

    /**
     * "cn.fh.lightning" -> "cn/fh/lightning"
     *
     * @param name 包全名(包含'.'的)
     * @return 路径全名(包含'/'的)
     */
    public static String dotToSplash(String name) {
        return name.replaceAll("\\.", "/");
    }

    /**
     * "Apple.class" -> "Apple"
     */
    public static String trimExtension(String name) {
        int pos = name.indexOf('.');
        if (-1 != pos) {
            return name.substring(0, pos);
        }

        return name;
    }
}
