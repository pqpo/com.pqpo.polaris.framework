package org.polaris.framework.common.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.helpers.LogLog;

public class MyRollingFileThread extends Thread
{
	private File[] files;
	private File zipFolder;

	public MyRollingFileThread(File[] files, File logFolder)
	{
		this.files = files;
		zipFolder = new File(logFolder, "zip");
		if (!zipFolder.exists())
		{
			zipFolder.mkdirs();
		}
	}

	@Override
	public void run()
	{
		String fileName = "polaris-" + DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd-HHmmss");
		File zipFile = new File(zipFolder, fileName + ".zip");
		OutputStream os = null;
		ZipOutputStream zos = null;
		InputStream is = null;
		try
		{
			os = new FileOutputStream(zipFile);
			zos = new ZipOutputStream(os);
			for (File file : files)
			{
				is = new FileInputStream(file);
				zos.putNextEntry(new ZipEntry(file.getName()));
				IOUtils.copy(is, zos);
				IOUtils.closeQuietly(is);
				file.delete();
			}
		}
		catch (Exception e)
		{
			LogLog.error("create zip file failed! fileName=" + fileName, e);
			IOUtils.closeQuietly(is);
		}
		finally
		{
			IOUtils.closeQuietly(zos);
			IOUtils.closeQuietly(os);
		}

	}
}
