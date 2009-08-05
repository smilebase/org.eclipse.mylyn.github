package org.eclipse.mylyn.github.ui.internal;

import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class GitHubRepositoryQueryPage extends AbstractRepositoryQueryPage {

	public GitHubRepositoryQueryPage(TaskRepository taskRepository,
			IRepositoryQuery query) {
		super("GitHub", taskRepository, query);
	}

	@Override
	public void applyTo(IRepositoryQuery query) {
		query.setSummary("Test GitHub Query");
        query.setAttribute("id", "QUERY1234");
	}

	@Override
	public String getQueryTitle() {
		return "GitHub Query";
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData());
		composite.setLayout(new GridLayout(1, false));
		new Label(composite, SWT.BORDER).setText("HALLO");
		setControl(composite);
	}

}
