package org.polaris.framework.common.config;

import org.polaris.framework.common.xml.annotation.XmlAttribute;

public class Property
{
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String value;

	public String getName()
	{
		return name;
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return name + ":" + value;
	}

}
