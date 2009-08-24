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

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;

/**
 * GitHub connector specific UI extensions.
 * 
 * @author Christian Trutz
 * @since 0.1.0
 */
public class GitHubRepositoryConnectorUI extends AbstractRepositoryConnectorUi {

	@Override
	public String getConnectorKind() {
		return GitHubRepositoryConnector.getKind();
	}

	@Override
	public ITaskRepositoryPage getSettingsPage(TaskRepository taskRepository) {
		return new GitHubRepositorySettingsPage(taskRepository);
	}

	@Override
	public boolean hasSearchPage() {
		return true;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository,
			ITaskMapping taskSelection) {
		return new NewTaskWizard(taskRepository, taskSelection);
	}

	@Override
	public IWizard getQueryWizard(TaskRepository taskRepository,
			IRepositoryQuery queryToEdit) {
		RepositoryQueryWizard wizard = new RepositoryQueryWizard(taskRepository);
		GitHubRepositoryQueryPage queryPage = new GitHubRepositoryQueryPage(
				taskRepository, queryToEdit);
		wizard.addPage(queryPage);
		return wizard;
	}

}
