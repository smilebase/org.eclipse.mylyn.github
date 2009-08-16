package org.eclipse.mylyn.github;

public class GitHubIssue {

	public GitHubIssue( String number, String user, String title, String body ) {
		this.number = number;
		this.user = user;
		this.title = title;
		this.body = body;
	}
	
	public GitHubIssue() {
		this.number = "";
		this.user = "";
		this.title = "";
		this.body = "";
	}
	
	private String number;
	
	private String user;

	private String title;

	private String body;
	
	public String getNumber() {
		return number;
	}
	
	public void setNumber( String number ) {
		this.number = number;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser( String user ) {
		this.user = user;
	}
	
	public String getTitle() {
		return title;
	}
		
	public void setTitle( String title ) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}
	
	public void setBody( String body ) {
		this.body = body;
	}

}
