package org.eclipse.mylyn.github.ui.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.github.GitHubServiceException;

class GitHubUi {
	public static final String BUNDLE_ID = "org.eclipse.mylyn.github.ui";
	
	public static IStatus createStatus(int severity,String message) {
		return new Status(severity,BUNDLE_ID,message);
	}
	
	public static IStatus createStatus(int severity,String message,Throwable e) {
		return new Status(severity,BUNDLE_ID,message,e);
	}
	
	public static IStatus createErrorStatus(String message) {
		return createStatus(IStatus.ERROR, message);
	}
	
	public static IStatus createErrorStatus(String message,Throwable t) {
		return createStatus(IStatus.ERROR, message,t);
	}

	public static IStatus createErrorStatus(Throwable e) {
		return createStatus(IStatus.ERROR, "Unexpected error: "+e.getMessage(),e);
	}
}
