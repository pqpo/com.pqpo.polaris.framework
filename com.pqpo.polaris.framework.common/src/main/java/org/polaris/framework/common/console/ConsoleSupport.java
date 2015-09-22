package org.polaris.framework.common.console;

/**
 * 控制台支撑
 * 
 * @author wang.sheng
 * 
 */
public interface ConsoleSupport
{
	/**
	 * 命令的正则表达式.可以解析出参数供execute方法调用
	 * 
	 * @return
	 */
	String commandRegex();

	/**
	 * 执行
	 * 
	 * @param args
	 */
	void execute(String[] args);
}
