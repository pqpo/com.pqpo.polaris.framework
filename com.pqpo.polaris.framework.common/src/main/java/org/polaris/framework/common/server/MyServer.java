package org.polaris.framework.common.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 基于Jetty的服务器
 * 
 * @author wang.sheng
 * 
 */
public class MyServer extends Server implements ApplicationListener<ApplicationContextEvent>
{
	Log log = LogFactory.getLog(getClass());

	public MyServer(ThreadPool pool)
	{
		super(pool);
	}

	@Override
	public void onApplicationEvent(final ApplicationContextEvent event)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if (event instanceof ContextRefreshedEvent)
				{
					try
					{
						start();
					}
					catch (Exception e)
					{
						log.error("Server start failed!", e);
					}
				}
				else if (event instanceof ContextClosedEvent)
				{
					try
					{
						stop();
					}
					catch (Exception e)
					{
						log.error("Server stop failed!", e);
					}
				}
			}
		}).start();
	}
}
