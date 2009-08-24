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

import java.util.Set;

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
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

/**
 * GitHub connector specific extensions.
 * 
 * @author Christian Trutz
 * @since 0.1.0
 */
public class GitHubRepositoryConnector extends AbstractRepositoryConnector {

	private static final String KIND = "github";

	private final GitHubService service = new GitHubService();

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
	}

	@Override
	public String getConnectorKind() {
		return KIND;
	}

	/**
	 * Get the connector kind.
	 * 
	 * @return The connector kind as a string
	 */
	public static String getKind() {
		return KIND;
	}

	@Override
	public String getLabel() {
		return "GitHub";
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

			TaskAttributeMapper taskAttributeMapper = new TaskAttributeMapper(
					repository);
			String repositoryUrl = repository.getRepositoryUrl();

			// collect task data
			for (GitHubIssue issue : issues.getIssues()) {
				TaskData data = new TaskData(taskAttributeMapper, getKind(),
						repositoryUrl, issue.getNumber());
				TaskAttribute root = data.getRoot();
				root.createAttribute(TaskAttribute.SUMMARY).setValue(
						issue.getTitle());
				collector.accept(data);
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
	public String getRepositoryUrlFromTaskUrl(String taskFullUrl) {
		return null;
	}

	@Override
	public TaskData getTaskData(TaskRepository taskRepository, String taskId,
			IProgressMonitor monitor) throws CoreException {
		TaskAttributeMapper taskAttributeMapper = new TaskAttributeMapper(
				taskRepository);
		return new TaskData(taskAttributeMapper, taskRepository
				.getConnectorKind(), taskRepository.getRepositoryUrl(), taskId);
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
	public boolean hasTaskChanged(TaskRepository taskRepository, ITask task,
			TaskData taskData) {
		return true;
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository taskRepository,
			IProgressMonitor monitor) throws CoreException {
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository taskRepository,
			ITask task, TaskData taskData) {
		ITaskMapping taskMapping = getTaskMapping(taskData);
		task.setSummary(taskMapping.getSummary());
	}

	@Override
	public AbstractTaskDataHandler getTaskDataHandler() {
		return new AbstractTaskDataHandler() {

			@Override
			public boolean initializeTaskData(TaskRepository repository,
					TaskData data, ITaskMapping initializationData,
					IProgressMonitor monitor) throws CoreException {

				return true;
			}

			@Override
			public TaskAttributeMapper getAttributeMapper(
					TaskRepository taskRepository) {
				return new TaskAttributeMapper(taskRepository);
			}

			@Override
			public RepositoryResponse postTaskData(TaskRepository repository,
					TaskData taskData, Set<TaskAttribute> oldAttributes,
					IProgressMonitor monitor) throws CoreException {
				return new RepositoryResponse(ResponseKind.TASK_CREATED,
						"ID12345");
			}
		};
	}

}
