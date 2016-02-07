package org.paxml.rest;

import javax.inject.Singleton;

@Singleton
public class RestImpl implements IRest {
	/* (non-Javadoc)
	 * @see org.paxml.rest.IRest#getJson()
	 */
	@Override
	public Response getJson() {
		Response rsp = new Response();
		rsp.setStrValue("1");
		return rsp;
	}

	/* (non-Javadoc)
	 * @see org.paxml.rest.IRest#getXml()
	 */
	@Override
	public Response getXml() {
		Response rsp = new Response();
		rsp.setStrValue("1");
		return rsp;
	}

	/* (non-Javadoc)
	 * @see org.paxml.rest.IRest#postJson(org.paxml.rest.Request)
	 */
	@Override
	public Response postJson(Request req) {
		Response rsp = new Response();
		rsp.setStrValue(""+ req.getIntValue());
		return rsp;
	}

	/* (non-Javadoc)
	 * @see org.paxml.rest.IRest#postXml(org.paxml.rest.Request)
	 */
	@Override
	public Response postXml(Request req) {
		Response rsp = new Response();
		rsp.setStrValue("" + req.getIntValue());
		return rsp;
	}
}
