/*
 * Copyright 2009 Christian Trutz 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 */
package org.eclipse.mylyn.github.ui.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.github.internal.GitHubRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Editor page factory for GitHub.
 * 
 * @author Christian Trutz
 * @since 0.1.0
 */
public class GitHubTaskEditorPageFactory extends AbstractTaskEditorPageFactory {

	private Image gitLogoImage = null;

	@Override
	public boolean canCreatePageFor(TaskEditorInput input) {
		if (GitHub.CONNECTOR_KIND.equals(
				input.getTask().getConnectorKind())) {
			return true;
		}
		if (TasksUiUtil.isOutgoingNewTask(input.getTask(),
				GitHub.CONNECTOR_KIND)) {
			return true;
		}
		return false;
	}

	@Override
	public Image getPageImage() {
		if (gitLogoImage != null)
			return gitLogoImage;
		ImageDescriptor imageDescriptor = AbstractUIPlugin
				.imageDescriptorFromPlugin("org.eclipse.mylyn.github.ui",
						"images/git-logo.png");
		if (imageDescriptor == null) {
			return null;
		}

		return gitLogoImage = new Image(Display.getCurrent(), imageDescriptor
				.getImageData());
	}

	@Override
	public String getPageText() {
		return "GitHub";
	}

	@Override
	public IFormPage createPage(TaskEditor parentEditor) {
		return new GitHubTaskEditorPage(parentEditor);
	}

}
