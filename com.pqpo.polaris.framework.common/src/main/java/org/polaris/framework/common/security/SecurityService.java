package org.polaris.framework.common.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

/**
 * 安全服务
 * 
 * @author wang.sheng
 * 
 */
@Service
public class SecurityService
{
	Log log = LogFactory.getLog(getClass());

	private static final String KEY = "org.polaris.framework";

	/**
	 * 将明文加密为密文
	 * 
	 * @param value
	 * @return
	 */
	public String encrypt(String value)
	{
		return AESUtil.encrypt(value, KEY);
	}

	/**
	 * 将密文转换为明文
	 * 
	 * @param value
	 * @return
	 */
	public String decrypt(String value)
	{
		return AESUtil.decrypt(value, KEY);
	}
}
