package org.eclipse.mylyn.github.internal;

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.github.GitHubService;
import org.eclipse.mylyn.github.GitHubIssues;
import org.junit.Test;

public class GitHubServiceTest {

	@Test
	public void searchIssues() throws Exception {
		GitHubService service = new GitHubService();
		GitHubIssues issues = service.searchIssues("smilebase",
				"org.eclipse.mylyn.github.issues", "open", "test");
		assertEquals(3, issues.getIssues().length);
	}

}
