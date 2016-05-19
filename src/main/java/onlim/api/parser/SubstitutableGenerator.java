package onlim.api.parser;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import onlim.api.generator.Constraint;
import onlim.api.parser.resources.ParsedSubstitutable;
import onlim.api.parser.resources.Triple;

public class SubstitutableGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubstitutableGenerator.class);
	private static final String RDF_TYPE = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
	private List<Triple> triples;

	public SubstitutableGenerator(final List<Triple> triples) {
		this.triples = triples;
	}

	public Set<ParsedSubstitutable> generateSubstitutables() {
		List<Triple> roots = getRoots();
		triples = removeTriplesWithNoType();
		Set<ParsedSubstitutable> subst = new HashSet<>();
		List<String> alreadyDone = new LinkedList<>();
		for(Triple t : roots) {
			if(alreadyDone.contains(t.getSubject())) continue;
			List<ParsedSubstitutable> sub = new LinkedList<>();
			List<String> properties = new LinkedList<>();
			if(!triples.contains(t)) {
				generate(sub, properties, t.getObject(), null);
			} else {
				generate(sub, properties, t.getSubject(), null);
			}
			alreadyDone.add(t.getSubject());
			subst.addAll(sub);
		}

		return subst;
	}

	private void generate(List<ParsedSubstitutable> subst, List<String> props, String next, Constraint c) {
		List<Triple> trip = getAssociatedTriples(next);
		
		if(isRoot(next)) c = buildSchemaConstraint(getType(next));
					
		for(Triple t: trip) {
			List<String> properties = new LinkedList<>(props);
			if(isTypeTriple(t)) continue;
			List<ParsedSubstitutable> sub = new LinkedList<>();
			if (isValue(t.getObject())) {
				if(!languageConstraintCheck(t.getObject())) continue;
				ParsedSubstitutable substi = new ParsedSubstitutable();
				substi.addProperties(properties);
				if(!isRoot(t))
					substi.addProperty(getType(t.getSubject()));
				substi.addProperty(t.getPredicate());
				substi.setValue(removeExtensions(t.getObject()));
				if(c != null) substi.addConstraint(c);
				sub.add(substi);
			} else {
				if(!isRoot(t)) properties.add(getType(t.getSubject()));
				properties.add(t.getPredicate());
				generate(sub, properties, t.getObject(), c);
			}
			subst.addAll(sub);
		}
	}
	
	public Constraint buildSchemaConstraint(final String expectedType) {
		Constraint c = new Constraint() {
			@Override
			public boolean evaluate(final Map<String, Object> data) {
				final Object value = data.get("schema_type");
				if (value == null)
					return false;

				return value.toString().equals(expectedType);
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
	
	public String removeExtensions(final String v) {
		int indexLang = v.indexOf("\"@en");
		int indexType = v.indexOf("\"^^");
		if(indexLang == -1 && indexType == -1)
			return v;
		return v.substring(0, (indexLang == -1) ? (indexType+1) : (indexLang+1));
	}
	
	public boolean languageConstraintCheck(final String v) {
		int index = v.indexOf("\"@");
		if(index == -1) return true;
		if(v.substring(index).equals("\"@en")) return true;
		return false;
	}
	
	public boolean isTypeTriple(final Triple t) {
		if(t.getPredicate().equals(RDF_TYPE))
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
