package org.eclipse.mylyn.github.tests;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import org.eclipse.mylyn.github.internal.GitHubIssue;
import org.eclipse.mylyn.github.internal.GitHubIssues;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.gson.Gson;

@SuppressWarnings("restriction")
@RunWith(JUnit4.class)
public class MarshalingTest {

	private Gson gson;

	@Before
	public void beforeTest() {
		gson = new Gson();
	}

	@Test
	public void unmarshalIssues() {
		GitHubIssues issues = gson.fromJson(
				getResource("resources/issues.json"), GitHubIssues.class);

		assertTrue(issues != null);
		assertTrue(issues.getIssues() != null);
		
		assertEquals(10,issues.getIssues().length);
		
		GitHubIssue issue = issues.getIssues()[9];
		//{"number":10,"votes":0,"created_at":"2010/02/04 21:03:54 -0800","body":"test description 2 ","title":"test issue for testing mylyn github connector2",
		// "updated_at":"2010/02/04 21:09:37 -0800","closed_at":null,"user":"dgreen99","labels":[],"state":"open"}]}
		assertEquals("10",issue.getNumber());
		assertEquals("2010/02/04 21:03:54 -0800",issue.getCreated_at());
		assertEquals("test description 2 ",issue.getBody());
		assertEquals("test issue for testing mylyn github connector2",issue.getTitle());
		assertEquals("2010/02/04 21:09:37 -0800",issue.getUpdated_at());
		assertNull(issue.getClosed_at());
		assertEquals("dgreen99",issue.getUser());
		assertEquals("open",issue.getState());
	}

	private String getResource(String resource) {
		try {
			InputStream stream = MarshalingTest.class
					.getResourceAsStream(resource);
			try {
				StringWriter writer = new StringWriter();
				Reader reader = new InputStreamReader(stream);
				int c;
				while ((c = reader.read()) != -1) {
					writer.write(c);
				}
				return writer.toString();
			} finally {
				stream.close();
			}
		} catch (Throwable t) {
			throw new IllegalStateException(t);
		}
	}
}
