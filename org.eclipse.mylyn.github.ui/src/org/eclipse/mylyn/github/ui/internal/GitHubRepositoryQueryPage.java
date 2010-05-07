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

	private static final String ATTR_QUERY_TEXT = "queryText";

	private static final String ATTR_STATUS = "status";

	private Text queryText = null;

	private Combo status = null;

	/**
	 * @param taskRepository
	 * @param query
	 */
	public GitHubRepositoryQueryPage(final TaskRepository taskRepository,
			final IRepositoryQuery query) {
		super("GitHub", taskRepository, query);
		setTitle("GitHub search query parameters");
		setDescription("Valid search query parameters entered.");
		setPageComplete(false);
	}

	@Override
	public String getQueryTitle() {
		return "GitHub Query";
	}

	@Override
	public void applyTo(IRepositoryQuery query) {
		String statusString = status.getText();
		String queryString = queryText.getText();
		
		String summary = statusString;
		summary += " issues";
		if (queryString!=null && queryString.trim().length() > 0) {
			summary += " matching "+queryString;
		}
		query.setSummary(summary);
		query.setAttribute(ATTR_STATUS, statusString);
		query.setAttribute(ATTR_QUERY_TEXT, queryString);
	}

	/**
	 * 
	 * 
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginTop = 20;
		gridLayout.marginLeft = 25;
		gridLayout.verticalSpacing = 8;
		gridLayout.horizontalSpacing = 8;
		composite.setLayout(gridLayout);

		
		// create the status option combo box
		new Label(composite, SWT.NONE).setText("Status:");
		status = new Combo(composite, SWT.READ_ONLY);
		String[] queryValues = new String[] { "all", "open", "closed" };
		status.setItems(queryValues);
		status.select(0);
		String queryModelStatus =  getQuery()==null?null:getQuery().getAttribute(ATTR_STATUS);
		if (queryModelStatus != null) {
			for (int x = 0;x<queryValues.length;++x) {
				if (queryValues[x].equals(queryModelStatus)) {
					status.select(x);
					break;
				}
			}
		}

		// create the query entry box
		new Label(composite, SWT.NONE).setText("Query text:");
		queryText = new Text(composite, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.widthHint = 250;
		queryText.setLayoutData(gridData);
		String queryModelText = getQuery()==null?null:getQuery().getAttribute(ATTR_QUERY_TEXT);
		queryText.setText(queryModelText==null?"":queryModelText);

		setControl(composite);
	}

	@Override
	public boolean isPageComplete() {
		setErrorMessage(null);
		return true;
	}

}
