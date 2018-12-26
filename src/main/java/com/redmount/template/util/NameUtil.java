package com.redmount.template.util;

import com.google.common.base.CaseFormat;
import com.redmount.template.core.ProjectConstant;

public class NameUtil {
    /**
     * 小驼峰/大驼峰格式转为下划线格式
     * adviserPk -> adviser_pk
     *
     * @param source
     * @return
     */
    public static String getDBFieldName(String source) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, source.replaceAll("Model", ""));
    }

    /**
     * 取Mapper类的全名
     *
     * @param source
     * @return
     */
    public static String getRepoClassName(String source) {
        return ProjectConstant.MAPPER_PACKAGE + "." + source + "Mapper";
    }

    public static String getShortClassName(String fullClassName) {
        return null;
    }
}
