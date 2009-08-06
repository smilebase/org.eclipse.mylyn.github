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

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.widgets.Composite;

/**
 * GitHub connector specific extensions.
 * 
 * @author Christian Trutz
 * @since 0.1.0
 */
public class GitHubRepositorySettingsPage extends
		AbstractRepositorySettingsPage {

	public GitHubRepositorySettingsPage(TaskRepository taskRepository) {
		super("GitHub Repository Settings", "", taskRepository);
	}

	@Override
	public String getConnectorKind() {
		return GitHubRepositoryConnector.KIND;
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		// TODO Auto-generated method stub
	}

	@Override
	protected Validator getValidator(TaskRepository repository) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isValidUrl(String url) {
		// TODO Auto-generated method stub
		return true;
	}

}
