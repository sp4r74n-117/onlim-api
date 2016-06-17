package onlim.api.bridge;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import onlim.api.generator.Resolver;
import onlim.api.generator.Substitutable;
import onlim.api.generator.Template;
import onlim.api.generator.TemplateStore;
import onlim.api.parser.ParsedSubstitutable;
import onlim.api.parser.SubstitutableGenerator;
import onlim.api.parser.Triple;

/**
 * Acts as bridge between onlim.api.generator and onlim.api.parser
 */
public class Bridge {
	/**
	 * Construct a concrete {@link onlim.api.generator.Substitutable}
	 * 
	 * @param properties a set of properties which are passed to the
	 *        underlying substitutable during construction
	 * @return
	 */
	public static Substitutable pro(String... properties) {
		final ParsedSubstitutable ps = new ParsedSubstitutable();
		ps.addProperties(Arrays.asList(properties));
		return ps;
	}
	
	/**
	 * Generate a list of text templates out of a set of triples
	 * 
	 * @param triples triples to use for template generation
	 * @return generated templates
	 */
	public static List<String> gen(final List<Triple> triples) throws Exception {
		final SubstitutableGenerator sg = new SubstitutableGenerator(triples);
		final List<String> result = new LinkedList<>();
		final List<Template> templates = TemplateStore.get().resolve(sg.generateSubstitutables(), new Resolver() {
			@Override
			public String resolve(Substitutable substitutable) throws Exception {
				return ParsedSubstitutable.class.cast(substitutable).getValue();
			}
		});
		for (final Template tm : templates)
			result.add(tm.toString());
		return result;
	}
	
	private Bridge() {
		// no-op
	}
}
