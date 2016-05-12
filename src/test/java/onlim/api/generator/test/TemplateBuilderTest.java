package onlim.api.generator.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import onlim.api.generator.Constraint;
import onlim.api.generator.Substitutable;
import onlim.api.generator.Template;
import onlim.api.generator.TemplateBuilder;

import org.junit.Before;
import org.junit.Test;

public class TemplateBuilderTest {
	
	private TemplateBuilder tb;
	
	@Before
    public void setUp() {
		if (tb != null)
			return;
		
		try {
			tb = new TemplateBuilder(getClass().getClassLoader(),
				"onlim/api/generator/test/resources/test.template");
		} catch (final IOException e) {
			e.printStackTrace();
		}
		assertNotEquals(null, tb);
    }
	
	@Test
	public void testSubstitution() {
		final Substitutable s1 = new MockSubstitutable("a");
		final Substitutable s2 = new MockSubstitutable("b");
		
		final Template tm = tb.addMapping("1", s1).build();
		assertTrue(tm.toString().contains("{1}"));
		assertFalse(tm.isResolved());
		assertFalse(tm.substitute(s2, "").isResolved());
		assertTrue(tm.substitute(s1, "test").isResolved());
		assertEquals("This is a test substitution.", tm.toString());
	}
	
	@Test
	public void testMetaValue() {
		final Template tm = tb.addMetaValue("a", "b").build();
		
		final Object value = tm.getMetaValue("a");
		assertNotEquals(null, value);
		assertEquals("b", value);
		assertTrue(value instanceof String);
	}
	
	@Test
	public void testConstraints() {
		final Constraint c = new Constraint() {
			@Override
			public boolean evaluate(final Map<String, Object> data) {
				final Object value = data.get("language");
				if (value == null) return false;
				
				return value.toString().equals("en_US");
			}
		};
		Template tm = tb.build();
		assertTrue(tm.isSatisfiable());
		
		tm = tb.rewind().addMetaValue("language", "de_DE").addConstraint(c).build();
		assertFalse(tm.isSatisfiable());
		
		tm = tb.rewind().addMetaValue("language", "en_US").addConstraint(c).build();
		assertTrue(tm.isSatisfiable());
		
		final Substitutable ss = new MockSubstitutable("a");
		ss.addConstraint(c);
		
		tm = tb.rewind().addMapping("1", ss).addMetaValue("language", "de_DE").build();
		assertFalse(tm.isSubstitutable(ss));
		
		tm = tb.rewind().addMapping("1", ss).addMetaValue("language", "en_US").build();
		assertTrue(tm.isSubstitutable(ss));
	}
}
