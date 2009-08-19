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
package org.eclipse.mylyn.github;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;

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
	public class GitHubAction {
		public final static String OPEN         = "open/";         // Implemented
		public final static String CLOSE        = "close/";
		public final static String EDIT         = "edit/";         // Implemented
		public final static String VIEW         = "view/";
		public final static String LIST         = "list/";
		public final static String SEARCH       = "search/";       // Implemented
		public final static String REOPEN       = "reopen/";
		public final static String COMMENT      = "comment/";
		public final static String ADD_LABEL    = "label/add/";    // Implemented
		public final static String REMOVE_LABEL = "label/remove/"; // Implemented
	}

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
	 *            - The git repository where the issue tracker is hosted
	 * @param state
	 *            - The issue state you want to filter your search by
	 * @param searchTerm
	 *            - The text search term to find in the issues.
	 * 
	 * @return A GitHubIssues object containing all issues from the search
	 *         results
	 * 
	 * @throws GitHubServiceException
	 */
	public GitHubIssues searchIssues(String user, String repo, String state,
			String searchTerm) throws GitHubServiceException {
		GitHubIssues issues = null;
		GetMethod method = null;
		try {
			// build HTTP GET method
			method = new GetMethod(gitURLBase + gitIssueRoot +
				GitHubAction.SEARCH+user+"/"+repo+"/"+state+"/"+searchTerm );
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
	 * @return
	 * 
	 * @throws GitHubServiceException
	 */
	public boolean addLabel(String user, String repo, String label,
			int issueNumber, String api) throws GitHubServiceException {
		PostMethod method = null;

		boolean success = false;

		try {
			// build HTTP GET method
			method = new PostMethod(gitURLBase + gitIssueRoot
					+ GitHubAction.ADD_LABEL + user + "/" + repo + "/" + label
					+ "/" + Integer.toString(issueNumber));

			// Set the users login and API token 
			NameValuePair login = new NameValuePair( "login", user );
			NameValuePair token = new NameValuePair( "token", api );
			method.setRequestBody(new NameValuePair[] { login, token });

			// execute HTTP GET method
			if (httpClient.executeMethod(method) == HttpStatus.SC_OK) {
				// Check the response, make sure the action was successful
				String response = method.getResponseBodyAsString();
				if (response.contains(label.subSequence(0, label.length()))) {
					success = true;
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug("Response: " + method.getResponseBodyAsString());
					LOG.debug("URL: " + method.getURI());
				}
			}
		} catch (RuntimeException runtimeException) {
			throw runtimeException;
		} catch (Exception exception) {
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
	 * @return A list of GitHub issues in the respone text.
	 * 
	 * @throws GitHubServiceException
	 */
	public boolean removeLabel( String user, String repo, String label,
			int issueNumber, String api ) throws GitHubServiceException {
		PostMethod method = null;
		boolean success = false;
		try {
			// build HTTP GET method
			method = new PostMethod(gitURLBase + gitIssueRoot
					+ GitHubAction.REMOVE_LABEL + user + "/" + repo + "/"
					+ label + "/" + Integer.toString(issueNumber));

			// Set the users login and API token 
			NameValuePair login = new NameValuePair( "login", user );
			NameValuePair token = new NameValuePair( "token", api );
			method.setRequestBody( new NameValuePair[] { login, token } );

			// execute HTTP GET method
			if (httpClient.executeMethod(method) == HttpStatus.SC_OK) {
				// Check the response, make sure the action was successful
				String response = method.getResponseBodyAsString();
				if ( !response.contains(label.subSequence(0, label.length())) ) {
					success = true;
				}
				if (LOG.isDebugEnabled() ) {
					LOG.debug("Response: " + method.getResponseBodyAsString());
					LOG.debug("URL: " + method.getURI());
				}
			}
		} catch (RuntimeException runtimeException) {
			throw runtimeException;
		} catch (Exception exception) {
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
	 * @return A true/false describing the success of the operation.
	 * 
	 * @throws GitHubServiceException
	 */
	public boolean openIssue(String user, String repo, GitHubIssue issue, String api)
			throws GitHubServiceException {

		if (issue.getUser().equals("") || issue.getUser() == null) {
			final Exception e = new Exception("No username set");
			throw new GitHubServiceException(e);
		}

		GitHubIssues issues = null;

		PostMethod method = null;
		boolean success = false;
		try {
			// Create the HTTP POST method
			method = new PostMethod(gitURLBase + gitIssueRoot
					+ GitHubAction.OPEN + user + "/" + repo );
			// Set the users login and API token 
			NameValuePair login = new NameValuePair( "login", user );
			NameValuePair token = new NameValuePair( "token", api );
			NameValuePair body = new NameValuePair( "body", issue.getBody() );
			NameValuePair title = new NameValuePair( "title", issue.getTitle() );

			method.setRequestBody( new NameValuePair[] { login, token, body, title } );

			if( httpClient.executeMethod(method) == HttpStatus.SC_OK )
			{
				issues = gson.fromJson(new String(method.getResponseBody()),
						GitHubIssues.class);

				GitHubIssue ghIssues[] = issues.getIssues();
				for( int i = 0; i < issues.getIssues().length; i++ )
				{
					boolean titleOk = ghIssues[i].getTitle().equals( issue.getTitle() );
					boolean bodyOk = ghIssues[i].getBody().equals( issue.getBody() );
					boolean userOk = ghIssues[i].getUser().equals( issue.getUser() );
					success = titleOk && bodyOk && userOk;
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug("Response: " + method.getResponseBodyAsString());
					LOG.debug("URL: " + method.getURI());
				}
			}
		} catch (RuntimeException runTimeException) {
			throw runTimeException;
		} catch (Exception e) {
			throw new GitHubServiceException(e);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		return success;
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
	 * @return A true/false describing the success of the operation.
	 * 
	 * @throws GitHubServiceException
	 */
	public boolean editIssue(String user, String repo, GitHubIssue issue, String api)
			throws GitHubServiceException {

		if (issue.getUser().equals("") || issue.getUser() == null) {
			final Exception e = new Exception("No username set");
			throw new GitHubServiceException(e);
		}

		GitHubIssues issues = null;

		PostMethod method = null;
		boolean success = false;
		try {
			// Create the HTTP POST method
			method = new PostMethod(gitURLBase + gitIssueRoot
					+ GitHubAction.EDIT + user + "/" + repo +"/" + issue.getNumber() );
			// Set the users login and API token 
			NameValuePair login = new NameValuePair( "login", user );
			NameValuePair token = new NameValuePair( "token", api );
			NameValuePair body = new NameValuePair( "body", issue.getBody() );
			NameValuePair title = new NameValuePair( "title", issue.getTitle() );

			method.setRequestBody( new NameValuePair[] { login, token, body, title } );

			if( httpClient.executeMethod(method) == HttpStatus.SC_OK )
			{
				issues = gson.fromJson( method.getResponseBodyAsString(),
					GitHubIssues.class );
				GitHubIssue ghIssues[] = issues.getIssues();
				// Make sure the changes were made properly
				for( int i = 0; i < issues.getIssues().length; i++ )
				{
					boolean titleOk = ghIssues[i].getTitle().equals( issue.getTitle() );
					boolean bodyOk = ghIssues[i].getBody().equals( issue.getBody() );
					boolean userOk = ghIssues[i].getUser().equals( issue.getUser() );
					if(titleOk && bodyOk && userOk) {
						success = true;
						break;
					}
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug("Response: " + method.getResponseBodyAsString());
					LOG.debug("URL: " + method.getURI());
				}
			}
		} catch (RuntimeException runTimeException) {
			throw runTimeException;
		} catch (Exception e) {
			throw new GitHubServiceException(e);
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		return success;
	}
}
