package org.polaris.framework.common.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class DefaultDaoSupport<T,PK extends Serializable> implements DaoSupport<T,PK> 
{
	
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) 
	{
		this.sessionFactory = sessionFactory;
	}
	
	public SessionFactory getSessionFactory()
	{
		return this.sessionFactory;
	}
	
	private Session getCurrentSession()
	{
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(Class<T> entityClazz, PK id) 
	{
		return (T) getCurrentSession().get(entityClazz, id);
	}

	@Override
	public Serializable save(T entity) 
	{
		return getCurrentSession().save(entity);
	}

	@Override
	public void update(T entity) 
	{
		getCurrentSession().update(entity);
	}

	@Override
	public void delete(T entity) 
	{
		getCurrentSession().delete(entity);
	}

	@Override
	public void delete(Class<T> entityClazz, PK id) 
	{
		String hql = "delete from "+entityClazz.getSimpleName()+" where id=?";
		executeHql(hql, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getList(Class<T> entityClazz) 
	{
		String hql = "from "+entityClazz.getSimpleName();
		return getCurrentSession().createQuery(hql).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getList(Class<T> entityClazz, int start, int limit) 
	{
		String hql = "from "+entityClazz.getSimpleName();
		return getCurrentSession()
				.createQuery(hql)
				.setFirstResult(start)
				.setMaxResults(limit)
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getList(String hql, Object... params) 
	{
		Query query = getCurrentSession().createQuery(hql);
		if(params!=null&&params.length>0)
		{
			for(int i=0;i<params.length;i++)
			{
				query.setParameter(i, params[i]);
			}
		}
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getList(String hql, int start, int limit, Object... params) 
	{
		Query query = getCurrentSession().createQuery(hql);
		query.setFirstResult(start);
		query.setMaxResults(limit);
		if(params!=null&&params.length>0)
		{
			for(int i=0;i<params.length;i++)
			{
				query.setParameter(i, params[i]);
			}
		}
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject(String hql, Object... params) 
	{
		Query query = getCurrentSession().createQuery(hql);
		if(params!=null&&params.length>0)
		{
			for(int i=0;i<params.length;i++)
			{
				query.setParameter(i, params[i]);
			}
		}
		List<T> list = query.list();
		if(list!=null&&!list.isEmpty())
		{
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public int executeHql(String hql, Object... params) 
	{
		Query query = getCurrentSession().createQuery(hql);
		if(params!=null&&params.length>0)
		{
			for(int i=0;i<params.length;i++)
			{
				query.setParameter(i, params[i]);
			}
		}
		return query.executeUpdate();
	}
}
