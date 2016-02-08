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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


@Path("/restTest")
public interface IRest {
	@GET
	@Path("getJson")
	@Consumes("application/json")
	@Produces("application/json")
	Response getJson();

	@GET
	@Path("getXml")
	@Consumes("application/xml")
	@Produces("application/xml")
	Response getXml();
	
	@POST
	@Path("postJson")
	@Consumes("application/json")
	@Produces("application/json")
	Response postJson(Request req);

	@POST
	@Path("postXml")
	@Consumes("application/xml")
	@Produces("application/xml")
	Response postXml(Request req);

}