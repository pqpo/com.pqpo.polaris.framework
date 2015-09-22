package org.polaris.framework.common.config;

import java.util.List;

import org.polaris.framework.common.xml.annotation.XmlList;

public class Config
{
	@XmlList(name = "property", itemClass = Property.class)
	private List<Property> propertyList;

	public List<Property> getPropertyList()
	{
		return propertyList;
	}

	@Override
	public String toString()
	{
		return propertyList == null ? "null" : propertyList.toString();
	}

}
