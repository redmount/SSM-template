import com.google.common.base.CaseFormat;
import com.redmount.template.core.ProjectConstant;
import com.redmount.template.util.ReflectUtil;
import lombok.Data;
import lombok.var;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocumentGenerator {
    //JDBC配置，请修改为你项目的实际配置
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/%s?serverTimezone=UTC";
    private static final String JDBC_USERNAME = "root";
    private static final String JDBC_PASSWORD = "root";
    private static final String DB_NAME = "test";
    private static final String JDBC_DIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";

    private static final String PROJECT_PATH = System.getProperty("user.dir"); //项目在硬盘上的基础路径

    private static final String TABLE_QUERY = "SELECT table_name,table_COMMENT FROM information_schema.TABLES WHERE table_schema = '%s' ORDER BY table_name; ";
    private static final String COLUMN_QUERY = "SELECT col.TABLE_NAME,col.COLUMN_NAME,col.COLUMN_TYPE,col.COLUMN_COMMENT FROM information_schema.`COLUMNS` AS col WHERE col.TABLE_SCHEMA = '%s' order by col.TABLE_NAME, col.ORDINAL_POSITION;";
    private static final String line = "\r\n";
    private static final String tab = "    ";
    private static final String tab2 = "        ";
    private static final String DATE = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());//@date

    public static void main(String[] args) {
        Connection connection = getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
        List<TableComment> tableCommentList = getTables(connection);
        genDBDocument(tableCommentList);
        genBaseModelJsCode(tableCommentList);
    }

    private static void genBaseModelJsCode(List<TableComment> tableCommentList) {
        var scan = new ClasspathPackageScanner(ProjectConstant.MODEL_PACKAGE);
        List<String> classNameList = scan.getFullyQualifiedClassNameList();
        genBaseModelToJsCode(classNameList, tableCommentList);
    }

    private static void genBaseModelToJsCode(List<String> classNameList, List<TableComment> tableCommentList) {
        Class cls;
        StringBuilder sb = new StringBuilder();
        sb.append("/// Generated on " + DATE + line);
        sb.append(line);
        sb.append("const baseModel = {" + line);
        try {
            for (String className : classNameList) {
                cls = Class.forName(className);
                List<Field> fieldList = ReflectUtil.getFieldList(cls);
                for (TableComment tableComment : tableCommentList) {
                    if (tableComment.getModelName().equals(cls.getSimpleName())) {
                        sb.append(tab + cls.getSimpleName() + "(args) {" + line);
                        sb.append(tab2 + "const arg = args || {}" + line);
                        for (ColumnComment columnComment : tableComment.getColumnCommentList()) {
                            for (Field field : fieldList) {
                                if (field.getName().equals(columnComment.getFieldName())) {
                                    sb.append(tab2 + "this." + field.getName() + " = arg." + field.getName() + " || null  ///" + columnComment.getColComment() + line);
                                }
                            }
                        }
                        sb.append(tab + "}," + line);
                    }
                }
            }
            sb.append("}");
            File file = new File(PROJECT_PATH + "/baseModel.js");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileWriter fw = new FileWriter(file);
            fw.write(sb.toString());//将字符串写入到指定的路径下的文件中
            fw.close();
        } catch (Exception ex) {

        }
        System.out.println(sb.toString());
    }

    private static void genJsDocument(List<TableComment> tableCommentList) {

    }

    private static void genDBDocument(List<TableComment> tableCommentList) {

        StringBuilder stringBuilder = new StringBuilder("# 基本实体说明" + line);
        stringBuilder.append("生成日期:" + DATE + line);
        stringBuilder.append("共有表格" + tableCommentList.size() + "张" + line);
        stringBuilder.append("# 表说明" + line);
        stringBuilder.append(line);
        stringBuilder.append("|表名|说明|" + line);
        stringBuilder.append("|----|----|" + line);
        for (TableComment tableComment : tableCommentList) {
            stringBuilder.append('|');
            stringBuilder.append(tableComment.getTableName());
            stringBuilder.append('|');
            stringBuilder.append(tableComment.getTableComment());
            stringBuilder.append('|');
            stringBuilder.append(line);
        }

        stringBuilder.append("# 字段说明" + line);

        for (TableComment tableComment : tableCommentList) {
            stringBuilder.append(line);
            stringBuilder.append("-------------------------------------------------" + line);
            stringBuilder.append("## " + tableComment.getTableName() + " : " + tableComment.getTableComment() + line);
            stringBuilder.append(line);
            stringBuilder.append("|属性名|类型|说明|" + line);
            stringBuilder.append("|-----|----|----|" + line);
            for (ColumnComment columnComment : tableComment.getColumnCommentList()) {
                stringBuilder.append('|');
                stringBuilder.append(columnComment.getColName());
                stringBuilder.append('|');
                stringBuilder.append(columnComment.getTypeForDB());
                stringBuilder.append('|');
                stringBuilder.append(columnComment.getColComment());
                stringBuilder.append('|' + line);
            }
        }
        File file = new File(PROJECT_PATH + "/数据库说明文档.md");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileWriter fw;
        try {
            fw = new FileWriter(file);
            fw.write(stringBuilder.toString());//将字符串写入到指定的路径下的文件中
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection(String connectionString, String userName, String password) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(String.format(JDBC_URL, DB_NAME), JDBC_USERNAME, JDBC_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private static List<TableComment> getTables(Connection connection) {
        TableComment tableComment;
        List<TableComment> tableCommentList = new ArrayList<>();
        try {
            connection.setAutoCommit(false);
            PreparedStatement pstmt = connection.prepareStatement(String.format(TABLE_QUERY, DB_NAME));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tableComment = new TableComment();
                // 获取每列的数据,使用的是ResultSet接口的方法getXXX
                tableComment.setTableName(rs.getString("table_name"));
                tableComment.setModelName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, rs.getString("table_name")));
                tableComment.setTableComment(rs.getString("table_COMMENT"));
                tableCommentList.add(tableComment);
            }
            List<ColumnComment> columnCommentList = getColumns(connection);
            for (TableComment item : tableCommentList) {
                item.setColumnCommentList(new ArrayList<>());
                for (ColumnComment columnComment : columnCommentList) {
                    if (item.getTableName().equalsIgnoreCase(columnComment.getTableName())) {
                        item.getColumnCommentList().add(columnComment);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableCommentList;
    }

    private static List<ColumnComment> getColumns(Connection connection) {
        ColumnComment columnComment;
        List<ColumnComment> columnCommentList = new ArrayList<>();
        try {
            connection.setAutoCommit(false);
            PreparedStatement pstmt = connection.prepareStatement(String.format(COLUMN_QUERY, DB_NAME));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                columnComment = new ColumnComment();
                // 获取每列的数据,使用的是ResultSet接口的方法getXXX
                columnComment.setTableName(rs.getString("table_name"));
                columnComment.setModelName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, rs.getString("table_name")));
                columnComment.setColName(rs.getString("COLUMN_NAME"));
                columnComment.setFieldName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, rs.getString("COLUMN_NAME")));
                columnComment.setTypeForDB(rs.getString("COLUMN_TYPE"));
                columnComment.setColComment(rs.getString("COLUMN_COMMENT"));
                columnCommentList.add(columnComment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columnCommentList;
    }
}

@Data
class TableComment {
    private String tableName;
    private String modelName;
    private String tableComment;
    List<ColumnComment> columnCommentList = new ArrayList<>();
}

@Data
class ColumnComment {
    private String tableName;
    private String modelName;
    private String typeForJs;
    private String typeForJava;
    private String typeForDB;
    private String colName;
    private String fieldName;
    private String colComment;

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