package onlim.api.parser;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import onlim.api.generator.Constraint;
import onlim.api.parser.resources.ParsedSubstitutable;
import onlim.api.parser.resources.Triple;
import onlim.api.reasoner.Reasoner;

public class SubstitutableGenerator {

	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(SubstitutableGenerator.class);
	// TODO use logger
	private static final String RDF_TYPE = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
	private List<Triple> triples;

	public SubstitutableGenerator(final List<Triple> triples) {
		this.triples = triples;
	}

	public List<ParsedSubstitutable> generateSubstitutables() {
		List<Triple> roots = getRoots();
		triples = removeTriplesWithNoType();
		List<ParsedSubstitutable> subst = new LinkedList<>();
		List<String> alreadyDone = new LinkedList<>();
		for (Triple t : roots) {
			if (alreadyDone.contains(t.getSubject()))
				continue;
			List<ParsedSubstitutable> sub = new LinkedList<>();
			List<String> properties = new LinkedList<>();
			if (!triples.contains(t)) {
				// if t had no type proceed with the object
				generate(sub, properties, t.getObject(), null);
			} else {
				// if t has a type proceed with t
				generate(sub, properties, t.getSubject(), null);
			}
			alreadyDone.add(t.getSubject());

			subst.addAll(sub);
		}

		return subst;
	}

	private void generate(List<ParsedSubstitutable> subst, List<String> props, String next, Constraint c) {
		List<Triple> trip = getAssociatedTriples(next);

		if (isRoot(next))
			c = buildSchemaConstraint(getType(next));
		
		Set<String> seen = new HashSet<>();

		for (Triple t : trip) {
			List<String> properties = new LinkedList<>(props);
			if (isTypeTriple(t))
				continue;
			List<ParsedSubstitutable> sub = new LinkedList<>();
					
			if (isValue(t.getObject())) {
				// if(!languageConstraintCheck(t.getObject())) continue;
				String lang = getLanguage(t.getObject());
				ParsedSubstitutable substi = new ParsedSubstitutable();
				
				substi.addProperties(properties);
				if (!isRoot(t))
					substi.addProperty(getType(t.getSubject()));
				
				if(lang == null)
					checkDuplicate(t.getPredicate(), seen, substi.getProperties());
				
				substi.addProperty(t.getPredicate());
				substi.setValue(removeQuotes(removeExtensions(t.getObject())));
				if (c != null)
					substi.addConstraint(c);
				if (lang != null) {
					substi.addConstraint(buildLanguageConstraint(lang));
				}
				sub.add(substi);
			} else {
				if (!isRoot(t))
					properties.add(getType(t.getSubject()));
				checkDuplicate(t.getPredicate(), seen, properties);
				properties.add(t.getPredicate());
				generate(sub, properties, t.getObject(), c);
			}
			seen.add(t.getPredicate());
			subst.addAll(sub);
		}
	}
	
	public boolean checkDuplicate(final String dup, final Set<String> seen, List<String> props) {
		if(seen.contains(dup)) {
			props.add(String.valueOf(Collections.frequency(seen, dup)));
			return true;
		}
		return false;
	}

	public Constraint buildSchemaConstraint(final String expectedType) {
		final Set<String> path = Reasoner.get().getClassPath(expectedType);
		Constraint c = new Constraint() {
			@Override
			public boolean evaluate(final Map<String, Object> data) {
				final Object value = data.get("schema_type");
				if (value == null)
					return false;
				return path.contains(value.toString());
			}
		};
		return c;
	}

	public Constraint buildLanguageConstraint(final String expectedLanguage) {
		Constraint c = new Constraint() {
			@Override
			public boolean evaluate(final Map<String, Object> data) {
				final Object value = data.get("language");
				if (value == null)
					return false;

				return value.toString().equals(expectedLanguage);
			}
		};
		return c;
	}

	public List<Triple> removeTriplesWithNoType() {
		List<Triple> result = new LinkedList<>();
		boolean insert = false;

		for (Triple t : triples) {
			for (Triple tmp : getAssociatedTriples(t.getSubject())) {
				if (tmp.getPredicate().equals(RDF_TYPE)) {
					insert = true;
					break;
				}
			}
			if (insert)
				result.add(t);
		}

		return result;
	}

	public List<Triple> getAssociatedTriples(final String id) {
		List<Triple> result = new LinkedList<>();

		for (Triple t : triples) {
			if (t.getSubject().equals(id))
				result.add(t);
		}
		return result;
	}

	public List<Triple> getRoots() {
		List<Triple> result = new LinkedList<>();
		for (Triple t : triples) {
			if (isRoot(t) && !result.contains(t)) {
				result.add(t);
			}
		}
		return result;
	}

	public String getType(final String id) {
		for (Triple t : getAssociatedTriples(id)) {
			if (t.getPredicate().equals(RDF_TYPE))
				return t.getObject();
		}
		return null;
	}

	public boolean isValue(final String v) {
		for (Triple t : triples) {
			if (t.getSubject().equals(v))
				return false;
		}
		return true;
	}

	public String removeQuotes(final String v) {
		if (v.charAt(0) == '\"' && v.charAt(v.length() - 1) == '\"')
			return v.substring(1, v.length() - 1);
		return v;
	}

	public String removeExtensions(final String v) {
		int indexLang = v.indexOf("\"@");
		int indexType = v.indexOf("\"^^");
		if (indexLang == -1 && indexType == -1)
			return v;
		return v.substring(0, (indexLang == -1) ? (indexType + 1) : (indexLang + 1));
	}

	public boolean languageConstraintCheck(final String v) {
		int index = v.indexOf("\"@");
		if (index == -1)
			return true;
		if (v.substring(index).equals("\"@en"))
			return true;
		return false;
	}

	public String getLanguage(final String v) {
		int index = v.indexOf("\"@");
		if (index == -1)
			return null;
		return v.substring(index + 2);
	}

	public boolean isTypeTriple(final Triple t) {
		if (t.getPredicate().equals(RDF_TYPE))
			return true;
		return false;
	}

	public boolean isRoot(final Triple t) {
		for (Triple triple : triples) {
			if (t.getSubject().equals(triple.getObject())) {
				return false;
			}
		}
		return true;
	}

	public boolean isRoot(final String id) {
		for (Triple triple : triples) {
			if (triple.getObject().equals(id)) {
				return false;
			}
		}
		return true;

	}
}
