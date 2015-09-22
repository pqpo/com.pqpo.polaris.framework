package org.polaris.framework.common.server;

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.springframework.beans.factory.FactoryBean;

/**
 * 静态资源的Handler工厂
 * 
 * @author wang.sheng
 * 
 */
public class ResourceHandlerFactory implements FactoryBean<ResourceHandler>
{
	private String resourceBase;
	private boolean directoriesListed;

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param resourceBase
	 */
	public void setResourceBase(String resourceBase)
	{
		this.resourceBase = resourceBase;
	}

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param directoriesListed
	 */
	public void setDirectoriesListed(boolean directoriesListed)
	{
		this.directoriesListed = directoriesListed;
	}

	@Override
	public ResourceHandler getObject() throws Exception
	{
		ResourceHandler handler = new ResourceHandler();
		handler.setDirectoriesListed(directoriesListed);
		handler.setWelcomeFiles(new String[] { "index.html" });
		handler.setResourceBase(resourceBase);
		handler.setMinMemoryMappedContentLength(-1);// 解决启动后资源文件无法修改保存的问题
		handler.setCacheControl("private");
		handler.setEtags(true);
		return handler;
	}

	@Override
	public Class<?> getObjectType()
	{
		return ResourceHandler.class;
	}

	@Override
	public boolean isSingleton()
	{
		return true;
	}

}
