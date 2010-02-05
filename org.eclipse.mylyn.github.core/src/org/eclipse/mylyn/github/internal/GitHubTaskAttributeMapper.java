package org.eclipse.mylyn.github.internal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

/**
 * 
 * @author Christian Trutz
 */
public class GitHubTaskAttributeMapper extends TaskAttributeMapper {

	private Map<String,String> commonKeyToRepositoryKey = new HashMap<String, String>();
	
	private DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
	
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

	@Override
	public Date getDateValue(TaskAttribute attribute) {
		String value = attribute.getValue();
		if (value != null) {
			try {
				return dateFormat.parse(value);
			} catch (ParseException e) {
				return super.getDateValue(attribute);
			}
		}
		return null;
	}
}
