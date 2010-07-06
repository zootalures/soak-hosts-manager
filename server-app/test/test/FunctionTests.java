package test;

import java.util.Date;

import junit.framework.TestCase;
import edu.bath.soak.web.tags.Functions;

public class FunctionTests extends TestCase {

	public void testRelativeTimes() {
		Date now = new Date();
		assertEquals("0 secs", Functions.relativeTime(now, now));
		assertEquals("1 sec", Functions.relativeTime(
				new Date(now.getTime() - 1000), now));
		assertEquals("1 min", Functions.relativeTime(new Date(
				now.getTime() - 60000), now));
		assertEquals("1 min", Functions.relativeTime(new Date(
				now.getTime() - 61000), now));
		assertEquals("2 mins", Functions.relativeTime(new Date(
				now.getTime() - 120000), now));
		assertEquals("1 hour", Functions.relativeTime(new Date(
				now.getTime() - 3600000), now));
		assertEquals("5 hours", Functions.relativeTime(new Date(now.getTime()
				- (3600000 * 5)), now));
		assertEquals("5 hours", Functions.relativeTime(new Date(now.getTime()
				- (3600000 * 5) -1000), now));
		assertEquals("1 day", Functions.relativeTime(new Date(now.getTime()
				- (3600000 * 24 )), now));

		assertEquals("1 week", Functions.relativeTime(new Date(now.getTime()
				- (3600000 * 24L * 7L)), now));
		assertEquals("2 weeks", Functions.relativeTime(new Date(now.getTime()
				- (3600000 * 24L * 14L)), now));
		assertEquals("2 weeks", Functions.relativeTime(new Date(now.getTime()
				- (3600000 * 24L * 15L)), now));
		assertEquals("1 year", Functions.relativeTime(new Date(now.getTime()
				- (3600000 * 24L * 365L)), now));
		assertEquals("10 years", Functions.relativeTime(new Date(now.getTime()
				- (3600000 * 24L * 3650L)), now));

	}
}
