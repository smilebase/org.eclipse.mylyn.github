package org.eclipse.mylyn.github.internal;

public enum GitHubTaskOperation {
	LEAVE("Leave as "),
	REOPEN("Reopen"),
	CLOSE("Close");
	
	private final String label;

	private GitHubTaskOperation(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}

	public String getId() {
		return name();
	}

	/**
	 * get the operation by its id
	 * @param opId the id, or null
	 * @return the operation, or null if the id was null or did not match any operation
	 */
	public static GitHubTaskOperation fromId(String opId) {
		for (GitHubTaskOperation op: values()) {
			if (op.getId().equals(opId)) {
				return op;
			}
		}
		return null;
	}
	
}
