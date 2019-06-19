package dev.sqlite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Column 字段配置 数据字段注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface Column {
    /**
     * 设置字段名
     *
     * @return name
     */
    String name() default "";

    /**
     * 字段默认值
     *
     * @return value
     */
    String defaultValue() default "";
}
