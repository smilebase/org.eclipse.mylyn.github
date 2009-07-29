package org.eclipse.mylyn.github;

public final class GitHubServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6287902058352190022L;

	/**
	 * @param exception
	 */
	protected GitHubServiceException(final Exception exception) {
		super(exception);
	}

}
