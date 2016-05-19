package onlim.api.generator;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract template text which can be additionally described by
 * meta information, constraints and a set of substitutables
 */
public class Template implements Cloneable {
	private static final Logger LOGGER = LoggerFactory.getLogger(Template.class);
	
	private String template;
	private Map<String, Substitutable> substitutables;
	private final Map<String, Object> metas;
	private final List<Constraint> constraints;
	
	/**
	 * Do not call this on your own, use {@link onlim.api.generator.TemplateBuilder} instead.
	 */
	public Template(final String template, final Map<String, Substitutable> substitutables,
			final Map<String, Object> metas, final List<Constraint> constraints) {
		this.template = template;
		this.substitutables = substitutables;
		this.metas = metas;
		this.constraints = constraints;
	}
	
	/**
	 * Checks whether the template has been marked as resolved.
	 * Such mark is set, if all substitutables have been substituted.
	 * 
	 * @return true iff the template is resolved
	 */
	public boolean isResolved() {
		return substitutables.isEmpty();
	}
	
	/**
	 * Checks whether the given set of substitutables contains enough
	 * information to resolve the given template.
	 * 
	 * @param substitutables substitutables to check for
	 * @return true iff the template can be resolved
	 */
	public boolean isResolvable(final Set<Substitutable> substitutables) {
		int count = 0;
		for (final Substitutable substitutable: substitutables) {
			if (isSubstitutable(substitutable))
				++count;
		}
		return count == this.substitutables.size();
	}
	
	/**
	 * Checks whether a given substitutable is mapped to the template
	 * 
	 * @param substitutable substitutable to check for
	 * @return true iff the substitutable is mapped to the template
	 */
	public boolean isSubstitutable(final Substitutable substitutable) {
		if (!substitutable.isSatisfiable(getMetaValues()))
			return false;
		
		for (final Entry<String, Substitutable> e : this.substitutables.entrySet()) {
			if (e.getValue().equals(substitutable))
				return true;
		}
		return false;
	}

	/**
	 * Checks whether all associated constraints are satisfied
	 * 
	 * @return true iff all constraints are satisfied
	 */
	public boolean isSatisfiable() {
		final Map<String, Object> arg = getMetaValues();
		for (final Constraint c : constraints) {
			if (!c.evaluate(arg))
				return false;
		}
		return true;
	}
	
	/**
	 * Returns an attached meta value, if present.
	 * 
	 * @param key used to lookup value
	 * @return non-null if key exists
	 */
	public Object getMetaValue(final String key) {
		return this.metas.get(key);
	}
	
	/**
	 * Obtain an unmodifiable object representing all attached meta values.
	 * 
	 * @return unmodifiable object
	 */
	public Map<String, Object> getMetaValues() {
		return Collections.unmodifiableMap(this.metas);
	}
	
	/**
	 * Tries to substitute a given substitutable with an abstract value
	 * 
	 * @param substitutable substitutable to substitute
	 * @param value value to use as replacement
	 * @return reference to this
	 */
	public Template substitute(final Substitutable substitutable, final String value) {
		if (isResolved())
			return this;
		
		if (!isSubstitutable(substitutable)) {
			LOGGER.warn("invalid attempt to subsitute invalid substitutable: " + substitutable.toString());
			return this;
		}
		
		final Set<String> processed = new HashSet<>();
		for (final Entry<String, Substitutable> e : this.substitutables.entrySet()) {
			if (!e.getValue().equals(substitutable))
				continue;
			
			substitute(e.getKey(), value);
			processed.add(e.getKey());	
		}
		for (final String t : processed) {
			this.substitutables.remove(t);
		}
		return this;
	}
	
	private void substitute(final String placeholder, final String value) {
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(placeholder);
		sb.append("}");
		this.template = this.template.replace(sb.toString(), value);
	}
	
	@Override
	public String toString() {
		return this.template;
	}

	@Override
	public Template clone() throws CloneNotSupportedException {
		 final Template result = Template.class.cast(super.clone());
		 result.template = new String(template);
		 result.substitutables = new HashMap<>(substitutables);
		 return result;
	}
}
