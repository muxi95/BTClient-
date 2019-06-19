package dev.sqlite.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * PrimaryKey 主键配置 不配置的时候默认找类的id或_id字段作为主键，column不配置的是默认为字段名，如果不设置主键，自动生成ID
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface PrimaryKey {
    /**
     * 设置主键名
     *
     * @return 主键名
     */
    String name() default "";

    /**
     * 字段默认值
     *
     * @return 默认值
     */
    String defaultValue() default "";

    /**
     * 是否自动自增
     *
     * @return Boolean
     */
    boolean autoIncrement() default false;
}