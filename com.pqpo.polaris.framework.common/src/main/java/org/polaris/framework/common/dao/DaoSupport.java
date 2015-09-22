package org.polaris.framework.common.dao;

import java.io.Serializable;
import java.util.List;

public interface DaoSupport<T,PK> {
	T get(Class<T> entityClazz , PK id);
	Serializable save(T entity);
	void update(T entity);
	void delete(T entity);
	void delete(Class<T> entityClazz , PK id);
	List<T> getList(Class<T> entityClazz);
	List<T> getList(Class<T> entityClazz,int start,int limit);
	List<T> getList(String hql,Object...params);
	List<T> getList(String hql,int start,int limit,Object...params);
	T getObject(String hql,Object...params);
	int executeHql(String hql,Object...params);
}
