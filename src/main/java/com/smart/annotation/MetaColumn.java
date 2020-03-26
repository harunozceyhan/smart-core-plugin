package com.smart.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MetaColumn {
    String type() default ""; // default

    String formType() default ""; // default

    String text() default ""; // default

    String value() default ""; // default

    String metadata() default ""; // custom

    String contextPath() default ""; // custom

    String url() default ""; // custom

    String responseKey() default ""; // custom

    String itemText() default ""; // custom

    String tableValue() default ""; // default

    String filterBy() default ""; // default

    boolean required() default false; // default

    boolean sortable() default false; // custom

    boolean searchable() default false; // custom

    boolean updatable() default true; // default

    String searchKey() default ""; // default

    boolean showInTable() default false; // custom

    boolean showInForm() default true; // custom

    boolean factory() default false;

    int width() default 20; // custom

    int min() default 1; // default

    int max() default 25; // default
}