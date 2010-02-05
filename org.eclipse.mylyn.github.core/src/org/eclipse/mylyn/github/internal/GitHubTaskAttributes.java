package org.eclipse.mylyn.github.internal;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public enum GitHubTaskAttributes {

	KEY("Key",TaskAttribute.TASK_KEY,TaskAttribute.TYPE_SHORT_TEXT,true,true),
	TITLE("Summary",TaskAttribute.SUMMARY,TaskAttribute.TYPE_SHORT_TEXT,false,true), 
	BODY("Description",TaskAttribute.DESCRIPTION,TaskAttribute.TYPE_LONG_RICH_TEXT,false,true),
	
	CREATION_DATE("Created",TaskAttribute.DATE_CREATION,TaskAttribute.TYPE_DATETIME,true,false),
	MODIFICATION_DATE("Modified",TaskAttribute.DATE_MODIFICATION,TaskAttribute.TYPE_DATETIME,true,false),
	CLOSED_DATE("Closed",TaskAttribute.DATE_COMPLETION,TaskAttribute.TYPE_DATETIME,true,false),
	
	STATUS("Status",TaskAttribute.STATUS,TaskAttribute.TYPE_SHORT_TEXT,false,true)
	;
	

	private String label;
	private String commonKey;
	private boolean readOnly;
	private boolean initTask;
	private final String type;
	
	private GitHubTaskAttributes(String label, String commonKey,String type, boolean readOnly, boolean initTask) {
		this.label = label;
		this.commonKey = commonKey;
		this.type = type;
		this.readOnly = readOnly;
		this.initTask = initTask;
	}
	
	public String getLabel() {
		return label;
	}
	public String getCommonKey() {
		return commonKey;
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
