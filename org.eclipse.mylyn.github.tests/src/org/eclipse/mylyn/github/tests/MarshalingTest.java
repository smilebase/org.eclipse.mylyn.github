package org.eclipse.mylyn.github.tests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.gson.Gson;

@RunWith(JUnit4.class)
public class MarshalingTest {

	private Gson gson;

	@Before
	public void beforeTest() {
		gson = new Gson();
	}
	
	@Test
	public void testUnmarshal() {
		
	}
}
