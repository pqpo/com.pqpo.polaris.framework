package org.polaris.framework.common.ftp;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FtpService
{
	Log log = LogFactory.getLog(getClass());

	private String ipAddress;
	private int port;
	private String user;
	private String password;
	private long period = 5000L;
	private String destPath;

	private Timer timer;

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param ipAddress
	 */
	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param port
	 */
	public void setPort(int port)
	{
		this.port = port;
	}

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param user
	 */
	public void setUser(String user)
	{
		this.user = user;
	}

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param password
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param period
	 */
	public void setPeriod(long period)
	{
		this.period = period;
	}

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param destPath
	 */
	public void setDestPath(String destPath)
	{
		this.destPath = destPath;
	}

	public void shutdown()
	{
		if (timer != null)
		{
			timer.cancel();
			timer = null;
		}
	}

	public void startup()
	{
		final FtpContext ftpContext = new FtpContext();
		ftpContext.setIpAddress(ipAddress);
		ftpContext.setPassword(password);
		ftpContext.setPort(port);
		ftpContext.setUser(user);
		final Set<String> fileLockSet = Collections.synchronizedSet(new HashSet<String>());
		final File destDir = new File(destPath);
		try
		{
			timer = new Timer();
			// 启动定时器用于监视FTP目录下的文件变化
			timer.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					log.info("TimerTask started!");
					String[] fileNames = FtpUtils.listFileNames(ftpContext);
					log.info("listFileNames length=" + fileNames.length);
					if (fileNames != null && fileNames.length > 0)
					{
						for (final String fileName : fileNames)
						{
							if (StringUtils.endsWith(fileName, ".csv"))
							{
								if (fileLockSet.contains(fileName))
								{
									// 该文件已经在分析中,故跳过
									continue;
								}
								fileLockSet.add(fileName);
								log.info("start download " + fileName);
								long t1 = System.currentTimeMillis();
								FtpUtils.download(ftpContext, fileName, destDir);
								FtpUtils.deleteFile(ftpContext, fileName);
								long t2 = System.currentTimeMillis();
								fileLockSet.remove(fileName);
								log.info("remote file " + fileName + " download successful! use time: " + (t2 - t1));
							}
							else
							{
								log.info("ignore FTP file: " + fileName);
							}
						}
					}
				}
			}, 5000L, period);
		}
		catch (Exception e)
		{
			log.error("openConnection failed! FTPStorageClient started failed!", e);
		}
	}

}
