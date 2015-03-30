package org.paxml.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;

import org.paxml.el.UtilFunctions;

import com.icegreen.greenmail.util.GreenMailUtil;

public class MailMap extends HashMap<String, Object> {

	public MailMap(Message msg) throws MessagingException {
		super();
		put("subject", msg.getSubject());
		put("text", GreenMailUtil.getBody(msg));
		put("to", getAddresses(msg.getRecipients(RecipientType.TO)));
		put("cc", getAddresses(msg.getRecipients(RecipientType.CC)));
		//put("bcc", getAddresses(msg.getRecipients(RecipientType.BCC))); // GreenMail seems to have a bug that does not recognize bcc
		put("from", getAddresses(msg.getFrom()));
		put("replyTo", getAddresses(msg.getReplyTo()));
		
	}
	@Override
	public Object put(String key, Object value) {
		if(value==null){
			return null;
		}
		return super.put(key, value);
	}
	private Object getAddresses(Address[] addrs) {
		if(addrs==null){
			return null;
		}
		List<String> r = new ArrayList<String>(addrs.length);
		for(Address a:addrs){
			r.add(a.toString());
		}
		return UtilFunctions.compactCollect(r);
	};
}
