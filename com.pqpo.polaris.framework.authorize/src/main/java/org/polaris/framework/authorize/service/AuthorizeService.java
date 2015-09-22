package org.polaris.framework.authorize.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polaris.framework.authorize.dao.AuthorizeDao;
import org.polaris.framework.authorize.vo.Module;
import org.polaris.framework.authorize.vo.ModuleRoleRel;
import org.polaris.framework.authorize.vo.Role;
import org.polaris.framework.authorize.vo.User;
import org.polaris.framework.authorize.vo.UserRoleRel;
import org.polaris.framework.common.rest.PagingResult;
import org.polaris.framework.common.security.SecurityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 授权模块的服务
 * 
 * @author wang.sheng
 * 
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class AuthorizeService
{
	@Resource
	private AuthorizeDao authorizeDao;
	@Resource
	private SecurityService securityService;

	Log log = LogFactory.getLog(getClass());

	/**
	 * 获取全部角色
	 * 
	 * @return
	 */
	public Role[] getRoles()
	{
		return authorizeDao.getRoles();
	}

	public Role getRole(String id)
	{
		return this.authorizeDao.getRole(id);
	}

	public Role[] getRolesByUser(String userId)
	{
		return this.authorizeDao.getRolesByUser(userId);
	}

	public Role getRoleByName(String roleName)
	{
		return this.authorizeDao.getRoleByName(roleName);
	}

	public User[] getUsersByRoleName(String roleName)
	{
		return this.authorizeDao.getUsersByRoleName(roleName);
	}

	/**
	 * 删除一个用户
	 * 
	 * @param userId
	 */
	public void deleteUser(String userId)
	{
		authorizeDao.deleteUser(userId);
	}

	/**
	 * 获取指定的交叉角色
	 * 
	 * @param moduleName
	 * @param userId
	 * @param writable
	 * @return
	 */
	public Set<String> getIntersectRoleIdSet(String moduleName, String userId, boolean writable)
	{
		Set<String> roleIdSet1 = authorizeDao.getRoleIdSetByModuleName(moduleName, writable);
		Set<String> roleIdSet2 = authorizeDao.getRoleIdSetByUserId(userId, writable);
		return intersectionSet(roleIdSet1, roleIdSet2);
	}

	/**
	 * 取两个集合的交集
	 * 
	 * @param setA
	 * @param setB
	 * @return
	 */
	private static <T> Set<T> intersectionSet(Set<T> setA, Set<T> setB)
	{
		Set<T> intersectionSet = new HashSet<T>();
		Iterator<T> iterA = setA.iterator();
		while (iterA.hasNext())
		{
			T tempInner = iterA.next();
			if (setB.contains(tempInner))
			{
				intersectionSet.add(tempInner);
			}
		}
		return intersectionSet;
	}

	public void insert(Role... roles)
	{
		for (Role role : roles)
		{
			authorizeDao.insert(role);
		}
	}

	public void insert(Module... modules)
	{
		for (Module module : modules)
		{
			authorizeDao.insert(module);
		}
	}

	public void insert(User... users)
	{
		for (User user : users)
		{
			String password = securityService.encrypt(user.getPassword());
			user.setPassword(password);
			authorizeDao.insert(user);
		}
	}

	public void insert(ModuleRoleRel... moduleRoleRels)
	{
		for (ModuleRoleRel rel : moduleRoleRels)
		{
			authorizeDao.insert(rel);
		}
	}

	public void insert(UserRoleRel... userRoleRels)
	{
		for (UserRoleRel rel : userRoleRels)
		{
			authorizeDao.insert(rel);
		}
	}

	public User findUserByName(String name)
	{
		return authorizeDao.findUserByName(name);
	}

	public PagingResult<User> fuzzyFindUserByName(String name, int start, int limit)
	{
		return this.authorizeDao.fuzzyFindUserByName(name, start, limit);
	}

	public User findUserByNameAndPassword(String name, String password)
	{
		password = securityService.encrypt(password);
		return this.authorizeDao.findUserByNameAndPassword(name, password);
	}

	public void modifyPassword(String userId, String password)
	{
		password = securityService.encrypt(password);
		this.authorizeDao.modifyPassword(userId, password);
	}
}
