package org.polaris.framework.common.lob.service;

import java.io.InputStream;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polaris.framework.common.lob.dao.BlobContentDao;
import org.polaris.framework.common.lob.vo.BlobContent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 二进制数据服务
 * 
 * @author wang.sheng
 * 
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class BlobContentService
{
	@Resource
	private BlobContentDao blobContentDao;

	Log log = LogFactory.getLog(getClass());

	/**
	 * 上传一个二进制文件
	 * 
	 * @param is
	 * @param fileName
	 * @param length
	 * @return
	 */
	public BlobContent upload(InputStream is, String fileName, long length)
	{
		if (is == null || length < 1)
		{
			throw new IllegalArgumentException("InputStream cannot empty!");
		}
		return blobContentDao.insert(is, fileName, length);
	}

	public BlobContent upload(byte[] bytes, String fileName)
	{
		return this.blobContentDao.insert(bytes, fileName);
	}

	public BlobContent getBlobContent(String id)
	{
		return blobContentDao.getBlobContent(id);
	}

	public void delete(String id)
	{
		blobContentDao.delete(id);
	}
}
