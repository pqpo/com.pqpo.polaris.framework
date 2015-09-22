package org.polaris.framework.authorize.dao;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.polaris.framework.authorize.vo.Module;
import org.polaris.framework.authorize.vo.ModuleRoleRel;
import org.polaris.framework.authorize.vo.Role;
import org.polaris.framework.authorize.vo.User;
import org.polaris.framework.authorize.vo.UserRoleRel;
import org.polaris.framework.common.dao.HibernateTemplate;
import org.polaris.framework.common.rest.PagingResult;
import org.springframework.stereotype.Repository;

/**
 * 授权管理DAO
 * 
 * @author wang.sheng
 * 
 */
@Repository
public class AuthorizeDao
{
	@Resource
	private HibernateTemplate hibernateTemplate;

	/**
	 * 添加用户
	 * 
	 * @param user
	 */
	public void insert(User user)
	{
		this.hibernateTemplate.save(user);
	}

	/**
	 * 添加模块
	 * 
	 * @param module
	 */
	public void insert(Module module)
	{
		this.hibernateTemplate.save(module);
	}

	/**
	 * 添加角色
	 * 
	 * @param role
	 */
	public void insert(Role role)
	{
		this.hibernateTemplate.save(role);
	}

	/**
	 * 添加用户角色关联
	 * 
	 * @param rel
	 */
	public void insert(UserRoleRel rel)
	{
		this.hibernateTemplate.save(rel);
	}

	/**
	 * 添加模块角色关联
	 * 
	 * @param rel
	 */
	public void insert(ModuleRoleRel rel)
	{
		this.hibernateTemplate.save(rel);
	}

	/**
	 * 删除指定用户
	 * 
	 * @param userId
	 */
	public void deleteUser(String userId)
	{
		Object[] params = new Object[] { userId };
		this.hibernateTemplate.executeUpdate("delete from User t where t.id=?", params);
		this.hibernateTemplate.executeUpdate("delete from UserRoleRel r where r.userId=?", params);
	}

	/**
	 * 删除指定模块
	 * 
	 * @param moduleId
	 */
	public void deleteModule(String moduleId)
	{
		Object[] params = new Object[] { moduleId };
		this.hibernateTemplate.executeUpdate("delete from Module t where t.id=?", params);
		this.hibernateTemplate.executeUpdate("delete from ModuleRoleRel r where r.moduleId=?", params);
	}

	/**
	 * 删除指定角色
	 * 
	 * @param roleId
	 */
	public void deleteRole(String roleId)
	{
		Object[] params = new Object[] { roleId };
		this.hibernateTemplate.executeUpdate("delete from Role t where t.id=?", params);
		this.hibernateTemplate.executeUpdate("delete from ModuleRoleRel r where r.roleId=?", params);
		this.hibernateTemplate.executeUpdate("delete from UserRoleRel r where r.roleId=?", params);
	}

	/**
	 * 根据用户获得角色列表
	 * 
	 * @param userId
	 * @return
	 */
	public Role[] getRolesByUser(String userId)
	{
		return this.hibernateTemplate.queryForArray("from Role t where t.id in(select r.roleId from UserRoleRel r where r.userId=?)", new Object[] { userId },
				Role.class);
	}

	public Role getRoleByName(String name)
	{
		return this.hibernateTemplate.queryForObject("from Role t where t.name=?", new Object[] { name }, Role.class);
	}

	/**
	 * 获取全部用户
	 * 
	 * @return
	 */
	public User[] getUsers()
	{
		return this.hibernateTemplate.queryForArray("from User t order by t.name", null, User.class);
	}

	/**
	 * 获取指定角色ID的用户集合
	 * 
	 * @param roleId
	 * @return
	 */
	public User[] getUsersByRoleId(String roleId)
	{
		return this.hibernateTemplate.queryForArray("from User u where u.id in(select r.userId from UserRoleRel r where r.roleId=?) order by u.name",
				new Object[] { roleId }, User.class);
	}

	/**
	 * 获取指定角色名称的用户集合
	 * 
	 * @param roleName
	 * @return
	 */
	public User[] getUsersByRoleName(String roleName)
	{
		String hql = "from User u where u.id in(select urr.userId from UserRoleRel urr,Role r where urr.roleId=r.id and r.name=?) order by u.name";
		return this.hibernateTemplate.queryForArray(hql, new Object[] { roleName }, User.class);
	}

	/**
	 * 获取全部角色
	 * 
	 * @return
	 */
	public Role[] getRoles()
	{
		return this.hibernateTemplate.queryForArray("from Role t order by t.name", null, Role.class);
	}

	public Role getRole(String id)
	{
		return this.hibernateTemplate.queryForObject("from Role t where t.id=?", new Object[] { id }, Role.class);
	}

	/**
	 * 获取全部模块
	 * 
	 * @return
	 */
	public Module[] getModules()
	{
		return this.hibernateTemplate.queryForArray("from Module t order by t.name", null, Module.class);
	}

	/**
	 * 根据用户名获取用户信息
	 * 
	 * @param name
	 * @return
	 */
	public User findUserByName(String name)
	{
		return this.hibernateTemplate.queryForObject("from User t where t.name=?", new Object[] { name }, User.class);
	}

	public PagingResult<User> fuzzyFindUserByName(String name, int start, int limit)
	{
		String hql = "from User t where t.name like ? order by t.name";
		Object[] params = new Object[] { "%" + name + "%" };
		long totalCount = this.hibernateTemplate.getTotalCount(hql, params);
		User[] users = this.hibernateTemplate.queryForArray(hql, start, limit, params, User.class);
		return new PagingResult<User>(totalCount, users);
	}

	/**
	 * 根据用户名和密码查找用户
	 * 
	 * @param name
	 * @param password
	 * @return
	 */
	public User findUserByNameAndPassword(String name, String password)
	{
		return this.hibernateTemplate.queryForObject("from User t where t.name=? and t.password=?", new Object[] { name, password }, User.class);
	}

	/**
	 * 修改用户的密码
	 * 
	 * @param userId
	 * @param password
	 */
	public void modifyPassword(String userId, String password)
	{
		this.hibernateTemplate.executeUpdate("update User t set t.password=? where t.id=?", new Object[] { password, userId });
	}

	/**
	 * 获取用户的角色ID集合
	 * 
	 * @param userId
	 * @return
	 */
	public Set<String> getRoleIdSetByUserId(String userId, boolean writable)
	{
		String hql = "select t.id from Role t,UserRoleRel r where r.roleId=t.id and r.userId=?";
		Object[] params = null;
		if (writable)
		{
			hql += " and t.writable=?";
			params = new Object[] { userId, true };
		}
		else
		{
			params = new Object[] { userId };
		}
		String[] roleIds = this.hibernateTemplate.queryForArray(hql, params, String.class);
		Set<String> roleIdSet = new HashSet<String>();
		CollectionUtils.addAll(roleIdSet, roleIds);
		return roleIdSet;
	}

	/**
	 * 根据模块名称获得该模块对应的角色ID集合
	 * 
	 * @param moduleName
	 * @return
	 */
	public Set<String> getRoleIdSetByModuleName(String moduleName, boolean writable)
	{
		String hql = "select t.id from Role t,ModuleRoleRel r,Module m where r.roleId=t.id and r.moduleId=m.id and m.name=?";
		Object[] params = null;
		if (writable)
		{
			hql += " and t.writable=?";
			params = new Object[] { moduleName, true };
		}
		else
		{
			params = new Object[] { moduleName };
		}
		String[] roleIds = this.hibernateTemplate.queryForArray(hql, params, String.class);
		Set<String> roleIdSet = new HashSet<String>();
		CollectionUtils.addAll(roleIdSet, roleIds);
		return roleIdSet;
	}

	/**
	 * 获取模块的角色ID集合
	 * 
	 * @param moduleId
	 * @return
	 */
	public Set<String> getRoleIdSetByModuleId(String moduleId, boolean writable)
	{
		String hql = "select t.id from Role t,ModuleRoleRel r where r.roleId=t.id and r.moduleId=?";
		Object[] params = null;
		if (writable)
		{
			hql += " and t.writable=?";
			params = new Object[] { moduleId, true };
		}
		else
		{
			params = new Object[] { moduleId };
		}
		String[] roleIds = this.hibernateTemplate.queryForArray(hql, params, String.class);
		Set<String> roleIdSet = new HashSet<String>();
		CollectionUtils.addAll(roleIdSet, roleIds);
		return roleIdSet;
	}
}
