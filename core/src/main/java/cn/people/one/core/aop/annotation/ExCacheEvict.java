package cn.people.one.core.aop.annotation;

import java.lang.annotation.*;

/**
 * Created by lml on 2017/6/5.
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExCacheEvict {
    String key() default "";
    String[] value() default {};
}
