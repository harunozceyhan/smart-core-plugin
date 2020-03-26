package com.smart.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Metadata {
    String value() default "";

    String title() default "";

    String detailTitleKey() default "";

    String baseUrl() default "";

    String getUrl() default "";

    String responseKey() default "";

    boolean factory() default false;

}