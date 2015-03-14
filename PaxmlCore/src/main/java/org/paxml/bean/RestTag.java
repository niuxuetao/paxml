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

import java.util.LinkedHashMap;
import java.util.Map;

import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.util.PaxmlUtils;
import org.paxml.util.XmlUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Rest tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = RestTag.TAG_NAME)
public class RestTag extends BeanTag {

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

	private String address;
	private String verb;
	private HttpMethod method;
	private Map headers;
	private String username;
	private String password;
	private boolean autoParse = true;

	@Override
	protected Object doInvoke(Context context) throws Exception {

		RestTemplate t = new RestTemplate();
		Object value = getValue();
		HttpEntity<String> entity = new HttpEntity<String>(value == null ? null : value.toString());
		if (username != null) {
			String[] auth = PaxmlUtils.makeHttpClientAutorizationHeader(username, password);
			entity.getHeaders().set(auth[0], auth[1]);
		}
		if (headers != null) {
			Map<String, String> map = new LinkedHashMap<String, String>();
			HttpHeaders hds = entity.getHeaders();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				hds.set(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
			}
		}

		ResponseEntity<String> rsp = t.exchange(address, method, entity, String.class);
		Object body = autoParse ? XmlUtils.parseJsonOrXmlOrString(rsp.getBody()) : rsp.getBody();
		return new RestResult(body, rsp.getHeaders(), rsp.getStatusCode().value());

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

	public Map getHeaders() {
		return headers;
	}

	public void setHeaders(Map headers) {
		this.headers = headers;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isAutoParse() {
		return autoParse;
	}

	public void setAutoParse(boolean autoParse) {
		this.autoParse = autoParse;
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {

		this.verb = verb;
		try {
			method = HttpMethod.valueOf(verb.toUpperCase());
		} catch (Exception e) {
			throw new PaxmlRuntimeException("Unsupported rest verb: " + verb);
		}
	}

}
