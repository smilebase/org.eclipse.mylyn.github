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

import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;

/**
 * Editor page for GitHub.
 * 
 * @author Christian Trutz
 * @since 0.1.0
 */
public class GitHubTaskEditorPage extends AbstractTaskEditorPage {

	/**
	 * Constructor for the GitHubTaskEditorPage
	 * 
	 * @param editor
	 *            The task editor to create for GitHub
	 */
	public GitHubTaskEditorPage(final TaskEditor editor) {
		super(editor, GitHub.CONNECTOR_KIND);
		setNeedsPrivateSection(false);
		setNeedsSubmitButton(true);
	}
	@Override
	protected AttributeEditorFactory createAttributeEditorFactory() {
		return new AttributeEditorFactory(getModel(), getTaskRepository(), getEditorSite()) {
			@Override
			public AbstractAttributeEditor createEditor(String type,
					TaskAttribute taskAttribute) {
				// TODO Auto-generated method stub
				return super.createEditor(type, taskAttribute);
			}
		};
	}
}
