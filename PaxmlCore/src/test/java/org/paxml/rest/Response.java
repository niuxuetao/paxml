package org.paxml.rest;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Response {
	private String strValue;

	public String getStrValue() {
		return strValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}
	
}
