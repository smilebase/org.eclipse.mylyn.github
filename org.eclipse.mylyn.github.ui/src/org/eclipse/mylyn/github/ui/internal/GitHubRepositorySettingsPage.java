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

import java.util.regex.Matcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.github.internal.GitHubService;
import org.eclipse.mylyn.github.internal.GitHubServiceException;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

/**
 * GitHub connector specific extensions.
 * 
 * @author Christian Trutz
 * @author Brian Gianforcaro
 * @since 0.1.0
 */
public class GitHubRepositorySettingsPage extends
		AbstractRepositorySettingsPage {

	static final String URL = "http://www.github.org";

	private static final String PASS_LABEL_TEXT = "GitHub API Key";

	/**
	 * Populate taskRepository with repository settings.
	 * 
	 * @param taskRepository
	 *            - Object to populate
	 */
	public GitHubRepositorySettingsPage(final TaskRepository taskRepository) {
		super("GitHub Repository Settings", "", taskRepository);
		this.setHttpAuth(false);
		this.setNeedsAdvanced(false);
		this.setNeedsAnonymousLogin(false);
		this.setNeedsTimeZone(false);
		this.setNeedsHttpAuth(false);
	}

	@Override
	public String getConnectorKind() {
		return GitHub.CONNECTOR_KIND;
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		// Set the URL now, because serverURL is definitely instantiated .
		if (serverUrlCombo != null && (serverUrlCombo.getText() == null || serverUrlCombo.getText().trim().length() == 0)) {
			String fullUrlText = URL+"/user/project";
			serverUrlCombo.setText(fullUrlText);
			// select the user/project part of the URL so that the user can just start
			// typing to replace the text.
			serverUrlCombo.setSelection(new Point(URL.length()+1,fullUrlText.length()));
		}

		// Specify that you need the GitHub User Name
		if (repositoryUserNameEditor != null) {
			String text = repositoryUserNameEditor.getLabelText();
			repositoryUserNameEditor.setLabelText("GitHub " + text);
		}

		// Use the password field for the API Token Key
		if (repositoryPasswordEditor != null) {
			repositoryPasswordEditor.setLabelText(PASS_LABEL_TEXT);
		}
	}

	@Override
	protected Validator getValidator(final TaskRepository repository) {
		Validator validator = new Validator() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				monitor.worked(25);
				
				String urlText = repository.getUrl();
				Matcher urlMatcher = GitHub.URL_PATTERN.matcher(urlText==null?"":urlText);
				if (!urlMatcher.matches()) {
					setStatus(GitHubUi.createErrorStatus("Server URL must be in the form http://www.github.org/user/project"));
					monitor.done();
					return;
				}
				String user = urlMatcher.group(1);
				String repo = urlMatcher.group(2);
				
				monitor.beginTask("Starting..", 25);

				GitHubService service = new GitHubService();

				monitor.beginTask("Contacting Server...", 50);

				try {
					service.searchIssues(user, repo, new String("open"),
							new String(""));
				} catch (GitHubServiceException e) {
					String msg = new String("Repository Test failed:"
							+ e.getMessage());
					this.setStatus(GitHubUi.createErrorStatus(msg));
					monitor.done();
					return;
				}
				
				// FIXME username/API key test
				
				Status stat = new Status(IStatus.OK,
						GitHubUi.BUNDLE_ID, "Success!");
				this.setStatus(stat);
				monitor.done();
			}
		};
		return validator;
	}

	@Override
	protected boolean isValidUrl(final String url) {
		if (url.contains("github")) {
			return true;
		}
		return false;
	}

}
