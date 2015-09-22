package org.polaris.framework.common.rest;

/**
 * 表单数据异常
 * 
 * @author wang.sheng
 * 
 */
public class FormException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public FormException(String msg)
	{
		super(msg);
	}

	public FormException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
}
