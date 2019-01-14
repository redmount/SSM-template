package com.redmount.template.util;

import com.google.common.base.CaseFormat;
import com.redmount.template.core.ProjectConstant;

import java.util.List;

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

    public static List<String> getRetain(List<String>... strList) {
        List<String> retList = strList[0];
        for (List<String> currentList : strList) {
            retList.retainAll(currentList);
        }
        return retList;
    }

    public static String transToDBCondition(String condition) {
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
}
