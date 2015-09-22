package org.polaris.framework.common.lob.vo;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 图片/视频等内容
 * 
 * @author wang.sheng
 * 
 */
@Table
@Entity
public class BlobContent
{
	@Id
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@GeneratedValue(generator = "system-uuid")
	@Column(length = 32)
	private String id;
	/**
	 * 原始的文件名
	 */
	@Column(length = 200)
	private String orignFileName;
	@Column
	private Blob content;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public Blob getContent()
	{
		return content;
	}

	public void setContent(Blob content)
	{
		this.content = content;
	}

	public String getOrignFileName()
	{
		return orignFileName;
	}

	public void setOrignFileName(String orignFileName)
	{
		this.orignFileName = orignFileName;
	}

}
