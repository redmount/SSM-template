package model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class ColumnComment {
    private String tableName;
    private String typeForDB;
    private String colName;
    private String isPrimaryKey;
    private String defaultValue;
    private String charLength;
    private String charCode;
    private String isNullable;

    private String colComment;
    private String modelName;
    private String fieldName;
    private String typeForJs;
    private String typeForJava;

    public static String getJsType(String typeForDB) {
        String[] typeOfString = {"VARCHAR", "CHAR", "TEXT"};
        String[] typeOfNumber = {"INT", "INTEGER", "TINYINT", "SMALLINT", "MEDIUMINT", "BIGINT", "FLOAT", "DOUBLE", "DECIMAL"};
        String[] typeOfDate = {"DATE", "TIME", "DATETIME", "TIMESTAMP", "YEAR"};
        String[] typeOfBool = {"BIT"};
        for (String str : typeOfString) {
            if (StringUtils.startsWithIgnoreCase(typeForDB, str)) {
                return "String";
            }
        }
        for (String str : typeOfNumber) {
            if (StringUtils.startsWithIgnoreCase(typeForDB, str)) {
                return "Number";
            }
        }
        for (String str : typeOfDate) {
            if (StringUtils.startsWithIgnoreCase(typeForDB, str)) {
                return "Date";
            }
        }
        for (String str : typeOfBool) {
            if (StringUtils.startsWithIgnoreCase(typeForDB, str)) {
                return "Boolean";
            }
        }
        return "Object/Unknown";
    }
}
