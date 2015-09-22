package org.polaris.framework.common.lob.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.polaris.framework.common.lob.service.BlobContentService;
import org.polaris.framework.common.lob.vo.BlobContent;
import org.polaris.framework.common.rest.FormResult;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Blob资源的REST接口
 * 
 * @author wang.sheng
 * 
 */
@RestController
@RequestMapping("/blob")
public class BlobContentController
{
	@Resource
	private BlobContentService blobContentService;

	Log log = LogFactory.getLog(getClass());

	/**
	 * 上传二进制文件
	 * 
	 * @param file
	 * @param userinfo
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public FormResult upload(@RequestBody MultipartFile file)
	{
		FormResult formResult = new FormResult();
		InputStream is = null;
		try
		{
			is = file.getInputStream();
			BlobContent blobContent = blobContentService.upload(is, file.getOriginalFilename(), file.getSize());
			formResult.setSuccess(true);
			formResult.setData(blobContent.getId());
		}
		catch (Exception e)
		{
			log.error("upload failed!", e);
			formResult.setSuccess(false);
			formResult.setMessage(e.getMessage());
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}
		return formResult;
	}

	/**
	 * 下载资料,前端将弹出下载对话框
	 * 
	 * @param id
	 * @param servletContext
	 * @param response
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public void download(@PathVariable String id, HttpServletResponse response)
	{
		BlobContent blobContent = blobContentService.getBlobContent(id);
		if (blobContent == null)
		{
			// 二进制内容不存在
			log.warn("BlobContent not found! id: " + id);
			try
			{
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
			catch (IOException e)
			{
				log.warn("sendError failed!", e);
			}
			return;
		}
		String fileName = blobContent.getOrignFileName();
		InputStream is = null;
		try
		{
			response.setContentType("APPLICATION/OCTET-STREAM");
			fileName = response.encodeURL(new String(fileName.getBytes(), "UTF-8"));// 转码
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			is = blobContent.getContent().getBinaryStream();
			IOUtils.copy(is, response.getOutputStream());
			response.setStatus(HttpServletResponse.SC_OK);
			response.flushBuffer();
		}
		catch (Exception e)
		{
			log.error("download failed!", e);
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * 加载一个二进制图片
	 * 
	 * @param id
	 * @param servletContext
	 * @param response
	 */
	@RequestMapping(value = "/image/{id}", method = RequestMethod.GET)
	public void openImage(@PathVariable String id, HttpServletRequest request, HttpServletResponse response)
	{
		BlobContent blobContent = blobContentService.getBlobContent(id);
		if (blobContent == null)
		{
			try
			{
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
			catch (IOException e)
			{
				log.warn("sendError failed!", e);
			}
		}
		else
		{
			String fileName = blobContent.getOrignFileName();
			if (StringUtils.isEmpty(fileName))
			{
				fileName = "unknown.jpg";
			}
			else
			{
				fileName = fileName.toLowerCase();
			}
			ServletContext servletContext = request.getServletContext();
			String contentType = servletContext.getMimeType(fileName);
			response.setContentType(contentType);
			InputStream is = null;
			try
			{
				is = blobContent.getContent().getBinaryStream();
				IOUtils.copy(is, response.getOutputStream());
			}
			catch (Exception e)
			{
				log.error("load image failed!", e);
			}
			finally
			{
				IOUtils.closeQuietly(is);
			}

		}
	}
}
