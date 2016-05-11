package onlim.api.generator;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Template {
	private String template;
	private final Map<String, Substitutable> substitutables;
	private final Map<String, Object> metas;
	private final List<Constraint> constraints;
	
	public Template(final String template, final Map<String, Substitutable> substitutables,
			final Map<String, Object> metas, final List<Constraint> constraints) {
		this.template = template;
		this.substitutables = substitutables;
		this.metas = metas;
		this.constraints = constraints;
	}
	
	public boolean isResolved() {
		return substitutables.isEmpty();
	}
	
	public boolean isSubstitutable(final Substitutable substitutable) {
		for (final Entry<String, Substitutable> e : this.substitutables.entrySet()) {
			if (e.getValue().equals(substitutable))
				return true;
		}
		return false;
	}
	
	public boolean isSatisfiable() {
		final Map<String, Object> arg = getMetaValues();
		for (final Constraint c : constraints) {
			if (!c.evaluate(arg))
				return false;
		}
		return true;
	}
	
	public Object getMetaValue(final String key) {
		return this.metas.get(key);
	}
	
	public Map<String, Object> getMetaValues() {
		return Collections.unmodifiableMap(this.metas);
	}
	
	public Template substitute(final Substitutable substitutable, final String value) {
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
}
