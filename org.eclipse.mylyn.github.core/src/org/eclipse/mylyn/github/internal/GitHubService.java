/*
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

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;

/**
 * Facility to perform API operations on a GitHub issue tracker.
 */
public class GitHubService {

	private static final Log LOG = LogFactory.getLog(GitHubService.class);

	/**
	 * GitHub Issues API Documentation: http://develop.github.com/p/issues.html
	 */
	private final String gitURLBase = "https://github.com/api/v2/json/";

	private final String gitIssueRoot = "issues/";

	private final HttpClient httpClient;

	private final Gson gson;

	/**
	 * Helper class, describing all of the possible GitHub API actions.
	 */
	private final static String OPEN = "open/"; // Implemented
	private static final String REOPEN = "reopen/";
	private final static String CLOSE = "close/";
	private final static String EDIT = "edit/"; // Implemented
	// private final static String VIEW = "view/";
	private final static String SHOW = "show/"; // :user/:repo/:number
	private final static String LIST = "list/"; // Implemented
	private final static String SEARCH = "search/"; // Implemented
	// private final static String REOPEN = "reopen/";
	// private final static String COMMENT = "comment/";
	private final static String ADD_LABEL = "label/add/"; // Implemented
	private final static String REMOVE_LABEL = "label/remove/"; // Implemented


	/**
	 * Constructor, create the client and JSON/Java interface object.
	 */
	public GitHubService() {
		httpClient = new HttpClient();
		gson = new Gson();
	}

	/**
	 * Search the GitHub Issues API for a given search term
	 * 
	 * @param user
	 *            - The user the repository is owned by
	 * @param repo
	 *            - The Git repository where the issue tracker is hosted
	 * @param state
	 *            - The issue state you want to filter your search by
	 * @param searchTerm
	 *            - The text search term to find in the issues.
	 * 
	 * @return A GitHubIssues object containing all issues from the search
	 *         results
	 * 
	 * @throws GitHubServiceException
	 * 
	 * @note API Doc: /issues/search/:user/:repo/:state/:search_term
	 */
	public GitHubIssues searchIssues(final String user, final String repo,
			final String state, final String searchTerm)
			throws GitHubServiceException {
		GitHubIssues issues = null;
		GetMethod method = null;
		try {
			// build HTTP GET method
			if (searchTerm.trim().length() == 0) { // no search term: list all
				method = new GetMethod(gitURLBase + gitIssueRoot + LIST + user
						+ "/" + repo + "/" + state);
			} else {
				method = new GetMethod(gitURLBase + gitIssueRoot + SEARCH
						+ user + "/" + repo + "/" + state + "/" + searchTerm);
			}
			// execute HTTP GET method
			executeMethod(method);
			// transform JSON to Java object
			issues = gson.fromJson(new String(method.getResponseBody()),
					GitHubIssues.class);
		} catch (GitHubServiceException e) {
			throw e;
		} catch (final RuntimeException runtimeException) {
			throw runtimeException;
		} catch (final Exception exception) {
			throw new GitHubServiceException(exception);
		} finally {
			if (method != null)
				method.releaseConnection();
		}
		return issues;
	}

	/**
	 * Add a label to an existing GitHub issue.
	 * 
	 * @param user
	 *            - The user the repository is owned by
	 * @param repo
	 *            - The git repository where the issue tracker is hosted
	 * @param label
	 *            - The text label to add to the existing issue
	 * @param issueNumber
	 *            - The issue number to add a label to
	 * @param api
	 *            - The users GitHub
	 * 
	 * @return A boolean representing the success of the function call
	 * 
	 * @throws GitHubServiceException
	 * 
	 * @note API Doc: issues/label/add/:user/:repo/:label/:number API POST
	 *       Variables: login, api-token
	 */
	public boolean addLabel(final String user, final String repo,
			final String label, final int issueNumber,final GitHubCredentials credentials)
			throws GitHubServiceException {
		PostMethod method = null;

		boolean success = false;

		try {
			// build HTTP GET method
			method = new PostMethod(gitURLBase + gitIssueRoot + ADD_LABEL
					+ user + "/" + repo + "/" + label + "/"
					+ Integer.toString(issueNumber));

			// Set the users login and API token
			final NameValuePair login = new NameValuePair("login", credentials.getUsername());
			final NameValuePair token = new NameValuePair("token", credentials.getApiToken());
			method.setRequestBody(new NameValuePair[] { login, token });

			// execute HTTP GET method
			executeMethod(method);
			// Check the response, make sure the action was successful
			final String response = method.getResponseBodyAsString();
			if (response.contains(label.subSequence(0, label.length()))) {
				success = true;
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug("Response: " + method.getResponseBodyAsString());
				LOG.debug("URL: " + method.getURI());
			}
		} catch (GitHubServiceException e) {
			throw e;
		} catch (final RuntimeException runtimeException) {
			throw runtimeException;
		} catch (final Exception exception) {
			throw new GitHubServiceException(exception);
		} finally {
			if (method != null)
				method.releaseConnection();
		}
		return success;
	}

	/**
	 * Remove an existing label from an existing GitHub issue.
	 * 
	 * @param user
	 *            - The user the repository is owned by
	 * @param repo
	 *            - The git repository where the issue tracker is hosted
	 * @param label
	 * @param issueNumber
	 * @param api
	 * 
	 * @return A list of GitHub issues in the response text.
	 * 
	 * @throws GitHubServiceException
	 * 
	 *             API Doc: issues/label/remove/:user/:repo/:label/:number API
	 *             POST Variables: login, api-token
	 */
	public boolean removeLabel(final String user, final String repo,
			final String label, final int issueNumber, final GitHubCredentials credentials)
			throws GitHubServiceException {
		PostMethod method = null;
		boolean success = false;
		try {
			// build HTTP GET method
			method = new PostMethod(gitURLBase + gitIssueRoot + REMOVE_LABEL
					+ user + "/" + repo + "/" + label + "/"
					+ Integer.toString(issueNumber));

			// Set the users login and API token
			final NameValuePair login = new NameValuePair("login", credentials.getUsername());
			final NameValuePair token = new NameValuePair("token", credentials.getUsername());
			method.setRequestBody(new NameValuePair[] { login, token });

			// execute HTTP GET method
			executeMethod(method);
			// Check the response, make sure the action was successful
			final String response = method.getResponseBodyAsString();
			if (!response.contains(label.subSequence(0, label.length()))) {
				success = true;
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("Response: " + method.getResponseBodyAsString());
				LOG.debug("URL: " + method.getURI());
			}
		} catch (GitHubServiceException e) {
			throw e;
		} catch (final RuntimeException runtimeException) {
			throw runtimeException;
		} catch (final Exception exception) {
			throw new GitHubServiceException(exception);
		} finally {
			if (method != null)
				method.releaseConnection();
		}
		return success;
	}

	/**
	 * Open a new issue using the GitHub Issues API.
	 * 
	 * @param user
	 *            - The user the repository is owned by
	 * @param repo
	 *            - The git repository where the issue tracker is hosted
	 * @param issue
	 *            - The GitHub issue object to create on the issue tracker.
	 * 
	 * @return the issue that was created
	 * 
	 * @throws GitHubServiceException
	 * 
	 *             API Doc: issues/open/:user/:repo API POST Variables: login,
	 *             api-token, title, body
	 */
	public GitHubIssue openIssue(final String user, final String repo,
			final GitHubIssue issue, final GitHubCredentials credentials)
			throws GitHubServiceException {

		GitHubShowIssue showIssue = null;

		PostMethod method = null;
		try {
			// Create the HTTP POST method
			method = new PostMethod(gitURLBase + gitIssueRoot + OPEN + user
					+ "/" + repo);
			// Set the users login and API token
			final NameValuePair login = new NameValuePair("login", credentials.getUsername());
			final NameValuePair token = new NameValuePair("token", credentials.getApiToken());
			final NameValuePair body = new NameValuePair("body", issue
					.getBody());
			final NameValuePair title = new NameValuePair("title", issue
					.getTitle());

			method.setRequestBody(new NameValuePair[] { login, token, body,
					title });

			executeMethod(method);
			showIssue = gson.fromJson(new String(method.getResponseBody()),
					GitHubShowIssue.class);

			
			if (showIssue == null || showIssue.getIssue() == null) {
				if (LOG.isErrorEnabled()) {
					LOG.error("Unexpected server response: "+method.getResponseBodyAsString());
				}
				throw new GitHubServiceException("Unexpected server response");
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("Response: " + method.getResponseBodyAsString());
				LOG.debug("URL: " + method.getURI());
			}
			return showIssue.getIssue();
		} catch (GitHubServiceException e) {
			throw e;
		} catch (final RuntimeException runTimeException) {
			throw runTimeException;
		} catch (final Exception e) {
			throw new GitHubServiceException(e);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}

	/**
	 * Edit an existing issue using the GitHub Issues API.
	 * 
	 * @param user
	 *            - The user the repository is owned by
	 * @param repo
	 *            - The git repository where the issue tracker is hosted
	 * @param issue
	 *            - The GitHub issue object to create on the issue tracker.
	 * 
	 * @return the issue with changes
	 * 
	 * @throws GitHubServiceException
	 * 
	 *             API Doc: issues/edit/:user/:repo/:number API POST Variables:
	 *             login, api-token, title, body
	 */
	public GitHubIssue editIssue(final String user, final String repo,
			final GitHubIssue issue,final GitHubCredentials credentials)
			throws GitHubServiceException {
		PostMethod method = null;
		try {
			
			// Create the HTTP POST method
			method = new PostMethod(gitURLBase + gitIssueRoot + EDIT + user
					+ "/" + repo + "/" + issue.getNumber());
			// Set the users login and API token
			final NameValuePair login = new NameValuePair("login", credentials.getUsername());
			final NameValuePair token = new NameValuePair("token", credentials.getApiToken());
			final NameValuePair body = new NameValuePair("body", issue
					.getBody());
			final NameValuePair title = new NameValuePair("title", issue
					.getTitle());

			method.setRequestBody(new NameValuePair[] { login, token, body,
					title });

			executeMethod(method);
			GitHubShowIssue showIssue = gson.fromJson(method.getResponseBodyAsString(),
						GitHubShowIssue.class);
				
			// Make sure the changes were made properly
			if (showIssue == null || showIssue.getIssue() == null) {
				if (LOG.isErrorEnabled()) {
					LOG.error("Unexpected server response: "+method.getResponseBodyAsString());
				}
				throw new GitHubServiceException("Unexpected server response");
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug("Response: " + method.getResponseBodyAsString());
				LOG.debug("URL: " + method.getURI());
			}
			return showIssue.getIssue();
		} catch (final RuntimeException runTimeException) {
			throw runTimeException;
		} catch (final Exception e) {
			throw new GitHubServiceException(e);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}


	public GitHubIssue showIssue(final String user, final String repo,final String issueNumber) throws GitHubServiceException {
		GetMethod method = null;
		try {
			// build HTTP GET method
			method = new GetMethod(gitURLBase + gitIssueRoot + SHOW 
            + user + "/" + repo + "/" + issueNumber);
			
			// execute HTTP GET method
			executeMethod(method);
			// transform JSON to Java object
			GitHubShowIssue issue = gson.fromJson(new String(method.getResponseBody()),
					GitHubShowIssue.class);
			
			return issue.getIssue();
		} catch (GitHubServiceException e) {
			throw e;
		} catch (final RuntimeException runtimeException) {
			throw runtimeException;
		} catch (final Exception exception) {
			throw new GitHubServiceException(exception);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}

	private void executeMethod(HttpMethod method) throws GitHubServiceException {
		int status;
		try {
			status = httpClient.executeMethod(method);
		} catch (HttpException e) { 
			throw new GitHubServiceException(e);
		} catch (IOException e) {
			throw new GitHubServiceException(e);
		}
		if (status != HttpStatus.SC_OK) {
			switch (status) {
			case HttpStatus.SC_UNAUTHORIZED:
			case HttpStatus.SC_FORBIDDEN:
				throw new PermissionDeniedException(method.getStatusLine());
			default:
				throw new GitHubServiceException(method.getStatusLine());
			}
		}
	}

	/**
	 * Edit an existing issue using the GitHub Issues API and change its status to open.
	 * 
	 * @param user
	 *            - The user the repository is owned by
	 * @param repo
	 *            - The git repository where the issue tracker is hosted
	 * @param issue
	 *            - The GitHub issue object to create on the issue tracker.
	 * 
	 * @return the issue with changes
	 * 
	 * @throws GitHubServiceException
	 * 
	 *             API Doc: issues/reopen/:user/:repo/:number API POST Variables:
	 *             login, api-token, title, body
	 */
	public GitHubIssue reopenIssue(String user, String repo, GitHubIssue issue,
			GitHubCredentials credentials) throws GitHubServiceException {
		issue = editIssue(user, repo, issue, credentials);
		return changeIssueStatus(user, repo, REOPEN, issue, credentials);
	}

	/**
	 * Edit an existing issue using the GitHub Issues API and change its status to closed.
	 * 
	 * @param user
	 *            - The user the repository is owned by
	 * @param repo
	 *            - The git repository where the issue tracker is hosted
	 * @param issue
	 *            - The GitHub issue object to create on the issue tracker.
	 * 
	 * @return the issue with changes
	 * 
	 * @throws GitHubServiceException
	 * 
	 *             API Doc: issues/close/:user/:repo/:number API POST Variables:
	 *             login, api-token, title, body
	 */
	public GitHubIssue closeIssue(String user, String repo, GitHubIssue issue,
			GitHubCredentials credentials) throws GitHubServiceException {
		issue = editIssue(user, repo, issue, credentials);
		return changeIssueStatus(user, repo, CLOSE, issue, credentials);
		
	}
	

	private GitHubIssue changeIssueStatus(final String user, final String repo,
			String githubOperation, final GitHubIssue issue,
			final GitHubCredentials credentials) throws GitHubServiceException {
		PostMethod method = null;
		try {
			
			// Create the HTTP POST method
			method = new PostMethod(gitURLBase + gitIssueRoot + githubOperation + user
					+ "/" + repo + "/" + issue.getNumber());
			// Set the users login and API token
			final NameValuePair login = new NameValuePair("login", credentials.getUsername());
			final NameValuePair token = new NameValuePair("token", credentials.getApiToken());
			
			method.setRequestBody(new NameValuePair[] { login, token });

			executeMethod(method);
			GitHubShowIssue showIssue = gson.fromJson(method.getResponseBodyAsString(),
						GitHubShowIssue.class);
				
			// Make sure the changes were made properly
			if (showIssue == null || showIssue.getIssue() == null) {
				if (LOG.isErrorEnabled()) {
					LOG.error("Unexpected server response: "+method.getResponseBodyAsString());
				}
				throw new GitHubServiceException("Unexpected server response");
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug("Response: " + method.getResponseBodyAsString());
				LOG.debug("URL: " + method.getURI());
			}
			return showIssue.getIssue();
		} catch (final RuntimeException runTimeException) {
			throw runTimeException;
		} catch (final Exception e) {
			throw new GitHubServiceException(e);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}
}
