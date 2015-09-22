package org.polaris.framework.common.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

//import org.springframework.stereotype.Service;

/**
 * 控制台服务
 * 
 * @author wang.sheng
 * 
 */
// @Service
public class ConsoleService
{
	Log log = LogFactory.getLog(getClass());

	@Resource
	private ApplicationContext applicationContext;
	private final List<ConsoleSupportWrapper> wrapperList = new ArrayList<ConsoleSupportWrapper>();

	@PostConstruct
	protected void initService()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				start();
			}

		}, "ConsoleService").start();
	}

	private void start()
	{
		log.info("loading Console service...");
		Map<String, ConsoleSupport> beanMap = applicationContext.getBeansOfType(ConsoleSupport.class);
		StringBuffer buffer = new StringBuffer();
		for (Map.Entry<String, ConsoleSupport> entry : beanMap.entrySet())
		{
			ConsoleSupport consoleSupport = entry.getValue();
			String regex = consoleSupport.commandRegex();
			wrapperList.add(new ConsoleSupportWrapper(regex, consoleSupport));
			buffer.append("\n command: " + regex + " ConsoleSupport: " + consoleSupport.getClass());
		}
		log.info(buffer);
		log.info("Console service loaded successful!");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int blankCommandCount = 0;
		try
		{
			while (true)
			{
				System.out.print("Please input command: ");
				String line = reader.readLine();
				if (StringUtils.isBlank(line))
				{
					blankCommandCount++;
					if (blankCommandCount > 10)
					{
						// 连续10次空命令,视为退出命令行控制台
						reader.close();
						return;
					}
					continue;
				}
				blankCommandCount = 0;
				if (StringUtils.equalsIgnoreCase(line, "exit"))
				{
					// 退出系统
					log.info("Good bye!");
					break;
				}
				if (StringUtils.equalsIgnoreCase(line, "?") || StringUtils.equalsIgnoreCase(line, "help"))
				{
					// 将命令行系统的提示字符打印一遍
					log.info(buffer);
					continue;
				}
				ConsoleSupportWrapper[] wrappers = this.findConsoleSupportWrappers(line);
				if (wrappers.length == 0)
				{
					// 找不到对应的应用
					log.warn("UnSupported command: " + line);
				}
				else if (wrappers.length > 1)
				{
					// 找到两个以上应用
					log.error("command executed failed! more than 1 ConsoleSupport is match this command: " + line);
					for (int i = 0; i < wrappers.length; i++)
					{
						log.info("ConsoleSupport[" + i + "] regex: " + wrappers[i].getConsoleSupport().commandRegex());
					}
				}
				else
				{
					// 唯一确定一个应用
					ConsoleSupportWrapper wrapper = wrappers[0];
					Matcher matcher = wrapper.getPattern().matcher(line);
					if (matcher.matches())
					{
						final String[] params = new String[matcher.groupCount()];
						for (int i = 0; i < params.length; i++)
						{
							params[i] = matcher.group(i + 1);
						}
						final ConsoleSupport consoleSupport = wrapper.getConsoleSupport();
						Thread thread = new Thread(new Runnable()
						{
							@Override
							public void run()
							{
								try
								{
									consoleSupport.execute(params);// 执行应用
								}
								catch (Exception e)
								{
									log.error("ConsultSupport Exception!", e);
								}
							}
						}, "ConsoleThread-" + consoleSupport.getClass());
						thread.start();
						try
						{
							thread.join();
						}
						catch (InterruptedException e)
						{
							log.warn("InterruptedException", e);
						}
					}
				}
			}
		}
		catch (IOException e)
		{
			log.error("Console Exception!", e);
		}
		finally
		{
			IOUtils.closeQuietly(reader);
		}
		System.exit(0);
	}

	/**
	 * 根据命令行支撑集合
	 * 
	 * @param line
	 * @param paramList
	 * @return
	 */
	private ConsoleSupportWrapper[] findConsoleSupportWrappers(String line)
	{
		List<ConsoleSupportWrapper> list = new ArrayList<ConsoleSupportWrapper>();
		for (ConsoleSupportWrapper wrapper : wrapperList)
		{
			String regex = wrapper.getConsoleSupport().commandRegex();
			if (line.matches(regex))
			{
				list.add(wrapper);
			}
		}
		return list.toArray(new ConsoleSupportWrapper[0]);
	}

	/**
	 * 控制台支撑类的包装
	 * 
	 * @author wang.sheng
	 * 
	 */
	private static class ConsoleSupportWrapper
	{
		private Pattern pattern;
		private ConsoleSupport consoleSupport;

		public ConsoleSupportWrapper(String regex, ConsoleSupport consoleSupport)
		{
			this.pattern = Pattern.compile(regex);
			this.consoleSupport = consoleSupport;
		}

		public Pattern getPattern()
		{
			return pattern;
		}

		public ConsoleSupport getConsoleSupport()
		{
			return consoleSupport;
		}

	}
}
