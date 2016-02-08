/**
 * This file is part of PaxmlCore.
 *
 * PaxmlCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlCore.  If not, see <http://www.gnu.org/licenses/>.
 */
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
