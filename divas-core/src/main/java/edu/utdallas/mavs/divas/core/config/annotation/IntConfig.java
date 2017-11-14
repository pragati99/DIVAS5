package edu.utdallas.mavs.divas.core.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntConfig
{
    boolean combo() default false;

    int min() default Integer.MIN_VALUE;

    int max() default Integer.MAX_VALUE;

    int step() default 1;

    int[] values() default {};
}
