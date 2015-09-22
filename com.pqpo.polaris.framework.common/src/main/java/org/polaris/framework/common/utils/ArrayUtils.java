package org.polaris.framework.common.utils;

import java.lang.reflect.Array;

/**
 * 数组工具类
 * 
 * @author wang.sheng
 * 
 */
public final class ArrayUtils
{
	private ArrayUtils()
	{
	}

	/**
	 * 根据指定数量和长度构建数组对象
	 * 
	 * @param clazz
	 * @param length
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] newArrayInstance(Class<T> clazz, int length)
	{
		return (T[]) Array.newInstance(clazz, length);
	}
}
