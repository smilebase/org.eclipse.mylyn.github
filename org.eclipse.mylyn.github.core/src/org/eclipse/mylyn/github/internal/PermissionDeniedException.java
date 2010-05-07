package org.eclipse.mylyn.github.internal;

import org.apache.commons.httpclient.StatusLine;

public class PermissionDeniedException extends GitHubServiceException {

	protected PermissionDeniedException(Exception exception) {
		super(exception);
	}

	protected PermissionDeniedException(StatusLine statusLine) {
		super(statusLine);
	}

	protected PermissionDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

	protected PermissionDeniedException(String message) {
		super(message);
	}

}
