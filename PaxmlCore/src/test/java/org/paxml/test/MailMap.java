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
