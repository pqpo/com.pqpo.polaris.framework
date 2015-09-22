package org.polaris.framework.common.config;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polaris.framework.common.xml.XmlParseService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;

/**
 * Config的工厂Bean
 * 
 * @author wang.sheng
 * 
 */
public class ConfigFatoryBean implements FactoryBean<Config>
{
	Log log = LogFactory.getLog(getClass());

	private Resource configPath;
	private XmlParseService xmlParseService;

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param configPath
	 */
	public void setConfigPath(Resource configPath)
	{
		this.configPath = configPath;
	}

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param xmlParseService
	 */
	public void setXmlParseService(XmlParseService xmlParseService)
	{
		this.xmlParseService = xmlParseService;
	}

	@Override
	public Config getObject() throws Exception
	{
		InputStream is = null;
		try
		{
			is = configPath.getInputStream();
			return xmlParseService.parseObject(Config.class, is);
		}
		catch (Exception e)
		{
			log.error("TableMeta getObject failed!", e);
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}
		return null;
	}

	@Override
	public Class<Config> getObjectType()
	{
		return Config.class;
	}

	@Override
	public boolean isSingleton()
	{
		return true;
	}

}
