package org.polaris.framework.common.security;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.util.Base64;

final class AESUtil
{
	private static int length = 128;

	static Log log = LogFactory.getLog(AESUtil.class);

	private static byte[] encryptBytes(String content, String password) throws Exception
	{

		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		secureRandom.setSeed(password.getBytes());
		kgen.init(length, secureRandom);
		SecretKey secretKey = kgen.generateKey();
		byte[] enCodeFormat = secretKey.getEncoded();
		SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
		Cipher cipher = Cipher.getInstance("AES");// 创建密码器
		byte[] byteContent = content.getBytes("utf-8");
		cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
		byte[] result = cipher.doFinal(byteContent);
		return result; // 加密

	}

	private static byte[] decryptBytes(byte[] content, String password) throws Exception
	{

		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		secureRandom.setSeed(password.getBytes());
		kgen.init(length, secureRandom);
		SecretKey secretKey = kgen.generateKey();
		byte[] enCodeFormat = secretKey.getEncoded();
		SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
		Cipher cipher = Cipher.getInstance("AES");// 创建密码器
		cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
		byte[] result = cipher.doFinal(content);
		return result; // 加密

	}

	public static String encrypt(String content, String password)
	{
		try
		{
			byte[] encryptResult = encryptBytes(content, password);
			return Base64.encodeBase64String(encryptResult);
		}
		catch (Exception e)
		{
			log.error("encrypt failed!", e);
			return null;
		}
	}

	public static String decrypt(String content, String password)
	{
		try
		{
			byte[] decryptResult = decryptBytes(Base64.decodeBase64(content), password);
			return new String(decryptResult, "UTF-8");
		}
		catch (Exception e)
		{
			log.error("decrypt failed!", e);
			return null;
		}

	}
}