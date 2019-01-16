import com.google.common.base.CaseFormat;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentGenerator {
    //JDBC配置，请修改为你项目的实际配置
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/%s?serverTimezone=UTC";
    private static final String JDBC_USERNAME = "root";
    private static final String JDBC_PASSWORD = "root";
    private static final String DB_NAME = "test";
    private static final String JDBC_DIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";

    private static final String PROJECT_PATH = System.getProperty("user.dir"); //项目在硬盘上的基础路径

    private static final String TABLE_QUERY = "SELECT table_name,table_COMMENT FROM information_schema.TABLES WHERE table_schema = '%s'; ";
    private static final String COLUMN_QUERY = "SELECT col.TABLE_NAME,col.COLUMN_NAME,col.COLUMN_TYPE,col.COLUMN_COMMENT FROM information_schema.`COLUMNS` AS col WHERE col.TABLE_SCHEMA = '%s';";

    public static void main(String[] args) {
        Connection connection = null;
        List<TableComment> tableCommentList = new ArrayList<>();
        List<ColumnComment> columnCommentList = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(String.format(JDBC_URL, DB_NAME), JDBC_USERNAME, JDBC_PASSWORD);
            tableCommentList = getTables(connection);
            columnCommentList = getColumns(connection);
            System.out.println(tableCommentList.size());
            System.out.println(columnCommentList.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String line = "\r\n";
        StringBuilder stringBuilder = new StringBuilder("# 基本实体说明" + line);
        stringBuilder.append("共有表格" + tableCommentList.size() + "张" + line);
        for (TableComment tableComment : tableCommentList) {
            stringBuilder.append(line);

            stringBuilder.append("-------------------------------------------------" + line);
            stringBuilder.append("## " + tableComment.getTableName() + " : " + tableComment.getTableComment() + line);
            stringBuilder.append(line);
            stringBuilder.append("|属性名|JavaScript类型|说明|" + line);
            stringBuilder.append("|-----|----|----|" + line);
            for (ColumnComment columnComment : columnCommentList) {
                if (tableComment.getTableName().equals(columnComment.getTableName())) {
                    stringBuilder.append(columnComment.toString() + line);
                }
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
                tableComment.setTableName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, rs.getString("table_name")));
                tableComment.setTableComment(rs.getString("table_COMMENT"));
                tableCommentList.add(tableComment);
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
                if (StringUtils.endsWithIgnoreCase(rs.getString("COLUMN_NAME"), "pk")) {
                    continue;
                }
                if (rs.getString("COLUMN_NAME").equalsIgnoreCase("updated") ||
                        rs.getString("COLUMN_NAME").equalsIgnoreCase("created")) {
                    continue;
                }
                columnComment = new ColumnComment();
                // 获取每列的数据,使用的是ResultSet接口的方法getXXX
                columnComment.setTableName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, rs.getString("table_name")));
                columnComment.setColName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, rs.getString("COLUMN_NAME")));
                columnComment.setType(rs.getString("COLUMN_TYPE"));
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
    private String tableComment;
    List<ColumnComment> columnCommentList = new ArrayList<>();
}

@Data
class ColumnComment {
    private String tableName;
    private String type;
    private String colName;
    private String colComment;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('|' + colName);
        stringBuilder.append('|' + getJsType(type));
        stringBuilder.append('|' + colComment);
        stringBuilder.append('|');
        return stringBuilder.toString();

    }

    private static String getJsType(String type) {
        String[] typeOfString = {"VARCHAR", "CHAR", "TEXT"};
        String[] typeOfNumber = {"INT", "INTEGER", "TINYINT", "SMALLINT", "MEDIUMINT", "BIGINT", "FLOAT", "DOUBLE", "DECIMAL"};
        String[] typeOfDate = {"DATE", "TIME", "DATETIME", "TIMESTAMP", "YEAR"};
        String[] typeOfBool = {"BIT"};
        for (String str : typeOfString) {
            if (StringUtils.startsWithIgnoreCase(type, str)) {
                return "String";
            }
        }
        for (String str : typeOfNumber) {
            if (StringUtils.startsWithIgnoreCase(type, str)) {
                return "Number";
            }
        }
        for (String str : typeOfDate) {
            if (StringUtils.startsWithIgnoreCase(type, str)) {
                return "Date";
            }
        }
        for (String str : typeOfBool) {
            if (StringUtils.startsWithIgnoreCase(type, str)) {
                return "Boolean";
            }
        }
        return "Object/Unknown";
    }
}