package org.polaris.framework.common.template;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * FTL模板的支持
 * 
 * @author wang.sheng
 * 
 */
@Service
public class FreemarkerTemplate
{
	Log log = LogFactory.getLog(getClass());

	private Configuration config;

	@PostConstruct
	protected void init()
	{
		config = new Configuration(Configuration.VERSION_2_3_21);
		try
		{
			config.setDirectoryForTemplateLoading(new File("./"));
		}
		catch (IOException e)
		{
			log.error("Configuration init failed!", e);
		}
	}

	/**
	 * 根据FTL文件路径,和模型数据输出结果到out中
	 * 
	 * @param out
	 * @param templatePath
	 * @param data
	 * @throws Exception
	 */
	public void execute(Writer out, String templatePath, Object data) throws Exception
	{
		Template template = config.getTemplate(templatePath);
		template.process(data, out);
	}

}
