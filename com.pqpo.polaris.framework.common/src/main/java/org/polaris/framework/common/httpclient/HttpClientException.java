package org.polaris.framework.common.httpclient;

public class HttpClientException extends Exception
{
	private static final long serialVersionUID = 1L;

	public HttpClientException(String msg, Throwable e)
	{
		super(msg, e);
	}
}
