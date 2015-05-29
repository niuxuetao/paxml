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


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.security.Secret;

/**
 * Email tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "email")
public class EmailTag extends BeanTag {

	private static final Log log = LogFactory.getLog(EmailTag.class);

	private LinkedHashSet to;
	private LinkedHashSet cc;
	private LinkedHashSet bcc;
	private boolean isolate;
	private String subject;
	private String text;
	private Object html;
	private String from;
	private LinkedHashSet replyTo;
	private LinkedHashSet attachment;
	private String host;
	private int port;
	private String username;
	private Object password;
	private boolean ssl;
	private boolean sslCheckServerIdentity;
	private boolean tls;

	@Override
	protected Object doInvoke(Context context) throws Exception {
		
		if (isolate) {
			Set<String> all = new LinkedHashSet<String>();
			if (to != null) {
				all.addAll(to);
			}
			if (cc != null) {
				all.addAll(cc);
			}
			if (bcc != null) {
				all.addAll(bcc);
			}
			List<String> ids = new ArrayList<String>(all.size());
			for (String r : all) {
				Email email = createEmail(Arrays.asList(r), null, null);
				String id = email.send();
				if (log.isDebugEnabled()) {
					log.debug("Isolated email sent to: " + r + ", from: " + from + ", replyTo: " + replyTo);
				}
				ids.add(id);
			}
			return ids;
		} else {
			Email email = createEmail(to, cc, bcc);
			String id = email.send();
			if (log.isDebugEnabled()) {
				log.debug("Email sent to: " + to + ", cc: " + cc + ", bcc: " + bcc + ", from: " + from + ", replyTo: " + replyTo);
			}
			return id;
		}

	}

	private Email createEmail(Collection<String> to, Collection<String> cc, Collection<String> bcc) throws EmailException {
		Email email;
		if (attachment == null || attachment.isEmpty()) {
			email = new SimpleEmail();
		} else {
			MultiPartEmail mpemail = new MultiPartEmail();
			for (Object att : attachment) {
				mpemail.attach(makeAttachment(att.toString()));
			}
			email = mpemail;
		}

		if (StringUtils.isNotEmpty(username)) {
			String pwd = null;
			if (password instanceof Secret) {
				pwd = ((Secret) password).getDecrypted();
			} else if (password != null) {
				pwd = password.toString();
			}
			email.setAuthenticator(new DefaultAuthenticator(username, pwd));
		}

		email.setHostName(findHost());
		email.setSSLOnConnect(ssl);
		if (port > 0) {
			if (ssl) {
				email.setSslSmtpPort(port + "");
			} else {
				email.setSmtpPort(port);
			}
		}
		if (replyTo != null) {
			for (Object r : replyTo) {
				email.addReplyTo(r.toString());
			}
		}
		email.setFrom(from);
		email.setSubject(subject);
		email.setMsg(text);
		if (to != null) {
			for (String r : to) {
				email.addTo(r);
			}
		}
		if (cc != null) {
			for (String r : cc) {
				email.addCc(r);
			}
		}
		if (bcc != null) {
			for (String r : bcc) {
				email.addBcc(r);
			}
		}
		email.setSSLCheckServerIdentity(sslCheckServerIdentity);
		email.setStartTLSEnabled(tls);
		email.setStartTLSRequired(tls);
		return email;
	}

	private EmailAttachment makeAttachment(String att) {
		EmailAttachment r = new EmailAttachment();
		r.setDisposition(EmailAttachment.ATTACHMENT);
		URL url = null;
		try {
			url = new URL(att);
			if (StringUtils.startsWithIgnoreCase(url.getProtocol(), "file")) {
				r.setPath(url.getPath());
			} else {
				r.setURL(url);
			}
		} catch (MalformedURLException e) {
			r.setPath(att);
		}
		r.setName(FilenameUtils.getName(att));
		return r;
	}

	private String findHost() {
		if (StringUtils.isBlank(host)) {
			return "smtp." + StringUtils.substringAfter(from, "@");
		} else {
			return host;
		}
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public boolean isIsolate() {
		return isolate;
	}

	public void setIsolate(boolean isolate) {
		this.isolate = isolate;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Object getHtml() {
		return html;
	}

	public void setHtml(Object html) {
		this.html = html;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Object getPassword() {
		return password;
	}

	public void setPassword(Object password) {
		this.password = password;
	}

	public LinkedHashSet getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(LinkedHashSet replyTo) {
		this.replyTo = replyTo;
	}

	public boolean isSslCheckServerIdentity() {
		return sslCheckServerIdentity;
	}

	public void setSslCheckServerIdentity(boolean sslCheckServerIdentity) {
		this.sslCheckServerIdentity = sslCheckServerIdentity;
	}

	public boolean isTls() {
		return tls;
	}

	public void setTls(boolean tls) {
		this.tls = tls;
	}

	public LinkedHashSet getTo() {
		return to;
	}

	public void setTo(LinkedHashSet to) {
		this.to = to;
	}

	public LinkedHashSet getCc() {
		return cc;
	}

	public void setCc(LinkedHashSet cc) {
		this.cc = cc;
	}

	public LinkedHashSet getBcc() {
		return bcc;
	}

	public void setBcc(LinkedHashSet bcc) {
		this.bcc = bcc;
	}

	public LinkedHashSet getAttachment() {
		return attachment;
	}

	public void setAttachment(LinkedHashSet attachment) {
		this.attachment = attachment;
	}

}
