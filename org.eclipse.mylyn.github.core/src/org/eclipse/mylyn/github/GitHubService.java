package org.eclipse.mylyn.github;

import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;

import com.google.gson.Gson;

public class GitHubService {

	private final String gitURLBase = "http://github.com/api/v2/json/";

	private final String gitURLSearch = "issues/search/";
	
	private final String gitIssueRoot = "issues/";

	private final HttpClient httpClient;

	private final Gson gson;

	public class GitHubAction {
		public final static String OPEN = "open/";
		public final static String CLOSE = "close/";
		public final static String SERACH = "search/";
		public final static String REOPEN = "reopen/";
		public final static String VIEW = "view/";
		public final static String REMOVE_LABEL = "label/remove/";
		public final static String ADD_LABEL = "label/add/";
		public final static String COMMENT = "comment/";
	}
	
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
	
	public GitHubIssues addLabel(String user, String repo, String label,
			int issueNumber,String api) throws GitHubServiceException
	{
		GitHubIssues issues = null;
		GetMethod method = null;
		try {
			// build HTTP GET method
			method = new GetMethod(gitURLBase + gitIssueRoot + GitHubAction.ADD_LABEL + user + "/" + repo + 
								"/" + label + "/" + Integer.toString(issueNumber) );
			// execute HTTP GET method
			if (httpClient.executeMethod(method) == HttpStatus.SC_OK) {
				// transform JSON to Java object
				System.out.println("[DEBUG] Response: " + method.getResponseBodyAsString());
				System.out.println("[DEBUG] URL: " + method.getURI());
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
		
	public GitHubIssues removeLabel(String user, String repo, String label,
			int issueNumber, String api) throws GitHubServiceException
	{
		GitHubIssues issues = null;
		GetMethod method = null;
		try {
			// build HTTP GET method
			method = new GetMethod(gitURLBase + gitIssueRoot + GitHubAction.REMOVE_LABEL + user + "/" + repo + 
								"/" + label + "/" + Integer.toString(issueNumber) );
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
	
	public boolean openIssue( String repo, GitHubIssue issue ) throws GitHubServiceException
	{
		
		if ( issue.getUser().equals("") || issue.getUser() == null ) {
			final Exception e = new Exception("No username set");
			throw new GitHubServiceException(e);
		}
		
		PutMethod method = null;
		int returnVal = 0; 
		try {
			String title = URLEncoder.encode( "title=" + issue.getTitle(), "UTF-8" );
			String body = URLEncoder.encode( "body=" + issue.getBody(), "UTF-8" );
			// execute HTTP GET method
			method = new PutMethod(gitURLBase + gitIssueRoot + GitHubAction.OPEN + issue.getUser() + "/" + repo+"?"+title+"&"+body);
			returnVal = httpClient.executeMethod(method);
			System.out.println("[DEBUG] Response: " + method.getResponseBodyAsString());
			System.out.println("[DEBUG] URL: " + method.getURI());
		} catch (RuntimeException runTimeException) {
			throw runTimeException;
		} catch (Exception e) {
			throw new GitHubServiceException(e);
		} finally { 
			if (method != null) {
				method.releaseConnection();
				if (returnVal == HttpStatus.SC_OK)
					return true;
			}
		}
		return false;
	}
}
