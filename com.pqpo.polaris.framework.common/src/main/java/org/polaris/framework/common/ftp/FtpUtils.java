package org.polaris.framework.common.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

/**
 * FTP协议的工具类
 * 
 * @author wang.sheng
 * 
 */
final class FtpUtils
{
	static Log log = LogFactory.getLog(FtpUtils.class);

	private FtpUtils()
	{
	}

	/**
	 * 列出FTP文件列表
	 * 
	 * @param context
	 * @return
	 */
	public static String[] listFileNames(FtpContext context)
	{
		FTPClient ftpClient = null;
		try
		{
			ftpClient = openConnection(context);
			return ftpClient.listNames();
		}
		catch (IOException e)
		{
			log.error("listFileNames failed!", e);
			return new String[0];
		}
		finally
		{
			if (ftpClient != null)
			{
				try
				{
					ftpClient.logout();
					ftpClient.disconnect();
				}
				catch (IOException e)
				{
					log.warn("disconnect failed!", e);
				}
			}
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param context
	 * @param remoteFile
	 * @param destDir
	 */
	public static void download(FtpContext context, String remoteFile, File destDir)
	{
		if (destDir == null || !destDir.isDirectory())
		{
			throw new IllegalArgumentException("tempDir: " + destDir.getAbsolutePath() + " is not Directory!");
		}
		FTPClient ftpClient = null;
		OutputStream output = null;
		try
		{
			ftpClient = openConnection(context);
			File tempFile = new File(destDir, remoteFile + ".tmp");
			output = new FileOutputStream(tempFile);
			ftpClient.retrieveFile(remoteFile, output);
			tempFile.renameTo(new File(destDir, remoteFile));
		}
		catch (IOException e)
		{
			log.error("readFile failed!", e);
		}
		finally
		{
			IOUtils.closeQuietly(output);
			if (ftpClient != null)
			{
				try
				{
					ftpClient.logout();
					ftpClient.disconnect();
				}
				catch (IOException e)
				{
					log.warn("disconnect failed!", e);
				}
			}
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param context
	 * @param fileName
	 */
	public static void deleteFile(FtpContext context, String fileName)
	{
		FTPClient ftpClient = null;
		try
		{
			ftpClient = openConnection(context);
			ftpClient.deleteFile(fileName);
		}
		catch (IOException e)
		{
			log.error("readFile failed!", e);
		}
		finally
		{
			if (ftpClient != null)
			{
				try
				{
					ftpClient.logout();
					ftpClient.disconnect();
				}
				catch (IOException e)
				{
					log.warn("disconnect failed!", e);
				}
			}
		}
	}

	/**
	 * 打开FTP连接
	 * 
	 * @param context
	 * @return
	 * @throws IOException
	 */
	private static FTPClient openConnection(FtpContext context) throws IOException
	{
		FTPClient ftpClient = new FTPClient();
		FTPClientConfig ftpConfig = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
		ftpConfig.setServerLanguageCode(FTP.DEFAULT_CONTROL_ENCODING);
		ftpClient.configure(ftpConfig);
		ftpClient.connect(context.getIpAddress(), context.getPort());
		ftpClient.enterLocalPassiveMode();
		int reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply))
		{
			ftpClient.disconnect();
			throw new RuntimeException("login ftp " + context.getIpAddress() + ":" + context.getPort() + " failed!");
		}
		ftpClient.login(context.getUser(), context.getPassword());
		return ftpClient;
	}
}
