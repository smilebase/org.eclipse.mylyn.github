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

import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * GitHub connector specific extensions.
 * 
 * @author Christian Trutz
 * @since 0.1.0
 */
public class GitHubRepositoryQueryPage extends AbstractRepositoryQueryPage {

	private Text owner = null, project = null, queryText = null;

	private Combo statOptions = null;

	String states[] = { "open", "closed" };

	/**
	 * @param taskRepository
	 * @param query
	 */
	public GitHubRepositoryQueryPage(final TaskRepository taskRepository,
			final IRepositoryQuery query) {
		super("GitHub", taskRepository, query);
	}

	@Override
	public void applyTo(IRepositoryQuery query) {
		query.setSummary("Test GitHub Query");
		query.setAttribute("owner", owner.getText());
		query.setAttribute("project", project.getText());
		query.setAttribute("status", statOptions.getText());
		query.setAttribute("queryText", queryText.getText());
	}

	@Override
	public String getQueryTitle() {
		return "GitHub Query";
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData());
		composite.setLayout(new GridLayout(2, false));

		final int MAX_WIDTH = 20;
		final int MAX_HEIGHT = 20;

		// Create the owner entry box
		new Label(composite, SWT.BORDER).setText("Owner:");
		owner = new Text(composite, SWT.BORDER);
		owner.setBounds(owner.getBounds().x, owner.getBounds().y, MAX_WIDTH,
				MAX_HEIGHT);

		// Create the project entry box
		new Label(composite, SWT.BORDER).setText("Project:");
		project = new Text(composite, SWT.BORDER);

		// Create the Status label and status option combo box
		(new Label(composite, SWT.NULL)).setText("Status:");
		statOptions = new Combo(composite, SWT.READ_ONLY);
		statOptions.setItems(states);
		// Set to a sane default
		statOptions.setText("open");

		// Create the query entry box
		new Label(composite, SWT.BORDER).setText("Query text:");
		queryText = new Text(composite, SWT.BORDER);

		setControl(composite);
	}

}
