package overrides;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.internal.DefaultCommentGenerator;

import org.mybatis.generator.api.dom.java.Field;

public class MyBatisGenerator extends DefaultCommentGenerator {
    private boolean suppressAllComments;

    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        field.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(introspectedColumn.getRemarks());
        field.addJavaDocLine(sb.toString());
        field.addJavaDocLine(" */");

        field.addAnnotation("@Comment(value=\"" + introspectedColumn.getRemarks() + "\")");
    }
}
