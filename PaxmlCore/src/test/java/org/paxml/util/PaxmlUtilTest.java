package org.paxml.util;

import java.util.regex.Pattern;

import org.junit.Test;

import junit.framework.Assert;

public class PaxmlUtilTest {
	@Test
	public void testGlobTranslate(){
		testMatch("*","blabla", true);
		testMatch("a*c","asdifksdfkhc", true);
		testMatch("a*","blabla", false);
		testMatch("?","b", true);
		testMatch("?","ba", false);
		testMatch("b?","bax", false);
		testMatch("b?","ba", true);
		testMatch("b??x","banx", true);
		
	}
	private void testMatch(String glob, String str, boolean  match){
		String regex=PaxmlUtils.createRegexFromGlob(glob);
		Assert.assertEquals(match,Pattern.compile(regex).matcher(str).matches());
	}
}
