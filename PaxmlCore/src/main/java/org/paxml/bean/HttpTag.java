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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;

/**
 * Http tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "http")
public class HttpTag extends BeanTag {

	public static final int DEFAULT_MAX_RETRY = 5;
    public static final String ENTITY_NAME = "name";
    public static final String ENTITY_VALUE = "value";
    private String url;
    private boolean responseless = false;
    private Object header;
    private Object body;
    private String method;
    private Object query;
    private int maxRetry = DEFAULT_MAX_RETRY;
    private boolean failOnError = true;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) throws Exception {
        String lowUrl = url.toLowerCase();
        if (!lowUrl.startsWith("http://") && !lowUrl.startsWith("https://")) {
            url = "http://" + url;
        }
        HttpClient client = new HttpClient();
        final HttpMethodBase m;
        if ("post".equalsIgnoreCase(method)) {
            m = setPostBody(new PostMethod(url));
        } else if ("get".equalsIgnoreCase(method)) {
            m = new GetMethod(url);
        } else {
            throw new PaxmlRuntimeException("Unknown method: " + method);
        }
        setHeader(m);
        setQueryString(m);
        // Provide custom retry handler is necessary
        m.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(maxRetry, false));

        onBeforeSend(m);
        // method.setr
        try {
            // Execute the method.

            final int statusCode = client.executeMethod(m);

            if (responseless) {
                return statusCode;
            }

            // Read the response body.
            Map<String, Object> result = new LinkedHashMap<String, Object>();
            result.put("code", statusCode);
            result.put("body", m.getResponseBodyAsString());
            result.put("all", m);
            return result;

        } finally {
            // Release the connection.
            m.releaseConnection();
        }
    }

    protected void onBeforeSend(HttpMethodBase m) {
        // do nothing here, let subclasses do stuff
    }

    private Map<String, List<String>> getNameValuePairs(Object object, String propertyName) {
        // System.out.println(object.getClass().getName()+"\r\n"+ object);
        if (object instanceof Map) {
            Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();
            Map<?, ?> hd = (Map<?, ?>) object;
            for (Map.Entry<?, ?> entry : hd.entrySet()) {
                Object value = entry.getValue();
                Object key = entry.getKey();
                if (value != null && key != null) {
                    String skey = key.toString();
                    List<String> vlist = result.get(skey);
                    if (vlist == null) {
                        vlist = new ArrayList<String>(1);
                        result.put(skey, vlist);
                    }
                    vlist.add(value.toString());
                }
            }
            return result.size() > 0 ? result : null;
        } else if (object instanceof List) {
            Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();

            int i = 1;
            for (Object v : (List<?>) object) {
                if (v instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) v;
                    Object name = map.get(ENTITY_NAME);
                    if (name == null) {
                        throw new PaxmlRuntimeException(propertyName + " list at index " + i + " is missing property: "
                                + ENTITY_NAME);
                    }
                    Object value = map.get(ENTITY_VALUE);
                    if (value != null) {
                        String skey = name.toString();
                        List<String> vlist = result.get(skey);
                        if (vlist == null) {
                            vlist = new ArrayList<String>(1);
                            result.put(skey, vlist);
                        }
                        vlist.add(value.toString());
                    }
                } else if (v != null) {
                    throw new PaxmlRuntimeException(propertyName + " list at index " + i
                            + " should contain list of name-value pairs but it contains: '" + v + "' of type: "
                            + v.getClass().getName());
                }
                i++;
            }
            return result.size() > 0 ? result : null;
        }
        return null;
    }

    private HttpMethodBase setHeader(HttpMethodBase method) {
        Map<String, List<String>> value = getNameValuePairs(header, "header");
        if (value != null) {
            for (Map.Entry<String, List<String>> entry : value.entrySet()) {
                for (String v : entry.getValue()) {
                    method.addRequestHeader(entry.getKey(), v);
                }
            }
        } else if (header != null) {
            throw new PaxmlRuntimeException("Header should be key-value pairs but got: " + header);
        }
        return method;
    }

    private HttpMethodBase setQueryString(HttpMethodBase method) {
        Map<String, List<String>> value = getNameValuePairs(query, "query");
        if (value != null) {
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            for (Map.Entry<String, List<String>> entry : value.entrySet()) {
                for (String v : entry.getValue()) {
                    pairs.add(new NameValuePair(entry.getKey(), v));
                }
            }
            method.setQueryString(pairs.toArray(new NameValuePair[pairs.size()]));
        } else if (query != null) {
            method.setQueryString(query.toString());
        }
        return method;

    }

    private PostMethod setPostBody(PostMethod post) {
        Map<String, List<String>> value = getNameValuePairs(body, "body");
        if (value != null) {

            for (Map.Entry<String, List<String>> entry : value.entrySet()) {
                for (String v : entry.getValue()) {
                    post.addParameter(entry.getKey(), v);
                }
            }

        } else if (body != null) {
            post.setRequestBody(body.toString());
        }
        return post;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getHeader() {
        return header;
    }

    public void setHeader(Object header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public boolean isResponseless() {
        return responseless;
    }

    public void setResponseless(boolean responseless) {
        this.responseless = responseless;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getQuery() {
        return query;
    }

    public void setQuery(Object query) {
        this.query = query;
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

}
