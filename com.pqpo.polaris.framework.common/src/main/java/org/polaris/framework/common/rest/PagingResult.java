package org.polaris.framework.common.rest;

/**
 * 分页查询结果
 * 
 * @author wang.sheng
 * 
 */
public class PagingResult<T>
{
	private long total;
	private T[] results;

	public PagingResult(long total, T[] results)
	{
		this.total = total;
		this.results = results;
	}

	public long getTotal()
	{
		return total;
	}

	public void setTotal(long total)
	{
		this.total = total;
	}

	public T[] getResults()
	{
		return results;
	}

	public void setResults(T[] results)
	{
		this.results = results;
	}

}
