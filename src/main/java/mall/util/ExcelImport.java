package mall.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sunnyzyq
 * @date 2021/12/17
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelImport {

    /** 字段名称 */
    String value();

    /** 导出映射，格式如：0-未知;1-男;2-女 */
    String kv() default "";

    /** 是否为必填字段（默认为非必填） */
    boolean required() default false;

    /** 是否为长数字字符串（默认为否） */
    boolean phone() default false;

    /** 是否为非科学计数法字符串（默认为否） */
    boolean notE() default false;

    /** 最大长度（默认255） */
    int maxLength() default 255;

    /** 最小值 */
    String min() default "";

    /** 导入唯一性验证（多个字段则取联合验证） */
    boolean unique() default false;

}