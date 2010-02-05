package org.eclipse.mylyn.github.internal;

import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public class GitHubCredentials {
	private final String username;
	private final String apiToken;
	
	
	public GitHubCredentials(String username, String apiToken) {
		this.username = username;
		this.apiToken = apiToken;
	}
	
	public GitHubCredentials(AuthenticationCredentials credentials) {
		this(credentials.getUserName(),credentials.getPassword());
	}

	public static GitHubCredentials create(TaskRepository repository) {
		return new GitHubCredentials(repository.getCredentials(AuthenticationType.REPOSITORY));
	}
	
	public String getUsername() {
		return username;
	}
	public String getApiToken() {
		return apiToken;
	}
	
}
