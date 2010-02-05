package org.eclipse.mylyn.github.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { // 
	GitHubServiceTest.class,
	MarshalingTest.class
	})
public class AllHeadlessTests {

}
