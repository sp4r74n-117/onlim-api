package onlim.api.reasoner.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import onlim.api.reasoner.Reasoner;

public class ReasonerTest {
	@Test
	public void testGetClassPath() {
		final Reasoner reasoner = Reasoner.get();
		
		final List<String> path = reasoner.getClassPath("<http://schema.org/LocalBusiness>");
		assertEquals(path.size(), 4);
		assertTrue(path.contains(Reasoner.THING));
		assertTrue(path.contains("<http://schema.org/Place>"));
		assertTrue(path.contains("<http://schema.org/LocalBusiness>"));
		assertTrue(path.contains("<http://schema.org/Organization>"));
	}
}
