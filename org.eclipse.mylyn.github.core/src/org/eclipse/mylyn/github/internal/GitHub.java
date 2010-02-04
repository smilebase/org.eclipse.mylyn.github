package org.eclipse.mylyn.github.internal;

import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class GitHub {
	public static final String BUNDLE_ID = "org.eclipse.mylyn.github.core";
	public static final String CONNECTOR_KIND = "github";

	public static final String HTTP_WWW_GITHUB_ORG = "http://www.github.org";

	public static final Pattern URL_PATTERN = Pattern.compile(Pattern.quote(HTTP_WWW_GITHUB_ORG)+"/([^/]+)/([^/]+)");

	public static IStatus createStatus(int severity, String message) {
		return new Status(severity, BUNDLE_ID, message);
	}

	public static IStatus createStatus(int severity, String message, Throwable e) {
		return new Status(severity, BUNDLE_ID, message, e);
	}

	public static IStatus createErrorStatus(String message) {
		return createStatus(IStatus.ERROR, message);
	}

	public static IStatus createErrorStatus(String message, Throwable t) {
		return createStatus(IStatus.ERROR, message, t);
	}

	public static IStatus createErrorStatus(Throwable e) {
		return createStatus(IStatus.ERROR, "Unexpected error: "
				+ e.getMessage(), e);
	}
}
