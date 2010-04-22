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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.mylyn.tasks.ui.TasksUi;
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

	private final Pattern issuePattern = Pattern.compile("(?:([a-zA-Z0-9_\\.-]+)(?:/([a-zA-Z0-9_\\.-]+))?)?\\#(\\d+)");
	
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
	
	
	public IHyperlink[] findHyperlinks(TaskRepository repository, String text, int index, int textOffset) {
		List<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
		
		Matcher matcher = issuePattern.matcher(text);
		while (matcher.find()) {
			if (index == -1 || (index >= matcher.start() && index <= matcher.end())) {
				String user = matcher.group(1);
				String project = matcher.group(2);
				String taskId = matcher.group(3);
				
				if (project == null && user != null) {
					// same project name, different user
					String url = repository.getUrl();
					project = GitHub.computeTaskRepositoryProject(url);
				}
				
				TaskRepository taskRepository = null;
				if (user == null && project == null) { 
					taskRepository = repository;
				} else if (user != null && project != null) {
					String repositoryUrl = GitHub.createGitHubUrl(user,project);
					taskRepository = TasksUi.getRepositoryManager().getRepository(GitHub.CONNECTOR_KIND, repositoryUrl);
					if (taskRepository == null) {
						repositoryUrl = GitHub.createGitHubUrlAlternate(user,project);
						taskRepository = TasksUi.getRepositoryManager().getRepository(GitHub.CONNECTOR_KIND, repositoryUrl);	
					}
				}
				if (taskRepository != null) {
					Region region = createRegion(textOffset, matcher);
					hyperlinks.add(new TaskHyperlink(region, repository, taskId));
				} else if (user != null && project != null) {
					Region region = createRegion(textOffset, matcher);
					String url = GitHub.createGitHubUrl(user, project)+"/issues/issue/"+taskId;
					hyperlinks.add(new URLHyperlink(region, url));
				}
			}
		}
		return hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
	}

	private Region createRegion(int textOffset, Matcher matcher) {
		return new Region(matcher.start()+textOffset,matcher.end()-matcher.start());
	}
}
