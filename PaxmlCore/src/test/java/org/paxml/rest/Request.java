package org.paxml.rest;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Request {
	private int intValue;

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
	
}
