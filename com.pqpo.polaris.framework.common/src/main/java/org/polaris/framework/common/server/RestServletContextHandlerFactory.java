package org.polaris.framework.common.server;

import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.polaris.framework.common.MainExecutor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 基于SpringMVC的Handler工厂
 * 
 * @author wang.sheng
 * 
 */
public class RestServletContextHandlerFactory implements FactoryBean<ServletContextHandler>
{
	private static final String ENCODING = "UTF-8";

	private String urlPattern = "/api/*";
	private String contextPath = "/";
	private String applicationContextConfigLocation = "classpath*:META-INF/servlet/*-applicationcontext.xml";
	private String servletContextConfigLocation = "classpath*:META-INF/servlet/*-servlet.xml";

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param applicationContextConfigLocation
	 */
	public void setApplicationContextConfigLocation(String applicationContextConfigLocation)
	{
		this.applicationContextConfigLocation = applicationContextConfigLocation;
	}

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param servletContextConfigLocation
	 */
	public void setServletContextConfigLocation(String servletContextConfigLocation)
	{
		this.servletContextConfigLocation = servletContextConfigLocation;
	}

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param contextPath
	 */
	public void setContextPath(String contextPath)
	{
		this.contextPath = contextPath;
	}

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param urlPattern
	 */
	public void setUrlPattern(String urlPattern)
	{
		this.urlPattern = urlPattern;
	}

	@Override
	public ServletContextHandler getObject() throws Exception
	{
		ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		handler.setContextPath(contextPath);
		handler.setInitParameter("parentContextKey", MainExecutor.FACTORY_KEY);
		handler.setInitParameter("contextConfigLocation", applicationContextConfigLocation);
		handler.addEventListener(new ContextLoaderListener());
		ServletHolder servletHolder = new ServletHolder(new DispatcherServlet());
		servletHolder.setInitParameter("contextConfigLocation", servletContextConfigLocation);
		handler.addServlet(servletHolder, urlPattern);
		FilterHolder filterHolder = new FilterHolder(new CharacterEncodingFilter());
		filterHolder.setInitParameter("encoding", ENCODING);
		filterHolder.setInitParameter("forceEncoding", "true");
		handler.addFilter(filterHolder, urlPattern, null);
		return handler;
	}

	@Override
	public Class<?> getObjectType()
	{
		return ServletContextHandler.class;
	}

	@Override
	public boolean isSingleton()
	{
		return true;
	}

}
