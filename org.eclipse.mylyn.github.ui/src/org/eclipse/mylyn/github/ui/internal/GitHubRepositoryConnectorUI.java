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
import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
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

	/**
	 * 
	 * 
	 * @return the unique type of the repository: "github"
	 */
	@Override
	public String getConnectorKind() {
		return GitHub.CONNECTOR_KIND;
	}

	/**
	 * 
	 * 
	 * @return {@link AbstractRepositorySettingsPage} with GitHub specific
	 *         parameter like user name, password, ...
	 */
	@Override
	public ITaskRepositoryPage getSettingsPage(
			final TaskRepository taskRepository) {
		return new GitHubRepositorySettingsPage(taskRepository);
	}

	/**
	 * 
	 * 
	 * @return {@link NewTaskWizard} with GitHub specific tab
	 */
	@Override
	public IWizard getNewTaskWizard(final TaskRepository taskRepository,
			final ITaskMapping taskSelection) {
		return new NewTaskWizard(taskRepository, taskSelection);
	}

	/**
	 * This {@link AbstractRepositoryConnectorUi} has search page.
	 * 
	 * @return {@code true}
	 */
	@Override
	public boolean hasSearchPage() {
		return true;
	}

	/**
	 * Returns {@link IWizard} used in Mylyn for creating new queries. This
	 * {@link IWizard} has a wizard page for creating GitHub specific task
	 * queries.
	 * 
	 * @return {@link RepositoryQueryWizard} with GitHub specific query page
	 */
	@Override
	public IWizard getQueryWizard(final TaskRepository taskRepository,
			final IRepositoryQuery queryToEdit) {
		RepositoryQueryWizard wizard = new RepositoryQueryWizard(taskRepository);
		GitHubRepositoryQueryPage queryPage = new GitHubRepositoryQueryPage(
				taskRepository, queryToEdit);
		wizard.addPage(queryPage);
		return wizard;
	}

}
