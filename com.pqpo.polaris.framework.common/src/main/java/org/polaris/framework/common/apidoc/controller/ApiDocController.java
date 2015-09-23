package org.polaris.framework.common.apidoc.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.polaris.framework.common.apidoc.service.ApiDocService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apiDoc")
public class ApiDocController{
	
	@Resource
	private ApiDocService apiDocService;
	
	@RequestMapping("/{moudle}")
	public void getApiDoc(@PathVariable String moudle,HttpServletResponse response) throws IOException{
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html,charset=urf-8");
		PrintWriter writer = response.getWriter();
		try {
			writer.write(apiDocService.getHtmlDoc(moudle));
		} catch (Exception e) {
			writer.write("error:"+e.getMessage());
		}
		writer.flush();
		writer.close();
	}
	
}
