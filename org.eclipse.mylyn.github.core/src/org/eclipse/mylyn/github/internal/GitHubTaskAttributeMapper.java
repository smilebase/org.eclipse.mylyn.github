package org.eclipse.mylyn.github.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.github.GitHubTaskAttributes;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

/**
 * 
 * @author Christian Trutz
 */
public class GitHubTaskAttributeMapper extends TaskAttributeMapper {

	private Map<String,String> commonKeyToRepositoryKey = new HashMap<String, String>();
	
	public GitHubTaskAttributeMapper(TaskRepository taskRepository) {
		super(taskRepository);
		for (GitHubTaskAttributes attr: GitHubTaskAttributes.values()) {
			if (attr.getCommonKey() != null) {
				commonKeyToRepositoryKey.put(attr.getCommonKey(), attr.name());
			}
		}
	}

	@Override
	public String mapToRepositoryKey(TaskAttribute parent, String key) {
		return commonKeyToRepositoryKey.get(key);
	}

}
