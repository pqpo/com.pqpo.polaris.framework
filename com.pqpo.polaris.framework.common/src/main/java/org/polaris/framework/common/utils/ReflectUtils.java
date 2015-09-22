package org.polaris.framework.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 类反射所使用的工具
 * 
 * @author wang.sheng
 */
public final class ReflectUtils
{
	static Log log = LogFactory.getLog(ReflectUtils.class);

	private ReflectUtils()
	{
	}

	/**
	 * 获取类下面的所有属性,包括父类
	 * 
	 * @param cl
	 * @return
	 */
	public static Field[] getAllFields(Class<?> cl)
	{
		return getFields(cl).toArray(new Field[0]);
	}

	/**
	 * 获取类下面所有的方法,包括父类
	 * 
	 * @param cl
	 * @return
	 */
	public static Method[] getAllMethods(Class<?> cl)
	{
		return getMethods(cl).toArray(new Method[0]);
	}

	/**
	 * 根据Field获取属性取值
	 * 
	 * @param object
	 * @param field
	 * @return
	 */
	public static Object getFieldValue(Object object, Field field)
	{
		if (object == null || field == null)
		{
			return null;
		}
		try
		{
			return FieldUtils.readField(field, object, true);
		}
		catch (IllegalAccessException e)
		{
			log.warn("getFieldValue failed! object:" + object + ",field:" + field.getName(), e);
			return null;
		}
	}

	/**
	 * 根据属性名获取属性取值
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	public static Object getFieldValue(Object object, String fieldName)
	{
		if (object == null || fieldName == null)
		{
			return null;
		}
		try
		{
			return FieldUtils.readField(object, fieldName, true);
		}
		catch (IllegalAccessException e)
		{
			log.warn("getFieldValue failed! object:" + object + ",fieldName:" + fieldName, e);
			return null;
		}
	}

	/**
	 * 根据Method实例获取属性名
	 * 
	 * @param method
	 * @return
	 */
	public static String getFieldNameByMethod(Method method)
	{
		if (method == null)
		{
			return null;
		}
		String fieldName = method.getName();
		if (fieldName.startsWith("set"))
		{
			char[] chs = fieldName.substring(3).toCharArray();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < chs.length; i++)
			{
				if (i == 0)
				{
					buffer.append(Character.toLowerCase(chs[i]));
				}
				else
				{
					buffer.append(chs[i]);
				}
			}
			fieldName = buffer.toString();
		}
		return fieldName;
	}

	private static List<Method> getMethods(Class<?> cl)
	{
		if (cl == Object.class)
		{
			return new ArrayList<Method>();
		}
		else
		{
			List<Method> list = new ArrayList<Method>();
			Method[] methods = cl.getDeclaredMethods();
			for (Method method : methods)
			{
				list.add(method);
			}
			list.addAll(getMethods(cl.getSuperclass()));// 递归调用
			return list;
		}
	}

	private static List<Field> getFields(Class<?> cl)
	{
		if (cl == Object.class)
		{
			return new ArrayList<Field>();
		}
		else
		{
			List<Field> list = new ArrayList<Field>();
			Field[] fields = cl.getDeclaredFields();
			for (Field field : fields)
			{
				list.add(field);
			}
			list.addAll(getFields(cl.getSuperclass()));// 递归调用
			return list;
		}
	}

	/**
	 * 获取类属性值
	 * @param obj
	 * @param field
	 * @param forceAccess
	 * @return
	 * @throws IllegalAccessException 
	 */
	public static Object getFieldValue(Object obj,Field field,boolean forceAccess) throws IllegalAccessException{
		if(obj==null){
			throw new IllegalArgumentException("Obj cannot be null");
		}
		if(field==null){
			throw new IllegalArgumentException("Field cannot be null");
		}
		if (forceAccess && !field.isAccessible()) {
			field.setAccessible(true);
		}
		return field.get(obj);
	}

}
