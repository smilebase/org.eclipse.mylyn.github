package org.eclipse.mylyn.github;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import com.google.gson.Gson;

public class GitHubService {

	private final String gitURLBase = "http://github.com/api/v2/json/";

	private final String gitURLSearch = "issues/search/";

	private final HttpClient httpClient;

	private final Gson gson;

	/**
	 * 
	 */
	public GitHubService() {
		httpClient = new HttpClient();
		gson = new Gson();
	}

	/**
	 * 
	 * @param user
	 * @param repo
	 * @param state
	 * @param searchTerm
	 * @return all founded issues
	 * @throws GitHubServiceException
	 */
	public GitHubIssues searchIssues(String user, String repo, String state,
			String searchTerm) throws GitHubServiceException {
		GitHubIssues issues = null;
		GetMethod method = null;
		try {
			// build HTTP GET method
			method = new GetMethod(gitURLBase + gitURLSearch + user + "/"
					+ repo + "/" + state + "/" + searchTerm);
			// execute HTTP GET method
			if (httpClient.executeMethod(method) == HttpStatus.SC_OK) {
				// transform JSON to Java object
				issues = gson.fromJson(new String(method.getResponseBody()),
						GitHubIssues.class);
			}
		} catch (RuntimeException runtimeException) {
			throw runtimeException;
		} catch (Exception exception) {
			throw new GitHubServiceException(exception);
		} finally {
			if (method != null)
				method.releaseConnection();
		}
		return issues;

	}
}
