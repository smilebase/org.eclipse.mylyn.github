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
 */
public class GitHubRepositoryConnector extends AbstractRepositoryConnector {

	protected static final String KIND = "github";

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
				TaskData data = new TaskData(taskAttributeMapper, KIND,
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
	public TaskData getTaskData(TaskRepository repo, String taskId,
			IProgressMonitor monitor) throws CoreException {

		TaskAttributeMapper mapper;
		mapper = new TaskAttributeMapper(repo);

		String kind = repo.getConnectorKind();
		String url = repo.getRepositoryUrl();

		return new TaskData(mapper, kind, url, taskId);
	}

	/** Not used because with github we can construct these on our own. */
	@Override
	public String getTaskIdFromTaskUrl(String taskFullUrl) {
		return null;
	}

	/** Note used because with github we can construct these on our own */
	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		return null;
	}

	// unnecessary: @SuppressWarnings("deprecation")
	@Override
	public boolean hasTaskChanged(TaskRepository repository, ITask task,
			TaskData taskData) {
		GitHubIssue issue = new GitHubIssue();

		TaskAttribute root = taskData.getRoot();
		TaskAttribute title = root.getAttribute(TaskAttribute.SUMMARY);
		TaskAttribute body = root.getAttribute(TaskAttribute.DESCRIPTION);

		String titleStr = title.getValue();
		String bodyStr = body.getValue();
		String userName = repository.getUserName();
		String userStr = repository.getUserName();
		String idStr = taskData.getTaskId();
		String repo = repository.getRepositoryLabel();

		issue.setBody(bodyStr);
		issue.setTitle(titleStr);
		issue.setUser(userStr);
		issue.setNumber(idStr);

		GitHubIssues issues = null;
		try {
			issues = service.searchIssues(userName, repo, "open", titleStr);
		} catch (GitHubServiceException e) {
			return true;
		}

		boolean result = true;
		// Make check if all the values are the same.
		if (issues != null) {
			for (GitHubIssue isu : issues.getIssues()) {
				if (isu.getNumber().equals(idStr)) {
					result = isu.getBody().equals(bodyStr)
							&& isu.getTitle().equals(titleStr)
							&& issue.getUser().equals(userName);
					break;
				}
			}
		}
		return !result;
	}

	/** Not real sure what this is for */

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

			@SuppressWarnings("deprecation")
			@Override
			public RepositoryResponse postTaskData(TaskRepository repository,
					TaskData taskData, Set<TaskAttribute> oldAttributes,
					IProgressMonitor monitor) throws CoreException {

				GitHubIssue issue = new GitHubIssue();

				TaskAttribute root = taskData.getRoot();
				TaskAttribute title = root.getAttribute(TaskAttribute.SUMMARY);
				TaskAttribute body = root
						.getAttribute(TaskAttribute.DESCRIPTION);

				String userName = repository.getUserName();
				String apiKey = repository.getHttpPassword();
				String titleStr = title.getValue();
				String bodyStr = body.getValue();
				String userStr = repository.getUserName();
				String idStr = taskData.getTaskId();
				String repo = repository.getRepositoryLabel();

				issue.setBody(bodyStr);
				issue.setTitle(titleStr);
				issue.setUser(userStr);
				issue.setNumber(idStr);

				boolean result;

				try {
					if (taskData.isNew()) {
						result = service.openIssue(userName, repo, issue,
								apiKey);
					} else {
						result = service.editIssue(userName, repo, issue,
								apiKey);
					}
				} catch (GitHubServiceException e) {
					IStatus status = Status.CANCEL_STATUS;
					CoreException up = new CoreException(status);
					throw up;
				}

				if (result) {
					return new RepositoryResponse(ResponseKind.TASK_CREATED,
							taskData.getTaskId());
				}
				// Error occurred when trying to open the issue.
				IStatus status = Status.CANCEL_STATUS;
				CoreException up = new CoreException(status);
				throw up;

			}
		};
	}
}
