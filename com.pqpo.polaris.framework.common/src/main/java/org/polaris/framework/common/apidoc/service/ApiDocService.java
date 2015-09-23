package org.polaris.framework.common.apidoc.service;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.polaris.framework.common.apidoc.annotation.ParamsDoc;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Service
public class ApiDocService implements ApplicationContextAware{

	private ApplicationContext applicationContext;

	private static final String HTML_TEMPLATE = 
			"<html>"
					+ "<head>"
					+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
					+ "</head>"
					+ "<body>"
					+ "%s"
					+ "</body>"
					+ "</html>";
	private static final String NEW_LINE = "<br>";
	private static final String DIVIDING_LINE1 = "================================================================";
	private static final String DIVIDING_LINE2 = "----------------------------------------------------------------";

	private final Map<String,String> cacheMap = new ConcurrentHashMap<>();
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public String getHtmlDoc(String moudle) throws Exception{
		String cache = cacheMap.get(moudle);
		if(cache!=null){
			return cache;
		}
		Object bean = null;
		try {
			bean = applicationContext.getBean(moudle);
		} catch (Throwable e1) {
			throw new Exception("cannot find the moudle!");
		}
		Class<? extends Object> clazz = bean.getClass();
		RestController restControllerAnn = clazz.getAnnotation(RestController.class);
		Controller controllerAnn = clazz.getAnnotation(Controller.class);
		if(restControllerAnn==null&&controllerAnn==null){
			throw new Exception("the moudle is not a Controller!");
		}
		String htmlDoc = doGetHtmlDoc(clazz,moudle);
		cacheMap.put(moudle, htmlDoc);
		return htmlDoc;
	}

	private String doGetHtmlDoc(Class<? extends Object> clazz,String moudle) {
		StringBuilder sb = new StringBuilder();
		sb.append(moudle)
		.append(NEW_LINE);
		Description classDescAnn = clazz.getAnnotation(Description.class);
		if(classDescAnn!=null){
			sb.append(classDescAnn.value())
			.append(NEW_LINE);
		}
		RequestMapping requestMappingAnn = clazz.getAnnotation(RequestMapping.class);
		sb.append("Path:");
		if(requestMappingAnn!=null){
			sb.append(arrayToString(requestMappingAnn.value()));
		}
		sb.append(NEW_LINE);
		sb.append(DIVIDING_LINE1);
		sb.append(NEW_LINE);
		for(Method method:clazz.getMethods()){
			sb.append(doPaserMethod(method));
		}
		return String.format(HTML_TEMPLATE, sb.toString());
	}

	private String doPaserMethod(Method method) {
		StringBuilder sb = new StringBuilder();
		RequestMapping methodMappingAnn = method.getAnnotation(RequestMapping.class);
		if(methodMappingAnn==null){
			return "";
		}
		Description methodDescAnn = method.getAnnotation(Description.class);
		if(methodDescAnn!=null){
			sb.append(methodDescAnn.value());
			sb.append(NEW_LINE);
		}
		sb.append(doPaserURI(methodMappingAnn.value()));
		sb.append(doPaserHttpMethod(methodMappingAnn.method()));
		sb.append(doPaserParams(method.getAnnotation(ParamsDoc.class)));
		sb.append(doPaserResponse(method.getReturnType()));
		sb.append(DIVIDING_LINE2);
		sb.append(NEW_LINE);
		return sb.toString();
	}

	private String doPaserResponse(Class<?> returnType) {
		StringBuilder sb = new StringBuilder("Response:");
		if(returnType!=null){
			if(returnType.isArray()){
				sb.append(returnType.getComponentType().getSimpleName()+" List");
			}else{
				sb.append(returnType.getSimpleName());
			}
		}
		sb.append(NEW_LINE);
		return sb.toString();
	}

	private String doPaserParams(ParamsDoc paramsDoc) {
		if(paramsDoc==null){
			return "Params:"+NEW_LINE;
		}
		return doPaserStringArray("Params:",paramsDoc.value());
	}

	private String doPaserHttpMethod(RequestMethod[] method) {
		StringBuilder sb = new StringBuilder("Method:");
		if(method.length==0){
			sb.append("GET");
		}else{
			for(RequestMethod m:method){
				sb.append(m.name()).append(",");
			}
			if(sb.length()>0){
				if(sb.charAt(sb.length()-1)==','){
					sb.deleteCharAt(sb.length()-1);
				}
			}
		}
		sb.append(NEW_LINE);
		return sb.toString();
	}

	private Object doPaserURI(String[] values) {
		return doPaserStringArray("URI:",values);
	}

	private String doPaserStringArray(String title,String[] values){
		StringBuilder sb = new StringBuilder(title);
		sb.append(arrayToString(values));
		sb.append(NEW_LINE);
		return sb.toString();
	}

	private String arrayToString(String[] array) {
		StringBuilder sb = new StringBuilder();
		for(String str:array){
			sb.append(str).append(",");
		}
		if(sb.length()>0){
			if(sb.charAt(sb.length()-1)==','){
				sb.deleteCharAt(sb.length()-1);
			}
		}
		return sb.toString();
	}


}
