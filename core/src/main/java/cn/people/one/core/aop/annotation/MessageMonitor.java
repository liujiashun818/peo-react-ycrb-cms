package cn.people.one.core.aop.annotation;

import cn.people.one.core.base.model.MessageType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lml on 2016/12/28.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageMonitor {

    String name() default "";//消息名称
    MessageType type(); //消息类型
}
