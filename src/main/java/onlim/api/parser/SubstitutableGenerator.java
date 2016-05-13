package onlim.api.parser;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import onlim.api.parser.resources.ParsedSubstitutable;
import onlim.api.parser.resources.Triple;

public class SubstitutableGenerator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SubstitutableGenerator.class);
	private List<Triple> triples;
	
	public SubstitutableGenerator(final List<Triple> triples) {
		this.triples = triples;
	}
	
	public List<ParsedSubstitutable> generateSubstitutables() {
		List<Triple> roots = getRoots();
		
		return null;
	}
	
	public List<Triple> getAssociatedTriples(final String id) {
		List<Triple> result = new LinkedList<>();
		
		for(Triple t : triples) {
			if(t.getSubject().equals(id))
				result.add(t);
		}
		return result;
	}
	
	public List<Triple> getRoots() {
		List<Triple> result = new LinkedList<>();
		for(Triple t : triples) {
			if(isRoot(t) && !result.contains(t)) {
				result.add(t);
			}
		}		
		return result;
	}
	
	private boolean isValue(final String v) {
		for(Triple t : triples) {
			if(t.getSubject().equals(v))
				return false;
		}	
		return true;
	}
	
	private boolean isRoot(final Triple t) {		
		for(Triple triple : triples) {
			if(t.getSubject().equals(triple.getObject())) {
				return false;
			}
		}		
		return true;
	}
}
