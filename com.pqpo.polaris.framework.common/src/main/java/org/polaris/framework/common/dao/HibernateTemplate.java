package org.polaris.framework.common.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.polaris.framework.common.utils.ArrayUtils;

/**
 * 基础的Hibernate模板类<br>
 * 
 * @author wang.sheng
 */
public class HibernateTemplate
{
	private SessionFactory sessionFactory;

	/**
	 * 使用Spring的依赖注入
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	/**
	 * 查询得到一个列表
	 * 
	 * @param hql
	 * @param params
	 * @return
	 */
	public List<?> queryForList(String hql, Object[] params)
	{
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql);
		if (params != null && params.length > 0)
		{
			for (int i = 0; i < params.length; i++)
			{
				query.setParameter(i, params[i]);
			}
		}
		return query.list();
	}

	/**
	 * 获得当前的Session实例
	 * 
	 * @return
	 */
	public Session getCurrentSession()
	{
		return sessionFactory.getCurrentSession();
	}

	/**
	 * 分页查询得到一个列表
	 * 
	 * @param hql
	 * @param start
	 * @param length
	 * @param params
	 * @return
	 */
	public List<?> queryForList(String hql, int start, int length, Object[] params)
	{
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql);
		query.setFirstResult(start);
		query.setMaxResults(length);
		if (params != null && params.length > 0)
		{
			for (int i = 0; i < params.length; i++)
			{
				query.setParameter(i, params[i]);
			}
		}
		return query.list();
	}

	/**
	 * 查询得到数组
	 * 
	 * @param hql
	 * @param params
	 * @return
	 */
	public Object[] queryForArray(String hql, Object[] params)
	{
		List<?> list = this.queryForList(hql, params);
		return list.toArray();
	}

	/**
	 * 查询得到一个数组
	 * 
	 * @param hql
	 * @param requireType
	 * @param params
	 * @return
	 */
	public <T> T[] queryForArray(String hql, Object[] params, Class<T> requireType)
	{
		List<?> list = this.queryForList(hql, params);
		return list.toArray(ArrayUtils.newArrayInstance(requireType, 0));
	}

	/**
	 * 分页查询得到一个数组
	 * 
	 * @param hql
	 * @param start
	 * @param length
	 * @param params
	 * @return
	 */
	public Object[] queryForArray(String hql, int start, int length, Object[] params)
	{
		List<?> list = this.queryForList(hql, start, length, params);
		return list.toArray();
	}

	/**
	 * 分页查询得到一个数组
	 * 
	 * @param hql
	 * @param start
	 * @param length
	 * @param requireType
	 * @param params
	 * @return
	 */
	public <T> T[] queryForArray(String hql, int start, int length, Object[] params, Class<T> requireType)
	{
		List<?> list = this.queryForList(hql, start, length, params);
		return list.toArray(ArrayUtils.newArrayInstance(requireType, 0));
	}

	/**
	 * 更新一个对象
	 * 
	 * @param object
	 */
	public void update(Object object)
	{
		Session session = sessionFactory.getCurrentSession();
		session.update(object);
	}

	/**
	 * 保存一个对象
	 * 
	 * @param object
	 */
	public void save(Object object)
	{
		Session session = sessionFactory.getCurrentSession();
		session.save(object);
	}

	/**
	 * 执行更新操作
	 * 
	 * @param hql
	 * @param params
	 * @return
	 */
	public int executeUpdate(String hql, Object[] params)
	{
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql);
		if (params != null && params.length > 0)
		{
			for (int i = 0; i < params.length; i++)
			{
				query.setParameter(i, params[i]);
			}
		}
		return query.executeUpdate();
	}

	/**
	 * 获取记录结果的数量
	 * 
	 * @param hql
	 * @param params
	 * @return
	 */
	public long getTotalCount(String hql, Object[] params)
	{
		hql = " " + hql;
		String lowerHql = hql.toLowerCase();
		int fromIndex = lowerHql.indexOf(" from ");
		int orderByIndex = lowerHql.indexOf(" order by ");
		String countHql = null;
		if (orderByIndex > 0)
		{
			countHql = hql.substring(fromIndex, orderByIndex);
		}
		else
		{
			countHql = hql.substring(fromIndex);
		}
		countHql = "select count(*) " + countHql;
		Long totalCount = this.queryForObject(countHql, params, Long.class);
		if (totalCount == null)
		{
			return -1L;
		}
		else
		{
			return totalCount;
		}
	}

	/**
	 * 查询得到一个对象,如果有多个则返回其中的第一个对象
	 * 
	 * @param hql
	 * @param params
	 * @return
	 */
	public Object queryForObject(String hql, Object[] params)
	{
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql);
		if (params != null && params.length > 0)
		{
			for (int i = 0; i < params.length; i++)
			{
				query.setParameter(i, params[i]);
			}
		}
		return query.uniqueResult();
	}

	/**
	 * 查询得到指定类型的对象
	 * 
	 * @param hql
	 * @param clazz
	 * @param params
	 * @return
	 */
	public <T> T queryForObject(String hql, Object[] params, Class<T> clazz)
	{
		Object result = this.queryForObject(hql, params);
		if (result == null)
		{
			return null;
		}
		else if (clazz.isInstance(result))
		{
			return clazz.cast(result);
		}
		else
		{
			throw new RuntimeException("Object:" + result + " cannot cast to " + clazz);
		}
	}
	
	
	public long getSqlQueryCount(String sql){
		Session session = sessionFactory.getCurrentSession();
		Query querycount = session.createSQLQuery("select count(1) from("+sql+" ) t");  
        Object count =  querycount.uniqueResult();  
        if(count == null){
        	return 0;
        }
        return Long.valueOf(count.toString());
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]>getSqlQuery(String sql,int start, int limit){
		Session session = sessionFactory.getCurrentSession();
		SQLQuery sqlQuery = session.createSQLQuery(sql);  
		sqlQuery.setFirstResult(start);
		sqlQuery.setMaxResults(limit);
		return sqlQuery.list();
	}
}
