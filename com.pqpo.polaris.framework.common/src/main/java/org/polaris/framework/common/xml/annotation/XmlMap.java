package org.polaris.framework.common.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XmlMap
{
	public String name();

	public String key();

	public Class<?> keyClass() default String.class;

	public Class<?> mapClass() default HashMap.class;

	public Class<?> valueClass();

	public boolean reduplicatable() default false;
}
