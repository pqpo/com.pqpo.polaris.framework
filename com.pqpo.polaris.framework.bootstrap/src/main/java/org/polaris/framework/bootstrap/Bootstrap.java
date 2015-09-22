package org.polaris.framework.bootstrap;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * 启动类
 * 
 * @author wang.sheng
 * 
 */
public class Bootstrap
{
	private static final String EXECUTOR_CLASSNAME = "org.polaris.framework.common.MainExecutor";

	/**
	 * 应用程序的主入口
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		File rootDir = new File(".");
		File libDir = new File(rootDir, "lib");
		File commonDir = new File(rootDir, "common");
		List<URL> urlList = new ArrayList<URL>();
		loadJarList(libDir, urlList);
		loadJarList(commonDir, urlList);
		if (args != null && args.length > 0)
		{
			File distDir = new File(rootDir, args[0]);
			loadJarList(distDir, urlList);
		}
		URLClassLoader classLoader = new URLClassLoader(urlList.toArray(new URL[0]), Bootstrap.class.getClassLoader());
		// 设置主线程的类加载器
		Thread.currentThread().setContextClassLoader(classLoader);
		try
		{
			Executor executor = (Executor) classLoader.loadClass(EXECUTOR_CLASSNAME).newInstance();
			executor.execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void loadJarList(File dir, List<URL> list)
	{
		File[] files = dir.listFiles();
		for (File file : files)
		{
			if (file.isDirectory())
			{
				loadJarList(file, list);
			}
			else if (file.isFile())
			{
				String fileName = file.getName().toLowerCase();
				if (fileName.endsWith(".jar"))
				{
					try
					{
						URL url = file.toURI().toURL();
						list.add(url);
					}
					catch (MalformedURLException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
}
