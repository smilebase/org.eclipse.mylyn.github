package org.eclipse.mylyn.github.ui.internal;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.github.GitHubIssue;
import org.eclipse.mylyn.github.GitHubTaskAttributes;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
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

	/**
	 * 
	 */
	private GitHubTaskAttributeMapper taskAttributeMapper = null;

	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository taskRepository) {
		if (this.taskAttributeMapper == null)
			this.taskAttributeMapper = new GitHubTaskAttributeMapper(
					taskRepository);
		return this.taskAttributeMapper;
	}

	public TaskData createPartialTaskData(TaskRepository repository,
			IProgressMonitor monitor, GitHubIssue issue) {

		TaskData data = new TaskData(getAttributeMapper(repository),
				GitHubRepositoryConnector.KIND, repository.getRepositoryUrl(),
				issue.getNumber());
		data.setVersion("1");

		createAttribute(data, GitHubTaskAttributes.TITLE.name(), issue
				.getTitle());

		data.setPartial(true);

		return data;
	}

	private void createAttribute(TaskData data, String key, String value) {
		TaskAttribute attr = data.getRoot().createAttribute(key);
		TaskAttributeMetaData metaData = attr.getMetaData();
		metaData.defaults();

		metaData.setType(TaskAttribute.TYPE_SHORT_TEXT);
		metaData.setKind(TaskAttribute.KIND_DEFAULT);
		metaData.setLabel("LABEL:");
		metaData.setReadOnly(true);

		if (value != null) {
			attr.addValue(value);
		}
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData data,
			ITaskMapping initializationData, IProgressMonitor monitor)
			throws CoreException {
		return false;
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository,
			TaskData taskData, Set<TaskAttribute> oldAttributes,
			IProgressMonitor monitor) throws CoreException {
		return null;
	}

}
