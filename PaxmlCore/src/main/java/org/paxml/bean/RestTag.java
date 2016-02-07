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
package org.paxml.bean;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.util.PaxmlUtils;
import org.paxml.util.XmlUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * Rest tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = RestTag.TAG_NAME)
public class RestTag extends BeanTag {
	private static final Log log = LogFactory.getLog(RestTag.class);
	public static class RestResult {
		private Object body;
		private Map headers;
		private int code;

		public RestResult(Object body, Map headers, int code) {

			this.body = body;
			this.headers = headers;
			this.code = code;
		}

		@Override
		public String toString() {
			return String.valueOf(body);
		}

		public Object getBody() {
			return body;
		}

		public void setBody(Object body) {
			this.body = body;
		}

		public Map getHeaders() {
			return headers;
		}

		public void setHeaders(Map headers) {
			this.headers = headers;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

	}

	/**
	 * The tag name.
	 */
	public static final String TAG_NAME = "rest";

	private String target;
	private String method = "get";
	private HttpMethod _method = HttpMethod.GET;
	private Map headers;
	private String username;
	private String password;
	private String contentType = MediaType.APPLICATION_JSON;
	private boolean parseResponse = true;
	private boolean simple = true;
	private String xmlRootTag="request";
	private String xmlTopCollectionTag;
	
	@Override
	protected Object doInvoke(Context context) throws Exception {

		RestTemplate t = new RestTemplate();
		if (!simple) {
			// cancel default error handling
			t.setErrorHandler(new ResponseErrorHandler() {

				@Override
				public boolean hasError(ClientHttpResponse response) throws IOException {
					// always say no error
					return false;
				}

				@Override
				public void handleError(ClientHttpResponse response) throws IOException {
					// do nothing
				}

			});
		}
		Object value = getValue();
		HttpHeaders hds = new HttpHeaders();
		if (username != null) {
			String[] auth = PaxmlUtils.makeHttpClientAutorizationHeader(username, password);
			hds.set(auth[0], auth[1]);
		}
		if (headers != null) {
			Map<String, String> map = new LinkedHashMap<String, String>();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				hds.set(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
			}
		}
		if(StringUtils.isNotBlank(contentType)){
			hds.setContentType(org.springframework.http.MediaType.parseMediaType(contentType));
		}
		String reqBody=makeRequestBody(value);
		log.debug("REST request body="+reqBody);
		HttpEntity<String> entity = new HttpEntity<String>(reqBody, hds);
		
		ResponseEntity<String> rsp = t.exchange(target, _method, entity, String.class);

		Object body = parseResponse ? parseResponse(rsp) : rsp.getBody();
		if (simple) {
			return body;
		}
		return new RestResult(body, rsp.getHeaders(), rsp.getStatusCode().value());

	}

	private Object parseResponse(ResponseEntity<String> rsp) {
		String body = rsp.getBody();
		if (StringUtils.isBlank(body)) {
			return null;
		}
		String ct = String.valueOf(rsp.getHeaders().getContentType());
		if (StringUtils.containsIgnoreCase(ct, "json")) {
			log.debug("Parsing REST response body as json");
			return XmlUtils.fromJson(body);
		}
		if (StringUtils.containsIgnoreCase(ct, "xml")) {

			log.debug("Parsing REST response body as xml");
			return XmlUtils.fromXml(body);
		}
		return body;
	}

	private String makeRequestBody(Object value) {
		if (value == null) {
			return null;
		}
		if(value instanceof String){
			return (String)value;
		}
		if (StringUtils.containsIgnoreCase(contentType, "json")) {
			log.debug("Serializing REST request body to json");
			return XmlUtils.toJson(value);
		}
		if (StringUtils.containsIgnoreCase(contentType, "xml")) {
			log.debug("Serializing REST request body to xml");
			return XmlUtils.toXml(value, xmlRootTag, xmlTopCollectionTag);
		}
		return value.toString();
	}

	public String getXmlTopCollectionTag() {
		return xmlTopCollectionTag;
	}

	public void setXmlTopCollectionTag(String xmlTopCollectionTag) {
		this.xmlTopCollectionTag = xmlTopCollectionTag;
	}

	public boolean isSimple() {
		return simple;
	}

	public void setSimple(boolean simple) {
		this.simple = simple;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getXmlRootTag() {
		return xmlRootTag;
	}

	public void setXmlRootTag(String xmlRootTag) {
		this.xmlRootTag = xmlRootTag;
	}

	public Map getHeaders() {
		return headers;
	}

	public void setHeaders(Map headers) {
		this.headers = headers;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isParseResponse() {
		return parseResponse;
	}

	public void setParseResponse(boolean parseResponse) {
		this.parseResponse = parseResponse;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {

		this.method = method;
		try {
			_method = HttpMethod.valueOf(method.toUpperCase());
		} catch (Exception e) {
			throw new PaxmlRuntimeException("Unsupported rest method: " + method);
		}
	}

}
