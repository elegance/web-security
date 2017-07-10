package org.orh.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD}) // 类、方法均可使用
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {

}
