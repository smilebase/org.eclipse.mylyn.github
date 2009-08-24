/**
 * Copyright 2009 Christian Trutz 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 */
package org.eclipse.mylyn.github.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.github.GitHubIssue;
import org.eclipse.mylyn.github.GitHubIssues;
import org.eclipse.mylyn.github.GitHubService;
import org.junit.Test;

/**
 * Run All the JUnit Tests for the GitHub API implementation
 */
public class GitHubServiceTest {

	// GitHub API key for user "eclipse-github-plugin"
	String API_KEY = "8b35af675fcdca9d254ae7a6ad4d0be8";

	String TEST_USER = "eclipse-github-plugin";

	String TEST_PASS = "plugin";

	String TEST_PROJECT = "org.eclipse.mylyn.github.issues";

	/**
	 * Test the GitHubService issue searching implementation
	 */
	@Test
	public void searchIssues() throws Exception {
		final GitHubService service = new GitHubService();
		final GitHubIssues issues = service.searchIssues(TEST_USER,
				TEST_PROJECT, "open", "test");
		assertEquals(0, issues.getIssues().length);
	}

	/**
	 * Test the GitHubService implementation for opening a new issue.
	 */
	@Test
	public void openIssue() throws Exception {
		final GitHubService service = new GitHubService();
		final GitHubIssue issue = new GitHubIssue();
		issue.setUser(TEST_USER);
		issue.setBody("This is a test body");
		issue.setTitle("Issue Title");
		boolean result = service.openIssue(TEST_USER, TEST_PROJECT, issue,
				API_KEY);
		assertTrue(result);
	}

	/**
	 * Test the GitHubService implementation for adding a label to an existing
	 * issue.
	 */
	@Test
	public void addLabel() throws Exception {
		final GitHubService service = new GitHubService();
		final boolean result = service.addLabel(TEST_USER, TEST_PROJECT,
				"lame", 1, API_KEY);
		assertTrue(result);
	}

	/**
	 * Test the GitHubService implementation for removing an existing label from
	 * any GitHub issue.
	 */
	@Test
	public void removeLable() throws Exception {
		final GitHubService service = new GitHubService();
		final boolean result = service.removeLabel(TEST_USER, TEST_PROJECT,
				"lame", 1, API_KEY);
		assertTrue(result);
	}
}