package org.polaris.framework.common.lob.dao;

import java.io.InputStream;
import java.sql.Blob;

import javax.annotation.Resource;

import org.polaris.framework.common.dao.HibernateTemplate;
import org.polaris.framework.common.lob.vo.BlobContent;
import org.springframework.stereotype.Repository;

/**
 * BlobContent表的DAO类
 * 
 * @author wang.sheng
 * 
 */
@Repository
public class BlobContentDao
{
	@Resource
	private HibernateTemplate hibernateTemplate;

	public BlobContent getBlobContent(String id)
	{
		return hibernateTemplate.queryForObject("from BlobContent t where t.id=?", new Object[] { id }, BlobContent.class);
	}

	public void delete(String id)
	{
		hibernateTemplate.executeUpdate("delete from BlobContent t where t.id=?", new Object[] { id });
	}

	public BlobContent insert(byte[] bytes, String fileName)
	{
		Blob blob = hibernateTemplate.getCurrentSession().getLobHelper().createBlob(bytes);
		BlobContent blobContent = new BlobContent();
		blobContent.setContent(blob);
		blobContent.setOrignFileName(fileName);
		hibernateTemplate.save(blobContent);
		return blobContent;
	}

	public BlobContent insert(InputStream is, String fileName, long length)
	{
		Blob blob = hibernateTemplate.getCurrentSession().getLobHelper().createBlob(is, length);
		BlobContent blobContent = new BlobContent();
		blobContent.setContent(blob);
		blobContent.setOrignFileName(fileName);
		hibernateTemplate.save(blobContent);
		return blobContent;
	}
}
