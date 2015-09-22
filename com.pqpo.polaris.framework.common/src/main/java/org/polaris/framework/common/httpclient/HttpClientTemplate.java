package org.polaris.framework.common.httpclient;

import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

/**
 * HttpClient的封装类
 * 
 * @author wang.sheng
 * 
 */
@Service
public class HttpClientTemplate
{
	/**
	 * 创建Http客户端会话
	 * 
	 * @param charset
	 * @return
	 */
	public HttpClientSession create(String charset)
	{
		return new HttpClientSession(HttpClients.createDefault(), charset);
	}

}
