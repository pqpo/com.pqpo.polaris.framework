package org.polaris.framework.common.httpclient;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * HTTP客户端的会话
 * 
 * @author wang.sheng
 * 
 */
public class HttpClientSession implements Closeable
{
	public static final int SOCKET_TIMEOUT = 20000;
	public static final int CONNECT_TIMEOUT = 20000;

	private CloseableHttpClient httpClient;
	private String charset = "UTF-8";
	private final Map<String, String> headerMap = Collections.synchronizedMap(new HashMap<String, String>());

	protected HttpClientSession(CloseableHttpClient httpClient, String charset)
	{
		if (httpClient == null)
		{
			throw new IllegalArgumentException("httpClient cannot null!");
		}
		if (!StringUtils.isBlank(charset))
		{
			this.charset = charset;
		}
		this.httpClient = httpClient;
	}

	/**
	 * 获取头参数
	 * 
	 * @param headerMap
	 */
	public Map<String, String> getHeaderMap()
	{
		return headerMap;
	}

	/**
	 * 向指定URL发送同步GET请求
	 * 
	 * @param url
	 * @param content
	 * @return
	 */
	public String asyncGet(String url) throws HttpClientException
	{
		return asyncGet(url, null);
	}

	/**
	 * 向指定URL发送同步GET请求
	 * 
	 * @param url
	 * @param proxy
	 * @return
	 * @throws HttpClientException
	 */
	public String asyncGet(String url, Proxy proxy) throws HttpClientException
	{
		CloseableHttpResponse response = null;
		try
		{
			HttpGet httpGet = new HttpGet(url);
			Builder builder = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT);
			if (proxy != null)
			{
				// 设置http代理
				HttpHost httpHost = new HttpHost(proxy.getIp(), proxy.getPort());
				builder.setProxy(httpHost);
			}
			httpGet.setConfig(builder.build());
			for (Map.Entry<String, String> entry : headerMap.entrySet())
			{
				httpGet.addHeader(entry.getKey(), entry.getValue());
			}
			response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity, charset);
		}
		catch (Exception e)
		{
			throw new HttpClientException("asyncGet failed!", e);
		}
		finally
		{
			IOUtils.closeQuietly(response);
		}
	}

	/**
	 * 向指定URL发送同步POST请求
	 * 
	 * @param url
	 * @param content
	 * @return
	 * @throws HttpClientException
	 */
	public String asyncPost(String url, String content) throws HttpClientException
	{
		return asyncPost(url, null, content);
	}

	/**
	 * 向指定URL发送同步POST请求
	 * 
	 * @param url
	 * @param proxy
	 * @param content
	 * @return
	 * @throws HttpClientException
	 */
	public String asyncPost(String url, Proxy proxy, String content) throws HttpClientException
	{
		CloseableHttpResponse response = null;
		try
		{
			HttpPost httpPost = new HttpPost(url);
			Builder builder = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT);
			if (proxy != null)
			{
				// 设置http代理
				HttpHost httpHost = new HttpHost(proxy.getIp(), proxy.getPort());
				builder.setProxy(httpHost);
			}
			httpPost.setConfig(builder.build());
			for (Map.Entry<String, String> entry : headerMap.entrySet())
			{
				httpPost.addHeader(entry.getKey(), entry.getValue());
			}
			httpPost.setEntity(new StringEntity(content, charset));
			response = httpClient.execute(httpPost);
			return EntityUtils.toString(response.getEntity(), charset);
		}
		catch (Exception e)
		{
			throw new HttpClientException("asyncPost failed!", e);
		}
		finally
		{
			IOUtils.closeQuietly(response);
		}
	}

	@Override
	public void close() throws IOException
	{
		IOUtils.closeQuietly(httpClient);
	}
}
