package model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TableComment {
    private String tableName;
    private String modelName;
    private String tableComment;
    List<ColumnComment> columnCommentList = new ArrayList<>();
}
