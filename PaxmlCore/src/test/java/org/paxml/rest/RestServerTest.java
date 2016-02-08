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

import java.net.URI;
import java.util.Arrays;

import javax.ws.rs.core.Application;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import junit.framework.Assert;


public class RestServerTest extends JerseyTest {

	@Override
	protected Application configure() {
		// use any available port
		forceSet(TestProperties.CONTAINER_PORT, "0");
		return new ResourceConfig(RestImpl.class);
	}
	
	@Override
	public URI getBaseUri(){
		return super.getBaseUri();
	}
	
	@Test
	public void testServer() throws Exception{
		IRest rest=getAPIClient(IRest.class);
		Assert.assertNotNull(rest.getJson());
		Assert.assertNotNull(rest.getXml());
		Request req=new Request();
		Assert.assertNotNull(rest.postJson(req));
		Assert.assertNotNull(rest.postXml(req));
		
	}
	public <T> T getAPIClient(Class<T> clazz) {

		T t = JAXRSClientFactory.create(target().getUri().toString(), clazz, Arrays.asList(new JacksonJsonProvider(), new JacksonJaxbJsonProvider()));
		HTTPConduit conduit = WebClient.getConfig(t).getHttpConduit();
		HTTPClientPolicy policy = new HTTPClientPolicy();
		policy.setReceiveTimeout(60*1000); // times out after 1 minute
		conduit.setClient(policy);
		return t;
	}
	
}
