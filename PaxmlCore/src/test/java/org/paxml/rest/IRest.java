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