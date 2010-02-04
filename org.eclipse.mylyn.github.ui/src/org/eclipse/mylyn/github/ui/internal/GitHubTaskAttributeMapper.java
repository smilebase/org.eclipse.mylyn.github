package org.eclipse.mylyn.github.ui.internal;

import org.eclipse.mylyn.github.GitHubTaskAttributes;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

/**
 * 
 * @author Christian Trutz
 */
public class GitHubTaskAttributeMapper extends TaskAttributeMapper {

	public GitHubTaskAttributeMapper(TaskRepository taskRepository) {
		super(taskRepository);
	}

	@Override
	public String mapToRepositoryKey(TaskAttribute parent, String key) {
		String result = null;
		if (key.equals(TaskAttribute.SUMMARY)) {
			result = GitHubTaskAttributes.TITLE.name();
		} else if (key.equals(TaskAttribute.DESCRIPTION)) {
			result = GitHubTaskAttributes.BODY.name();
		}
		return result;
	}

}
