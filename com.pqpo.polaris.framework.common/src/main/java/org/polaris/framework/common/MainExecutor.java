package org.polaris.framework.common;

import org.polaris.framework.bootstrap.Executor;
import org.polaris.framework.common.log.Log4jUtils;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;

/**
 * 主执行器
 * 
 * @author wang.sheng
 * 
 */
public final class MainExecutor implements Executor
{
	public static final String FACTORY_KEY = "polarisApplicationContext";

	@Override
	public void execute()
	{
		Log4jUtils.initSystemOut();
		ContextSingletonBeanFactoryLocator.getInstance().useBeanFactory(FACTORY_KEY);
	}

}
