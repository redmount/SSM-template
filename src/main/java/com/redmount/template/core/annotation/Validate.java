package com.redmount.template.core.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Validate {
    boolean nullable() default true;

    int exceptionCode() default 990002;

    int stringMaxLength() default 255;

    String stringRegex() default "";
}
