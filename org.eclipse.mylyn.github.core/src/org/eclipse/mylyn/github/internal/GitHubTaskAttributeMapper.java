package org.eclipse.mylyn.github.internal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

/**
 * 
 * @author Christian Trutz
 */
public class GitHubTaskAttributeMapper extends TaskAttributeMapper {

	private DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
	
	public GitHubTaskAttributeMapper(TaskRepository taskRepository) {
		super(taskRepository);
	}

	@Override
	public String mapToRepositoryKey(TaskAttribute parent, String key) {
		return key;
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
