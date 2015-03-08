package org.paxml.web;

import org.apache.axiom.om.OMElement;
import org.paxml.annotation.RootTag;
import org.paxml.core.IParserContext;
import org.paxml.tag.AbstractPaxmlEntity;
import org.paxml.tag.AbstractPaxmlEntityFactory;
import org.paxml.tag.ScenarioEntityFactory.Scenario;

@RootTag(PageFactory.TAG_NAME)
public class PageFactory extends AbstractPaxmlEntityFactory {
	public static final String TAG_NAME = "page";

	public static class Page extends Scenario {

	}

	@Override
	protected AbstractPaxmlEntity doCreate(OMElement root, IParserContext context) {
		return new Page();
	}

}
