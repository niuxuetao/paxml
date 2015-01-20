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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.ObjectTree;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.util.AxiomUtils;
import org.paxml.util.Elements;


/**
 * Soap tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "soap")
public class SoapTag extends BeanTag {
    /**
     * SOAP envelope namespace.
     */
    public static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
    /**
     * Number of retries for http.
     */
    public static final int RETRY = 3;

    private String url;
    private boolean responseless = false;
    private Object header;
    private Object body;
    private String targetNamespace;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) throws Exception {

        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(out);

        // method.setRequestHeader(headerName, headerValue);
        method.setRequestBody(out.toString("UTF-8"));
        method.setRequestHeader("Content-Type", "text/xml;charset=UTF-8");
        if (header != null) {
            Map<?, ?> hd = (Map<?, ?>) this.header;
            for (Map.Entry<?, ?> entry : hd.entrySet()) {
                Object obj = entry.getValue();
                if (obj != null) {
                    method.setRequestHeader(entry.getKey().toString(), obj.toString());
                }
            }
        }

        // Provide custom retry handler is necessary
        method.getParams()
                .setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(RETRY, false));

        // method.setr
        try {
            // Execute the method.
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                throw new PaxmlRuntimeException("Http post failed: " + method.getStatusLine());
            }

            // Read the response body.
            byte[] responseBody = method.getResponseBody();

            // Deal with the response.
            // Use caution: ensure correct character encoding and is not binary
            // data
            return read(new ByteArrayInputStream(responseBody));
        } finally {
            // Release the connection.
            method.releaseConnection();
        }
    }

    private String getWsdlUrl() {
        return url + "?WSDL";
    }

    private String detectTargetNamespace() {
        InputStream in = null;
        try {
            in = new URL(getWsdlUrl()).openStream();
            OMElement root = AxiomUtils.getRootElement(in);
            if (root != null) {
                String ns = AxiomUtils.getAttribute(root, "targetNamespace");
                return StringUtils.isNotBlank(ns) ? ns : null;
            }
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private Object read(InputStream in) throws IOException, XMLStreamException {
        if (responseless) {
            return null;
        }
        OMElement root = AxiomUtils.getRootElement(in);
        OMElement bd = AxiomUtils.getFirstChildElement(root, "Body");

        OMElement ele = bd.getFirstElement();
        if (SOAP_NS.equals(ele.getNamespaceURI())) {
            OMElement code = AxiomUtils.getFirstChildElement(ele, "faultcode");
            OMElement string = AxiomUtils.getFirstChildElement(ele, "faultstring");
            throw new PaxmlRuntimeException("Soap fault code (" + code == null ? " "
                    : code.getText() + "): " + string == null ? "" : string.getText());
        }
        return fromXml(ele.getFirstElement());

    }

    private void write(OutputStream out) throws IOException, XMLStreamException {
        if (targetNamespace == null) {
            targetNamespace = detectTargetNamespace();
        }
        if (targetNamespace == null) {

            throw new PaxmlRuntimeException("No @targetNamespace attribute given on <soap> tag,"
                    + " and the target namespace cannot be detected from WSDL: " + getWsdlUrl());
        }
        OMFactory factory = AxiomUtils.getOMFactory();
        Map<?, ?> bd = (Map<?, ?>) this.body;

        OMNamespace targetNs = factory.createOMNamespace(targetNamespace, "ns");

        OMNamespace ns = factory.createOMNamespace(SOAP_NS, "soapenv");

        OMDocument doc = AxiomUtils.newDocument();
        doc.setXMLEncoding("UTF-8");

        OMElement root = factory.createOMElement("Envelope", ns);
        doc.addChild(root);

        OMElement headerEle = factory.createOMElement("Header", ns);
        root.addChild(headerEle);

        OMElement bodyEle = factory.createOMElement("Body", ns);
        root.addChild(bodyEle);

        Iterator<?> it = bd.entrySet().iterator();
        if (!it.hasNext()) {
            throw new PaxmlRuntimeException("No webservice operation name given!");
        }
        toXml(bodyEle, bd, targetNs, factory);

        doc.serializeAndConsume(out);
    }

    private Object fromXml(OMElement ele) {
        if (ele == null) {
            return null;
        }
        if (ele.getFirstElement() == null) {
            // simple value, return the text
            return ele.getText();
        } else {
            ObjectTree tree = new ObjectTree();

            populateTree(tree, ele);

            return tree;
        }
    }

    private void populateTree(ObjectTree tree, OMElement ele) {

        if (ele.getFirstElement() == null) {
            tree.addValue(ele.getLocalName(), ele.getText());
        } else {
            ObjectTree subTree = new ObjectTree();
            tree.addValue(ele.getLocalName(), subTree);
            for (OMElement child : new Elements(ele)) {
                populateTree(subTree, child);
            }
        }

    }

    private void toXml(OMElement ele, Object obj, OMNamespace ns, OMFactory factory) {
        if (obj == null) {
            return;
        }
        if (obj instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                String name = entry.getKey().toString();
                Object value = entry.getValue();
                OMElement child = factory.createOMElement(name, ns.getNamespaceURI(), "");
                ele.addChild(child);
                toXml(child, value, ns, factory);
            }
        } else if (obj instanceof List) {
            for (Object value : ((List<?>) obj)) {
                toXml(ele, value, ns, factory);
            }
        } else {
            ele.setText(obj.toString());
        }
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

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

}
