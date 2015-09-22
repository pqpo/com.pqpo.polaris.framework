package org.polaris.framework.common.log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;

public class MyRollingFileAppender extends FileAppender
{
	protected long maxFileSize;
	protected int maxBackupIndex;
	private long nextRollover;
	private static int count = 0;
	private static boolean[] syncObject = new boolean[0];
	private static String firstFileName = null;
	private boolean first = true;
	private static String oldFileName = null;
	private static File logFolder;

	public MyRollingFileAppender()
	{
		maxFileSize = 0xa00000L;
		maxBackupIndex = 1;
		nextRollover = 0L;
	}

	@Override
	public void setFile(String file)
	{
		super.setFile(file);
		if (oldFileName == null)
		{
			oldFileName = file;
			logFolder = new File(oldFileName).getParentFile();
			File[] logFiles = getLogFiles(logFolder);
			if (logFiles != null && logFiles.length > 0)
			{
				count = logFiles.length;
			}
			checkBackup();
			firstFileName = newLogFileName();
			System.out.println("log file:" + firstFileName + ", log files count:" + count);
		}
	}

	private static File[] getLogFiles(File logFolder)
	{
		File[] children = logFolder.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return name.startsWith("sirius");
			}
		});
		return children;
	}

	private void checkBackup()
	{
		if (count >= maxBackupIndex)
		{
			count = 0;
			MyRollingFileThread thread = new MyRollingFileThread(getLogFiles(logFolder), logFolder);
			thread.start();
		}
		count++;
	}

	private String newLogFileName()
	{
		String result = DateFormatUtils.format(System.currentTimeMillis(), "-yyyyMMdd-HHmmss");
		if (oldFileName.endsWith(".log"))
		{
			result = oldFileName.substring(0, oldFileName.length() - 4) + result + "-" + count + ".log";
			return result;
		}
		else
		{
			return oldFileName + result + "-" + count + ".log";
		}
	}

	public int getMaxBackupIndex()
	{
		return maxBackupIndex;
	}

	public long getMaximumFileSize()
	{
		return maxFileSize;
	}

	public void rollOver()
	{
		if (super.qw != null)
		{
			long size = ((CountingQuietWriter) super.qw).getCount();
			LogLog.debug("rolling over count=" + size);
			nextRollover = size + maxFileSize;
		}
		checkBackup();
		try
		{
			setFile(super.fileName, false, super.bufferedIO, super.bufferSize);
			nextRollover = 0L;
		}
		catch (IOException e)
		{
			LogLog.error("setFile(" + super.fileName + ", false) call failed.", e);
		}
	}

	@Override
	public void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize) throws IOException
	{
		synchronized (syncObject)
		{
			if (first)
			{
				fileName = firstFileName;
				first = false;
			}
			else
			{
				fileName = this.newLogFileName();
			}
			super.setFile(fileName, append, super.bufferedIO, super.bufferSize);
			if (append)
			{
				File f = new File(fileName);
				((CountingQuietWriter) super.qw).setCount(f.length());
			}
		}
	}

	public void setMaxBackupIndex(int maxBackups)
	{
		maxBackupIndex = maxBackups;
	}

	public void setMaximumFileSize(long maxFileSize)
	{
		this.maxFileSize = maxFileSize;
	}

	public void setMaxFileSize(String value)
	{
		maxFileSize = OptionConverter.toFileSize(value, maxFileSize + 1L);
	}

	protected void setQWForFiles(Writer writer)
	{
		super.qw = new CountingQuietWriter(writer, super.errorHandler);
	}

	protected void subAppend(LoggingEvent event)
	{
		super.subAppend(event);
		if (super.fileName != null && super.qw != null)
		{
			long size = ((CountingQuietWriter) super.qw).getCount();
			if (size >= maxFileSize && size >= nextRollover)
			{
				rollOver();
			}
		}
	}

}
