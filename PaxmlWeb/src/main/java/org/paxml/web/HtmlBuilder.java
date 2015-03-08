package org.paxml.web;

import java.util.ArrayList;
import java.util.List;

import org.paxml.core.IObjectContainer;

public class HtmlBuilder {
	private final List from;

	public HtmlBuilder(Object from) {
		if (from instanceof IObjectContainer) {
			this.from = ((IObjectContainer) from).getList();
		} else if (from instanceof List) {
			this.from = (List) from;
		} else {
			this.from = new ArrayList();
			this.from.add(from);
		}
	}

	public String build() {
		return null;
	}
}
