package org.polaris.framework.common.log;

import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Log4jUtils
{
	static Log stdoutLog = LogFactory.getLog("STDOUT");
	static Log stderrLog = LogFactory.getLog("STDERR");

	private Log4jUtils()
	{
	}

	private static PrintStream createPrintStreamProxy(PrintStream printStream, final Log log)
	{
		return new PrintStream(printStream)
		{
			public void println(boolean x)
			{
				log.info(Boolean.valueOf(x));
			}

			public void println(char x)
			{
				log.info(Character.valueOf(x));
			}

			public void println(char[] x)
			{
				log.info(x == null ? null : new String(x));
			}

			public void println(double x)
			{
				log.info(Double.valueOf(x));
			}

			public void println(float x)
			{
				log.info(Float.valueOf(x));
			}

			public void println(int x)
			{
				log.info(Integer.valueOf(x));
			}

			public void println(long x)
			{
				log.info(x);
			}

			public void println(Object x)
			{
				log.info(x);
			}

			public void println(String x)
			{
				log.info(x);
			}
		};
	}

	public static void initSystemOut()
	{
		PrintStream stdoutStream = createPrintStreamProxy(System.out, stdoutLog);
		System.setOut(stdoutStream);
		PrintStream stderrStream = createPrintStreamProxy(System.err, stderrLog);
		System.setErr(stderrStream);
	}
}
