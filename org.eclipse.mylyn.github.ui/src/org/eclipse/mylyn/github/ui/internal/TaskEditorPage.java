package org.eclipse.mylyn.github.ui.internal;

import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;

public class TaskEditorPage extends AbstractTaskEditorPage {

	public TaskEditorPage(TaskEditor editor) {
		super(editor, GitHubRepositoryConnector.KIND);
		setNeedsPrivateSection(false);
	}

}
