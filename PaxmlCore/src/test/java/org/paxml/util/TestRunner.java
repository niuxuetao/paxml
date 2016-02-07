package org.paxml.util;

import org.junit.Test;
import org.paxml.launch.PaxmlRunner;

public class TestRunner {
	@Test
	public void runPaxml(){
		new PaxmlRunner().run("restfulTest", null, "");
	}
}
