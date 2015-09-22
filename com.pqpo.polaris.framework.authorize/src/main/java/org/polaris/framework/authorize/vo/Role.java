package org.polaris.framework.authorize.vo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 角色
 * 
 * @author wang.sheng
 * 
 */
@Entity
@Table
public class Role
{
	@Id
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@GeneratedValue(generator = "system-uuid")
	@Column(length = 32)
	private String id;
	@Column(length = 50, nullable = false)
	private String name;
	@Column(length = 200)
	private String remark;
	@Column
	private boolean writable;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getRemark()
	{
		return remark;
	}

	public void setRemark(String remark)
	{
		this.remark = remark;
	}

	public boolean isWritable()
	{
		return writable;
	}

	public void setWritable(boolean writable)
	{
		this.writable = writable;
	}

}
