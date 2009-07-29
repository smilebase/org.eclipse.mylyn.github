package org.eclipse.mylyn.github.ui.internal;

import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.IFormPage;

public class TaskEditorPageFactory extends AbstractTaskEditorPageFactory {

	public TaskEditorPageFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canCreatePageFor(TaskEditorInput input) {
		if (TasksUiUtil.isOutgoingNewTask(input.getTask(),
				RepositoryConnector.KIND)) {
			return true;
		}
		return false;
	}

	@Override
	public Image getPageImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPageText() {
		return "GitHub";
	}

	@Override
	public IFormPage createPage(TaskEditor parentEditor) {
		return new TaskEditorPage(parentEditor);
	}

}
