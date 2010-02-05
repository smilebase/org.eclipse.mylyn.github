package org.eclipse.mylyn.github.internal;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * 
 * @author Christian Trutz
 */
public class GitHubTaskDataHandler extends AbstractTaskDataHandler {

	private static final String DATA_VERSION = "1";
	/**
	 * 
	 */
	private GitHubTaskAttributeMapper taskAttributeMapper = null;
	private final GitHubRepositoryConnector connector;

	public GitHubTaskDataHandler(GitHubRepositoryConnector connector) {
		this.connector = connector;
	}
	
	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository taskRepository) {
		if (this.taskAttributeMapper == null)
			this.taskAttributeMapper = new GitHubTaskAttributeMapper(
					taskRepository);
		return this.taskAttributeMapper;
	}

	public TaskData createPartialTaskData(TaskRepository repository,
			IProgressMonitor monitor,String user, String project, GitHubIssue issue) {

		TaskData data = new TaskData(getAttributeMapper(repository),
				GitHubRepositoryConnector.KIND, repository.getRepositoryUrl(),
				issue.getNumber());
		data.setVersion(DATA_VERSION);
		
		createAttribute(data, GitHubTaskAttributes.KEY,issue.getNumber());
		createAttribute(data, GitHubTaskAttributes.TITLE, issue.getTitle());
		createAttribute(data, GitHubTaskAttributes.BODY, issue.getBody());

		data.setPartial(true);

		return data;
	}
	
	
	public TaskData createTaskData(TaskRepository repository,
			IProgressMonitor monitor, String user, String project,
			GitHubIssue issue) {
		TaskData taskData = createPartialTaskData(repository, monitor, user, project, issue);
		taskData.setPartial(false);
		
		return taskData;
	}

	private GitHubIssue createIssue(TaskData taskData) {
		GitHubIssue issue = new GitHubIssue();
		if (!taskData.isNew()) {
			issue.setNumber(taskData.getTaskId());
		}
		issue.setBody(getAttributeValue(taskData,GitHubTaskAttributes.BODY));
		issue.setTitle(getAttributeValue(taskData,GitHubTaskAttributes.TITLE));
		return issue;
	}
	
	private String getAttributeValue(TaskData taskData,
			GitHubTaskAttributes attr) {
		TaskAttribute attribute = taskData.getRoot().getAttribute(attr.name());
		return attribute==null?null:attribute.getValue();
	}

	private void createAttribute(TaskData data, GitHubTaskAttributes attribute, String value) {
		TaskAttribute attr = data.getRoot().createAttribute(attribute.name());
		TaskAttributeMetaData metaData = attr.getMetaData();
		metaData.defaults()
			.setType(attribute.getType())
			.setKind(attribute.getKind())
			.setLabel(attribute.getLabel())
			.setReadOnly(attribute.isReadOnly());

		if (value != null) {
			attr.addValue(value);
		}
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData data,
			ITaskMapping initializationData, IProgressMonitor monitor)
			throws CoreException {
		
		data.setVersion(DATA_VERSION);

		for (GitHubTaskAttributes attr: GitHubTaskAttributes.values()) {
			if (attr.isInitTask()) {
				createAttribute(data, attr,null);		
			}
		}
		
		return true;
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository,
			TaskData taskData, Set<TaskAttribute> oldAttributes,
			IProgressMonitor monitor) throws CoreException {
		
		GitHubIssue issue = createIssue(taskData);
		String user = connector.computeTaskRepositoryUser(repository);
		String repo = connector.computeTaskRepositoryProject(repository);
		try {
			GitHubService service = connector.getService();
			if (taskData.isNew()) {
				issue = service.openIssue(user , repo, issue, GitHubCredentials.create(repository));
			} else {
				service.editIssue(user , repo, issue, GitHubCredentials.create(repository));
			}
			return new RepositoryResponse(taskData.isNew()?ResponseKind.TASK_CREATED:ResponseKind.TASK_UPDATED,issue.getNumber());
		} catch (GitHubServiceException e) {
			throw new CoreException(GitHub.createErrorStatus(e));
		}
		
	}


}
