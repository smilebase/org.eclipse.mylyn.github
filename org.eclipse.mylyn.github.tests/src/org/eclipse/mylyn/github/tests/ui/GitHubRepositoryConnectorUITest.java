package org.eclipse.mylyn.github.tests.ui;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import junit.framework.Assert;

import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.github.internal.GitHub;
import org.eclipse.mylyn.github.ui.internal.GitHubRepositoryConnectorUI;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.junit.Before;
import org.junit.Test;

public class GitHubRepositoryConnectorUITest {

	private GitHubRepositoryConnectorUI connectorUI;
	private TaskRepository repository;

	@Before
	public void before() {
		connectorUI = new GitHubRepositoryConnectorUI();
		repository = new TaskRepository(GitHub.CONNECTOR_KIND, GitHub.createGitHubUrl("foo", "bar"));
	}
	
	@Test
	public void testFindHyperlinksTaskRepositoryStringIntInt() {
		IHyperlink[] hyperlinks = connectorUI.findHyperlinks(repository, "one #2 three", -1, 0);
		assertNotNull(hyperlinks);
		assertEquals(1,hyperlinks.length);
		assertEquals(new Region(4,2),hyperlinks[0].getHyperlinkRegion());
		
		hyperlinks = connectorUI.findHyperlinks(repository, "one #2 three", -1, 4);
		assertNotNull(hyperlinks);
		assertEquals(1,hyperlinks.length);
		assertEquals(new Region(8,2),hyperlinks[0].getHyperlinkRegion());
	}

}
