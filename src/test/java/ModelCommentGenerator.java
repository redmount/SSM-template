import com.redmount.template.core.ProjectConstant;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import tk.mybatis.mapper.generator.MapperPlugin;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;


public class ModelCommentGenerator extends MapperPlugin {

    // 可序列化实体
    private boolean implementSerializableInterface;
    // 字段生成枚举值
    private boolean modelFieldEnum;
    // swagger注解
    private boolean swaggerApiEnabled;
    // columnType注解
    private boolean columnTypeEnabled;
    // setter链式调用
    private boolean setterMethodChainEnabled;
    // lombok注解
    private boolean lombokEnabled;
    // 是否开启Mybatis自身的二级缓存
    private boolean cacheMapper;

    public ModelCommentGenerator() {
        this.implementSerializableInterface = true;
        this.modelFieldEnum = false;
        this.swaggerApiEnabled = true;
        this.columnTypeEnabled = false;
        this.setterMethodChainEnabled = false;
        this.lombokEnabled = true;
        this.cacheMapper = true;
    }

    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String implementSerializableInterface = this.properties.getProperty("implementSerializableInterface");
        if (stringHasValue(implementSerializableInterface)) {
            this.implementSerializableInterface = Boolean.parseBoolean(implementSerializableInterface);
        }

        String modelFieldEnum = this.properties.getProperty("modelFieldEnum");
        if (stringHasValue(modelFieldEnum)) {
            this.modelFieldEnum = Boolean.parseBoolean(modelFieldEnum);
        }
        String swaggerApiEnabled = this.properties.getProperty("swaggerApiEnabled");
        if (stringHasValue(swaggerApiEnabled)) {
            this.swaggerApiEnabled = Boolean.parseBoolean(swaggerApiEnabled);
        }

        String columnTypeEnabled = this.properties.getProperty("columnTypeEnabled");
        if (stringHasValue(columnTypeEnabled)) {
            this.columnTypeEnabled = Boolean.parseBoolean(columnTypeEnabled);
        }

        String setterMethodChainEnabled = this.properties.getProperty("setterMethodChainEnabled");
        if (stringHasValue(setterMethodChainEnabled)) {
            this.setterMethodChainEnabled = Boolean.parseBoolean(setterMethodChainEnabled);
        }
        String lombokEnabled = this.properties.getProperty("lombokEnabled");
        if (stringHasValue(lombokEnabled)) {
            this.lombokEnabled = Boolean.parseBoolean(lombokEnabled);
        }

        String cacheMapper = this.properties.getProperty("cacheMapper");
        if (stringHasValue(cacheMapper)) {
            this.cacheMapper = Boolean.parseBoolean(cacheMapper);
        }
    }

    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        super.clientGenerated(interfaze, topLevelClass, introspectedTable);
        if (this.cacheMapper) {
            interfaze.addAnnotation("@CacheNamespace");
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.CacheNamespace"));
        }
        return true;
    }

    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
        impSerializableInterface(topLevelClass, introspectedTable);
        addFieldEnum(topLevelClass, introspectedTable);
        addClassComment(topLevelClass);
        swaggerApiAnnotation(topLevelClass, introspectedTable);
        columnTypeAnnotation(topLevelClass, introspectedTable);
        if (lombokEnabled) {
            topLevelClass.addImportedType("lombok.Data");
            topLevelClass.addAnnotation("@Data");
            if (setterMethodChainEnabled) {
                topLevelClass.addImportedType("lombok.experimental.Accessors");
                topLevelClass.addAnnotation("@Accessors(chain = true)");
            }
        }

        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        for (IntrospectedColumn column : allColumns) {
            if (ProjectConstant.LOGIC_DELETION_FIELD_NAME.equals(column.getActualColumnName())) {
                topLevelClass.addImportedType(ProjectConstant.BASE_PACKAGE + ".core.annotation.LogicDeletion");
                topLevelClass.addAnnotation("@LogicDeletion");
            }
        }
        // 基础Model开启RelationData注解
        topLevelClass.addImportedType(ProjectConstant.BASE_PACKAGE + ".core.annotation.RelationData");
        topLevelClass.addImportedType(ProjectConstant.MAPPER_PACKAGE + "." + topLevelClass.getType().getShortName() + "Mapper");
        topLevelClass.addAnnotation("@RelationData(baseDOClass = " + topLevelClass.getType().getShortName() + ".class, baseDOMapperClass = " + topLevelClass.getType().getShortName() + "Mapper.class)");
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * @author Mybatis Generator");
        // topLevelClass.addJavaDocLine(" * @date " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        topLevelClass.addJavaDocLine(" */");
        return true;
    }

    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        super.modelPrimaryKeyClassGenerated(topLevelClass, introspectedTable);
        impSerializableInterface(topLevelClass, introspectedTable);
        addFieldEnum(topLevelClass, introspectedTable);
        addClassComment(topLevelClass);
        swaggerApiAnnotation(topLevelClass, introspectedTable);
        columnTypeAnnotation(topLevelClass, introspectedTable);
        return true;
    }

    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass,
                                                      IntrospectedTable introspectedTable) {
        super.modelRecordWithBLOBsClassGenerated(topLevelClass, introspectedTable);
        impSerializableInterface(topLevelClass, introspectedTable);
        addFieldEnum(topLevelClass, introspectedTable);
        addClassComment(topLevelClass);
        swaggerApiAnnotation(topLevelClass, introspectedTable);
        columnTypeAnnotation(topLevelClass, introspectedTable);
        return false;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass,
                                              IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (lombokEnabled) {
            return false;
        }
        if (setterMethodChainEnabled) {
            method.setReturnType(new FullyQualifiedJavaType(topLevelClass.getType().getShortName()));
            method.addBodyLine("return this;");
        }
        return true;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass,
                                              IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {
        return !lombokEnabled;
    }

    private void impSerializableInterface(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (this.implementSerializableInterface) {
            String serializable = "java.io.Serializable";
            topLevelClass.addImportedType(serializable);
            FullyQualifiedJavaType superInterface = new FullyQualifiedJavaType(serializable);
            topLevelClass.addSuperInterface(superInterface);

            Field serialVersionUID = new Field("serialVersionUID", new FullyQualifiedJavaType("long"));
            serialVersionUID.setVisibility(JavaVisibility.PRIVATE);
            serialVersionUID.setStatic(true);
            serialVersionUID.setFinal(true);

            serialVersionUID.setInitializationString("1L");
            topLevelClass.addField(serialVersionUID);
        }
    }

    private void swaggerApiAnnotation(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Iterator i$;
        if (this.swaggerApiEnabled) {
            String apiModel = "io.swagger.annotations.ApiModel";
            String apiModelProperty = "io.swagger.annotations.ApiModelProperty";
            topLevelClass.addImportedType(apiModel);
            topLevelClass.addImportedType(apiModelProperty);
            topLevelClass.addImportedType(ProjectConstant.BASE_PACKAGE + ".core.annotation.Validate");
            String remarks = introspectedTable.getRemarks();
            if (StringUtil.isEmpty(remarks)) {
                remarks = "";
            } else {
                remarks = remarks.replaceAll("\n", " ");
                remarks = remarks.replaceAll("\r", " ");
                remarks = remarks.trim();
            }
            if (remarks.endsWith("表")) {
                remarks = remarks.substring(0, remarks.length() - 1) + "对象";
            }
            remarks = topLevelClass.getType().getShortName() + "（" + remarks + "）";
            topLevelClass.addAnnotation("@ApiModel(\"" + remarks + "\")");
            List<Field> fields = topLevelClass.getFields();
            for (i$ = fields.iterator(); i$.hasNext(); ) {
                Field field = (Field) i$.next();
                List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
                for (IntrospectedColumn introspectedColumn : allColumns)
                    if (field.getName().equals(introspectedColumn.getJavaProperty())) {
                        String remark = introspectedColumn.getRemarks();
                        if (StringUtil.isEmpty(remark)) {
                            remark = "";
                        } else {
                            remark = remark.replaceAll("\n", " ");
                            remark = remark.replaceAll("\r", " ");
                            remark = remark.trim();
                        }
                        field.addAnnotation("@ApiModelProperty(value = \"" + remark + "\")");
                        /// 验证相关注解
                        List<String> validateAnnoatationValues = new ArrayList<>();
                        if (!introspectedColumn.isNullable()) {
                            validateAnnoatationValues.add("nullable = false");
                            if ("java.lang.String".equals(field.getType().getFullyQualifiedName())) {
                                validateAnnoatationValues.add("stringMaxLength = " + introspectedColumn.getLength());
                            }
                        }
                        if (validateAnnoatationValues.size() > 0) {
                            field.addAnnotation("@Validate(" + StringUtils.join(validateAnnoatationValues.toArray(), ", ") + ")");
                        }
                    }
            }
        }
    }

    private void columnTypeAnnotation(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (this.columnTypeEnabled) {
            String columnType = "tk.mybatis.mapper.annotation.ColumnType";
            String jdbcType = "org.apache.ibatis.type.JdbcType";
            topLevelClass.addImportedType(columnType);
            topLevelClass.addImportedType(jdbcType);
            List<Field> fields = topLevelClass.getFields();
            for (Field field : fields) {
                List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
                for (IntrospectedColumn introspectedColumn : allColumns)
                    if (field.getName().equals(introspectedColumn.getJavaProperty())) {
                        String jdbcTypeName = introspectedColumn.getJdbcTypeName();
                        if (StringUtil.isEmpty(jdbcTypeName)) {
                            continue;
                        } else {
                            jdbcTypeName = "JdbcType." + jdbcTypeName;
                        }
                        field.addAnnotation("@ColumnType(jdbcType = " + jdbcTypeName + ")");
                    }
            }
        }
    }

    private void addClassComment(TopLevelClass topLevelClass) {
        topLevelClass.addFormattedJavadoc(new StringBuilder("实体类请不要修改，如有改变请从数据库重新生成"), 0);
    }

    private void addFieldEnum(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (this.modelFieldEnum) {
            String enumName = "FieldEnum";
            String javaFieldName = "javaFieldName";
            String dbFieldName = "dbFieldName";
            InnerEnum enum1 = new InnerEnum(new FullyQualifiedJavaType(enumName));
            enum1.setVisibility(JavaVisibility.PUBLIC);

            StringBuilder enumConstant = new StringBuilder();
            List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
            int index = 0;
            for (IntrospectedColumn column : allColumns) {
                String dbName = column.getActualColumnName();
                String javaName = column.getJavaProperty();
                enumConstant.append(dbName.toUpperCase()).append("(\"").append(javaName).append("\",\"").append(dbName).append("\")");
                if (++index < allColumns.size()) {
                    enumConstant.append(",\n\t\t");
                }
            }
            enum1.addEnumConstant(enumConstant.toString());

            //java字段
            Field field = new Field();
            field.setVisibility(JavaVisibility.PRIVATE);
            field.setStatic(false);
            field.setType(new FullyQualifiedJavaType("String"));
            field.setName(javaFieldName);
            enum1.addField(field);
            //db字段
            Field field1 = new Field();
            field1.setVisibility(JavaVisibility.PRIVATE);
            field1.setStatic(false);
            field1.setType(new FullyQualifiedJavaType("String"));
            field1.setName(dbFieldName);
            enum1.addField(field1);

            //构造器
            Method method = new Method();
            method.setConstructor(true);
            method.setVisibility(JavaVisibility.DEFAULT);
            method.setStatic(false);
            method.setName(enumName);
            method.addParameter(new Parameter(new FullyQualifiedJavaType("String"), javaFieldName));
            method.addParameter(new Parameter(new FullyQualifiedJavaType("String"), dbFieldName));
            method.addBodyLine("this." + javaFieldName + " = " + javaFieldName + ";");
            method.addBodyLine("this." + dbFieldName + " = " + dbFieldName + ";");
            enum1.addMethod(method);


            //方法
            Method getMethod = new Method();
            getMethod.setConstructor(false);
            getMethod.setVisibility(JavaVisibility.PUBLIC);
            getMethod.setStatic(false);
            getMethod.setName(javaFieldName);
            getMethod.addBodyLine("return " + javaFieldName + ";");
            getMethod.setReturnType(new FullyQualifiedJavaType("String"));
            enum1.addMethod(getMethod);

            Method getMethod1 = new Method();
            getMethod1.setConstructor(false);
            getMethod1.setVisibility(JavaVisibility.PUBLIC);
            getMethod1.setStatic(false);
            getMethod1.setName(dbFieldName);
            getMethod1.addBodyLine("return " + dbFieldName + ";");
            getMethod1.setReturnType(new FullyQualifiedJavaType("String"));
            enum1.addMethod(getMethod1);

            topLevelClass.addInnerEnum(enum1);
        }
    }

    public String getDelimiterName(String name) {
        if (name.indexOf(".") > 0) {
            name = name.substring(name.indexOf(".") + 1);
        }
        String beginningDelimiter = this.properties.getProperty("beginningDelimiter");
        String endingDelimiter = this.properties.getProperty("endingDelimiter");
        StringBuilder nameBuilder = new StringBuilder();
        if (!StringUtil.isEmpty(beginningDelimiter)) {
            nameBuilder.append(beginningDelimiter);
        }
        nameBuilder.append(name);
        if (!StringUtil.isEmpty(endingDelimiter)) {
            nameBuilder.append(endingDelimiter);
        }
        return nameBuilder.toString();
    }

    public static void main(String[] args) {
    }
}
