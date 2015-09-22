package org.polaris.framework.common.dao.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询属性
 * 
 * @author wang.sheng
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Query
{
	String value() default "";

	Compare compare() default Compare.Eq;
}
