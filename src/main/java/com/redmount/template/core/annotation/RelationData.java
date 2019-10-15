package com.redmount.template.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示此字段是否为关系数据容器
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RelationData {
    // String baseDOTypeName() default "";

    Class baseDOClass() default Object.class;

    Class baseDOMapperClass() default Object.class;

    String relationDOTypeName() default "";

    String mainProperty() default "";

    String foreignProperty() default "";

    boolean isOneToMany() default false;

    boolean isRelation() default false;

    boolean isManyToMany() default false;
}
