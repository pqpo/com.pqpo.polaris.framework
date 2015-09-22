package org.polaris.framework.common.xml;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.polaris.framework.common.utils.ReflectUtils;
import org.polaris.framework.common.xml.annotation.XmlAttribute;
import org.polaris.framework.common.xml.annotation.XmlList;
import org.polaris.framework.common.xml.annotation.XmlMap;
import org.polaris.framework.common.xml.annotation.XmlObject;
import org.polaris.framework.common.xml.annotation.XmlContent;
import org.springframework.stereotype.Service;

/**
 * XML自动化解析服务<br>
 * 
 * @author wang.sheng
 * 
 */
@Service
public class XmlParseService
{
	Log log = LogFactory.getLog(getClass());

	/**
	 * 根据类型和XML输入流生成解析后的对象
	 * 
	 * @param cl
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public <T> T parseObject(Class<T> cl, InputStream is) throws Exception
	{
		SAXReader reader = new SAXReader();
		Document document = reader.read(is);
		Element root = document.getRootElement();
		return parseObject(cl, root);
	}

	/**
	 * 根据类型和根节点Element生成对象
	 * 
	 * @param cl
	 * @param element
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T parseObject(Class<T> cl, Element element) throws Exception
	{
		if (cl == null)
		{
			throw new IllegalArgumentException("target object is null!");
		}
		T target = cl.newInstance();
		Field[] fields = ReflectUtils.getAllFields(cl);
		for (Field field : fields)
		{
			if (field.isAnnotationPresent(XmlAttribute.class))
			{
				XmlAttribute xmlAttribute = (XmlAttribute) field.getAnnotation(XmlAttribute.class);
				String fieldName = xmlAttribute.name();
				if (fieldName == null || fieldName.length() < 1)
				{
					fieldName = field.getName();
				}
				String value = element.attributeValue(fieldName);
				if (value == null)
				{
					continue;
				}
				field.setAccessible(true);
				if (field.getType() == String.class)
				{
					field.set(target, value);
				}
				else
				{
					Object attribute = ConvertUtils.convert(value, field.getType());
					field.set(target, attribute);
				}
			}
			else if (field.isAnnotationPresent(XmlContent.class))
			{
				String value = element.getText();
				if (value == null)
				{
					value = "";
				}
				else
				{
					value = value.trim();
				}
				field.setAccessible(true);
				if (field.getType() == String.class)
				{

					field.set(target, value);
				}
				else
				{
					Object attribute = ConvertUtils.convert(value, field.getType());
					field.set(target, attribute);
				}
			}
			else if (field.isAnnotationPresent(XmlObject.class))
			{
				XmlObject xmlObject = (XmlObject) field.getAnnotation(XmlObject.class);
				String fieldName = xmlObject.name();
				if (fieldName == null || fieldName.length() < 1)
				{
					fieldName = field.getName();
				}
				Element childElement = element.element(fieldName);
				if (childElement == null)
				{
					continue;
				}
				Class<?> fieldClass = field.getType();
				Object childTarget = parseObject(fieldClass, childElement);
				field.setAccessible(true);
				field.set(target, childTarget);
			}
			else if (field.isAnnotationPresent(XmlList.class))
			{
				XmlList xmlChildren = (XmlList) field.getAnnotation(XmlList.class);
				String fieldName = xmlChildren.name();
				if (fieldName == null || fieldName.length() < 1)
				{
					fieldName = field.getName();
				}
				Iterator<Element> it = (Iterator<Element>) element.elementIterator();
				while (it.hasNext())
				{
					Element childElement = it.next();
					if (!childElement.getName().equalsIgnoreCase(fieldName))
					{
						continue;
					}
					Object childTarget = parseObject(xmlChildren.itemClass(), childElement);
					field.setAccessible(true);
					Collection<Object> list = (Collection<Object>) field.get(target);
					if (list == null)
					{
						list = (Collection<Object>) xmlChildren.listClass().newInstance();
						field.set(target, list);
					}
					list.add(childTarget);
				}
			}
			else if (field.isAnnotationPresent(XmlMap.class))
			{
				XmlMap xmlChildren = (XmlMap) field.getAnnotation(XmlMap.class);
				String fieldName = xmlChildren.name();
				if (fieldName == null || fieldName.length() < 1)
				{
					fieldName = field.getName();
				}
				Iterator<Element> it = (Iterator<Element>) element.elementIterator();
				while (it.hasNext())
				{
					Element childElement = it.next();
					if (!childElement.getName().equalsIgnoreCase(fieldName))
					{
						continue;
					}
					Object childTarget = parseObject(xmlChildren.valueClass(), childElement);
					String keyValue = childElement.attributeValue(xmlChildren.key());
					Object key = keyValue;
					if (xmlChildren.keyClass() != String.class)
					{
						key = ConvertUtils.convert(keyValue, xmlChildren.keyClass());
					}
					field.setAccessible(true);
					Map<Object, Object> map = (Map<Object, Object>) field.get(target);
					if (map == null)
					{
						map = (Map<Object, Object>) xmlChildren.mapClass().newInstance();
						field.set(target, map);
					}
					if (!xmlChildren.reduplicatable())
					{
						if (map.containsKey(key))
						{
							throw new RuntimeException("Reduplicate key define! key=" + key + ",value=" + childTarget);
						}
					}
					map.put(key, childTarget);
				}
			}
		}
		return target;
	}

}
