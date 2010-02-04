package org.eclipse.mylyn.github;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public enum GitHubTaskAttributes {

	KEY("Key",TaskAttribute.TASK_KEY,true,true),
	TITLE("Summary",TaskAttribute.SUMMARY,false,true), 
	BODY("Description",TaskAttribute.DESCRIPTION,false,true);

	private String label;
	private String type;
	private boolean readOnly;
	private boolean initTask;
	
	private GitHubTaskAttributes(String label, String type, boolean readOnly, boolean initTask) {
		this.label = label;
		this.type = type;
		this.readOnly = readOnly;
		this.initTask = initTask;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getType() {
		return type;
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}

	public String getKind() {
		return TaskAttribute.KIND_DEFAULT;
	}
	
	public boolean isInitTask() {
		return initTask;
	}
}
