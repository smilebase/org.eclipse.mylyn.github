package org.eclipse.mylyn.github.tests;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.eclipse.mylyn.github.internal.GitHub;
import org.junit.Test;

/**
 * Tests for the {@link GitHub} class.
 * @author Fabian Steeg (fsteeg)
 */
public class GitHubTest {
  @Test
  public void parseDate() {
    Date date = GitHub.parseDate("2009/04/17 12:57:52 -0700");
    Assert.assertNotNull(date);
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    Assert.assertEquals(2009, cal.get(Calendar.YEAR));
    Assert.assertEquals(Calendar.APRIL, cal.get(Calendar.MONTH));
    Assert.assertEquals(17, cal.get(Calendar.DAY_OF_MONTH));
    // Testing other fields gets nasty with time zones, daylight savings, etc.
  }
}
