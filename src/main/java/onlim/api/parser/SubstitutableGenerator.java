package onlim.api.parser;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import onlim.api.generator.Constraint;
import onlim.api.parser.resources.ParsedSubstitutable;
import onlim.api.parser.resources.Triple;
import onlim.api.reasoner.Reasoner;

/**
 * This class generates the {@link ParsedSubstitutable} objects, which can be inserted into templates.
 * Simply create a new instance of this class and hand over the list of triples received from {@link JsonLdParser} and call
 * the generateSubstitutables method.
 */
public class SubstitutableGenerator {

	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(SubstitutableGenerator.class);
	// TODO use logger
	private static final String RDF_TYPE = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
	private List<Triple> triples;

	/**
	 * Constructor for the generator
	 * @param triples - list of triples received from the parser
	 */
	public SubstitutableGenerator(final List<Triple> triples) {
		this.triples = triples;
	}

	/**
	 * use this function to generate {@link ParsedSubstitutable} out of the triples
	 * @return list of {@link ParsedSubstitutable}
	 */
	public List<ParsedSubstitutable> generateSubstitutables() {
		List<Triple> roots = getRoots();
		// we don't need triples without a type
		triples = removeTriplesWithNoType();
		List<ParsedSubstitutable> subst = new LinkedList<>();
		List<String> alreadyDone = new LinkedList<>();
		for (Triple t : roots) {
			// don't process a subject more than once
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

	/**
	 * recursive function to go through the NQUADS RDF
	 * @param subst - list of current {@link ParsedSubstitutable}
	 * @param props - properties for this recursive step (e.g. schema.org/Name ...)
	 * @param next  - next object to convert
	 * @param c     - schema constraint for all {@link ParsedSubstitutable} (e.g. only usable in Offers)
	 */
	private void generate(List<ParsedSubstitutable> subst, List<String> props, String next, Constraint c) {
		List<Triple> trip = getAssociatedTriples(next);

		// if next is a root, build a new constraint
		if (isRoot(next))
			c = buildSchemaConstraint(getType(next));
		
		Set<String> seen = new HashSet<>();

		for (Triple t : trip) {
			// copy the properties so that every recursive call has a copy
			List<String> properties = new LinkedList<>(props);
			if (isTypeTriple(t))
				continue;
			List<ParsedSubstitutable> sub = new LinkedList<>();
					
			// if right side is a value
			if (isValue(t.getObject())) {
				String lang = getLanguage(t.getObject());
				ParsedSubstitutable substi = new ParsedSubstitutable();
				
				// add all properties that were so far
				substi.addProperties(properties);
				// if this triple is not a root add its subject to the properties
				if (!isRoot(t))
					substi.addProperty(getType(t.getSubject()));
				
				// if there is no language constraint given, check for duplicates and insert a unique ID
				if(lang == null)
					checkDuplicate(t.getPredicate(), seen, substi.getProperties());
				// else add a constraint for the language
				else
					substi.addConstraint(buildLanguageConstraint(lang));
				
				substi.addProperty(t.getPredicate());
				// remove quotes and extensions (e.g. ^^ @)
				substi.setValue(removeQuotes(removeExtensions(t.getObject())));
				
				// add the root constraint (e.g. all properties belong to an Offer)
				if (c != null)
					substi.addConstraint(c);
				
				sub.add(substi);
			// else it is an ID to another subject
			} else {
				// if t is not a root add its subject to the properties
				if (!isRoot(t))
					properties.add(getType(t.getSubject()));
				// again check for duplicates and insert a unique ID if so
				checkDuplicate(t.getPredicate(), seen, properties);
				properties.add(t.getPredicate());
				// recursive call, because the right side is a reference to another subject
				generate(sub, properties, t.getObject(), c);
			}
			// at the current property to the seen list, as we need unique substitutables
			seen.add(t.getPredicate());
			// add all substitutables
			subst.addAll(sub);
		}
	}
	
	/**
	 * checks if dup has already been seen, if yes add a new property with an unique ID
	 * @param dup 	- item to check
	 * @param seen 	- already seen items
	 * @param props - current properties
	 * @return
	 */
	private boolean checkDuplicate(final String dup, final Set<String> seen, List<String> props) {
		if(seen.contains(dup)) {
			props.add(String.valueOf(Collections.frequency(seen, dup)));
			return true;
		}
		return false;
	}

	/**
	 * builds a schema constraint for the {@link ParsedSubstitutable} (e.g. this {@link ParsedSubstitutable} is only 
	 * valid in a template that is for type Offer or its super classes
	 * @param expectedType - type of the root subject
	 * @return {@link Constraint}
	 */
	private Constraint buildSchemaConstraint(final String expectedType) {
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

	/**
	 * builds a language constraint for a {@link ParsedSubstitutable} (e.g. this {@link ParsedSubstitutable} can be only used 
	 * in english templates
	 * @param expectedLanguage
	 * @return
	 */
	private Constraint buildLanguageConstraint(final String expectedLanguage) {
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

	/**
	 * this function removes triples with no type, as they are useless to process
	 * @return new list of triples
	 */
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

	/**
	 * this function returns all triples with the specified {@link Id}
	 * @param id
	 * @return list of triples
	 */
	public List<Triple> getAssociatedTriples(final String id) {
		List<Triple> result = new LinkedList<>();

		for (Triple t : triples) {
			if (t.getSubject().equals(id))
				result.add(t);
		}
		return result;
	}

	/**
	 * this function returns all root nodes of the list of triples
	 * @return list of triples
	 */
	public List<Triple> getRoots() {
		List<Triple> result = new LinkedList<>();
		for (Triple t : triples) {
			if (isRoot(t) && !result.contains(t)) {
				result.add(t);
			}
		}
		return result;
	}

	/**
	 * this function determines the type of the specified {@link Id}
	 * @param id
	 * @return type
	 */
	public String getType(final String id) {
		for (Triple t : getAssociatedTriples(id)) {
			if (t.getPredicate().equals(RDF_TYPE))
				return t.getObject();
		}
		return null;
	}

	
	/**
	 * this function checks if the the object of a triples is never on the right side. This means that is has to be a value.
	 * @param v
	 * @return true if v is a value, false otherwise
	 */
	private boolean isValue(final String v) {
		for (Triple t : triples) {
			if (t.getSubject().equals(v))
				return false;
		}
		return true;
	}

	/**
	 * this function simply removes the quotes of a string
	 * @param v
	 * @return unquoted string
	 */
	public String removeQuotes(final String v) {
		if (v.charAt(0) == '\"' && v.charAt(v.length() - 1) == '\"')
			return v.substring(1, v.length() - 1);
		return v;
	}
	
	/**
	 * this function simply removes any extensions starting with @ and ^^
	 * @param v
	 * @return string with removed extensions
	 */
	public String removeExtensions(final String v) {
		int indexLang = v.indexOf("\"@");
		int indexType = v.indexOf("\"^^");
		if (indexLang == -1 && indexType == -1)
			return v;
		return v.substring(0, (indexLang == -1) ? (indexType + 1) : (indexLang + 1));
	}

	/**
	 * this function determines the language of a value
	 * @param v
	 * @return language of the value, null if language extension is not given
	 */
	private String getLanguage(final String v) {
		int index = v.indexOf("\"@");
		if (index == -1)
			return null;
		return v.substring(index + 2);
	}
	
	/**
	 * this function checks if the triple t has a type property
	 * @param t
	 * @return true if t has a type property, false otherwise
	 */
	public boolean isTypeTriple(final Triple t) {
		if (t.getPredicate().equals(RDF_TYPE))
			return true;
		return false;
	}

	/**
	 * this function simply checks if the triple t is a root
	 * @param t
	 * @return true if t is a root, false otherwise
	 */
	public boolean isRoot(final Triple t) {
		return isRoot(t.getSubject());
	}

	/**
	 * this function simply checks if id is a root
	 * @param id
	 * @return true if id is a root, false otherwise
	 */
	public boolean isRoot(final String id) {
		for (Triple triple : triples) {
			if (triple.getObject().equals(id)) {
				return false;
			}
		}
		return true;
	}
}
