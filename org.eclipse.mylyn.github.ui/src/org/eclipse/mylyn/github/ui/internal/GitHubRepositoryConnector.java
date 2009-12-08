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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.github.GitHubIssue;
import org.eclipse.mylyn.github.GitHubIssues;
import org.eclipse.mylyn.github.GitHubService;
import org.eclipse.mylyn.github.GitHubServiceException;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

/**
 * GitHub connector.
 * 
 * @author Christian Trutz
 */
public class GitHubRepositoryConnector extends AbstractRepositoryConnector {

	/**
	 * GitHub kind.
	 */
	protected static final String KIND = "github";

	/**
	 * GitHub service which creates, lists, deletes, etc. GitHub tasks.
	 */
	private final GitHubService service = new GitHubService();

	/**
	 * GitHub specific {@link AbstractTaskDataHandler}.
	 */
	private final GitHubTaskDataHandler taskDataHandler = new GitHubTaskDataHandler();

	/**
	 * {@inheritDoc}
	 * 
	 * @return always {@code true}
	 */
	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return always {@code true}
	 */
	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #KIND
	 */
	@Override
	public String getConnectorKind() {
		return KIND;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return "GitHub";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractTaskDataHandler getTaskDataHandler() {
		return this.taskDataHandler;
	}

	@Override
	public IStatus performQuery(TaskRepository repository,
			IRepositoryQuery query, TaskDataCollector collector,
			ISynchronizationSession session, IProgressMonitor monitor) {

		IStatus result = Status.OK_STATUS;
		monitor.beginTask("Querying repository ...", IProgressMonitor.UNKNOWN);
		try {

			// perform query
			GitHubIssues issues = service.searchIssues(query
					.getAttribute("owner"), query.getAttribute("project"),
					query.getAttribute("status"), query
							.getAttribute("queryText"));

			// collect task data
			for (GitHubIssue issue : issues.getIssues()) {
				TaskData taskData = taskDataHandler.createPartialTaskData(
						repository, monitor, issue);
				collector.accept(taskData);
			}

			result = Status.OK_STATUS;
		} catch (GitHubServiceException gitHubServiceException) {
			// TODO inform user about this
			result = Status.CANCEL_STATUS;
		}

		monitor.done();
		return result;
	}

	@Override
	public TaskData getTaskData(TaskRepository repo, String taskId,
			IProgressMonitor monitor) throws CoreException {

		String kind = repo.getConnectorKind();
		String url = repo.getRepositoryUrl();

		return new TaskData(taskDataHandler.getAttributeMapper(repo), kind,
				url, taskId);
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String taskFullUrl) {
		return null;
	}

	@Override
	public String getTaskIdFromTaskUrl(String taskFullUrl) {
		return null;
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		return null;
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository taskRepository,
			IProgressMonitor monitor) throws CoreException {
	}

	@Override
	public boolean hasTaskChanged(TaskRepository repository, ITask task,
			TaskData taskData) {
		return new TaskMapper(taskData).hasChanges(task);
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository taskRepository,
			ITask task, TaskData taskData) {
		new TaskMapper(taskData).applyTo(task);
	}

}
