package org.polaris.framework.common.server;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * HandlerCollection工厂
 * 
 * @author wang.sheng
 * 
 */
@Service("handlerCollection")
public class HandlerCollectionFactory implements FactoryBean<HandlerCollection>
{
	Log log = LogFactory.getLog(getClass());

	@Resource
	private ApplicationContext applicationContext;

	@Override
	public HandlerCollection getObject() throws Exception
	{
		HandlerCollection handlerCollection = new HandlerCollection();
		Map<String, ServletContextHandler> contextHandlerMap = applicationContext.getBeansOfType(ServletContextHandler.class);
		for (Map.Entry<String, ServletContextHandler> entry : contextHandlerMap.entrySet())
		{
			ServletContextHandler handler = entry.getValue();
			log.info("add ServletContextHandler: " + handler.getClass());
			handlerCollection.addHandler(handler);
		}
		Map<String, ResourceHandler> resourceHandlerMap = applicationContext.getBeansOfType(ResourceHandler.class);
		for (Map.Entry<String, ResourceHandler> entry : resourceHandlerMap.entrySet())
		{
			ResourceHandler handler = entry.getValue();
			log.info("add ResourceHandler: " + handler.getClass());
			handlerCollection.addHandler(handler);
		}
		return handlerCollection;
	}

	@Override
	public Class<?> getObjectType()
	{
		return HandlerCollection.class;
	}

	@Override
	public boolean isSingleton()
	{
		return true;
	}

}
